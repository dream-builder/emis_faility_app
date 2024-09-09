package org.sci.rhis.utilities;

/**
 * Created by arafat.hasan on 5/23/2018.
 */

public class XORCrypt {

    /*public static void main(String args[]) { // test
        int[] encrypted = encrypt(value,keyval);
        for(int i = 0; i < encrypted.length; i++)
            System.out.printf("%d,", encrypted[i]);
        System.out.println("");
        System.out.println(decrypt(encrypted,keyval));
    }*/

    public static String encrypt(String str, String key) {
        StringBuilder output= new StringBuilder();
        //int[] output = new int[str.length()];
        for(int i = 0; i < str.length(); i++) {
            int o = (Integer.valueOf(str.charAt(i)) ^ Integer.valueOf(key.charAt(i % (key.length() - 1))));
            output.append(o+",");
        }
        return output.toString();
    }

    public static String decrypt(String input, String key) {
        String output = "";
        String [] str = input.split(",");
        for(int i = 0; i < str.length; i++) {
            output += (char) (Integer.parseInt(str[i])      ^ (int) key.charAt(i % (key.length() - 1)));
        }
        return output;
    }

}

