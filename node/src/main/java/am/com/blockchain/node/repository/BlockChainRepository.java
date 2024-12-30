package am.com.blockchain.node.repository;

import am.com.blockchain.common.balance.UTXO;
import am.com.blockchain.common.block.CoinBaseEntry;
import am.com.blockchain.common.tx.TX;
import am.com.blockchain.common.tx.TxInEntry;
import am.com.blockchain.common.tx.TxOutEntry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import am.com.blockchain.common.block.Block;
import am.com.blockchain.node.model.block.InsertionOnlyList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class BlockChainRepository {

    @Getter
    private final List<Block> blocks = new InsertionOnlyList<>();
    private final ObjectMapper objectMapper;
    private final String genesisFileName;
    private final String storageFileName;

    public BlockChainRepository(ObjectMapper objectMapper,
                                @Value("${genesis.data}") String genesisFileName,
                                @Value("${storage.blocks.data}") String storageFileName) {
        this.objectMapper = objectMapper;
        this.genesisFileName = genesisFileName;
        this.storageFileName = storageFileName;
    }

    @PostConstruct
    public void init() {
        try {
            List<Block> oblocks = loadBlocks();
            blocks.addAll(oblocks);
            if (blocks.isEmpty()) {
                throw new RuntimeException("No blocks found");
            }
        } catch (IOException e) {
            log.error("Error initializing BlockChainRepository: {}", e.getMessage());
        }
    }

    public List<UTXO> getUTXO() {
        List<UTXO> utxoData = new ArrayList<>();
        for (Block block : blocks) {
            CoinBaseEntry coinBaseEntry = block.getCoinBaseEntry();
            if (coinBaseEntry != null) {
                UTXO utxo = generateCoinBaseUTXO(coinBaseEntry);
                utxoData.add(utxo);
            }
            int i = coinBaseEntry == null ? 0 : 1;
            for (; i < block.getTx().size(); i++) {
                TX tx = block.getTx().get(i);
                List<TxInEntry> txInEntries = tx.getVin();
                for (TxInEntry txInEntry : txInEntries) {
                    discardUTXOByTxIn(txInEntry, utxoData);
                }
                List<TxOutEntry> txOutEntries = tx.getVout();
                for (TxOutEntry txOutEntry : txOutEntries) {
                    generateAndInsertUTXOByTxOut(txOutEntry, utxoData, tx.getTxid());
                }
            }
        }
        return utxoData;
    }

    private void discardUTXOByTxIn(@NotNull TxInEntry txInEntry, List<UTXO> utxoData) {
        boolean removed = false;
        for (UTXO utxo : utxoData) {
            if (utxo.getTx().equals(txInEntry.getTxid()) && txInEntry.getVout().equals(utxo.getVout())) {
                utxoData.remove(utxo);
                removed = true;
                break;
            }
        }
        if (!removed) {
            log.warn("Not found UTXO for discard: " + txInEntry.getTxid());
        }
    }

    private void generateAndInsertUTXOByTxOut(TxOutEntry txOutEntry, List<UTXO> utxoData, String txid) {
        UTXO utxo = new UTXO(txid, txOutEntry.getValue(), txOutEntry.getAddress(), txOutEntry.getN());
        utxoData.add(utxo);
    }

    private UTXO generateCoinBaseUTXO(CoinBaseEntry coinBaseEntry) {
        return new UTXO(coinBaseEntry.getTxid(),
                coinBaseEntry.getValue(), coinBaseEntry.getAddress(), coinBaseEntry.getN());
    }

    private List<Block> loadBlocks() throws IOException {
        try (var inputStream = new ClassPathResource(genesisFileName).getInputStream()) {
            List<Block> data = objectMapper.readValue(inputStream,
                    new TypeReference<>() {
                    });
            log.info("Loaded JSON Data: {}", data);
            return data;
        } catch (IOException e) {
            log.error("Error loading blocks from file: {}", e.getMessage());
            throw e;
        }
    }

    public void addBlock(Block block) {
        blocks.add(block);
        saveBlocksToFile();
    }

    private void saveBlocksToFile() {
        try {
            Files.createDirectories(Paths.get(storageFileName).getParent());
            objectMapper.writeValue(new File(storageFileName), blocks);
            log.info("Blocks saved as JSON in file: {}", storageFileName);
        } catch (IOException e) {
            log.error("Error while saving blocks to file: {}", e.getMessage());
        }
    }

    public Block getLastBlock() {
        return blocks.isEmpty() ? null : blocks.getLast();
    }
}
