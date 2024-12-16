package org.am.com.blockchainnode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.am.com.blockchainnode.domain.block.Block;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class GenesisLoadConfig {

    private final ObjectMapper objectMapper;

    public GenesisLoadConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Block> getJsonData() {
        try {
            ClassPathResource resource = new ClassPathResource("genesis.json");
            List<Block> data = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {
            });
            log.info("Loaded JSON Data: " + data);
            return data;
        } catch (IOException e) {
            log.error("Failed to load JSON: " + e.getMessage());
        }
        return List.of();
    }

}
