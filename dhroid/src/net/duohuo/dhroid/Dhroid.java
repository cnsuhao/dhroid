package net.duohuo.dhroid;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;
import net.duohuo.dhroid.db.DhDB;
import net.duohuo.dhroid.dialog.DialogImpl;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.Instance.InstanceScope;
import net.duohuo.dhroid.ioc.Ioc;
import net.duohuo.dhroid.ioc.IocContainer;
/**
 * 完成一些系统的初始化的工作
 * @author Administrator
 *
 */
public class Dhroid {
	public static void init(Application app){
		Ioc.initApplication(app);
		//对话框的配置
		Ioc.bind(DialogImpl.class).to(IDialog.class).scope(InstanceScope.SCOPE_PROTOTYPE);
		
		ImageLoaderConfiguration	 imageconfig = new ImageLoaderConfiguration.Builder(
				app.getApplicationContext())
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
