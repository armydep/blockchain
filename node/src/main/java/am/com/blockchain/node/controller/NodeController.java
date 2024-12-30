package am.com.blockchain.node.controller;

import am.com.blockchain.common.balance.UTXO;
import am.com.blockchain.common.block.Block;
import am.com.blockchain.common.tx.TX;
import am.com.blockchain.node.service.BlockChainServiceV2;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class NodeController {
    private final BlockChainServiceV2 blockChainService;

    @GetMapping("/blocks")
    public ResponseEntity<List<Block>> getBlocks() {
        return ResponseEntity.ok(blockChainService.getBlocks());
    }

    @GetMapping("/mempool")
    public ResponseEntity<List<TX>> getMempool() {
        return ResponseEntity.ok(blockChainService.getMempool());
    }

    @GetMapping("/utxo")
    public ResponseEntity<List<UTXO>> getUTXO() {
        return ResponseEntity.ok(blockChainService.getUTXO());
    }
}
