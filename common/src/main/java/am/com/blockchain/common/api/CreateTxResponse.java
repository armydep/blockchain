package am.com.blockchain.common.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateTxResponse {
    //todo remove txid. null always
    private String txid;
    private String message;
    private Boolean submitted;
    private Double totalToSend;
    private Double remaining;

    @JsonCreator
    public CreateTxResponse(@JsonProperty("txid") String txid,
                            @JsonProperty("message") String message,
                            @JsonProperty("submitted") Boolean submitted,
                            @JsonProperty("totalToSend") Double totalToSend,
                            @JsonProperty("remaining") Double remaining) {
        this.txid = txid;
        this.message = message;
        this.submitted = submitted;
        this.totalToSend = totalToSend;
        this.remaining = remaining;
    }
}
