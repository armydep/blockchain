package org.am.com.blockchain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.am.com.blockchain.model.block.Block;
import org.am.com.blockchain.model.block.UTXO;
import org.am.com.blockchain.repository.BlockChainRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class BlockChainServiceTest {

    private AutoCloseable closeable;

    @InjectMocks
    private BlockChainService service;

    @Mock
    private BlockChainRepository repository;


    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @ParameterizedTest
    @MethodSource("provideBlocks")
    public void testGetUTXO(List<Block> blocks, List<UTXO> expectedUTXOs) {
        when(repository.getBlocks()).thenReturn(blocks);
        List<UTXO> utxos = service.getUTXO();
        assertEquals(expectedUTXOs, utxos);
    }

    @ParameterizedTest
    @MethodSource("provideFileNames")
    public void testGetUTXO2(String blocksFile, String utxoFile) throws IOException {
        List<Block> blocks = loadBlocks(blocksFile);
        List<UTXO> expectedUTXOs = loadUTXOs(utxoFile);
        when(repository.getBlocks()).thenReturn(blocks);
        List<UTXO> utxos = service.getUTXO();
        assertEquals(expectedUTXOs, utxos);
    }

    private List<Block> loadBlocks(String blocksFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Block> blocks = objectMapper
                .readValue(new ClassPathResource(blocksFile).getInputStream(),
                        new TypeReference<List<Block>>() {
                        });
        return blocks;
    }

    private List<UTXO> loadUTXOs(String utxoFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<UTXO> utxos = objectMapper
                .readValue(new ClassPathResource(utxoFile).getInputStream(),
                        new TypeReference<List<UTXO>>() {
                        });
        return utxos;
    }

    static Stream<Arguments> provideFileNames() {
        return Stream.of(Arguments.of("utxo/blocks0.json", "utxo/utxos0.json"),
                Arguments.of("utxo/blocks1.json", "utxo/utxos1.json"));
    }

    static Stream<Arguments> provideBlocks() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Block> blocks = objectMapper
                .readValue(new ClassPathResource("blocks.json").getInputStream(),
                        new TypeReference<List<Block>>() {
                        });
        List<UTXO> expectedUTXOs = objectMapper.readValue(
                new ClassPathResource("expectedUTXOs.json").getInputStream(),
                new TypeReference<List<UTXO>>() {
                }
        );
        return Stream.of(Arguments.of(blocks, expectedUTXOs));
    }
}
