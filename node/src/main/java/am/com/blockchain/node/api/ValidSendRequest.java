package am.com.blockchain.node.api;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SendRequestValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSendRequest {
    String message() default "Invalid send request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
