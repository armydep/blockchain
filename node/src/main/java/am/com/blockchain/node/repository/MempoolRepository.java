package am.com.blockchain.node.repository;

import am.com.blockchain.common.tx.TX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class MempoolRepository {
    private final List<TX> mempool = Collections.synchronizedList(new ArrayList<>());

    public synchronized List<TX> getMempool() {
        return TX.copyList(mempool);
    }

    public synchronized void addTX(TX copy) {
        mempool.add(copy);
    }

    public synchronized List<TX> getMempoolBatch(int batchSize) {
        if (batchSize <= 0 || mempool.isEmpty()) {
            return List.of();
        }
        List<TX> tmp = mempool.subList(0, Math.min(batchSize, mempool.size()));
        return TX.copyList(tmp);
    }

    public synchronized void clearMempoolTX(List<TX> validMempoolTXs) {
        mempool.removeAll(validMempoolTXs);
    }

    public synchronized void clearTX(String txid) {
        TX tx = null;
        for (TX t : mempool) {
            if (t.getTxid().equals(txid)) {
                tx = t;
                break;
            }
        }
        if (tx != null) {
            mempool.remove(tx);
        }
    }

}
