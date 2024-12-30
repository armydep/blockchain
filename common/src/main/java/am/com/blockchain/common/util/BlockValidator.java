package am.com.blockchain.common.util;

import am.com.blockchain.common.block.Block;

public class BlockValidator {
    public static void validate(Block block) {
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
    }
}
