package net.duohuo.dhroid.eventbus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.thread.Task;
import net.duohuo.dhroid.thread.ThreadWorker;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 事件线管理类
 * 
 * @author duohuo-jinghao
 */
public class EventBus {

	Map<String, EventQueue> eventQueues = new HashMap<String, EventQueue>();
	Map<String, List<OnEventListener>> eventListeners = new HashMap<String, List<OnEventListener>>();
	Map<String, OnEventListener> Listeners = new HashMap<String, OnEventListener>();
	static SharedPreferences listenerFireTime = IocContainer.getShare()
			.getApplicationContext()
			.getSharedPreferences("EventBusTime", Context.MODE_WORLD_WRITEABLE);

	/**
	 * 清空事件
	 * 
	 * @param name
	 */
	public void clearEvents(String name) {
		EventQueue queue = eventQueues.get(name);
		if (queue == null) {
			queue = new EventQueue();
			eventQueues.put(name, queue);
		}
		queue.clearEvents();
	}

	/**
	 * 发布事件
	 */
	public void fireEvent(String name, Object... params) {
		Event event = new Event();
		event.setName(name);
		event.setEventTime(System.currentTimeMillis());
		event.setParams(params);
		fireEvent(event);
	}

	/**
	 * 发布事件
	 */
	public void fireEvent(final Event event) {
		if (event != null) {
			EventQueue queue = eventQueues.get(event.getName());
			if (queue == null) {
				queue = new EventQueue();
				eventQueues.put(event.getName(), queue);
			}
			queue.addEvent(event);
		}

		final String eventname = event.getName();
		// 在主线程里
		if (Looper.myLooper() == Looper.getMainLooper()) {
			ThreadWorker.execuse(false, new Task(IocContainer.getShare()
					.getApplicationContext()) {
				@Override
				public void doInBackground() {
					super.doInBackground();
					List<OnEventListener> list = eventListeners.get(eventname);
					for (int i = 0; list != null && i < list.size(); i++) {
						OnEventListener listener = list.get(i);
						listener.doInBg(event);
					}
				}

				@Override
				public void doInUI(Object obj, Integer what) {
					List<OnEventListener> list = eventListeners.get(eventname);
					for (int i = 0; list != null && i < list.size(); i++) {
						OnEventListener listener = list.get(i);
						listener.doInUI(event);
						listenerFireTime
								.edit()
								.putLong(
										eventname + listener.getListenerName(),
										System.currentTimeMillis()).commit();
					}
				}
			});
		} else {
			List<OnEventListener> list = eventListeners.get(eventname);
			for (int i = 0; list != null && i < list.size(); i++) {
				OnEventListener listener = list.get(i);
				listener.doInBg(event);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("listener", listener);
				map.put("event", event);
				handler.sendMessage(handler.obtainMessage(0x10000, map));
			}
		}
	}

	/**
	 * 事件监听
	 * 
	 * @param name
	 * @param listener
	 * @param queue
	 */
	public void registerListener(final String name, final String listenerName,
			final OnEventListener listener) {
		String key = name + "#" + listenerName;
		OnEventListener oldlis = Listeners.get(key);
		if (oldlis != null) {
			// 防止重复注册相同名字的监听器
			unregisterListener(name, oldlis);
		}
		listener.setEventName(name);
		listener.setListenerName(listenerName);
		List<OnEventListener> listeners = eventListeners.get(name);
		if (listeners == null) {
			listeners = new ArrayList<OnEventListener>();
			eventListeners.put(name, listeners);
		}
		listeners.add(listener);
		Listeners.put(key, listener);
		// 触发已发生的时间
		ThreadWorker.execuse(false, new Task(IocContainer.getShare()
				.getApplicationContext()) {
			@Override
			public void doInBackground() {
				super.doInBackground();
				EventQueue queue = eventQueues.get(name);
				if (queue == null)
					return;
				Long time = listenerFireTime.getLong(name + listenerName, 0);
				List<Event> events = queue.getEvents(time);
				if (events != null) {
					for (int i = 0; i < events.size(); i++) {
						Event event = events.get(i);
						if (!listener.doInBg(event)) {
							break;
						}
					}
				}
			}

			@Override
			public void doInUI(Object obj, Integer what) {
				EventQueue queue = eventQueues.get(name);
				if (queue == null)
					return;
				Long time = listenerFireTime.getLong(name + listenerName, 0);
				List<Event> events = queue.getEvents(time);
				if (events != null) {
					for (int i = 0; i < events.size(); i++) {
						Event event = events.get(i);
						if (!listener.doInUI(event)) {
							break;
						}
					}
				}
				listenerFireTime
						.edit()
						.putLong(name + listenerName,
								System.currentTimeMillis()).commit();
			}
		});
	}

	/**
	 * 移除监听
	 * 
	 * @param eventName
	 * @param listenerName
	 */
	public void unregisterListener(String eventName, String listenerName) {
		String key = eventName + "#" + listenerName;
		;
		OnEventListener listener = Listeners.get(key);
		unregisterListener(eventName, listener);
	}

	/**
	 * 更新事件时间为最新时间
	 * 
	 * @param eventName
	 * @param listenerName
	 */
	public void clearEventTime(String eventName, String listenerName) {
		listenerFireTime.edit()
				.putLong(eventName + listenerName, System.currentTimeMillis())
				.commit();
	}

	/**
	 * 移除监听
	 * 
	 * @param eventName
	 * @param listerName
	 */
	public void unregisterListener(String eventName, OnEventListener listener) {
		List<OnEventListener> lis = eventListeners.get(eventName);
		lis.remove(listener);
		Listeners.remove(listener);
	}

	static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x10000 && msg.obj != null
					&& msg.obj instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				OnEventListener listener = (OnEventListener) map
						.get("listener");
				Event event = (Event) map.get("event");
				listener.doInUI(event);
				listenerFireTime
						.edit()
						.putLong(listener.getListenerName(),
								System.currentTimeMillis()).commit();
			}
		}
	};
}
