package org.am.com.blockchainnode.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchainnode.domain.block.Block;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@AllArgsConstructor
public class BlockChainService {
    //should be deep cloned in getter
    @Getter
    private List<Block> blocks;
    private final ReentrantLock lock = new ReentrantLock();
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws IOException {
        blocks = loadBlocks();
    }

    private List<Block> loadBlocks() {
        try {
            ClassPathResource resource = new ClassPathResource("genesis.json");
            List<Block> data = objectMapper.readValue(resource.getInputStream(),
                    new TypeReference<>() {
                    });
            log.info("Loaded JSON Data: " + data);
            return data;
        } catch (Throwable e) {
            log.error("Failed to load JSON: " + e.getMessage());
        }
        return List.of();
    }
}
