package org.am.com.blockchainnode.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchainnode.model.MempoolTransaction;
import org.am.com.blockchainnode.model.block.Block;
import org.am.com.blockchainnode.service.MempoolService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/miner/api")
public class MinerController {
    //private final BlockChain blockChain;
    private final MempoolService mempoolService;
    private final static List<MempoolTransaction> localpool = Collections
            .synchronizedList(new ArrayList<>());

    /*
        private final localPool

        isFinished all tasks?(){
            if(localpool.is empty){
                return true
            }
            else{
                log. print "busy"
                return;
            }
        }

        if mempool is empty{

        }
        else{
            tx <- mempool.take()

        }
     */

    //@Scheduled(fixedRate = 20000, initialDelay = 10000)
    private void invokeMiner() {
        log.info("Invoke Miner. mempool size: " + mempoolService.getMempool().size());
        if (localpool.isEmpty()) {
            log.info("Miner is Idle");
            if (mempoolService.getMempool().isEmpty()) {
                log.info("Mempool is empty. Nothing to mine.");
            } else {
                MempoolTransaction tx = mempoolService.getMempool().getFirst();
                localpool.add(tx);
                log.info("Taking: " + tx);
                mine(tx);
                //*****
                Block block = createBlock(tx);
                localpool.remove(tx);
                log.info("Mined: " + tx);
            }
        } else {
            log.info("Miner is busy");
        }
    }

    private Block createBlock(MempoolTransaction tx) {
        return null;
    }

    private void mine(MempoolTransaction tx) {
        try {
            sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
