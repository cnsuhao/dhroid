package net.duohuo.dhroid.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 支持String,InputStream和文件类型<br/>
 * 如果是文件类型会先将文件拷贝出来
 * @author Administrator
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface InjectAssert {
	
	public String path() default "";
	//文件注入时是异步,文件注入完成后会回调这个方法
	public String fileInjected() default "";
}
