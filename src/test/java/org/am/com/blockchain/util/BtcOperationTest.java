package org.am.com.blockchain.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BtcOperationTest {

    @Test
    void sumInts() {
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
    void sumFloats() {
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
    void floatToSatoshi() {
        // Positive case
        BigDecimal result = BtcOperation.floatToSatoshi(1.5);
        assertEquals(new BigDecimal("150000000.0"), result);

        // Zero case
        result = BtcOperation.floatToSatoshi(0.0);
        assertEquals(BigDecimal.ZERO, result);

        // Negative case (should throw exception)
        assertThrows(IllegalArgumentException.class, () -> BtcOperation.floatToSatoshi(-1.0));
    }

    @Test
    void satoshiToFloat() {
        // Positive case
        double result = BtcOperation.satoshiToDouble(new BigDecimal("150000000"));
        assertEquals(1.5, result, 0.0001);

        // Zero case
        result = BtcOperation.satoshiToDouble(BigDecimal.ZERO);
        assertEquals(0.0, result, 0.0001);

        // Negative case (should throw exception)
        assertThrows(IllegalArgumentException.class, () ->
                BtcOperation.satoshiToDouble(new BigDecimal("-100000000")));
    }
}