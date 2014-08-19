/**
 * 
 */
package net.duohuo.dhroid.ioc;

import android.app.Application;
import android.content.Context;

/**
 * 
 * @author duohuo-jinghao
 * @date 2014-8-18
 */
public class Ioc {

	public static void initApplication(Application application) {
		IocContainer.getShare().initApplication(application);
	}

	public static <T extends Application> T getApplication() {
		return IocContainer.getShare().getApplication();
	}

	public static Context getApplicationContext() {
		return IocContainer.getShare().getApplicationContext();
	}

	public static <T> T get(String name) {
		return IocContainer.getShare().get(name);
	}

	public static <T> T get(Class<T> clazz) {
		return IocContainer.getShare().get(clazz);
	}

	public static <T> T get(Class<T> clazz, String tag) {
		return IocContainer.getShare().get(clazz, tag);
	}

	public static Instance bind(Class clazz) {
		return IocContainer.getShare().bind(clazz);
	}

}
