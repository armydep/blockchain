package org.am.com.tx;

import org.am.com.balance.UTXO;

import java.util.ArrayList;
import java.util.List;

public class TXBuilder {

    public static TX generateTX(String sender,
                                String recipient,
                                List<UTXO> utxos,
                                Double amount,
                                Double change,
                                String txid) {
        List<TxInEntry> txInEntries = createTxInFromUTXOs(utxos);
        List<TxOutEntry> txOutEntries = new ArrayList<>();
        TxOutEntry txOutEntry = new TxOutEntry(amount, recipient, 0);
        txOutEntries.add(txOutEntry);
        if (change > 0) {
            TxOutEntry txOutEntryChange = new TxOutEntry(change, sender, 1);
            txOutEntries.add(txOutEntryChange);
        }
        return new TX(txid, txInEntries, txOutEntries);
    }

    private static List<TxInEntry> createTxInFromUTXOs(List<UTXO> txCoversSum) {
        List<TxInEntry> txInEntries = new ArrayList<>();
        for (int i = 0; i < txCoversSum.size(); i++) {
            UTXO utxo = txCoversSum.get(i);
            TxInEntry txInEntry = new TxInEntry(utxo.getTx(), utxo.getVout(), null);
            txInEntries.add(txInEntry);
        }
        return txInEntries;
    }

}
