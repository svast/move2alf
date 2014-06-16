package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.ConfigurableObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassInfo {
    String classId();
    String category() default ConfigurableObject.CAT_DEFAULT;
    String description() default "";
}
