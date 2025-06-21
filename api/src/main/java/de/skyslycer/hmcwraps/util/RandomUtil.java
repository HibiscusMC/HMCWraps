package de.skyslycer.hmcwraps.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    private static final String SAFE_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final java.util.Random RANDOM = ThreadLocalRandom.current();

    /**
     * Generates a random ID of length 16 which follows the limitations for namespaced keys.
     *
     * @return A random ID
     */
    public static String generateRandomId() {
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(SAFE_CHARS.charAt(RANDOM.nextInt(SAFE_CHARS.length())));
        }
        return sb.toString();
    }

}
