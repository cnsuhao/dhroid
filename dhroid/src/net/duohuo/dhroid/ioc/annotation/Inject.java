package net.duohuo.dhroid.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 注入 ioc容器中的对象
 * @author duohuo-jinghao
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface Inject {
	
	public String name() default "";
	
	public String tag() default "";
}
