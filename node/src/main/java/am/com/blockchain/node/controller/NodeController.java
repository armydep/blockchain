package am.com.blockchain.node.controller;

import lombok.AllArgsConstructor;
import am.com.blockchain.node.model.MempoolTransaction;
import am.com.blockchain.common.block.Block;
import am.com.blockchain.common.balance.UTXO;
import am.com.blockchain.node.service.BlockChainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class NodeController {
    private final BlockChainService blockChainService;

    @GetMapping("/blocks")
    public ResponseEntity<List<Block>> getBlocks() {
        return ResponseEntity.ok(blockChainService.getBlocks());
    }

    @GetMapping("/mempool")
    public ResponseEntity<List<MempoolTransaction>> getMempool() {
        return ResponseEntity.ok(blockChainService.getMempool());
    }

    @GetMapping("/utxo")
    public ResponseEntity<List<UTXO>> getUTXO() {
        return ResponseEntity.ok(blockChainService.getUTXO());
    }
}
