package org.am.com.blockchain.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchain.model.block.Block;
import org.am.com.blockchain.model.block.InsertionOnlyList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Repository
public class BlockChainRepository {
    private List<Block> blocks;
    private final ObjectMapper objectMapper;
    private final String genesisFileName;
    private final String storageFileName;

    public BlockChainRepository( ObjectMapper objectMapper,
                                @Value("${genesis.data}") String genesisFileName,
                                @Value("${storage.blocks.data}") String storageFileName) {
        this.objectMapper = objectMapper;
        this.genesisFileName = genesisFileName;
        this.storageFileName = storageFileName;
    }

    @PostConstruct
    public void init() throws IOException {
        List<Block> oblocks = loadBlocks();
        blocks = new InsertionOnlyList<>();
        blocks.addAll(oblocks);
    }

    private List<Block> loadBlocks() throws IOException {
        ClassPathResource resource = new ClassPathResource(genesisFileName);
        List<Block> data = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {
        });
        log.info("Loaded JSON Data: " + data);
        return data;
    }

    public List<Block> gBlocks() {
        return blocks;
    }

    public void addBlock(Block block) {
        blocks.add(block);
        saveBlockToFile();
    }

    private void saveBlockToFile() {
        try {
            objectMapper.writeValue(new File(storageFileName), blocks);
            log.info("Block saved as JSON in file: " + storageFileName);
        } catch (IOException e) {
            log.error("Error while saving the block to file: " + e.getMessage());
        }
    }

    public Block getLastBlock() {
        return blocks.getLast();
    }
}
