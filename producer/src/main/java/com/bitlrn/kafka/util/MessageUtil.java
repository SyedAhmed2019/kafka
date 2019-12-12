package com.bitlrn.kafka.util;

import java.util.Arrays;

public class MessageUtil {

    /**
     * Creates a message of size @size in KB.
     */
    public static String createDataSize(int size) {
        char[] chars = new char[size];
        Arrays.fill(chars, 'a');
        return new String(chars);
    }
}
