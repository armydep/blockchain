package org.am.com.blockchainnode.model.block;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class Block {
    String hash;
    String previousHash;
    Long timeStamp;
    Integer nonce;
    Integer index;
    List<TX> tx;

    public Block(String hash,
                 String previousHash,
                 Long timeStamp,
                 Integer nonce,
                 Integer index,
                 List<TX> tx) {
        this.hash = hash;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.nonce = nonce;
        this.index = index;
        this.tx = Collections.unmodifiableList(tx);
    }

    @JsonIgnore
    public CoinBaseEntry getCoinBaseEntry() {
        if (tx == null || tx.isEmpty()) {
            return null;
        }
        TX firstTx = tx.getFirst();
        List<TxInEntry> vin = firstTx.getVin();
        List<TxOutEntry> vout = firstTx.getVout();
        if (vin != null && vin.size() == 1 && vout != null && vout.size() == 1) {
            if (vin.getFirst().getCoinbase() != null && !vin.getFirst().getCoinbase().isEmpty()) {
                TxOutEntry txout = firstTx.getVout().getFirst();
                CoinBaseEntry coinBaseEntry = new CoinBaseEntry(txout.getValue(),
                        txout.getAddress(),
                        txout.getN(),
                        firstTx.getTxid());
                return coinBaseEntry;
            }
        }
        return null;
    }

//    @JsonIgnore
//    public List<TxInEntry> getTxInEntries() {
//        List<TxInEntry> txInEntries = new ArrayList<>();
//        for (TX tx : tx) {
//            tx.getVin().forEach(txInEntry -> txInEntries.add(txInEntry));
//        }
//        return txInEntries;
//    }

//    @JsonIgnore
//    public List<TxOutEntry> getTxOutEntries() {
//        List<TxOutEntry> txOutEntries = new ArrayList<>();
//        for (TX tx : tx) {
//            tx.getVout().forEach(txOutEntry -> txOutEntries.add(txOutEntry));
//        }
//        return txOutEntries;
//    }
}
