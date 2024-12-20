package org.am.com.blockchain.controller;

import lombok.AllArgsConstructor;
import org.am.com.blockchain.model.MempoolTransaction;
import org.am.com.blockchain.model.block.Block;
import org.am.com.blockchain.service.BlockChainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/node/api")
public class NodeController {
    private final BlockChainService blockChainService;

    @GetMapping("/node")
    public ResponseEntity<List<Block>> getBlocks() {
        return new ResponseEntity<>(blockChainService.getBlocks(), HttpStatus.OK);
    }

    @GetMapping("/mempool")
    public ResponseEntity<List<MempoolTransaction>> getMempool() {
        return new ResponseEntity<>(blockChainService.getMempool(), HttpStatus.OK);
    }
}
