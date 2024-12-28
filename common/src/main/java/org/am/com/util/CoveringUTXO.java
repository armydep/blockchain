package org.am.com.util;

import org.am.com.balance.UTXO;

import java.util.List;

public record CoveringUTXO(String sender, String recipient, List<UTXO> utxos, Double amount, Double change) {

}
