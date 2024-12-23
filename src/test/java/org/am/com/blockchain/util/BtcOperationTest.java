package org.am.com.blockchain.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BtcOperationTest {

    @Test
    void testSumInts() {
        // Positive case
        double result = BtcOperation.sumInts(1, 50_000_000, 25_000_000);
        assertEquals(1.75, result, 0.0001);

        double result2 = BtcOperation.sumInts(1, 0, 18);
        assertEquals(1.00000018, result2, 0.0001);

        // Edge case: Overflow of Satoshi to BTC
        result = BtcOperation.sumInts(0, 100_000_000, 50_000_000);
        assertEquals(1.5, result, 0.0001);

        // Zero case
        result = BtcOperation.sumInts(0, 0, 0);
        assertEquals(0.0, result, 0.0001);
    }

    @Test
    void testSumDoubles() {
        // Positive case
        double result = BtcOperation.sumDoubles(1.5, 0.25);
        assertEquals(1.75, result, 0.0001);

        // Zero case
        result = BtcOperation.sumDoubles(0.0, 0.0);
        assertEquals(0.0, result, 0.0001);

        // Large BTC values
        result = BtcOperation.sumDoubles(100000.5, 200000.25);
        assertEquals(300000.75, result, 0.0001);
    }

    @Test
    void testDoubleToSatoshi() {
        // Positive case
        BigDecimal result = BtcOperation.doubleToSatoshi(1.5);
        assertEquals(new BigDecimal("150000000.0"), result);

        // Zero case
        result = BtcOperation.doubleToSatoshi(0.0);
        assertEquals(BigDecimal.ZERO, result);

        assertEquals(BigDecimal.valueOf(-100000000), BtcOperation.doubleToSatoshi(-1.0));
    }

    @Test
    void testSatoshiToDouble() {
        // Positive case
        double result = BtcOperation.satoshiToBTCDouble(new BigDecimal("150000000"));
        assertEquals(1.5, result, 0.0001);

        // Zero case
        result = BtcOperation.satoshiToBTCDouble(BigDecimal.ZERO);
        assertEquals(0.0, result, 0.0001);

        // Negative case
        assertEquals(-1.0, BtcOperation.satoshiToBTCDouble(new BigDecimal("-100000000")));
    }
}