package util;

import java.util.Scanner;

public final class InputUtil {
    private static Scanner scan = new Scanner(System.in);

    public static String getStringInput(){
        String textToReturn;
        boolean hasSpace = false;
        boolean isEmpty = false;

        do {
            textToReturn = scan.nextLine();
            isEmpty = textToReturn.isEmpty();

            if (isEmpty) {
                printLine(" No value entered");
            }

        } while (isEmpty);

        return textToReturn;
    }

    public static int getIntegerInput(int maximum) throws NumberFormatException{
        boolean valid = true;
        int input = 0;

        do {
            try {
                input = Integer.valueOf(scan.nextLine());
                valid = true;
            } catch (NumberFormatException e){
                printLine("Invalid Input");
                valid = false;
            }

            if(input <= 0 || input > maximum) {
                valid = false;
                printLine("Only accepts values 1-" + String.valueOf(maximum));
            }

        } while (!valid);

        return input;
    }

    public static void printLine(String message){
        System.out.println(message);
    }
}
