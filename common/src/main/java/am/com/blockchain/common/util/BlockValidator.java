package am.com.blockchain.common.util;

import am.com.blockchain.common.block.Block;
import am.com.blockchain.common.exceptions.BlockValidationException;
import lombok.NonNull;

import javax.annotation.processing.SupportedAnnotationTypes;

public class BlockValidator {

    /*
        Validate List<TX>
        1.
        for each i, j TX(i).in(j) that is not CoinbaseTX
            there is a k
                    such that k < i
                    such that TX(k).txid = TX(i).in(j).txid
                    such that TX(k).out(j).value >= TX(i).in(j).value

          2.
          Sum(tx.in.value) >= Sub(tx.out.value)
     */
    public static void validate(@NonNull Block block, @NonNull Block last) throws BlockValidationException {
        String hash = block.getPreviousHash();
        String prev = last.getHash();
        if (block.getPreviousHash().equals(last.getHash())) {

        } else {
            throw new BlockValidationException(String.format("Previous hash value not valid: %s. %s", hash, prev));
        }
    }
}
