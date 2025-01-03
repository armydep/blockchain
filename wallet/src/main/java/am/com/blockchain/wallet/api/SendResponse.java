package am.com.blockchain.wallet.api;

import lombok.Data;

@Data
public class SendResponse {
    //todo record
    String message;
    Boolean accepted;

    public SendResponse(boolean b, String msg) {
        accepted = b;
        message = msg;
    }
}
