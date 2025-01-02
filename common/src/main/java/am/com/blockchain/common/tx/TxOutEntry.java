package am.com.blockchain.common.tx;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class TxOutEntry {
    Double value;
    String address;
    Integer n;

    @JsonCreator
    public TxOutEntry(@JsonProperty("value") Double value,
                      @JsonProperty("address") String address,
                      @JsonProperty("n") Integer n) {
        this.value = value;
        this.address = address;
        this.n = n;
    }

    public TxOutEntry copy() {
        return new TxOutEntry(value, address, n);
    }
}
