package am.com.blockchain.node.blockchain.service;

import am.com.blockchain.node.service.BlockChainService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import am.com.blockchain.common.api.CreateTxResponse;
import am.com.blockchain.common.block.Block;
import am.com.blockchain.common.balance.UTXO;
import am.com.blockchain.common.balance.Balance;
import am.com.blockchain.node.model.wallet.api.SendRequest;
import am.com.blockchain.node.repository.BlockChainRepository;
import am.com.blockchain.common.util.BtcOperation;
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

import static am.com.blockchain.node.service.BlockChainService.FEE_SATOSHI;
import static org.junit.jupiter.api.Assertions.*;
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


    /*
    post condition:
        response.submitted
        response.remaining
        response.totalToSend
        response.txid
        response.message is null

        Mempool

        cases:
            1.1 no such address among
            1.2 address exists but not enough balances on address
            2. enough addresses: mpTx added to mempool
     */
    @Test
    public void testSubmitEmptyTransaction() {
        BlockChainService serviceSpy = Mockito.spy(service);
        SendRequest sendRequest = new SendRequest();
        Optional<Balance> balanceOptional = Optional.empty();
//        doReturn(balanceOptional).when(serviceSpy).findBalanceByAddress(anyString());
//        CreateTxResponse response = serviceSpy.submitTransaction(sendRequest);
//        assertNotNull(response);
//        assertFalse(response.getSubmitted());
    }

    @Test
    public void testSubmitTransactionNoEnoughBalance() {
        BlockChainService serviceSpy = Mockito.spy(service);
        String address = "addr";
        UTXO utxo = new UTXO("tx", 1.0, address, 1);
        Balance balance = new Balance(address, List.of(utxo));
        Optional<Balance> balanceOptional = Optional.of(balance);
//        doReturn(balanceOptional).when(serviceSpy).findBalanceByAddress(anyString());
//        SendRequest sendRequest = new SendRequest();
//        sendRequest.setSender(address);
//        sendRequest.setBtc(5);
//        sendRequest.setSat(0);
//        CreateTxResponse response = serviceSpy.submitTransaction(sendRequest);
//        assertNotNull(response);
//        assertFalse(response.getSubmitted());
    }

    //fees > 0
    @Test
    public void testSubmitTransactionNotEnoughForFee() {
        BlockChainService serviceSpy = Mockito.spy(service);
        String address = "addr";
        UTXO utxo = new UTXO("tx", 1.0, address, 1);
        Balance balance = new Balance(address, List.of(utxo));
        Optional<Balance> balanceOptional = Optional.of(balance);
//        doReturn(balanceOptional).when(serviceSpy).findBalanceByAddress(anyString());
//        SendRequest sendRequest = new SendRequest();
//        sendRequest.setSender(address);
//        sendRequest.setBtc(1);
//        sendRequest.setSat(0);
//        CreateTxResponse response = serviceSpy.submitTransaction(sendRequest);
//        assertNotNull(response);
//        assertFalse(response.getSubmitted());
    }

    //no change
    @Test
    public void testSubmitTransactionNoChange() {
        BlockChainService serviceSpy = Mockito.spy(service);
        String address = "addr";
        int spend = 1;
        double totalToSend = BtcOperation.sumInts(spend, FEE_SATOSHI, 0);
        UTXO utxo = new UTXO("tx", totalToSend, address, 1);
        Balance balance = new Balance(address, List.of(utxo));
        Optional<Balance> balanceOptional = Optional.of(balance);
//        doReturn(balanceOptional).when(serviceSpy).findBalanceByAddress(anyString());
//        SendRequest sendRequest = new SendRequest();
//        sendRequest.setSender(address);
//        sendRequest.setBtc(spend);
//        sendRequest.setSat(0);
//        CreateTxResponse response = serviceSpy.submitTransaction(sendRequest);
//        assertTrue(response.getSubmitted());
//        assertEquals(totalToSend, response.getTotalToSend());
        //assertEquals(0, response.getRemaining());
    }

    //change > 0
    @Test
    public void testSubmitTransactionWithChange() {
        BlockChainService serviceSpy = Mockito.spy(service);
        String address = "addr";
        int spend = 3;
        double totalOnBalance = BtcOperation.sumInts(spend + 1, FEE_SATOSHI, 0);
//        UTXO utxo = new UTXO("tx", totalOnBalance, address, 1);
//        Balance balance = new Balance(address, List.of(utxo));
//        Optional<Balance> balanceOptional = Optional.of(balance);
//        doReturn(balanceOptional).when(serviceSpy).findBalanceByAddress(anyString());
//        SendRequest sendRequest = new SendRequest();
//        sendRequest.setSender(address);
//        sendRequest.setBtc(spend);
//        sendRequest.setSat(0);
//        CreateTxResponse response = serviceSpy.submitTransaction(sendRequest);
//        assertTrue(response.getSubmitted());
//        assertEquals(BtcOperation.sumInts(spend, FEE_SATOSHI, 0), response.getTotalToSend());
        //assertEquals(1.0, response.getRemaining());
    }

    @ParameterizedTest
    @MethodSource("provideBlocks")
    public void testGetEmptyUTXO(List<Block> blocks, List<UTXO> expectedUTXOs) {
        when(repository.getBlocks()).thenReturn(blocks);
        //List<UTXO> utxos = repository.getUTXO();
        //assertEquals(expectedUTXOs, utxos);
    }

    @ParameterizedTest
    @MethodSource("provideFileNames")
    public void testGetUTXO(String blocksFile, String utxoFile) throws IOException {
        List<Block> blocks = loadBlocks(blocksFile);
        List<UTXO> expectedUTXOs = loadUTXOs(utxoFile);
        when(repository.getBlocks()).thenReturn(blocks);
        //List<UTXO> utxos = repository.getUTXO();
        //assertEquals(expectedUTXOs, utxos);
    }

    @ParameterizedTest
    @MethodSource("provideBalanceFileNames")
    public void testGetBalances(String blocksFile, String utxoFile, String balanceFile) throws IOException {
        List<Balance> expectedBalances = loadBalances(balanceFile);
        List<Block> blocks = loadBlocks(blocksFile);
        List<UTXO> utxos = loadUTXOs(utxoFile);
        //when(service.getUTXO()).thenReturn(utxos);
        //when(service.getBlocks()).thenReturn(blocks);
        //List<Balance> balances = service.getBalances();
        //assertEquals(expectedBalances, balances);
    }

    @ParameterizedTest
    @MethodSource("provideBalanceFileNames")
    public void testGetBalancesSpy(String blocksFile, String utxoFile, String balanceFile) throws IOException {
        List<Balance> expectedBalances = loadBalances(balanceFile);
        List<UTXO> mockUTXOs = loadUTXOs(utxoFile);
        BlockChainService serviceSpy = Mockito.spy(service);
        //doReturn(mockUTXOs).when(serviceSpy).getUTXO();
        //List<Balance> balances = serviceSpy.getBalances();
        //assertEquals(expectedBalances, balances);
    }

    @Test
    void testFindBalanceByAddress_NotFound() throws IOException {
        BlockChainService serviceSpy = Mockito.spy(service);
        List<Balance> balances = loadBalances("balance/balance0.json");
        //doReturn(balances).when(serviceSpy).getBalances();
        String addressToSearch = "abc";
        //Optional<Balance> result = serviceSpy.findBalanceByAddress(addressToSearch);
        //assertEquals(Optional.empty(), result);
        //verify(serviceSpy).getBalances();
    }

    @Test
    void testFindBalanceByAddress() throws IOException {
        BlockChainService serviceSpy = Mockito.spy(service);
        List<Balance> balances = loadBalances("balance/balance1.json");
        //doReturn(balances).when(serviceSpy).getBalances();
        String addressToSearch = "15ZbsZw8zhSBToqBkAvdQzBjWeAg43htBf";
        //Optional<Balance> result = serviceSpy.findBalanceByAddress(addressToSearch);
        //assertEquals(Optional.of(balances.get(1)), result);
        //verify(serviceSpy).getBalances();
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
                )
                ,
                Arguments.of(
                        "balance/blocks1.json",
                        "balance/utxos1.json",
                        "balance/balance1.json"
                )
                ,
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
