package org.am.com.blockchain.util;

import jakarta.validation.constraints.NotNull;
import org.am.com.util.crypto.CryptoUtil;

import java.util.ArrayList;
import java.util.List;

public class MerkleRootUtil {

    /*
        this is a copy of txs
    */
    public static String createMerkleRoot(@NotNull List<String> txs) {
        if (txs.isEmpty()) {
            throw new IllegalArgumentException("Transaction list cannot be empty");
        }
        shrinkToEven(txs);
        List<String> hashes = new ArrayList<>();
        for (int i = 0; i < txs.size(); i += 2) {
            String left = txs.get(i);
            String right = txs.get(i + 1);
            String hashL = CryptoUtil.generateSHA256(left);
            String hashR = CryptoUtil.generateSHA256(right);
            hashes.add(CryptoUtil.generateSHA256(hashL + hashR));
        }
        if (hashes.size() == 1) {
            return hashes.getFirst();
        } else {
            return createMerkleRoot(hashes);
        }
    }

    private static void shrinkToEven(List<String> txs) {
        if (txs.size() % 2 != 0) {
            txs.addLast(txs.getLast());
        }
    }
}
