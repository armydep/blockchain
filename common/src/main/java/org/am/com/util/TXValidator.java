package org.am.com.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.am.com.exceptions.SignatureException;
import org.am.com.tx.StrippedTX;
import org.am.com.tx.TX;
import org.am.com.tx.TXBuilder;

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
    public static void validate(/*@NotNull*/ TX tx) throws SignatureException {
        StrippedTX strippedTX = TXBuilder.stripTX(tx);
        SignatureUtil.verifyDigitalSignature(strippedTX.tx().toString(),
                strippedTX.scriptSig().signature(), strippedTX.scriptSig().publicKey());
    }

    //2
    public static boolean isValid(TX tx) {
        return true;
    }
}
