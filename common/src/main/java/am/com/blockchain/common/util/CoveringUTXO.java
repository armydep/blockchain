package am.com.blockchain.common.util;

import am.com.blockchain.common.balance.UTXO;

import java.util.List;

public record CoveringUTXO(String sender,
                           String recipient,
                           List<UTXO> utxos,
                           Double amount,
                           Integer feeSatoshi,
                           Double change) {

}
