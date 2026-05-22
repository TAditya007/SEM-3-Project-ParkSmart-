package service;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserService {
    private final Map<String, String> passwords = new LinkedHashMap<>();
    private final Map<String, String> roles = new LinkedHashMap<>();

    public UserService() {
        addUser("admin", "admin123", "ADMIN");
        addUser("emp01", "park101", "STAFF");
        addUser("emp02", "park102", "STAFF");
        addUser("emp03", "park103", "STAFF");
        addUser("emp04", "park104", "STAFF");
        addUser("emp05", "park105", "STAFF");
        addUser("emp06", "park106", "STAFF");
        addUser("emp07", "park107", "STAFF");
        addUser("emp08", "park108", "STAFF");
    }

    public boolean addUser(String username, String password, String role) {
        if (username == null || password == null || role == null) {
            return false;
        }

        username = username.trim();
        password = password.trim();
        role = role.trim().toUpperCase();

        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }

        if (passwords.containsKey(username)) {
            return false;
        }

        passwords.put(username, password);
        roles.put(username, role);
        return true;
    }

    public boolean removeUser(String username) {
        if (username == null) {
            return false;
        }
        username = username.trim();
        if (!passwords.containsKey(username)) {
            return false;
        }
        passwords.remove(username);
        roles.remove(username);
        return true;
    }

    public boolean isValidLogin(String username, String password, String role) {
        if (username == null || password == null || role == null) {
            return false;
        }
        username = username.trim();
        password = password.trim();
        role = role.trim().toUpperCase();

        String savedPassword = passwords.get(username);
        String savedRole = roles.get(username);

        return savedPassword != null
                && savedPassword.equals(password)
                && savedRole != null
                && savedRole.equalsIgnoreCase(role);
    }

    public boolean userExists(String username) {
        return username != null && passwords.containsKey(username.trim());
    }

    public String getRole(String username) {
        if (username == null) return null;
        return roles.get(username.trim());
    }

    public Map<String, String> getAllUsers() {
        return new LinkedHashMap<>(roles);
    }

    public String getPassword(String username) {
        if (username == null) return null;
        return passwords.get(username.trim());
    }
}