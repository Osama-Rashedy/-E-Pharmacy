package com.epharmacy.patterns.singleton;

import com.epharmacy.models.User;

/**
 * ╔══════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN: Singleton (Creational)          ║
 * ║  Maintains the currently logged-in user across   ║
 * ║  all screens without passing it as a parameter.  ║
 * ╚══════════════════════════════════════════════════╝
 */
public class SessionManager {

    private static volatile SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    /** Store the user who just logged in. */
    public void login(User user) { this.currentUser = user; }

    /** Clear session on logout. */
    public void logout() { this.currentUser = null; }

    /** Returns the currently authenticated user (null if not logged in). */
    public User getCurrentUser() { return currentUser; }

    /** Convenience check. */
    public boolean isLoggedIn() { return currentUser != null; }

    public boolean isAdmin()       { return isLoggedIn() && currentUser.getRole() == User.Role.ADMIN; }
    public boolean isPharmacist()  { return isLoggedIn() && currentUser.getRole() == User.Role.PHARMACIST; }
    public boolean isPatient()     { return isLoggedIn() && currentUser.getRole() == User.Role.PATIENT; }
}
