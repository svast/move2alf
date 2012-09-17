package eu.xenit.move2alf.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TotalStringSizeValidator implements ConstraintValidator<TotalStringSize, List<String>> {

    private int maxLength;

    public void initialize(TotalStringSize constraintAnnotation) {
        this.maxLength = constraintAnnotation.max();
    }

    public boolean isValid(List<String> object, ConstraintValidatorContext constraintContext) {

        if (object == null) {
            return true;
        } else {
			String concatenatedString = "";
			for (int i = 0; i < object.size(); i++) {
				if (i == 0) {
					concatenatedString = object.get(i);
				} else {
					concatenatedString = concatenatedString + "|" + object.get(i);
				}
			}
	        return concatenatedString.length() <= maxLength;
        }
    }

}