package org.am.com.blockchain.wallet.service.user;

import org.am.com.balance.Balance;
import org.am.com.balance.UTXO;
import org.am.com.blockchain.wallet.controller.api.SendResponse;
import org.am.com.blockchain.wallet.controller.api.WalletSend;
import org.am.com.blockchain.wallet.model.User;
import org.am.com.blockchain.wallet.repository.UserRepository;
import org.am.com.blockchain.wallet.rest.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TxService {
    private final UserRepository userRepository;
    private final RestClient restClient;
    private final String nodeUrl;

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
        String fullUrl = String.format("%s/%s/%s%s", nodeUrl, "api", "balance/", address);
        return restClient.sendGetRequest(fullUrl, Balance.class);
    }

    public SendResponse send(WalletSend request, String username) {
        if (!belongToUser(username, request.getSender())) {
            throw new NoSuchElementException("Address not belongs to user");
        }
        List<UTXO> utxoList = obtainUTXO(request.getSender());
        return new SendResponse();
    }

    private List<UTXO> obtainUTXO(String sender) {
        return null;
    }

    private boolean belongToUser(String username, String address) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && address.equals(user.get().getAddress());
    }

}
