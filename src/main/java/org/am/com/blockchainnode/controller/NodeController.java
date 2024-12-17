package org.am.com.blockchainnode.controller;

import lombok.AllArgsConstructor;
import org.am.com.blockchainnode.domain.MempoolTransaction;
import org.am.com.blockchainnode.domain.block.Block;
import org.am.com.blockchainnode.service.BlockChainService;
import org.am.com.blockchainnode.service.MempoolService;
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
    private final MempoolService mempoolService;
    private final BlockChainService blockChainService;

    @GetMapping("/node")
    public ResponseEntity<List<Block>> getBlocks() {
        return new ResponseEntity<>(blockChainService.getBlocks(), HttpStatus.OK);
    }

    @GetMapping("/mempool")
    public ResponseEntity<List<MempoolTransaction>> getMempool() {
        return new ResponseEntity<>(mempoolService.getMempool(), HttpStatus.OK);
    }
}
