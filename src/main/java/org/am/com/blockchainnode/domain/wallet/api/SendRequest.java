package org.am.com.blockchainnode.domain.wallet.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.am.com.blockchainnode.domain.wallet.api.validator.ValidSendRequest;

@Data
@ValidSendRequest
public class SendRequest {
    @NotEmpty(message = "Sender address cannot be empty")
    private String from;
    @NotEmpty(message = "Receiver address cannot be empty")
    private String to;
    @Min(value = 0, message = "BTC amount must be at least 0")
    private int btc;
    @Min(value = 0, message = "Sat amount must be at least 0")
    private int sat;
}
