package org.am.com.blockchainnode.service;

import org.am.com.blockchainnode.domain.MempoolTransaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MempoolService {

    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final List<MempoolTransaction> mempool =
            Collections.synchronizedList(new ArrayList<>());

    public int addTransaction(MempoolTransaction transactionRequest) {
        mempool.add(transactionRequest);
        return counter.incrementAndGet();
    }

    public List<MempoolTransaction> getMempool() {
        synchronized (mempool) {
            List<MempoolTransaction> copy = new ArrayList<>(mempool.size());
            for (MempoolTransaction item : mempool) {
                copy.add(item.clone());
            }
            return copy;
        }
    }
}
