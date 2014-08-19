/**
 * 
 */
package net.duohuo.dhroid.ioc.annotation;

/**
 * 表明需要对属性进行注入<br/>
 * 注入时机是在类构建完成后注入<br/>
 * 所以在构造方法中别使用需要注入的属性(可以进行手动注入,或手动调用InjectUtil进行注入,这种情况请保证类不会相互依赖)	
 * @author duohuo-jinghao
 * @date 2014-3-17
 */
public interface FieldsInjectable {
	//注入完成
	public void injected();
	
}
