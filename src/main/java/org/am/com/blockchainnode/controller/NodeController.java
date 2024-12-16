package org.am.com.blockchainnode.controller;

import lombok.AllArgsConstructor;
import org.am.com.blockchainnode.GenesisLoadConfig;
import org.am.com.blockchainnode.MempoolService;
import org.am.com.blockchainnode.domain.block.Block;
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

    private final GenesisLoadConfig genesisLoadConfig;
    private final MempoolService mempoolService;

/*
    public NodeController(GenesisLoadConfig genesisLoadConfig) {
        this.genesisLoadConfig = genesisLoadConfig;
    }
*/

    @GetMapping("/node")
    public ResponseEntity<List<Block>> getBlocks() {
        return new ResponseEntity<>(genesisLoadConfig.getJsonData(), HttpStatus.OK);
    }

    @GetMapping("/mempool")
    public ResponseEntity<List<MempoolTransaction>> getMempool() {
        return new ResponseEntity<>(mempoolService.getMempool(), HttpStatus.OK);
    }
}
