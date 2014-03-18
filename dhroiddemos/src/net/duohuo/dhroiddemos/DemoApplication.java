package net.duohuo.dhroiddemos;

import net.duohuo.dhroid.Const;
import net.duohuo.dhroid.adapter.ValueFix;
import net.duohuo.dhroid.db.DhDB;
import net.duohuo.dhroid.dialog.DialogImpl;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.Instance.PerpareAction;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.ioc.Instance.InstanceScope;
import net.duohuo.dhroiddemos.ioc.TestDateHelper;
import net.duohuo.dhroiddemos.ioc.TestManagerMM;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;

public class DemoApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		
		Const.netadapter_page_no = "p";
		Const.netadapter_step = "step";
		Const.response_total = "totalRows";
		Const.response_data = "data";
		Const.netadapter_step_default = 7;
		Const.netadapter_json_timeline="pubdate";
		Const.DATABASE_VERSION = 20;
		Const.net_pool_size=30;
		Const.net_error_try=true;
		
		//IOC的初始化
		IocContainer.getShare().initApplication(this);
		//IOC配置下面两个是必须配置的
		//配置对话框对象,这是接口配置写法
		//项目中可以自己写对话框对象,然后在这进行配置,这里使用的是提供的默认配置
		IocContainer.getShare().bind(DialogImpl.class).to(IDialog.class)
		//这是单例
		.scope(InstanceScope.SCOPE_SINGLETON);
		//配置ValueFix对象基本每个项目都有自己的实现
		IocContainer.getShare().bind(DemoValueFixer.class)
		.to(ValueFix.class)
		.scope(InstanceScope.SCOPE_SINGLETON);
		
		//这是使用名字配置的方法,这样可以通过名字获取对象,使用不多
		IocContainer.getShare().bind(TestManagerMM.class)
		.name("testmm")
		.scope(InstanceScope.SCOPE_SINGLETON);
		
		//这个测试作用域InstanceScope.SCOPE_PROTOTYPE
		IocContainer.getShare().bind(TestDateHelper.class)
		.to(TestDateHelper.class)
		//这种作用域获取的每个对象都是独立的对象
		.scope(InstanceScope.SCOPE_PROTOTYPE).perpare(new PerpareAction() {
			//同时测试初始化后的回调
			@Override
			public void perpare(Object obj) {
				//在初始化完成后回调,当然在InjectFields接口中injected也有回调
				TestDateHelper helper=(TestDateHelper) obj;
				helper.setName("我是在初始化是提供名字的");
			}
		});
		//IOC中基本bind的类对象和to的类想象如果是同一个且是单例或通过Tag获取是不需要配置的
		
		
		
		
		
		
		
		
		ImageLoaderConfiguration	 imageconfig = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(1500000)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.build();
		ImageLoader.getInstance().init(imageconfig);
		//数据库初始化
		DhDB db=IocContainer.getShare().get(DhDB.class);
		db.init("dhdbname", Const.DATABASE_VERSION);
		
	}
	
}
