package util;

import java.io.IOException;
import java.util.Scanner;

public final class InputUtil {
    private static Scanner scan = new Scanner(System.in);

    public static void promptAnyKey() {
        System.out.println("Press Enter to continue...");
        scan.nextLine();
    }

    public static String getStringInput() {
        String textToReturn;
        boolean isEmpty;
        do {
            textToReturn = scan.nextLine();
            isEmpty = textToReturn.isEmpty();

            if (isEmpty) {
                System.out.println(" No value entered");
            }

        } while (isEmpty);

        return textToReturn;
    }

    public static int getIntegerInput(int maximum) throws NumberFormatException {
        boolean valid;
        int input = 0;

        do {
            try {
                input = Integer.valueOf(scan.nextLine());
                valid = true;
            } catch (NumberFormatException e){
                System.out.println("Invalid Input");
                valid = false;
            }

            if(input <= 0 || input > maximum) {
                valid = false;
                System.out.println("Only accepts values 1-" + maximum);
            }

        } while (!valid);

        return input;
    }
}
