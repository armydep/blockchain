package am.com.blockchain.node.model.wallet.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import am.com.blockchain.node.model.wallet.api.validator.ValidSendRequest;

@Data
@ValidSendRequest
public class SendRequest {
    @NotEmpty(message = "Sender address cannot be empty")
    private String sender;
    @NotEmpty(message = "Receiver address cannot be empty")
    private String recipient;
    @Min(value = 0, message = "BTC amount must be at least 0")
    private Integer btc;
    @Min(value = 0, message = "Sat amount must be at least 0")
    @Max(value = 99999999, message = "Sat amount must be at less than 100M")
    private Integer sat;
}
