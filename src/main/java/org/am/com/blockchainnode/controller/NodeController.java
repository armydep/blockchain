package org.am.com.blockchainnode.controller;

import org.am.com.blockchainnode.GenesisLoadConfig;
import org.am.com.blockchainnode.domain.block.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node/api")
public class NodeController {

    @Autowired
    private GenesisLoadConfig genesisLoadConfig;

    @GetMapping("/node")
    public ResponseEntity<List<Block>> getBlocks() {
        return new ResponseEntity<>(genesisLoadConfig.getJsonData(), HttpStatus.OK);
    }
}
