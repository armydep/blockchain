package org.am.com.blockchainnode.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateTxResponse {
    String txid;
    String message;
    boolean submitted = false;
    float totalToSend;
    float remaining;
}
