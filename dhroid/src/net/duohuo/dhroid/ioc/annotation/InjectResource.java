package net.duohuo.dhroid.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 *
 * @author duohuo-jinghao 
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface InjectResource {

	public int drawable() default 0;
	public int string() default 0;
	public int color() default 0;
	public int dimen() default 0;
	
	
}
