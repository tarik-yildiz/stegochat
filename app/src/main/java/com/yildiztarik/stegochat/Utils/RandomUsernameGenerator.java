package com.yildiztarik.stegochat.Utils;

import java.util.Random;

public class RandomUsernameGenerator {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";

    public String generateUsername() {
        Random random = new Random();

        int length = random.nextInt(7) + 4; // En az 4 haneli, en fazla 10 haneli
        StringBuilder username = new StringBuilder();

        char firstChar = ALPHABET.charAt(random.nextInt(ALPHABET.length()));
        username.append(firstChar);

        for (int i = 1; i < length; i++) {
            String characters = ALPHABET + NUMBERS;
            char randomChar = characters.charAt(random.nextInt(characters.length()));
            username.append(randomChar);
        }

        return username.toString();
    }
}