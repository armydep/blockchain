package am.com.blockchain.node.blockchainnode.controller;

import am.com.blockchain.node.controller.NodeController;
import am.com.blockchain.common.block.Block;
import am.com.blockchain.node.service.BlockChainService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NodeControllerTest {

    @InjectMocks
    private NodeController nodeController;

    //private MockMvc mockMvc;
    private AutoCloseable closeable;

    @Mock
    private BlockChainService blockChainService;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        //mockMvc = MockMvcBuilders.standaloneSetup(nodeController).build();
        MockMvcBuilders.standaloneSetup(nodeController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getBlocks_whenValidRequest_shouldReturnBlockListWithStatusOk() {
        ResponseEntity<List<Block>> response = nodeController.getBlocks();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
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

    private String readJsonFileFromResources(String fileName) throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + fileName);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}