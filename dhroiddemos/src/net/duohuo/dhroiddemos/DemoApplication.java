package net.duohuo.dhroiddemos;

import net.duohuo.dhroid.Const;
import net.duohuo.dhroid.adapter.ValueFix;
import net.duohuo.dhroid.db.DhDB;
import net.duohuo.dhroid.dialog.DialogImpl;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.ioc.Instance.InstanceScope;

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
		Const.netadapter_step_default = 10;
		Const.netadapter_json_timeline="pubdate";
		Const.DATABASE_VERSION = 20;
		Const.net_pool_size=30;
		Const.net_error_try=true;
		IocContainer.getShare().initApplication(this);
		IocContainer.getShare().bind(DialogImpl.class).to(IDialog.class)
		.scope(InstanceScope.SCOPE_SINGLETON);
		IocContainer.getShare().bind(DemoValueFixer.class)
		.to(ValueFix.class)
		.scope(InstanceScope.SCOPE_SINGLETON);
		ImageLoaderConfiguration	 imageconfig = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(1500000)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.build();
		ImageLoader.getInstance().init(imageconfig);
		DhDB db=IocContainer.getShare().get(DhDB.class);
		db.init("dhdbname", Const.DATABASE_VERSION);
		
	}
	
}
