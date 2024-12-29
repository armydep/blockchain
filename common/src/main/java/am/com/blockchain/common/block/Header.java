package am.com.blockchain.common.block;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class Header {
    String hash;
    String previousHash;
    String merkleRoot;
    Integer nonce;
    Long timestamp;
    Integer index;
    Integer size;

    public Header(Header blockHeader, int size) {
        this.hash = blockHeader.hash;
        this.previousHash = blockHeader.previousHash;
        this.merkleRoot = blockHeader.merkleRoot;
        this.nonce = blockHeader.nonce;
        this.timestamp = blockHeader.timestamp;
        this.index = blockHeader.index;
        this.size = size;
    }

    public Header(Header header, String hash) {
        this.hash = hash;
        this.previousHash = header.previousHash;
        this.merkleRoot = header.merkleRoot;
        this.nonce = header.nonce;
        this.timestamp = header.timestamp;
        this.index = header.index;
        this.size = null;
    }
}
