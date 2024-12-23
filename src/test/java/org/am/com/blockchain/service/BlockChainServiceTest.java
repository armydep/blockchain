package org.am.com.blockchain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.am.com.blockchain.model.block.Block;
import org.am.com.blockchain.model.block.UTXO;
import org.am.com.blockchain.model.wallet.Balance;
import org.am.com.blockchain.repository.BlockChainRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
    public void testGetEmptyUTXO(List<Block> blocks, List<UTXO> expectedUTXOs) {
        when(repository.getBlocks()).thenReturn(blocks);
        List<UTXO> utxos = service.getUTXO();
        assertEquals(expectedUTXOs, utxos);
    }

    @ParameterizedTest
    @MethodSource("provideFileNames")
    public void testGetUTXO(String blocksFile, String utxoFile) throws IOException {
        List<Block> blocks = loadBlocks(blocksFile);
        List<UTXO> expectedUTXOs = loadUTXOs(utxoFile);
        when(repository.getBlocks()).thenReturn(blocks);
        List<UTXO> utxos = service.getUTXO();
        assertEquals(expectedUTXOs, utxos);
    }

    @ParameterizedTest
    @MethodSource("provideBalanceFileNames")
    public void testGetBalances(String blocksFile,
                                String utxoFile,
                                String balanceFile) throws IOException {
        List<Balance> expectedBalances = loadBalances(balanceFile);
        List<Block> blocks = loadBlocks(blocksFile);
        List<UTXO> utxos = loadUTXOs(utxoFile);
        when(service.getUTXO()).thenReturn(utxos);
        when(service.getBlocks()).thenReturn(blocks);
        List<Balance> balances = service.getBalances();
        assertEquals(expectedBalances, balances);
    }

    @Test
    void testFindBalanceByAddress_NotFound() throws IOException {
        BlockChainService serviceSpy = Mockito.spy(service);
        List<Balance> balances = loadBalances("balance/balance0.json");
        doReturn(balances).when(serviceSpy).getBalances();
        String addressToSearch = "abc";
        Optional<Balance> result = serviceSpy.findBalanceByAddress(addressToSearch);
        assertEquals(Optional.empty(), result);
        verify(serviceSpy).getBalances();
    }

    @Test
    void testFindBalanceByAddress() throws IOException {
        BlockChainService serviceSpy = Mockito.spy(service);
        List<Balance> balances = loadBalances("balance/balance1.json");
        doReturn(balances).when(serviceSpy).getBalances();
        String addressToSearch = "15ZbsZw8zhSBToqBkAvdQzBjWeAg43htBf";
        Optional<Balance> result = serviceSpy.findBalanceByAddress(addressToSearch);
        assertEquals(Optional.of(balances.get(1)), result);
        verify(serviceSpy).getBalances();
    }

    private List<Balance> loadBalances(String balanceFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper
                .readValue(new ClassPathResource(balanceFile).getInputStream(),
                        new TypeReference<List<Balance>>() {
                        });
    }

    private List<Block> loadBlocks(String blocksFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper
                .readValue(new ClassPathResource(blocksFile).getInputStream(),
                        new TypeReference<List<Block>>() {
                        });
    }

    private List<UTXO> loadUTXOs(String utxoFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper
                .readValue(new ClassPathResource(utxoFile).getInputStream(),
                        new TypeReference<List<UTXO>>() {
                        });
    }

    static Stream<Arguments> provideBalanceFileNames() {
        return Stream.of(
                Arguments.of(
                        "balance/blocks0.json",
                        "balance/utxos0.json",
                        "balance/balance0.json"
                ),
                Arguments.of(
                        "balance/blocks1.json",
                        "balance/utxos1.json",
                        "balance/balance1.json"
                ),
                Arguments.of(
                        "balance/blocks2.json",
                        "balance/utxos2.json",
                        "balance/balance2.json"
                )
        );
    }

    static Stream<Arguments> provideFileNames() {
        return Stream.of(
                Arguments.of("utxo/blocks0.json", "utxo/utxos0.json"),
                Arguments.of("utxo/blocks1.json", "utxo/utxos1.json"),
                Arguments.of("utxo/blocks2.json", "utxo/utxos2.json")
        );
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
