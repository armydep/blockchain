package org.am.com.blockchainnode.domain.wallet.api.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.am.com.blockchainnode.domain.wallet.api.SendRequest;

public class SendRequestValidator implements ConstraintValidator<ValidSendRequest, SendRequest> {

    @Override
    public boolean isValid(SendRequest sendRequest, ConstraintValidatorContext context) {
        if (sendRequest.getBtc() == 0 && sendRequest.getSat() == 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Satoshi cannot be zero when BTC is zero")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
