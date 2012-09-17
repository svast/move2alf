package eu.xenit.move2alf.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target( { FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = TotalStringSizeValidator.class)
public @interface TotalStringSize {

    String message() default "{eu.xenit.move2alf.constraints.totalstringsize}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    int max();

}