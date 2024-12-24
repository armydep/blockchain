package org.am.com.blockchain.util;

import lombok.experimental.UtilityClass;
import org.am.com.blockchain.model.block.Header;
import org.am.com.tx.TX;
import org.am.com.tx.TxInEntry;
import org.am.com.tx.TxOutEntry;

import java.nio.charset.StandardCharsets;
import java.util.List;

@UtilityClass
public class BlockSizeCalculator {

    public static int calculateBlockSize(Header minedHeader, List<TX> txs) {
        int size = 0;

        // Calculate header size
        size += calculateHeaderSize(minedHeader);

        // Calculate size of transaction list
        size += 4; // Size for transaction count (int)

        // Add size of each transaction
        for (TX tx : txs) {
            size += calculateTransactionSize(tx);
        }

        return size;
    }

    private static int calculateHeaderSize(Header header) {
        int size = 0;

        // Fixed-size fields
        size += 32; // hash (256 bits)
        size += 32; // previousHash (256 bits)
        size += 32; // merkleRoot (256 bits)
        size += 4;  // nonce (integer)
        size += 8;  // timestamp (long)
        size += 4;  // index (integer)
        size += 4;  // size (integer)

        return size;
    }

    private static int calculateTransactionSize(TX tx) {
        int size = 0;

        // Transaction ID (256-bit hash)
        size += 32;

        // Size of input vector
        size += 4; // Size for count of inputs (int)
        for (TxInEntry vin : tx.getVin()) {
            size += calculateTxInSize(vin);
        }

        // Size of output vector
        size += 4; // Size for count of outputs (int)
        for (TxOutEntry vout : tx.getVout()) {
            size += calculateTxOutSize(vout);
        }

        return size;
    }

    private static int calculateTxInSize(TxInEntry vin) {
        int size = 0;

        // Transaction ID reference (256-bit hash)
        size += 32;

        // Output index
        size += 4;

        // Coinbase (if present)
        if (vin.getCoinbase() != null) {
            // Variable length string - assume UTF-8 encoding
            size += 4; // Length prefix
            size += vin.getCoinbase().getBytes(StandardCharsets.UTF_8).length;
        }

        return size;
    }

    private static int calculateTxOutSize(TxOutEntry vout) {
        int size = 0;

        // Value (double - 8 bytes)
        size += 8;

        // Address (variable length string)
        size += 4; // Length prefix
        size += vout.getAddress().getBytes(StandardCharsets.UTF_8).length;

        // Output index
        size += 4;

        return size;
    }
}
