package com.epharmacy.database;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Runs DDL (CREATE TABLE) statements and seeds default data
 * the very first time the application launches.
 */
public class DatabaseInitializer {

    public static void initialize() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            createTables(conn);
            seedData(conn);
            System.out.println("[DB] Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("[DB] Initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  DDL — Create all tables
    // ─────────────────────────────────────────────────────────────────
    private static void createTables(Connection conn) throws Exception {
        Statement st = conn.createStatement();

        // users table — holds Admin, Pharmacist, Patient
        st.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id         INT AUTO_INCREMENT PRIMARY KEY,
                name       VARCHAR(100)  NOT NULL,
                email      VARCHAR(150)  NOT NULL UNIQUE,
                password   VARCHAR(255)  NOT NULL,
                role       ENUM('ADMIN','PHARMACIST','PATIENT') NOT NULL,
                phone      VARCHAR(20),
                address    TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // medicines table
        st.execute("""
            CREATE TABLE IF NOT EXISTS medicines (
                id                    INT AUTO_INCREMENT PRIMARY KEY,
                name                  VARCHAR(150) NOT NULL,
                category              VARCHAR(100),
                price                 DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                quantity              INT NOT NULL DEFAULT 0,
                expiry_date           DATE,
                manufacturer          VARCHAR(150),
                requires_prescription BOOLEAN NOT NULL DEFAULT FALSE,
                description           TEXT,
                image_path            VARCHAR(500)
            )
        """);

        // orders table
        st.execute("""
            CREATE TABLE IF NOT EXISTS orders (
                id             INT AUTO_INCREMENT PRIMARY KEY,
                patient_id     INT NOT NULL,
                total_amount   DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                payment_method VARCHAR(50),
                status         ENUM('PENDING','APPROVED','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PENDING',
                created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """);

        // order_items table
        st.execute("""
            CREATE TABLE IF NOT EXISTS order_items (
                id          INT AUTO_INCREMENT PRIMARY KEY,
                order_id    INT NOT NULL,
                medicine_id INT NOT NULL,
                quantity    INT NOT NULL,
                unit_price  DECIMAL(10,2) NOT NULL,
                FOREIGN KEY (order_id)    REFERENCES orders(id)    ON DELETE CASCADE,
                FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE RESTRICT
            )
        """);

        // prescriptions table
        st.execute("""
            CREATE TABLE IF NOT EXISTS prescriptions (
                id          INT AUTO_INCREMENT PRIMARY KEY,
                patient_id  INT NOT NULL,
                order_id    INT,
                image_path  VARCHAR(500),
                status      ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
                notes       TEXT,
                uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (patient_id) REFERENCES users(id)   ON DELETE CASCADE,
                FOREIGN KEY (order_id)   REFERENCES orders(id)  ON DELETE SET NULL
            )
        """);

        // notifications table
        st.execute("""
            CREATE TABLE IF NOT EXISTS notifications (
                id         INT AUTO_INCREMENT PRIMARY KEY,
                user_id    INT,
                message    TEXT NOT NULL,
                type       VARCHAR(50),
                is_read    BOOLEAN NOT NULL DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """);

        st.close();
    }

    // ─────────────────────────────────────────────────────────────────
    //  Seed: Admin + Pharmacists + Patients + Medicines
    // ─────────────────────────────────────────────────────────────────
    private static void seedData(Connection conn) throws Exception {
        // Only seed if users table is empty
        var rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM users");
        rs.next();
        if (rs.getInt(1) > 0) return;

        // BCrypt hash for "01019" — used for all default accounts
        String hash = org.mindrot.jbcrypt.BCrypt.hashpw("01019", org.mindrot.jbcrypt.BCrypt.gensalt());
        String adminHash  = hash;
        String pharmaHash = hash;
        String patientHash= hash;

        var ps = conn.prepareStatement(
                "INSERT INTO users(name,email,password,role,phone,address) VALUES(?,?,?,?,?,?)");

        // ── Admin (fixed, only one) ───────────────────────────────
        ps.setString(1,"System Admin");ps.setString(2,"admin@epharmacy.com");
        ps.setString(3,adminHash);ps.setString(4,"ADMIN");
        ps.setString(5,"01000000000");ps.setString(6,"HQ");
        ps.addBatch();

        // ── Pharmacists ───────────────────────────────────────────
        ps.setString(1,"Sara Ahmed");ps.setString(2,"sara@epharmacy.com");
        ps.setString(3,pharmaHash);ps.setString(4,"PHARMACIST");
        ps.setString(5,"01111111111");ps.setString(6,"Cairo Branch");
        ps.addBatch();

        ps.setString(1,"Omar Hassan");ps.setString(2,"omar@epharmacy.com");
        ps.setString(3,pharmaHash);ps.setString(4,"PHARMACIST");
        ps.setString(5,"01222222222");ps.setString(6,"Giza Branch");
        ps.addBatch();

        // ── Patients ──────────────────────────────────────────────
        ps.setString(1,"Mona Ali");ps.setString(2,"mona@gmail.com");
        ps.setString(3,patientHash);ps.setString(4,"PATIENT");
        ps.setString(5,"01333333333");ps.setString(6,"Alexandria");
        ps.addBatch();

        ps.setString(1,"Khaled Ibrahim");ps.setString(2,"khaled@gmail.com");
        ps.setString(3,patientHash);ps.setString(4,"PATIENT");
        ps.setString(5,"01444444444");ps.setString(6,"Cairo");
        ps.addBatch();

        ps.executeBatch();
        ps.close();

        // ── Medicines ─────────────────────────────────────────────
        seedMedicines(conn);

        System.out.println("[DB] Default data seeded.");
    }

    private static void seedMedicines(Connection conn) throws Exception {
        var ps = conn.prepareStatement(
            "INSERT INTO medicines(name,category,price,quantity,expiry_date,manufacturer," +
            "requires_prescription,description) VALUES(?,?,?,?,?,?,?,?)");

        Object[][] meds = {
            {"Paracetamol 500mg","Analgesic",     5.50, 200,"2026-12-01","PharmaCo",      false,"Relieves mild to moderate pain and fever."},
            {"Amoxicillin 250mg","Antibiotic",   18.00,  80,"2025-08-15","MedLab",         true,"Broad-spectrum penicillin antibiotic."},
            {"Omeprazole 20mg", "GI",             12.00,150,"2026-06-30","GutHealth Inc",  true,"Proton pump inhibitor for acid reflux."},
            {"Vitamin C 1000mg","Supplement",      8.00,300,"2027-01-01","VitaPlus",       false,"Boosts immunity and collagen synthesis."},
            {"Ibuprofen 400mg", "Analgesic",       7.00,180,"2026-09-20","PainAway",       false,"Anti-inflammatory pain reliever."},
            {"Metformin 500mg", "Diabetes",       15.00, 60,"2025-11-30","DiaCare",        true,"First-line medication for type 2 diabetes."},
            {"Loratadine 10mg", "Antihistamine",   9.00,220,"2026-04-15","AllerShield",    false,"Non-drowsy allergy relief."},
            {"Atorvastatin 20mg","Cardiovascular",20.00, 40,"2026-07-01","HeartPlus",      true,"Lowers LDL cholesterol."},
            {"Salbutamol Inhaler","Respiratory",  35.00, 25,"2025-10-01","BreathEasy",     true,"Bronchodilator for asthma relief."},
            {"Zinc Supplement",  "Supplement",    10.00,250,"2027-03-01","NutriLife",      false,"Supports immune function and wound healing."},
            {"Ciprofloxacin 500mg","Antibiotic",  22.00, 70,"2026-02-28","BioMed",         true,"Fluoroquinolone antibiotic."},
            {"Aspirin 81mg",    "Cardiovascular",  4.00,400,"2027-06-01","HeartSafe",      false,"Low-dose aspirin for cardiovascular protection."},
            {"Pantoprazole 40mg","GI",            14.00,100,"2026-05-15","GastroPlus",     true,"PPI for GERD and peptic ulcers."},
            {"Cetirizine 10mg", "Antihistamine",   6.00,180,"2026-08-20","AllerClear",     false,"Antihistamine for allergic rhinitis."},
            {"Omega-3 Fish Oil","Supplement",     25.00,120,"2027-01-15","OceanHealth",    false,"Supports heart and brain health."},
            {"Amlodipine 5mg",  "Cardiovascular", 12.00, 55,"2026-11-01","CardioMed",      true,"Calcium channel blocker for hypertension."},
            {"Azithromycin 500mg","Antibiotic",   30.00, 45,"2025-09-30","ZPharma",        true,"Macrolide antibiotic for respiratory infections."},
            {"Glucosamine 1500mg","Supplement",   32.00, 90,"2027-04-01","JointCare",      false,"Supports joint health and cartilage."},
            {"Insulin Glargine","Diabetes",       65.00, 20,"2025-12-15","DiaCare",        true,"Long-acting insulin for diabetes."},
            {"Montelukast 10mg","Respiratory",    18.00, 85,"2026-03-10","LungGuard",      true,"Leukotriene blocker for asthma prevention."},
        };

        for (Object[] m : meds) {
            ps.setString(1,(String)m[0]);
            ps.setString(2,(String)m[1]);
            ps.setDouble(3,(Double)m[2]);
            ps.setInt   (4,(Integer)m[3]);
            ps.setString(5,(String)m[4]);
            ps.setString(6,(String)m[5]);
            ps.setBoolean(7,(Boolean)m[6]);
            ps.setString(8,(String)m[7]);
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
    }
}
