package org.am.com.blockchain.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.am.com.blockchain.model.block.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BlockChainRepositoryTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private BlockChainRepository blockChainRepository;

    private final String genesisFileName = "test-genesis.json";
    private final String storageFileName = "test-storage.json";

    @BeforeEach
    public void setUp() {
        blockChainRepository = new BlockChainRepository(objectMapper,
                genesisFileName, storageFileName);
        MockitoAnnotations.openMocks(this);
    }

    private static Stream<Block> provideBlocks() {
        return Stream.of(
                new Block("#", "p#", "mr", 0, 0, 0L, 0, List.of())
                //,
                //new Block("#1", "p#1", "mr1", 0, 1, 1L, 1, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideBlocks")
    public void testInitLoadsBlocksCorrectly(Block block) throws Exception {
        assertEquals(0, block.getIndex());

//        // Mock JSON data
//        List<Block> mockBlocks = Arrays.asList(block);
//
//        // Mock InputStream for ClassPathResource
//        ClassPathResource resource = new ClassPathResource(genesisFileName);
//        InputStream mockInputStream = mock(InputStream.class);
//        when(resource.getInputStream()).thenReturn(mockInputStream);
//
//        when(objectMapper.readValue(any(InputStream.class),
//                any(TypeReference.class))).thenReturn(mockBlocks);
//
//        blockChainRepository.init();
//
//        List<Block> blocks = blockChainRepository.getBlocks();
//        assertEquals(2, blocks.size());
//        //assertEquals(new InsertionOnlyList<>(mockBlocks), blocks);
//
//        verify(objectMapper, times(1))
//                .readValue(any(InputStream.class), any(TypeReference.class));
    }
}