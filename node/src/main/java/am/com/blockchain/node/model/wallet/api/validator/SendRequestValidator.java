package am.com.blockchain.node.model.wallet.api.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import am.com.blockchain.node.model.wallet.api.SendRequest;

public class SendRequestValidator implements ConstraintValidator<ValidSendRequest, SendRequest> {

    @Override
    public boolean isValid(SendRequest sendRequest, ConstraintValidatorContext context) {
        if (sendRequest.getBtc() == 0 && sendRequest.getSat() == 0) {
            String msg = "BTC and Satoshi cannot be zero";
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg)
                    .addConstraintViolation();
            return false;
        }
        if (sendRequest.getSender().equals(sendRequest.getRecipient())) {
            String msg = "From and to addresses cannot be the same";
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
            return false;
        }
        return true;
    }
}
