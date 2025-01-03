package am.com.blockchain.common.tx;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class TxOutEntry {
    //todo record
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

    public static List<TxOutEntry> copyList(List<TxOutEntry> vout) {
        List<TxOutEntry> copy = new ArrayList<>();
        for (TxOutEntry out : vout) {
            copy.add(out.copy());
        }
        return copy;
    }

    public TxOutEntry copy() {
        return new TxOutEntry(value, address, n);
    }
}
