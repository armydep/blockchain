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

        @Getter
        private List<Block> jsonData;

        public GenesisLoadConfig(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @PostConstruct
        public void loadData() {
            try {
                ClassPathResource resource = new ClassPathResource("genesis.json");
                jsonData = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
                log.info("Loaded JSON Data: " + jsonData);
            } catch (IOException e) {
                log.error("Failed to load JSON: " + e.getMessage());
            }
        }

}
