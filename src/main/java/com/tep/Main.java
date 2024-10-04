package com.tep;

import com.tep.service.Service;
import com.tep.util.Greeter;
import com.tep.util.ScannerUtil;
import com.tep.factory.ServiceFactory;
import com.tep.util.ScriptInitialization;

import java.io.IOException;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws IOException {
        ScriptInitialization scriptInitialization = new ScriptInitialization();
        scriptInitialization.initializeScript();
        Greeter greeter = new Greeter();
        greeter.greet();
        ServiceFactory serviceFactory = new ServiceFactory();

        while (true) {
            try {
                System.out.println("\nEnter the entity you wish to create");
                System.out.println("Press 1 for Topic");
                System.out.println("Press 2 for Slice");
                System.out.println("Press 3 for Deck");
                System.out.println("Press 4 to search by hashtag");
                System.out.println("Press 5 to exit");

                int choice = Integer.parseInt(ScannerUtil.getInstance().nextLine());

                if (choice == 5) {
                    System.out.println("Thank you for using the app !");
                    System.exit(0);
                }

                final Optional<Service> optionalService = serviceFactory.getInstance(choice);

                if (optionalService.isEmpty()) {
                    continue;
                }

                optionalService.get().displayOption();

            } catch (NumberFormatException e) {
                System.out.println("\nPlease select from the above option");
            }
        }
    }
}