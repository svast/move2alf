package eu.xenit.move2alf.web.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import eu.xenit.move2alf.web.dto.JobConfig;

public class JobValidator implements Validator{

	public boolean supports(Class aClass) {
		return JobConfig.class.equals(aClass);
	}
 
	public void validate(Object obj, Errors errors) {
		JobConfig jobConfig = (JobConfig) obj;
 
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required", "Required field");
 
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "field.required", "Required field");
	
	}
	
}
