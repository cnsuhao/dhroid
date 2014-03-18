package net.duohuo.dhroiddemos.ioc;

import net.duohuo.dhroid.db.DhDB;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.Instance.InstanceScope;
import net.duohuo.dhroid.ioc.annotation.Inject;
import android.content.Context;
import android.util.Log;
import net.duohuo.dhroid.ioc.InjectFields;
import net.duohuo.dhroid.ioc.IocContainer;
/**
 * 这个类的构造函数需要Context IOC容器默认会将application的context传入<br/>
 * 同时测试作用域   InstanceScope.SCOPE_PROTOTYPE<br/>
 * 同时测试类的相互依赖问题,依赖于其他可以实现InjectFields接口
 * @author duohuo-jinghao
 */
public class TestDateHelper implements InjectFields{

	public static int count=0;
	
	public String name;
	@Inject(tag="manager2")
	public TestManager manager;
	
	public TestDateHelper(Context context){
		super();
		count++;
		//这里不要要调用注入工具
		//InjectUtil.inject(this);
		//这时候manager为空
		
		
		//构造完成后才会有值
		//这里两个类相互依赖,不然可以手动调用InjectUtil.inject(this);或share.get(xxx);
		if(manager==null){
			
		}
		//它们没有相互依赖关系可以在在编码获取
		IDialog dialoger=IocContainer.getShare().get(IDialog.class);
		
		
	}
	/* (non-Javadoc)
	 * @see net.duohuo.dhroid.ioc.InjectFields#injected()
	 */
	@Override
	public void injected() {
		//这时候注入的属性已经有值了
		if(	manager.helper!=null){
			Log.v("DH-INFO", "TestDateHelper这个对象被配置为InstanceScope.SCOPE_PROTOTYPE 每次获取时都会初始化   manager.helper!=null");
		}
	}

	public String getName() {
		return name+"我是第"+count+"个对象我依赖"+manager.getName();
	}

	public void setName(String name) {
		this.name = name;
	}


	
}
