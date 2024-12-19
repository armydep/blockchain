package org.am.com.blockchainnode.controller;

import org.am.com.blockchainnode.model.block.Block;
import org.am.com.blockchainnode.service.BlockChainService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NodeControllerTest {

    @InjectMocks
    private NodeController nodeController;

    private MockMvc mockMvc;
    private AutoCloseable closeable;

    @Mock
    private BlockChainService blockChainService;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(nodeController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getBlocks_whenValidRequest_shouldReturnBlockListWithStatusOk() {
        ResponseEntity<List<Block>> response = nodeController.getBlocks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(2, response.getBody().size());
//           verify(genesisLoadConfig, times(1)).getJsonData();
    }

    @Test
    public void testGetBlocks() throws Exception {
        // List<Block> mockBlocks = getBlocks();
//        when(blockChainService.getBlocks()).thenReturn(mockBlocks);

        ResponseEntity<List<Block>> response = nodeController.getBlocks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        //  assertEquals(mockBlocks, response.getBody());
        // verify(blockChainService).getBlocks();
    }

//    @Test
//    public void testGetBlocks() throws Exception {

    /// /        when(genesisLoadConfig.getJsonData()).thenReturn(getBlocks());
//        String expectedJson = readJsonFileFromResources("genesis.json");
//        mockMvc.perform(get("/node/api/node"))
//                .andExpect(status().isOk());
//        //.andExpect(content().json(expectedJson));
//    }
/*
    private static ArrayList<Block> getBlocks() {
        ArrayList<Block> blocks = new ArrayList<>();

        Block block1 = new Block();
        block1.setIndex(1L);
        block1.setHash("123456");
        block1.setPreviousHash("000000");
        block1.setData("Block 1 data: ");

        TxInEntry txInEntry = new TxInEntry("t11", 0, null);
        TxOutEntry txOutEntry = new TxOutEntry(2, "ad123", 4);
        List<TxInEntry> txInEntries = new ArrayList<>();
        txInEntries.add(txInEntry);
        List<TxOutEntry> txOutEntries = new ArrayList<>();
        txOutEntries.add(txOutEntry);
        TX tx = new TX("t10000", txInEntries, txOutEntries);
        List<TX> txList = new ArrayList<>();
        txList.add(tx);
        block1.setTx(txList);

        blocks.add(block1);
        return blocks;
    }
*/

    private String readJsonFileFromResources(String fileName) throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + fileName);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}