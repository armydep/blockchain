package org.am.com.blockchain.model.block;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class Block {
    String hash;
    String previousHash;
    String merkleRoot;
    Integer nonce;
    Integer size;
    Long timeStamp;
    Integer index;
    List<TX> tx;

    @JsonCreator
    public Block(String hash,
                 String previousHash,
                 String merkleRoot,
                 Integer nonce,
                 Integer size,
                 Long timeStamp,
                 Integer index,
                 List<TX> tx) {
        this.hash = hash;
        this.previousHash = previousHash;
        this.merkleRoot = merkleRoot;
        this.nonce = nonce;
        this.size = size;
        this.timeStamp = timeStamp;
        this.index = index;
        this.tx = Collections.unmodifiableList(tx);
    }

    public Block(Header header, List<TX> txs) {
        this.hash = header.getHash();
        this.previousHash = header.getPreviousHash();
        this.merkleRoot = header.getMerkleRoot();
        this.nonce = header.getNonce();
        this.size = header.getSize();
        this.timeStamp = header.getTimestamp();
        this.index = header.getIndex();
        this.tx = Collections.unmodifiableList(txs);
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
}