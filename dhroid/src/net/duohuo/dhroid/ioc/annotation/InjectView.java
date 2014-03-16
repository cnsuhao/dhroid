package net.duohuo.dhroid.ioc.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 
 *click 事件  method(View v)  或者method();
 *
 *longClick 事件 public boolean method(View v)或者method();
 * 
 *itemClick 事件 public void method(AdapterView<?> parent, View view,int position, long id) 或者method(int position, long id)
 * 
 *itemLongClick 事件  public boolean method(AdapterView<?> parent, View view,int position, long id)
 * 
 * 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface InjectView {
	
	//view 的id
	public int id() default 0;	
	
	//view 的 layout
	public int layout() default 0;
	
	//view 在其他view view需要在 类中注入
	public String inView() default "";
	
	//点击事件
	public String click() default "";
	
	//长按
	public String longClick() default "";
	
	//adapterview 的单个item的点击事件
	public String itemClick() default "";
	
	//adapterview 的单个对象的长点击
	public String itemLongClick() default "";
	

}
