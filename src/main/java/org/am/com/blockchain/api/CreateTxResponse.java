package org.am.com.blockchain.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateTxResponse {
    String txid;
    String message;
    boolean submitted;
    double totalToSend;
    double remaining;
}
