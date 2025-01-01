package am.com.blockchain.common.util;

import am.com.blockchain.common.util.crypto.CryptoUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import am.com.blockchain.common.exceptions.SignatureException;
import am.com.blockchain.common.tx.StrippedTX;
import am.com.blockchain.common.tx.TX;
import am.com.blockchain.common.tx.TXBuilder;

@Slf4j
@UtilityClass
public class TXValidator {

    /*
        1. endpoint input validate: before submitting to mempool
        2. miner get from mempool validate: before block assembly
        3. block validation: before adding to blockchain
        4. block validation: as a part of blockchain validation
     */

    //1
    public static void validate(TX tx) throws SignatureException {
        validateTXScriptSig(tx);
        validateTxId(tx);
    }

    private static void validateTxId(TX tx) throws SignatureException {
        try {
            TX strippedTX = TXBuilder.stripTXfromTxId(tx);
            if (!CryptoUtil.verifySha256(strippedTX.toString(), tx.getTxid())) {
                throw new SignatureException("Didn't pass txid validation");
            }
        } catch (Exception e) {
            throw new SignatureException(e);
        }
    }

    private static void validateTXScriptSig(TX tx) throws SignatureException {
        try {
            StrippedTX strippedTX = TXBuilder.stripTXfromScriptSigAndTxId(tx);
            if (!SignatureUtil.verifyDigitalSignature(strippedTX.tx().toString(),
                    strippedTX.scriptSig().signature(), strippedTX.scriptSig().publicKey())) {
                throw new SignatureException("Didn't pass TX ScriptSig validation");
            }
        } catch (Exception e) {
            throw new SignatureException(e);
        }
    }

    //2
    public static boolean isValid(TX tx) {
        return true;
    }
}
