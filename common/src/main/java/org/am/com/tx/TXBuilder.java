package org.am.com.tx;

import org.am.com.balance.UTXO;

import java.util.ArrayList;
import java.util.List;

public class TXBuilder {

    public static TX buildUnsignedTX(String sender,
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
            TxInEntry txInEntry = new TxInEntry(utxo.getTx(), utxo.getVout(), null, null);
            txInEntries.add(txInEntry);
        }
        return txInEntries;
    }

    private static List<TxInEntry> createSignedTxIns(List<TxInEntry> otxInEntries, ScriptSig scriptSig) {
        List<TxInEntry> txInEntries = new ArrayList<>();
        for (int i = 0; i < otxInEntries.size(); i++) {
            TxInEntry oentry = otxInEntries.get(i);
            TxInEntry txInEntry;
            if (oentry.isCoinbase()) {
                txInEntry = new TxInEntry(oentry.getTxid(), oentry.getVout(), null, null);
            } else {
                txInEntry = new TxInEntry(oentry.getTxid(), oentry.getVout(), null, scriptSig);
            }
            txInEntries.add(txInEntry);
        }
        return txInEntries;
    }

    public static TX buildSignedTX(TX tx, ScriptSig scriptSig) {
        List<TxInEntry> txInEntries = createSignedTxIns(tx.getVin(), scriptSig);
        List<TxOutEntry> txOutEntries = new ArrayList<>(tx.getVout());
        return new TX(tx.getTxid(), txInEntries, txOutEntries);
    }

    //remove sigScript and coinbase entry
    //only before the coinbase added to txOut entries
    //only for tx with singe TxInEntry
    public static StrippedTX stripTX(TX tx) {
        List<TxInEntry> txInEntries = new ArrayList<>();
        ScriptSig scriptSig = null;
        for (int i = 0; i < tx.getVin().size(); i++) {
            TxInEntry oentry = tx.getVin().get(i);
            if (!oentry.isCoinbase()) {
                TxInEntry txInEntry;
                txInEntry = new TxInEntry(oentry.getTxid(), oentry.getVout(), null, null);
                txInEntries.add(txInEntry);
                scriptSig = oentry.getScriptSig();
            }
        }
        return new StrippedTX(new TX(tx.getTxid(), txInEntries, new ArrayList<>(tx.getVout())), scriptSig);
    }
}
