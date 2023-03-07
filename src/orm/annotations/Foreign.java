package orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import orm.Entity;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Foreign {
	Class<? extends Entity> ForeignClass();
	String ForeignAttributeName() default "";
}