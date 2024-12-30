package am.com.blockchain.wallet.service.tx;

import am.com.blockchain.common.api.CreateTxResponse;
import am.com.blockchain.common.balance.Balance;
import am.com.blockchain.common.balance.UTXO;
import am.com.blockchain.wallet.controller.api.WalletSend;
import am.com.blockchain.wallet.model.User;
import am.com.blockchain.wallet.repository.UserRepository;
import am.com.blockchain.wallet.rest.RestClient;
import am.com.blockchain.common.exceptions.SignatureException;
import am.com.blockchain.common.tx.ScriptSig;
import am.com.blockchain.common.tx.TX;
import am.com.blockchain.common.tx.TXBuilder;
import am.com.blockchain.common.user.Key;
import am.com.blockchain.common.util.BtcOperation;
import am.com.blockchain.common.util.CoveringUTXO;
import am.com.blockchain.common.util.SignatureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    public CreateTxResponse send(WalletSend request, String username) throws SignatureException {
        if (!belongToUser(username, request.getSender())) {
            throw new NoSuchElementException("Address not belongs to user");
        }
        //1. amount + fee > balance ?
        Balance balance = getBalance(request.getSender(), username);
        Double sendWithFee = BtcOperation.sumInts(request.getBtc(), request.getSat(), FEE_SATOSHI);
        Double sendOrig = BtcOperation.sumInts(request.getBtc(), request.getSat(), 0);
        if (sendWithFee <= balance.getAmount()) {
            //2.
            List<UTXO> utxoList = balance.getUTXOs();
            CoveringUTXO coveringUTXO = BtcOperation.getCoveringUTXO(
                    request.getSender(),
                    request.getRecipient(),
                    utxoList,
                    sendOrig,
                    sendWithFee,
                    FEE_SATOSHI);
            Key keys = getUserPrivateKey(username);
            TX tx = buildSignedTX(coveringUTXO, keys);
            String fullUrl = String.format("%s/%s/%s/%s", nodeUrl, "api", "v2", "send");
            return restClient.sendPostRequest(fullUrl, tx, CreateTxResponse.class);
        } else {
            return new
                    CreateTxResponse(null,
                    "Not enough balance: " + balance.getAmount() + ". fee: " + FEE_SATOSHI,
                    false,
                    null,
                    null);
        }
    }

    private Key getUserPrivateKey(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Key key = new Key();
        key.setPrivateKey(user.get().getPrivateKey());
        key.setPublicKey(user.get().getPublicKey());
        return key;
    }

    private TX buildSignedTX(CoveringUTXO coveringUTXO, Key keys) throws SignatureException {
        String txid = "wallet_sa_txid-" + count.incrementAndGet() + "_" + new Date();
        TX tx = TXBuilder.buildUnsignedTX(coveringUTXO.sender(),
                coveringUTXO.recipient(),
                coveringUTXO.utxos(),
                coveringUTXO.amount(),
                coveringUTXO.change(),
                txid);
        String txStr = tx.toString();
        String txSignature = SignatureUtil.sign(txStr, keys.getPrivateKey());
        ScriptSig scriptSig = new ScriptSig(txSignature, keys.getPublicKey());
        return TXBuilder.buildSignedTX(tx, scriptSig);
    }

    private boolean belongToUser(String username, String address) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && address.equals(user.get().getAddress());
    }

}
