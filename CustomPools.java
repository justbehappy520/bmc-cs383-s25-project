package org.evosuite.seeding;

import java.util.Random;

public class CustomPools {

    private static final Random rand = new Random();

    public static String randomDate() {
        String date = "";
        int digit;
        for (int i=0; i<4; i++) {
            digit = rand.nextInt(10);
            date += digit;
        }
        date += "-";

        digit = rand.nextInt(1);
        date += digit;

        digit = rand.nextInt(10);
        date += digit;

        date += "-";

        digit = rand.nextInt(1);
        date += digit;

        digit = rand.nextInt(10);
        date += digit;

        return date;
    }

    public static String randomHex() {
        int high = rand.nextInt();
        return String.format("0x%08X", high);
    }

    public static String randomUnicode() {
        StringBuilder sb = new StringBuilder();

        int length = rand.nextInt(2) + 1;

        for (int i = 0; i < 4; i++) {
            int codeUnit = rand.nextInt(0x10000); 
            sb.append(String.format("\\u%04X", codeUnit));
        }

        return sb.toString();
    }

    public static String randomUnderscoreString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        StringBuilder sb = new StringBuilder("_");
        for (int i = 0; i < 2; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
    
        return sb.toString();
    }

    public static String randomLongDecimal() {
        String dec = "1.";
        for (int i=0; i<8; i++) {
            int digit = rand.nextInt(10);
            dec += digit;
        }
        return dec;
    }

    public static String randomNegativeNumber() {
        boolean isDecimal = rand.nextBoolean();
    
        String result;
        if (isDecimal) { // make decimal
            double number = -1 * (rand.nextDouble() * Math.pow(10, rand.nextInt(14) + 1));
            result = String.valueOf(number);
        } else { // make non-decimal
            long number = -1 * (Math.abs(rand.nextLong()) % (long) Math.pow(10, 15));
            result = String.valueOf(number);
        }
    
        // trim
        if (result.length() > 16) {
            result = result.substring(0, 16);
        }
    
        return result;
    }
    
}

