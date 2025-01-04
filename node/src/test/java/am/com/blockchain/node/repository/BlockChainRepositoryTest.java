package am.com.blockchain.node.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import am.com.blockchain.common.block.Block;
import am.com.blockchain.node.model.block.InsertionOnlyList;
import am.com.blockchain.common.tx.TX;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlockChainRepositoryTest {

    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private BlockChainRepository repository;
    private final String genesisFileName = "test-genesis.json";
    private final String storageFileName = "test-storage.json";
    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        repository = new BlockChainRepository(objectMapper, genesisFileName, storageFileName);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    private static Stream<Arguments> provideBlocks() {
        return Stream.of(
                Arguments.of(new Block("#", "p#", "mr", 0, 0, 0L, 0, List.of()),
                        new Block("#1", "p#1", "mr1", 0, 1, 1L, 1, List.of()))
        );
    }

    @ParameterizedTest
    @MethodSource("provideBlocks")
    public void testInitLoadsBlocksCorrectly(Block block1, Block block2) throws Exception {
        List<Block> mockBlocks = List.of(block1, block2);
        ClassPathResource resource = mock(ClassPathResource.class);
        InputStream mockInputStream = mock(InputStream.class);
        when(resource.getInputStream()).thenReturn(mockInputStream);
        when(objectMapper.readValue(any(InputStream.class),
                ArgumentMatchers.<TypeReference<List<Block>>>any())).thenReturn(mockBlocks);
        repository.init();
        List<Block> blocks = repository.getBlocks();
        assertEquals(2, blocks.size());
        verify(objectMapper, times(1))
                .readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<Block>>>any());
    }

    @ParameterizedTest
    @MethodSource("provideBlocks")
    public void testInsertOnlyBlocks(Block block) throws Exception {
        List<Block> mockBlocks = List.of(block);
        ClassPathResource resource = mock(ClassPathResource.class);
        InputStream mockInputStream = mock(InputStream.class);
        when(resource.getInputStream()).thenReturn(mockInputStream);
        when(objectMapper.readValue(any(InputStream.class),
                ArgumentMatchers.<TypeReference<List<Block>>>any())).thenReturn(mockBlocks);
        repository.init();
        List<Block> blocks = repository.getBlocks();
        Block actual = blocks.getFirst();
        List<TX> txList = actual.getTx();
        assertThrows(UnsupportedOperationException.class, () -> {
            txList.add(new TX(null, List.of(), List.of()));
        });
        assertEquals(Collections.unmodifiableList(List.of()).getClass(), txList.getClass());
    }

    @ParameterizedTest
    @MethodSource("provideBlocks")
    void initShouldLoadBlocksSuccessfully(Block block) throws IOException {
        List<Block> mockBlocks = Collections.singletonList(block);
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(mockBlocks);
        repository.init();
        assertFalse(repository.getBlocks().isEmpty());
        assertEquals(1, repository.getBlocks().size());
        verify(objectMapper).readValue(any(InputStream.class), any(TypeReference.class));
    }

    @Test
    public void testFailureOnEmptyList() throws Exception {
        List<Block> mockBlocks = List.of();
        ClassPathResource resource = mock(ClassPathResource.class);
        InputStream mockInputStream = mock(InputStream.class);
        when(resource.getInputStream()).thenReturn(mockInputStream);
        when(objectMapper.readValue(any(InputStream.class),
                ArgumentMatchers.<TypeReference<List<Block>>>any())).thenReturn(mockBlocks);
        assertThrows(RuntimeException.class, () -> {
            repository.init();
        });
    }

}