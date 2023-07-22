package de.evilcodez.classicserver.utils;

public class StringUtils {

    public static String removeColorCodes(String s) {
        return s.replaceAll("&([0-9a-fA-F]|r|R|m|M|n|N|o|O|k|K)?", "");
    }
}
