package org.am.com.util;

import lombok.experimental.UtilityClass;
import org.am.com.tx.TX;

@UtilityClass
public class TXValidator {

    /*
        1. endpoint input validate: before submitting to mempool
        2. miner get from mempool validate: before block assembly
        3. block validation: before adding to blockchain
        4. block validation: as a part of blockchain validation
     */

    //1
    public static void validate(/*@NotNull*/ TX tx) {

    }

    //2
    public static boolean isValid(TX tx) {
        return true;
    }
}
