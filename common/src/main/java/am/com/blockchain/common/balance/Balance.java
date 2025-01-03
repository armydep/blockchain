package am.com.blockchain.common.balance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import am.com.blockchain.common.util.BtcOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
public class Balance {
    @Getter
    private final String address;
    private List<UTXO> utxos;
    @Getter
    private Double amount;

    public Balance() {
        this.address = "";
    }

    @JsonCreator
    public Balance(@JsonProperty("address") String address,
                   @JsonProperty("utxos") List<UTXO> utxos) {
        this.address = address;
        this.utxos = utxos;
        setAmount();
    }

    public Balance(UTXO utxo) {
        utxos = new ArrayList<>(List.of(utxo.clone()));
        address = utxo.getAddress();
        setAmount();
    }

    //todo Bug fix
    //btc operation
    public void setAmount() {
        if (utxos != null && !utxos.isEmpty()) {
            amount = utxos.stream().mapToDouble(UTXO::getValue).sum();
            amount = BtcOperation.roundDoubleToBTC(amount);
        } else {
            amount = null;
        }
    }

    public void addUTXO(UTXO utxo) {
        if (utxos == null) {
            utxos = new ArrayList<>();
        }
        utxos.add(utxo);
        setAmount();
    }

    public List<UTXO> getUTXOs() {
        List<UTXO> destination = new ArrayList<>(Collections.nCopies(utxos.size(), null));
        Collections.copy(destination, utxos);
        return destination;
    }
}
