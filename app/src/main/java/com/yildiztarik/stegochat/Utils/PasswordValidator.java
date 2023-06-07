package com.yildiztarik.stegochat.Utils;


import java.util.regex.Pattern;

public class PasswordValidator {
    private String password;
    private static final int MIN_LENGTH = 8;

    public PasswordValidator(String password) {
        this.password = password;
    }

    public boolean isValid() {
        if (password.length() < MIN_LENGTH) {
            return false;
        }

        if (!Pattern.matches(".*[A-Z].*", password)) {
            return false;
        }

        if (!Pattern.matches(".*[a-z].*", password)) {
            return false;
        }

        if (!Pattern.matches(".*\\d.*", password)) {
            return false;
        }

        return true;
    }
}