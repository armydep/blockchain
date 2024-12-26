package org.am.com.blockchain.miner;

import org.am.com.blockchain.util.MerkleRootUtil;
import org.am.com.util.crypto.CryptoUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class MerkleRootTest {

    @Test
    void shouldCreateMerkleRootForTwoTransactions() {
        try (MockedStatic<CryptoUtil> cryptoUtil = mockStatic(CryptoUtil.class)) {
            List<String> txs = new ArrayList<>(Arrays.asList("tx1", "tx2"));
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("tx1")).thenReturn("hash1");
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("tx2")).thenReturn("hash2");
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("hash1hash2")).thenReturn("rootHash");
            String result = MerkleRootUtil.createMerkleRoot(txs);
            assertEquals("rootHash", result);
            cryptoUtil.verify(() -> CryptoUtil.generateSHA256("tx1"));
            cryptoUtil.verify(() -> CryptoUtil.generateSHA256("tx2"));
            cryptoUtil.verify(() -> CryptoUtil.generateSHA256("hash1hash2"));
        }
    }

    @Test
    void shouldCreateMerkleRootForThreeTransactions() {
        try (MockedStatic<CryptoUtil> cryptoUtil = mockStatic(CryptoUtil.class)) {
            List<String> txs = new ArrayList<>(Arrays.asList("tx1", "tx2", "tx3"));
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("tx1")).thenReturn("hash1");
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("tx2")).thenReturn("hash2");
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("tx3")).thenReturn("hash3");
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("hash1hash2")).thenReturn("hash12");
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("hash3hash3")).thenReturn("hash33");
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("hash33")).thenReturn("hash33#");
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("hash12")).thenReturn("hash12#");
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("hash13")).thenReturn("hash13#");
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("hash12#hash33#")).thenReturn("rootHash");
            String result = MerkleRootUtil.createMerkleRoot(txs);
            assertEquals("rootHash", result);
            cryptoUtil.verify(() -> CryptoUtil.generateSHA256("tx1"));
            cryptoUtil.verify(() -> CryptoUtil.generateSHA256("tx2"));
            cryptoUtil.verify(() -> CryptoUtil.generateSHA256("tx3"), times(2));
            cryptoUtil.verify(() -> CryptoUtil.generateSHA256("hash1hash2"));
            cryptoUtil.verify(() -> CryptoUtil.generateSHA256("hash3hash3"));
            cryptoUtil.verify(() -> CryptoUtil.generateSHA256("hash12#hash33#"));
        }
    }

    @Test
    void shouldCreateMerkleRootForSingleTransaction() {
        try (MockedStatic<CryptoUtil> cryptoUtil = mockStatic(CryptoUtil.class)) {
            List<String> txs = new ArrayList<>(List.of("tx1"));
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("tx1")).thenReturn("hash1");
            cryptoUtil.when(() -> CryptoUtil.generateSHA256("hash1hash1")).thenReturn("rootHash");
            String result = MerkleRootUtil.createMerkleRoot(txs);
            assertEquals("rootHash", result);
            cryptoUtil.verify(() -> CryptoUtil.generateSHA256("tx1"), times(2));
            cryptoUtil.verify(() -> CryptoUtil.generateSHA256("hash1hash1"));
        }
    }

    @Test
    void shouldThrowExceptionForEmptyTransactionList() {
        List<String> txs = Collections.emptyList();
        assertThrows(IllegalArgumentException.class, () -> MerkleRootUtil.createMerkleRoot(txs),
                "Transaction list cannot be empty");
    }
}