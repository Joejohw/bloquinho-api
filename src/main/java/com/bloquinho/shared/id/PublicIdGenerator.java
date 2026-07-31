package com.bloquinho.shared.id;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public final class PublicIdGenerator {
    public static final int LENGTH = 21;
    public static final String PATTERN = "[0-9A-Z_a-z-]{" + LENGTH + "}";
    private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz-".toCharArray();
    private final SecureRandom random;
    public PublicIdGenerator() {
        this(new SecureRandom());
    }

    PublicIdGenerator(SecureRandom random) {
        this.random = random;
    }

    public String generate() {
        var value = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            value.append(ALPHABET[random.nextInt(ALPHABET.length)]);
        }
        return value.toString();
    }
}
