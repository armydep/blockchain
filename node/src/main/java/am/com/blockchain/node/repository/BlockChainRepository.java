package am.com.blockchain.node.repository;

import am.com.blockchain.common.block.Block;
import am.com.blockchain.node.model.block.InsertionOnlyList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
