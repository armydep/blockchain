package org.am.com.blockchainnode.domain.block;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class Block {
    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;
    private Long index;
    private List<TX> tx;

    @JsonIgnore
    public CoinBaseEntry getCoinBaseEntry() {
        if (tx == null || tx.isEmpty()) {
            return null;
        }
        TX firstTx = tx.getFirst();
        try {
            List<TxInEntry> vin = firstTx.getVin();
            List<TxOutEntry> vout = firstTx.getVout();
            if (vin != null && vin.size() == 1 && vout != null && vout.size() == 1) {
                if (vin.getFirst().getCoinbase() != null && !vin.getFirst().getCoinbase().isEmpty()) {
                    TxOutEntry txout = firstTx.getVout().getFirst();
                    CoinBaseEntry coinBaseEntry = new CoinBaseEntry(txout.getValue(),
                            txout.getAddress(),
                            txout.getN(),
                            firstTx.getTxid());
                    tx.removeFirst();
                    return coinBaseEntry;
                }
            }
        } catch (Exception e) {
            //
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
