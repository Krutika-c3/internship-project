package com.tep.util;

import java.util.Scanner;

public class ScannerUtil {

    private static Scanner scanner;

    private ScannerUtil() {
        // private constructor because this is a util class..
        // No object should be created
    }

    public static Scanner getInstance() {
        if (scanner == null)
            scanner = new Scanner(System.in);
        return scanner;
    }
}
