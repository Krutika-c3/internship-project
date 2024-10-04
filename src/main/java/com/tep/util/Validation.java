package com.tep.util;

public class Validation {
    public boolean verifyLength(String string, Integer minLength, Integer maxLength) {
        if (string.length() == minLength || string.length() > maxLength) {
            return false;
        }
        return true;
    }

    public boolean notValidLength(String string, Integer minLength, Integer maxLength) {
        if (string.length() == minLength || string.length() > maxLength) {
            return true;
        }
        return false;
    }
}
