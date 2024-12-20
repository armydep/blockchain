package org.am.com.blockchain.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BtcOperation {
    private static final BigDecimal SATOSHI_PER_BTC = new BigDecimal("100000000");

    public static double sumInts(int baseBTC, int baseSatoshi, int addSatoshi) {
        BigDecimal baseBTCSat = BigDecimal.valueOf(baseBTC).multiply(SATOSHI_PER_BTC);
        BigDecimal totalSat = baseBTCSat
                .add(BigDecimal.valueOf(baseSatoshi))
                .add(BigDecimal.valueOf(addSatoshi));
        return satoshiToDouble(totalSat);
    }

    public static double sumDoubles(double left, double right) {
        BigDecimal leftSatoshi = floatToSatoshi(left);
        BigDecimal rightSatoshi = floatToSatoshi(right);
        return satoshiToDouble(leftSatoshi.add(rightSatoshi));
    }

    public static BigDecimal floatToSatoshi(double btc) {
        if (btc < 0) {
            throw new IllegalArgumentException("BTC value cannot be negative.");
        }
        return new BigDecimal(btc).multiply(SATOSHI_PER_BTC);
    }

    public static double satoshiToDouble(BigDecimal satoshi) {
        if (satoshi.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Satoshi value cannot be negative.");
        }
        return satoshi.divide(SATOSHI_PER_BTC, 10, RoundingMode.UP).doubleValue();
    }
}
