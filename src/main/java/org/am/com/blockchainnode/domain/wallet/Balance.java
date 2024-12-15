package org.am.com.blockchainnode.domain.wallet;

import lombok.Value;

@Value
public class Balance {
    int btc;
    int satoshi;
    String address;
    String date = new java.util.Date().toString();
    String currency = "BTC";

}
