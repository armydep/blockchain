package org.am.com.blockchain.wallet.service.user;

import org.am.com.api.CreateTxResponse;
import org.am.com.balance.Balance;
import org.am.com.balance.UTXO;
import org.am.com.blockchain.wallet.controller.api.SendResponse;
import org.am.com.blockchain.wallet.controller.api.WalletSend;
import org.am.com.blockchain.wallet.model.User;
import org.am.com.blockchain.wallet.repository.UserRepository;
import org.am.com.blockchain.wallet.rest.RestClient;
import org.am.com.tx.TX;
import org.am.com.tx.TXBuilder;
import org.am.com.util.BtcOperation;
import org.am.com.util.CoveringUTXO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TxService {
    private final UserRepository userRepository;
    private final RestClient restClient;
    private final String nodeUrl;
    public static final int FEE_SATOSHI = 15_000_000;
    private final AtomicInteger count = new AtomicInteger(0);


    public TxService(UserRepository userRepository,
                     RestClient restClient,
                     @Value("${node.url}") String nodeUrl) {
        this.userRepository = userRepository;
        this.restClient = restClient;
        this.nodeUrl = nodeUrl;
    }

    public Balance getBalance(String address, String username) {
        if (!belongToUser(username, address)) {
            throw new NoSuchElementException("Address does not belongs to user");
        }
        String fullUrl = String.format("%s/%s/%s/%s", nodeUrl, "api", "balance", address);
        return restClient.sendGetRequest(fullUrl, Balance.class);
    }

    public CreateTxResponse send(WalletSend request, String username) {
        if (!belongToUser(username, request.getSender())) {
            throw new NoSuchElementException("Address not belongs to user");
        }
        //1. amount + fee > balance ?
        Balance balance = getBalance(request.getSender(), username);
        Double sendWithFee = BtcOperation.sumInts(request.getBtc(), request.getSat(), FEE_SATOSHI);
        if (sendWithFee <= balance.getAmount()) {
            //2.
            List<UTXO> utxoList = balance.getUTXOs();
            CoveringUTXO coveringUTXO = BtcOperation
                    .getCoveringUTXO(request.getSender(), request.getRecipient(), utxoList, sendWithFee);
            TX tx = buildSignedTX(coveringUTXO);
            String fullUrl = String.format("%s/%s/%s/%s", nodeUrl, "api", "v2", "send");
            return restClient.sendPostRequest(fullUrl, tx, CreateTxResponse.class);
            //return restClient.sendGetRequest(fullUrl, Balance.class);
//            return new SendResponse(true, null);
        } else {
            return new
                    CreateTxResponse(null,
                    "Not enough balance: " + balance.getAmount() + ". fee: " + FEE_SATOSHI,
                    false,
                    null,
                    null);
        }
    }

    private TX buildSignedTX(CoveringUTXO coveringUTXO) {
        String txid = "wallet_sa_txid-" + count.incrementAndGet();
        return TXBuilder.generateTX(coveringUTXO.sender(),
                coveringUTXO.recipient(),
                coveringUTXO.utxos(),
                coveringUTXO.amount(),
                coveringUTXO.change(),
                txid);
    }

    private boolean isEnoughBalanceToSend(Double amount, Integer btc, Integer sat, int feeSatoshi) {
        return false;
    }
    /*
            Balance balance = balanceOptional.get();
            double sum = BtcOperation.sumInts(sendRequest.getBtc(), sendRequest.getSat(), FEE_SATOSHI);
        double remaining = BtcOperation.roundDoubleToBTC(balance.getAmount() - sum);
        if (balance.getAmount() >= sum) {
            long ts = System.currentTimeMillis() / 1000;
            Pair<List<UTXO>, Double> balancePair = getBalanceCoversSumForAddress(sendRequest.getSender(), sum);
            MempoolTransaction mpTx = new MempoolTransaction(sendRequest.getSender(),
                    sendRequest.getRecipient(), sum, ts, balancePair.getLeft(), balancePair.getRight());
            addTransaction(mpTx);
            String txid = "w_mp_tx_" + sendRequest.getSender() + "_" + ts;
            return CreateTxResponse.builder().txid(txid).submitted(true).totalToSend(sum).remaining(remaining).build();
        } else {
            return CreateTxResponse.builder()
                    .submitted(false).message("Not enough balance. Fee: 0." + FEE_SATOSHI + " btc").build();
        }
     */

/*
    private List<UTXO> obtainUTXO(String sender) {
        return null;
    }
*/

    private boolean belongToUser(String username, String address) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && address.equals(user.get().getAddress());
    }

}
