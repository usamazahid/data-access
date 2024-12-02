package org.irs.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashUtil {
    
    // Hash a password for storage
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Verify that an entered password matches the stored hash
    public static boolean checkPassword(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }
}
