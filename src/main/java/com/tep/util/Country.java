package com.tep.util;

public enum Country {
    USA("EST"),
    INDIA("IST"),
    JAPAN("JST");

    Country(final String timeZone) {
        this.timeZone = timeZone;
    }

    private final String timeZone;

    public String getTimeZone() {
        return timeZone;
    }
}

