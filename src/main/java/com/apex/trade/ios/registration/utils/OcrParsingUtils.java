package com.apex.trade.ios.registration.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OcrParsingUtils {

    public static String parseNameFromText(String text) {
        // Example: look for line starting with "Name:" or "Full Name:"
        Pattern pattern = Pattern.compile("(?i)(Name|Full Name)\\s*[:\\-]?\\s*(.+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }

        return null;
    }

    public static String parsePhoneFromText(String text) {
        // Regex to find phone numbers (simple example)
        Pattern pattern = Pattern.compile("\\+?\\d[\\d\\s\\-]{7,}\\d");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group().replaceAll("\\s+", "");
        }
        return null;
    }
}

