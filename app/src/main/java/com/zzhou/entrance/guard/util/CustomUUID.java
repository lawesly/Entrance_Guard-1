package com.zzhou.entrance.guard.util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;

/**
 * <desc>
 * Created by The Moss on 2018/10/24.
 */

public class CustomUUID {
    private static final char[] CHAR_BASE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static final BigInteger RADIX = new BigInteger(String.valueOf(CHAR_BASE.length));

    private static final int LEN = initLen();

    private static int initLen() {
        int len = 0;
        BigInteger bigInteger = new BigInteger("ffffffffffffffffffffffffffffffff", 16);
        BigInteger[] bigIntegers = bigInteger.divideAndRemainder(RADIX);
        do {
            len++;
            bigIntegers = bigIntegers[0].divideAndRemainder(RADIX);
        }
        while (bigIntegers[0].compareTo(BigInteger.ZERO) > 0 || bigIntegers[1].compareTo(BigInteger.ZERO) > 0);
        return len;
    }

    public static String genUUID() {
        BigInteger bigInteger = new BigInteger(UUID.randomUUID().toString().replaceAll("-", ""), 16);
        char[] chs = new char[LEN];
        Arrays.fill(chs, CHAR_BASE[0]);
        int index = LEN;

        BigInteger[] bigIntegers = bigInteger.divideAndRemainder(RADIX);
        do {
            chs[--index] = CHAR_BASE[bigIntegers[1].intValue()];
            bigIntegers = bigIntegers[0].divideAndRemainder(RADIX);
        }
        while (bigIntegers[0].compareTo(BigInteger.ZERO) > 0 || bigIntegers[1].compareTo(BigInteger.ZERO) > 0);
        return new String(chs);
    }

    public static void main(String[] args) {
//        System.out.println(CustomUUID.genUUID());
//        System.out.println((int) (23439f/345355f*100)+"");
        String cardid = "04BB1D22EF4A80";
        long cardNo = Long.valueOf(cardid, 16);
        System.out.println(cardNo);
        float t = (float) (1.5 + 2 + 1.3 + 1 + 2 + 3 - 1 - 1);
        System.out.println(t);
    }
}
