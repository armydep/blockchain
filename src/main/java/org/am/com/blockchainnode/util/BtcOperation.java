package org.am.com.blockchainnode.util;

public class BtcOperation {


    public static float sum(int baseBTC, int baseSatoshi , int addSatoshi) {
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
}
/*
to fix
send
{
  "from": "satoshiAddr100",
  "to": "string",
  "btc": 2,
  "sat": 35
}
fee 0.1
result
{
  "txid": "0",
  "submitted": true,
  "totalToSend": 2.3600001,
  "remaining": 42.64
}
 */