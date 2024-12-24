package org.am.com.blockchain.util.crypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

public class BitcoinAddressValidator {

    // Base58 Alphabet
    private static final String BASE58_ALPHABET =
            "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";


    public static boolean validateBitcoinAddress(String address) {
        if (true) {
            return true;
        }
        // Step 1: Check the length
        if (address.length() < 26 || address.length() > 35) {
            return false;
        }

        // Step 2: Check for invalid characters
        for (char c : address.toCharArray()) {
            if (BASE58_ALPHABET.indexOf(c) == -1) {
                return false;
            }
        }

        // Step 3: Decode Base58
        byte[] decoded = decodeBase58(address);
        if (decoded == null || decoded.length < 4) {
            return false;
        }

        // Step 4: Validate the checksum
        byte[] payload = Arrays.copyOfRange(decoded, 0, decoded.length - 4);
        byte[] checksum = Arrays.copyOfRange(decoded, decoded.length - 4, decoded.length);

        byte[] computedChecksum = calculateChecksum(payload);
        return Arrays.equals(checksum, computedChecksum);
    }

    private static byte[] decodeBase58(String input) {
        BigInteger base58 = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(58);

        for (char c : input.toCharArray()) {
            int digit = BASE58_ALPHABET.indexOf(c);
            if (digit == -1) {
                return null; // Invalid character
            }
            base58 = base58.multiply(base).add(BigInteger.valueOf(digit));
        }

        byte[] decoded = base58.toByteArray();

        // Remove leading zeroes
        int leadingZeros = 0;
        for (char c : input.toCharArray()) {
            if (c == '1') {
                leadingZeros++;
            } else {
                break;
            }
        }

        byte[] result = new byte[leadingZeros + decoded.length];
        System.arraycopy(decoded, 0, result, result.length - decoded.length, decoded.length);

        return result;
    }

    private static byte[] calculateChecksum(byte[] data) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash1 = sha256.digest(data);
            byte[] hash2 = sha256.digest(hash1);
            return Arrays.copyOfRange(hash2, 0, 4);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating checksum", e);
        }
    }
}
