package de.evilcodez.classicserver.utils;

public class NameValidator {

    public static boolean isValidUsername(String username) {
        for(char c : username.toCharArray()) {
            if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '_') {
                return false;
            }
        }
        return true;
    }
}
