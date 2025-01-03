package am.com.blockchain.common.balance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class UTXO implements Cloneable {
    String tx;
    Double value;
    String address;
    Integer vout;

    @JsonCreator
    public UTXO(@JsonProperty("tx") String tx,
                @JsonProperty("value") Double value,
                @JsonProperty("address") String address,
                @JsonProperty("vout") Integer vout) {
        this.tx = tx;
        this.value = value;
        this.address = address;
        this.vout = vout;
    }

    @Override
    public UTXO clone() {
        try {
            return (UTXO) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
