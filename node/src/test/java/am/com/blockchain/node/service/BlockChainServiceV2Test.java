package am.com.blockchain.node.service;

import am.com.blockchain.common.balance.Balance;
import am.com.blockchain.common.balance.UTXO;
import am.com.blockchain.common.block.Block;
import am.com.blockchain.common.block.Header;
import am.com.blockchain.common.tx.TX;
import am.com.blockchain.common.tx.TxInEntry;
import am.com.blockchain.common.tx.TxOutEntry;
import am.com.blockchain.node.TestUtils;
import am.com.blockchain.node.repository.BlockChainRepository;
import lombok.SneakyThrows;
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

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BlockChainServiceV2Test {

    private AutoCloseable closeable;

    @InjectMocks
    private BlockChainServiceV2 service;

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

    @Test
    public void testGetEmptyUTXO() {
        when(repository.getBlocks()).thenReturn(List.of());
        List<UTXO> utxos = service.getUTXO();
        assertEquals(List.of(), utxos);
    }

    @Test
    public void testGetUTXOSingle() {
        TX tx = generateCoinbaseTX("tx1", "addr1", 10.0, 0);
        List<TX> txs = List.of(tx);
        Header header = mock(Header.class);
        Block block = new Block(header, txs);
        List<Block> blocks = List.of(block);
        when(repository.getBlocks()).thenReturn(blocks);
        UTXO utxo = new UTXO("tx1", 10.0, "addr1", 0);
        List<UTXO> expected = List.of(utxo);
        List<UTXO> utxos = service.getUTXO();
        assertEquals(expected, utxos);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideBlockUtxoFileNames")
    public void testGetUTXO(String blocksFile, String utxosFile) {
        List<Block> blocks = TestUtils.loadBlocks(blocksFile);
        when(repository.getBlocks()).thenReturn(blocks);
        List<UTXO> expected = TestUtils.loadUTXOs(utxosFile);
        List<UTXO> utxos = service.getUTXO();
        assertEquals(expected, utxos);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideUtxoBalancesFileNames")
    void getBalances(String utxosFile, String balancesFile) {
        BlockChainServiceV2 serviceSpy = Mockito.spy(service);
        List<UTXO> utxos = TestUtils.loadUTXOs(utxosFile);
        doReturn(utxos).when(serviceSpy).getUTXO();
        List<Balance> expected = TestUtils.loadBalances(balancesFile);
        List<Balance> balances = serviceSpy.getBalances();
        assertEquals(expected, balances);
    }

    @Test
    void findBalanceByAddress() {
    }

    private TX generateCoinbaseTX(String txid, String addr, Double amount, int vout) {
        TxInEntry txin = new TxInEntry(txid, 0, "yes", null);
        List<TxInEntry> txins = List.of(txin);
        TxOutEntry txout = new TxOutEntry(amount, addr, vout);
        List<TxOutEntry> txouts = List.of(txout);
        return new TX(txid, txins, txouts);
    }

    static Stream<Arguments> provideBlockUtxoFileNames() {
        return Stream.of(
                Arguments.of("v2/block-1.json", "v2/utxo-1.json"),
                Arguments.of("v2/block-2.json", "v2/utxo-2.json"),
                Arguments.of("v2/block-3.json", "v2/utxo-3.json")
        );
    }

    static Stream<Arguments> provideUtxoBalancesFileNames() {
        return Stream.of(
                Arguments.of("v2/utxo-1.json", "v2/balance-1.json"),
                Arguments.of("v2/utxo-2.json", "v2/balance-2.json"),
                Arguments.of("v2/utxo-3.json", "v2/balance-3.json")
        );
    }

}