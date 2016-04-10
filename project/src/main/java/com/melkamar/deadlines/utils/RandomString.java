package com.melkamar.deadlines.utils;

import java.util.Random;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 10.04.2016 17:33
 */
public class RandomString {
    public static String get(String prefix){
        int length = 8;
        String characters = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();

        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(random.nextInt(characters.length()));
        }
        return prefix + new String(text);
    }
}
