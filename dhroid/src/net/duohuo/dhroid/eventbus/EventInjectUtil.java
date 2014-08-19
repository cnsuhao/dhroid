package net.duohuo.dhroid.eventbus;

import java.lang.reflect.Method;

import net.duohuo.dhroid.eventbus.ann.OnEvent;
import net.duohuo.dhroid.ioc.IocContainer;

public class EventInjectUtil {

	/**
	 * 注入时间监听
	 * 
	 * @param obj
	 */
	public static void inject(Object obj) {
		EventBus eventBus = IocContainer.getShare().get(EventBus.class);
		Method[] methods = obj.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			OnEvent onEvent = method.getAnnotation(OnEvent.class);
			if (onEvent == null)
				continue;
			OnEventListener listener = new InjectOnEventListener(obj, method,
					onEvent);
			if (!onEvent.onBefore()) {
				eventBus.clearEventTime(onEvent.name(), obj.getClass()
						.getSimpleName() + "." + method.getName());
			}
			eventBus.registerListener(onEvent.name(), obj.getClass()
					.getSimpleName() + "." + method.getName(), listener);
		}
	}

	/**
	 * 取消注册监听
	 * 
	 * @param obj
	 */
	public static void unInject(Object obj) {
		EventBus eventBus = IocContainer.getShare().get(EventBus.class);
		Method[] methods = obj.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			OnEvent onEvent = method.getAnnotation(OnEvent.class);
			if (onEvent == null)
				continue;
			if (onEvent.autoUnRegist()) {
				eventBus.unregisterListener(onEvent.name(), obj.getClass()
						.getSimpleName() + "." + method.getName());
			}
		}
	}

}
