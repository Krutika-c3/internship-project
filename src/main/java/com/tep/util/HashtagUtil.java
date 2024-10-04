package com.tep.util;

import java.util.HashSet;
import java.util.Set;

public class HashtagUtil {

    public static Set<String> extractHashtags(String hashtagString) {
        Set<String> hashtags = new HashSet<>();
        String[] stringSplit = hashtagString.split(" ");

        for (String characters : stringSplit) {
            if (characters.charAt(0) == '#') {
                characters = characters.toLowerCase();
                String removedHash = sanitise(characters);
                hashtags.add(removedHash);
            }
        }
        return hashtags;
    }

    public static String sanitise(String hashtagString)
    {
        return hashtagString.substring(1);
    }
}