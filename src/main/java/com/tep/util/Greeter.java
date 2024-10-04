package com.tep.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Greeter {

    public static final SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy EEEE hh:mm:ss a");

    public void greet() {
        while (true) {
            try {
                System.out.println("Welcome User select the country you are from:");
                System.out.println("Press 1 for USA");
                System.out.println("Press 2 for INDIA");
                System.out.println("Press 3 for JAPAN");
                int choice = Integer.parseInt(ScannerUtil.getInstance().nextLine());

                switch (choice) {
                    case 1:
                        greetings(Country.USA.getTimeZone());
                        return;
                    case 2:
                        greetings(Country.INDIA.getTimeZone());
                        return;
                    case 3:
                        greetings(Country.JAPAN.getTimeZone());
                        return;
                    default:
                        System.out.println("\nPlease select from the above option");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nPlease select from the above option");
            }
        }
    }

    public static void greetings(String timeZone) {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        date_format.setTimeZone(TimeZone.getTimeZone(timeZone));
        String currentDateTime = date_format.format(new Date());
        String message = "Good Evening";

        System.out.println("Time for " + timeZone + " is: " + currentDateTime);

        if (hour >= 1 && hour < 12) {
            message = "Good Morning";
        } else if (hour >= 12 && hour <= 16) {
            message = "Good Afternoon";
        }
        System.out.println(message);
    }
}