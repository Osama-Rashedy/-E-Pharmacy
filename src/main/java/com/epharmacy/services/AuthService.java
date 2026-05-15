package com.epharmacy.services;

import com.epharmacy.dao.UserDAO;
import com.epharmacy.models.User;
import com.epharmacy.patterns.factory.UserFactory;
import com.epharmacy.patterns.singleton.SessionManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Authentication service: login, register, logout.
 * Passwords are hashed with BCrypt before storage.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    // ── LOGIN ────────────────────────────────────────────────────────
    public User login(String email, String password) throws Exception {
        Optional<User> opt = userDAO.findByEmail(email);
        if (opt.isEmpty()) throw new Exception("No account found with this email.");

        User user = opt.get();
        if (!BCrypt.checkpw(password, user.getPassword()))
            throw new Exception("Incorrect password.");

        SessionManager.getInstance().login(user);
        return user;
    }

    // ── REGISTER (patients only self-register) ───────────────────────
    public User registerPatient(String name, String email, String password,
                                String phone, String address) throws Exception {
        if (userDAO.findByEmail(email).isPresent())
            throw new Exception("An account with this email already exists.");

        // Validation
        if (name.isBlank())     throw new Exception("Name cannot be empty.");
        if (email.isBlank())    throw new Exception("Email cannot be empty.");
        if (password.length() < 6) throw new Exception("Password must be at least 6 characters.");

        // Use Factory Method pattern to create Patient
        User patient = UserFactory.createUser("PATIENT", name, email,
                BCrypt.hashpw(password, BCrypt.gensalt()));
        patient.setPhone(phone);
        patient.setAddress(address);

        userDAO.save(patient);
        return patient;
    }

    // ── ADD PHARMACIST (Admin only) ──────────────────────────────────
    public User addPharmacist(String name, String email, String password, String phone) throws Exception {
        User current = SessionManager.getInstance().getCurrentUser();
        if (current == null || current.getRole() != User.Role.ADMIN)
            throw new SecurityException("Only Admins can add pharmacists.");

        if (userDAO.findByEmail(email).isPresent())
            throw new Exception("Email already registered.");

        User pharmacist = UserFactory.createUser("PHARMACIST", name, email,
                BCrypt.hashpw(password, BCrypt.gensalt()));
        pharmacist.setPhone(phone);
        userDAO.save(pharmacist);
        return pharmacist;
    }

    // ── DELETE USER ──────────────────────────────────────────────────
    public void deleteUser(int userId) throws Exception {
        User current = SessionManager.getInstance().getCurrentUser();
        if (current == null || current.getRole() != User.Role.ADMIN)
            throw new SecurityException("Only Admins can delete users.");
        if (current.getId() == userId)
            throw new Exception("Cannot delete your own account.");
        userDAO.delete(userId);
    }

    // ── UPDATE PROFILE ───────────────────────────────────────────────
    public void updateProfile(User user) throws SQLException {
        userDAO.update(user);
        // Refresh session
        SessionManager.getInstance().login(user);
    }

    // ── LOGOUT ──────────────────────────────────────────────────────
    public void logout() {
        SessionManager.getInstance().logout();
    }
}
