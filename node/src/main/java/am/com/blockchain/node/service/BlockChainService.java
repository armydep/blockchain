package am.com.blockchain.node.service;

import am.com.blockchain.common.api.CreateTxResponse;
import am.com.blockchain.common.balance.Balance;
import am.com.blockchain.common.balance.UTXO;
import am.com.blockchain.common.block.Block;
import am.com.blockchain.common.util.BtcOperation;
import am.com.blockchain.common.util.CoveringUTXO;
import am.com.blockchain.node.model.MempoolTransaction;
import am.com.blockchain.node.model.wallet.api.SendRequest;
import am.com.blockchain.node.repository.BlockChainRepository;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class BlockChainService {

    private final BlockChainRepository blockChainRepository;
    private final List<MempoolTransaction> mempool = Collections.synchronizedList(new ArrayList<>());
    public static final int FEE_SATOSHI = 5_000_000;

/*
    public CreateTxResponse submitTransaction(SendRequest sendRequest) {
        Optional<Balance> balanceOptional = findBalanceByAddress(sendRequest.getSender());
        if (balanceOptional.isEmpty()) {
            return CreateTxResponse.builder()
                    .submitted(false).message("Address does not contain any spendable inputs").build();
        }
        Balance balance = balanceOptional.get();
        double sumBase = BtcOperation.sumInts(sendRequest.getBtc(), sendRequest.getSat(), 0);
        double sum1 = BtcOperation.sumInts(sendRequest.getBtc(), sendRequest.getSat(), FEE_SATOSHI);
        double remaining = BtcOperation.roundDoubleToBTC(balance.getAmount() - sum1);
        if (balance.getAmount() >= sum1) {
            long ts = System.currentTimeMillis() / 1000;
            CoveringUTXO balancePair = getBalanceCoversSumForAddress(sendRequest.getSender(),
                    sendRequest.getRecipient(), sumBase, sum1, FEE_SATOSHI);
            MempoolTransaction mpTx = new MempoolTransaction(sendRequest.getSender(),
                    sendRequest.getRecipient(), sumBase, ts, balancePair.utxos(), balancePair.change());
            addTransaction(mpTx);
            String txid = "w_mp_tx_" + sendRequest.getSender() + "_" + ts;
            return CreateTxResponse.builder().txid(txid).submitted(true).totalToSend(sum1).remaining(remaining).build();
        } else {
            return CreateTxResponse.builder()
                    .submitted(false).message("Not enough balance. Fee: 0." + FEE_SATOSHI + " btc").build();
        }
    }
*/

/*
    public CoveringUTXO getBalanceCoversSumForAddress(String sender,
                                                      String recipient,
                                                      double amount,
                                                      double sum,
                                                      int fee) {
        Optional<Balance> optionalBalance = findBalanceByAddress(sender);
        if (optionalBalance.isEmpty()) {
            return new CoveringUTXO(null,
                    null, List.of(), null, null, null);
        }
        return BtcOperation.getCoveringUTXO(sender,
                recipient, optionalBalance.get().getUTXOs(), amount, sum, fee);
    }
*/

    public List<MempoolTransaction> getMempool() {
        List<MempoolTransaction> copy = new ArrayList<>(mempool.size());
        for (MempoolTransaction item : mempool) {
            copy.add(item.clone());
        }
        return copy;
    }

    public void addTransaction(MempoolTransaction transactionRequest) {
        mempool.add(transactionRequest);
    }

    public List<MempoolTransaction> getBatch(int batchSize) {
        if (batchSize <= 0 || mempool.isEmpty()) {
            return List.of();
        }
        return mempool.subList(0, Math.min(batchSize, mempool.size()));
    }

    public void submitBlock(Block block) {
        blockChainRepository.addBlock(block);
    }

    public void clearMempoolTX(List<MempoolTransaction> validMempoolTransactions) {
        for (MempoolTransaction mempoolTransaction : validMempoolTransactions) {
            mempool.remove(mempoolTransaction);
        }
    }

    public void updateUTXO(List<MempoolTransaction> validMempoolTransactions) {
        log.warn("Not implemented");
    }

    public Block getLatestBlock() {
        return blockChainRepository.getLastBlock();
    }

    public List<Block> getBlocks() {
        return blockChainRepository.getBlocks();
    }

    public List<UTXO> getUTXO() {
        return blockChainRepository.getUTXO();
    }
}
