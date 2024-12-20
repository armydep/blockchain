package org.am.com.blockchain.util;

import java.math.BigDecimal;

public class BtcOperation {

    public static float sum(int baseBTC, int baseSatoshi, int addSatoshi) {
        int totalBase = baseBTC;
        int totalSatoshi = baseSatoshi + addSatoshi;
        if (totalSatoshi >= 100_000_000) {
            totalBase += totalSatoshi / 100_000_000;
            totalSatoshi = totalSatoshi % 100_000_000;
        }
        String decimalPartStr = "0." + totalSatoshi;
        float satFloat = Float.parseFloat(decimalPartStr);
        return totalBase + satFloat;
    }

    public static double sumFloats(Double left, Double right) {
        BigDecimal leftSatoshi = convertToSatoshis(left);
        BigDecimal rightSatoshi = convertToSatoshis(right);
        double result = leftSatoshi.add(rightSatoshi).divide(new BigDecimal(100_000_000), BigDecimal.ROUND_HALF_UP).floatValue();
        return result;
    }

    public static BigDecimal convertToSatoshis(Double btc) {
        return new BigDecimal(btc * 100_000_000);
    }
}
