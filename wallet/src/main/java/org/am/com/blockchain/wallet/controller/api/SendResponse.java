package org.am.com.blockchain.wallet.controller.api;

import lombok.Data;

@Data
public class SendResponse {
    String message;
    Boolean accepted;

    public SendResponse(boolean b, String msg) {
        accepted = b;
        message = msg;
    }
}
