package org.mobilehub.payment_service.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

public final class HashingUtils {

    private static final HexFormat HEX = HexFormat.of();

    private HashingUtils() {} // prevent instantiation

    public static String sha256(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return HEX.formatHex(digest);
        } catch (Exception e) {
            throw new RuntimeException("Cannot hash with SHA-256", e);
        }
    }
}
