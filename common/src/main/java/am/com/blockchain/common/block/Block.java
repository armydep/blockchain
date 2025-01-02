package am.com.blockchain.common.block;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import am.com.blockchain.common.tx.TX;
import am.com.blockchain.common.tx.TxInEntry;
import am.com.blockchain.common.tx.TxOutEntry;

import java.util.ArrayList;
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
    public Block(@JsonProperty("hash") String hash,
                 @JsonProperty("previousHash") String previousHash,
                 @JsonProperty("merkleRoot") String merkleRoot,
                 @JsonProperty("nonce") Integer nonce,
                 @JsonProperty("size") Integer size,
                 @JsonProperty("timeStamp") Long timeStamp,
                 @JsonProperty("index") Integer index,
                 @JsonProperty("tx") List<TX> tx) {
        this.hash = hash;
        this.previousHash = previousHash;
        this.merkleRoot = merkleRoot;
        this.nonce = nonce;
        this.size = size;
        this.timeStamp = timeStamp;
        this.index = index;
        List<TX> copiedList = new ArrayList<>(tx);
        this.tx = Collections.unmodifiableList(copiedList);
    }

    public Block(Header header, List<TX> txs) {
        this.hash = header.getHash();
        this.previousHash = header.getPreviousHash();
        this.merkleRoot = header.getMerkleRoot();
        this.nonce = header.getNonce();
        this.size = header.getSize();
        this.timeStamp = header.getTimestamp();
        this.index = header.getIndex();
        List<TX> copiedList = new ArrayList<>();
        for (TX t : txs) {
            copiedList.add(t.copy());
        }
        this.tx = Collections.unmodifiableList(copiedList);
    }

    /*
        coinbase = true
        1. tx.size = 1 and tx.out.size = 0
            then utxo =  empty
        2. tx.size = 1 and tx.out.size = 1
            then utxo =  tx.out
        2. tx.size = 1 and tx.out.size = 2
            then utxo =  tx.out[]

        coinbase = false
            then utxo = empty
     */
    /*
        is coinbase TX[tx1, tx2, ...]:
                tx = TX[0]
                tx.in.size = 1 &&
                tx.out.size = 1 &&
                tx.in[0].coinbase = true
        then create UTXO utxo = tx.out[0]
     */
    @JsonIgnore
    public CoinBaseEntry getCoinBaseEntry() {
        if (tx == null || tx.isEmpty()) {
            return null;
        }
        TX firstTx = tx.getFirst();
        List<TxInEntry> vin = firstTx.getVin();
        List<TxOutEntry> vout = firstTx.getVout();
        if (vin != null && vin.size() == 1 && vout != null && vout.size() == 1) {
            if (vin.getFirst().isCoinbase()) {
                TxOutEntry txout = firstTx.getVout().getFirst();
                return new CoinBaseEntry(txout.getValue(), txout.getAddress(), txout.getN(), firstTx.getTxid());
            }
        }
        return null;
    }
}