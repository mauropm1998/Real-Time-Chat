package com.chat.app.utils;

import java.util.Base64;

public class Base64Converter {
    public static String numberToBase64(Long number) {
        return Base64.getUrlEncoder().encodeToString(number.toString().getBytes());
    }

    public static Long base64ToNumber(String base64) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(base64);
        String decodedString = new String(decodedBytes);
        return Long.parseLong(decodedString);
    }
}
