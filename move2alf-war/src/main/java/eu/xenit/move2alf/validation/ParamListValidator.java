package eu.xenit.move2alf.validation;

import eu.xenit.move2alf.logic.PipelineAssemblerImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ParamListValidator implements ConstraintValidator<ParamList, List<String>> {

    private int maxKeyLength;
    private int maxValueLength;

    public void initialize(ParamList constraintAnnotation) {
        this.maxKeyLength = constraintAnnotation.maxKey();
        this.maxValueLength = constraintAnnotation.maxValue();
    }

    public boolean isValid(List<String> object, ConstraintValidatorContext constraintContext) {
    	boolean isValid = true;
        if (object != null) {
        	for (String parameter : object) {
				String[] keyValuePair = parameter.split(PipelineAssemblerImpl.SEPARATOR);
				String key = keyValuePair[0];
				String value;
				if (keyValuePair.length == 1) {
					value = "";
				} else {
					value = keyValuePair[1];
				}
				if ( key.length() > maxKeyLength || value.length() > maxValueLength ) {
					isValid = false;
				}
			}
        }
        return isValid;
    }
}