package am.com.blockchain.node;

import am.com.blockchain.common.balance.Balance;
import am.com.blockchain.common.balance.UTXO;
import am.com.blockchain.common.block.Block;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class TestUtils {
    public static List<Block> loadBlocks(String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Block> blocks = objectMapper
                .readValue(new ClassPathResource(path).getInputStream(),
                        new TypeReference<List<Block>>() {
                        });
        return blocks;
    }

    public static List<UTXO> loadUTXOs(String utxoFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper
                .readValue(new ClassPathResource(utxoFile).getInputStream(),
                        new TypeReference<List<UTXO>>() {
                        });
    }

    public static List<Balance> loadBalances(String balancesFlie) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper
                .readValue(new ClassPathResource(balancesFlie).getInputStream(),
                        new TypeReference<List<Balance>>() {
                        });
    }
}
