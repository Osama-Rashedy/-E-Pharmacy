# 💊 Smart E-Pharmacy System

A complete, production-style **JavaFX 21 + Java 17 + MySQL** desktop pharmacy management application — built as a university graduation project demonstrating all 7 key software design patterns.

---

## 🚀 Setup Instructions

### Prerequisites
| Tool | Version |
|------|---------|
| Java JDK | 17 or higher |
| Maven | 3.9+ |
| MySQL Server | 8.0+ |

### Step 1 — Configure Database

1. Open `src/main/resources/config.properties`
2. Set your MySQL credentials:
```properties
db.url=jdbc:mysql://localhost:3306/epharmacy?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
db.username=root
db.password=YOUR_PASSWORD
```

### Step 2 — Build the Project
```bash
cd SmartEPharmacy
mvn clean install -DskipTests
```

### Step 3 — Run the Application
```bash
mvn javafx:run
```
Or run the fat JAR:
```bash
java -jar target/SmartEPharmacy-1.0.0.jar
```

### First Run
The database tables are **created automatically** on first launch.
Default seeded accounts:

| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@epharmacy.com` | `Admin@123` |
| Pharmacist | `sara@epharmacy.com` | `Pharma@123` |
| Patient | `mona@gmail.com` | `Patient@123` |

---

## 📁 Project Structure

```
SmartEPharmacy/
├── pom.xml
└── src/main/
    ├── java/com/epharmacy/
    │   ├── App.java                        ← Entry point
    │   ├── database/
    │   │   ├── DatabaseConnection.java     ← SINGLETON pattern
    │   │   └── DatabaseInitializer.java    ← Auto DDL + seed
    │   ├── models/
    │   │   ├── User.java / Admin / Pharmacist / Patient
    │   │   ├── Medicine.java               ← BUILDER pattern
    │   │   ├── Order.java / OrderItem.java
    │   │   ├── Prescription.java / Notification.java / CartItem.java
    │   ├── dao/
    │   │   ├── UserDAO / MedicineDAO / OrderDAO
    │   │   ├── OrderItemDAO / PrescriptionDAO / NotificationDAO
    │   ├── services/
    │   │   ├── AuthService / MedicineService / OrderService
    │   │   ├── PrescriptionService / NotificationService
    │   │   ├── ReportService / PDFService
    │   ├── patterns/
    │   │   ├── singleton/  DatabaseConnection + SessionManager
    │   │   ├── factory/    UserFactory
    │   │   ├── proxy/      MedicineServiceProxy
    │   │   ├── decorator/  Invoice + Tax + Discount decorators
    │   │   ├── strategy/   PaymentStrategy + Cash/Card/Wallet
    │   │   └── command/    OrderCommand + Approve/Cancel/UpdateInventory + Invoker
    │   ├── controllers/    (19 MVC controllers)
    │   └── utils/          AlertHelper / SceneNavigator / Validator / FormatUtil
    └── resources/
        ├── fxml/           (11 FXML views)
        ├── css/            dark-theme.css
        ├── config.properties
        └── schema.sql
```

---

## 🎨 Design Patterns Used

| # | Pattern | Category | Where Applied |
|---|---------|----------|---------------|
| 1 | **Singleton** | Creational | `DatabaseConnection`, `SessionManager` |
| 2 | **Factory Method** | Creational | `UserFactory` — creates Admin/Pharmacist/Patient |
| 3 | **Builder** | Creational | `Medicine.Builder` — fluent medicine construction |
| 4 | **Proxy** | Structural | `MedicineServiceProxy` — role-based access guard |
| 5 | **Decorator** | Structural | `TaxDecorator`, `DiscountDecorator` — invoice enhancements |
| 6 | **Strategy** | Behavioral | `CashPayment`, `CreditCardPayment`, `WalletPayment` |
| 7 | **Command** | Behavioral | `ApproveOrderCommand`, `CancelOrderCommand`, `UpdateInventoryCommand` |

---

## 👥 User Roles

| Role | Access |
|------|--------|
| **Admin** | Full access: manage pharmacists, patients, medicines, reports |
| **Pharmacist** | Manage medicines, review prescriptions, handle orders |
| **Patient** | Browse medicines, cart checkout, upload prescriptions, order history |

---

## ✨ Features

- 🔐 BCrypt password hashing
- 🎨 Dark glassmorphism UI theme
- 📊 Sales charts (BarChart + PieChart)
- 📄 PDF invoice export (iText 7)
- ⚠️ Low stock & expiry warnings
- 🔍 Real-time search & filtering
- ↩ Undo order actions (Command pattern)
- 📋 Prescription image upload & review
- 🛒 Shopping cart with payment strategies

---

## 🛠 Tech Stack

- **Java 17** — Modern Java features (switch expressions, text blocks)
- **JavaFX 21** — Rich desktop UI
- **MySQL 8** — Relational database
- **JDBC** — Direct SQL with PreparedStatement
- **BCrypt** — Secure password hashing
- **iText 7** — PDF generation
- **Maven** — Dependency management


-Osama
-Yousry
-Rashedy

-- second edit from Osama Rashedy
