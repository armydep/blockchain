package org.am.com.blockchain.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.model.user.Key;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

@Slf4j
@UtilityClass
public class CryptoUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void generateKeys1(Key key) throws NoSuchAlgorithmException {
        // 1. Create a KeyPairGenerator object for RSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        // 2. Initialize the generator with a key size
        keyPairGenerator.initialize(2048); // Use 2048 or 4096 bits for stronger keys

        // 3. Generate the key pair
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 4. Extract the public and private keys
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        log.info("Public Key:\n" + publicKey);
        log.info("Private Key:\n" + privateKey);

        // 5. Encode keys as Base64 (Optional for readability)
        String publicKeyEncoded = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyEncoded = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        // 6. Print the keys
        log.info("Public Key encoded:\n" + publicKeyEncoded);
        log.info("Private Key encoded:\n" + privateKeyEncoded);

        key.setPrivateKey(privateKeyEncoded);
        key.setPublicKey(publicKeyEncoded);
    }

    public static void generateKeys(Key key) throws Exception {
        // Step 1: Generate a key pair (private and public keys)
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
        keyGen.initialize(new ECGenParameterSpec("secp256k1"));
        KeyPair keyPair = keyGen.generateKeyPair();

        // Extract the public key
        PublicKey publicKey = keyPair.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();

        // Step 2: Perform SHA-256 on the public key
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] sha256Hash = sha256.digest(publicKeyBytes);

        // Step 3: Perform RIPEMD-160 on the SHA-256 hash
        MessageDigest ripemd160 = MessageDigest.getInstance("RIPEMD160", "BC");
        byte[] ripemd160Hash = ripemd160.digest(sha256Hash);

        // Step 4: Add network prefix (0x00 for mainnet)
        byte[] versionedPayload = new byte[ripemd160Hash.length + 1];
        versionedPayload[0] = 0x00; // Version byte for mainnet
        System.arraycopy(ripemd160Hash, 0, versionedPayload, 1, ripemd160Hash.length);

        // Step 5: Calculate the checksum
        byte[] checksum = calculateChecksum(versionedPayload);

        // Step 6: Append the checksum to the versioned payload
        byte[] finalPayload = new byte[versionedPayload.length + 4];
        System.arraycopy(versionedPayload, 0, finalPayload, 0, versionedPayload.length);
        System.arraycopy(checksum, 0, finalPayload, versionedPayload.length, 4);

        // Step 7: Encode in Base58
        String address = encodeBase58(finalPayload);

        String publicKeyEncoded = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        PrivateKey privateKey = keyPair.getPrivate();
        String privateKeyEncoded = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        // Print the generated address
        log.info("Public Key encoded:\n" + publicKeyEncoded);
        log.info("Private Key encoded:\n" + privateKeyEncoded);
        log.info("Bitcoin Address: " + address);
        key.setPublicKey(publicKeyEncoded);
        key.setPrivateKey(privateKeyEncoded);
        key.setAddress(address);
    }

    private static byte[] calculateChecksum(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hash1 = sha256.digest(data);
        byte[] hash2 = sha256.digest(hash1);
        return Arrays.copyOfRange(hash2, 0, 4);
    }

    private static String encodeBase58(byte[] input) {
        // Base58 encoding table
        final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        int zeroCount = 0;
        for (byte b : input) {
            if (b == 0) {
                zeroCount++;
            } else {
                break;
            }
        }
        byte[] temp = Arrays.copyOf(input, input.length);
        for (int i = zeroCount; i < temp.length; i++) {
            int carry = temp[i] & 0xFF;
            for (int j = sb.length() - 1; j >= 0; j--) {
                carry += 256 * ALPHABET.indexOf(sb.charAt(j));
                sb.setCharAt(j, ALPHABET.charAt(carry % 58));
                carry /= 58;
            }
            while (carry > 0) {
                sb.insert(0, ALPHABET.charAt(carry % 58));
                carry /= 58;
            }
        }
        for (int i = 0; i < zeroCount; i++) {
            sb.insert(0, '1');
        }
        return sb.toString();
    }
}
