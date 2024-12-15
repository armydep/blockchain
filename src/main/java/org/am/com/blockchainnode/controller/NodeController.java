package org.am.com.blockchainnode.controller;

import org.am.com.blockchainnode.domain.block.Block;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/node/api")
public class NodeController {

    //private static final String NODE_ID = "123456";
    //private MempoolService mempoolService;

    //node
    @GetMapping("/node")
    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();

        Block block1 = new Block();
        block1.setIndex(1L);
        block1.setHash("123456");
        block1.setPreviousHash("000000");
        block1.setData("Block 1 data: " + new Date());

        Block block2 = new Block();
        block2.setIndex(2L);
        block2.setHash("789012");
        block2.setPreviousHash("123456");
        block2.setData("Block 2 data: " + new Date());

        blocks.add(block1);
        blocks.add(block2);

        return blocks;
    }

    //wallet send
//    @PostMapping("/tx")
//    public void addTransactionToMempool(@RequestBody TransactionRequest transactionRequest) {
//        mempoolService.addTransaction(transactionRequest);
//    }

    //wallet
//    @GetMapping("/tx")
//    public String getTransactionStatus(@RequestParam String transactionId) {
//        return "";
//    }

    //miner
//    @GetMapping("/mempool")
//    public TransactionRequest getTransactionFromMempool(@RequestParam String tx) {
//        return mempoolService.getTransaction();
//    }

    //miner
//    @PostMapping("/block")
//    public String submitBlock(String address) {
//        return "";
//    }
}
