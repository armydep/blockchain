package org.am.com.blockchainnode.util;

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

    public static float sumFloats(float left, float right) {
        int leftSatoshi = convertToSatoshis(left);
        int rightSatoshi = convertToSatoshis(right);
        return (float) (leftSatoshi + rightSatoshi) / 100_000_000;
    }

    public static int convertToSatoshis(float btc) {
        return (int) (btc * 100_000_000);
    }
}
