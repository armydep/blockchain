package org.am.com.util;

import lombok.extern.slf4j.Slf4j;
import org.am.com.exceptions.SignatureException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class SignatureUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String sign(String input, String privateKeyStr) throws SignatureException {
        String encodedSignature = null;
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            PrivateKey privateKey = decodePrivateKey(privateKeyStr);
            signature.initSign(privateKey);
            byte[] data = input.getBytes(StandardCharsets.UTF_8);
            signature.update(data);
            byte[] digitalSignature = signature.sign();
            encodedSignature = Base64.getEncoder().encodeToString(digitalSignature);
            log.info("Text: " + input);
            log.info("Digital Signature: " + encodedSignature);
        } catch (Exception e) {
            throw new SignatureException(e);
        }
        return encodedSignature;
    }

    public static boolean verifyDigitalSignature(String inputStr, String signatureStr, String publicKeyStr)
            throws SignatureException {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initVerify(pubKey);
            byte[] data = inputStr.getBytes();
            signature.update(data);
            boolean verified = signature.verify(signatureBytes);
            log.info("Verified: " + verified);
            return verified;
        } catch (Exception e) {
            log.warn("TX signature verification failure", e);
            throw new SignatureException(e);
        }
    }

    private static PrivateKey decodePrivateKey(String privateKeyStr)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        //KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
        return keyFactory.generatePrivate(keySpec);
    }
}

