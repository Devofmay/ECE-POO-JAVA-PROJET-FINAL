package fr.ece.javaprojetfinal;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    private static final int WORK_FACTOR = 12;

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}

