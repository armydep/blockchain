package am.com.blockchain.common.tx;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Value
public class TX {
    String txid;
    List<TxInEntry> vin;
    List<TxOutEntry> vout;

    @JsonCreator
    public TX(@JsonProperty("txid") String txid,
              @JsonProperty("vin") List<TxInEntry> vin,
              @JsonProperty("vout") List<TxOutEntry> vout) {
        this.txid = txid;
        this.vin = Collections.unmodifiableList(vin);
        this.vout = Collections.unmodifiableList(vout);
    }

    public static TX generateCoinBaseTX(String txid, String minerAddress, Double reward) {
        TxInEntry txInEntry = new TxInEntry("", 0, "true", null);
        TxOutEntry txOutEntry = new TxOutEntry(reward, minerAddress, 0);
        return new TX(txid, List.of(txInEntry), List.of(txOutEntry));
    }

    public static List<TX> copyList(List<TX> txso) {
        List<TX> copy = new ArrayList<>();
        for (TX tx : txso) {
            copy.add(tx.copy());
        }
        return copy;
    }

    public TX copy() {
        List<TxInEntry> tin = null;
        List<TxOutEntry> tout = null;
        if (vin != null) {
            tin = new ArrayList<>();
            for (TxInEntry i : vin) {
                tin.add(i.copy());
            }
        }
        if (vout != null) {
            tout = new ArrayList<>();
            for (TxOutEntry o : vout) {
                tout.add(o.copy());
            }
        }
        return new TX(txid, tin, tout);
    }
}
