package com.yildiztarik.stegochat.Utils;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Pattern;

public class UsernameValidator {
    private String username;
    private Context context;
    private final String NOT_START_WITH_NUMBER = "The username should not start with a number.";
    private final String MORE_CHARACTERS = "The username must be 4 characters or more.";
    private final String INAPPROPRIATE_CHARACTERS = "The username should not contain inappropriate characters.";

    public UsernameValidator(String username, Context context) {
        this.username = username;
        this.context = context;
    }

    public boolean isValid() {
        if (username.length() < 4) {
            Toast.makeText(context, MORE_CHARACTERS, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Pattern.matches("^\\d.*", username)) {
            Toast.makeText(context, NOT_START_WITH_NUMBER, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Pattern.matches(".*[a-zA-Z0-9]$", username)) {
            Toast.makeText(context, INAPPROPRIATE_CHARACTERS, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
