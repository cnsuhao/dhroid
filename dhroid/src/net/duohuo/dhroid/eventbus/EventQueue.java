package net.duohuo.dhroid.eventbus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 事件队列
 * @author duohuo-jinghao 
 *
 */
public class EventQueue {
	
	List<Event> events=new ArrayList<Event>();;
	
	
	/**
	 * 事件有效期   小于0 时表示没有过期时间
	 */
	long validTime=3000000;
	
	/**
	 * 有效事件个数  超过个数之前的事件会被覆盖
	 */
	int validSize=20;
	
	/**
	 * 添加一个时间
	 * @param event
	 */
	public void addEvent(Event event){
		if(event!=null){
			event.setEventTime(System.currentTimeMillis());
			events.add(event);
		}
		clearInValideEvent();
	}

	/**
	 * 获取某个时间后发生的事件  按时间倒序排
	 * @param time
	 */
	public List<Event> getEvents(long time){
		List<Event> ets=null;
		for (int i = events.size()-1; i >-1; i--) {
			Event event=events.get(i);
			long eventtime=event.getEventTime();
			if(eventtime>time){
				if(ets==null)ets=new ArrayList<Event>();
				ets.add(event);
			}else{
				break;
			}
		}
		return ets;
	}	
	
	public void clearEvents(){
		events.clear();
	}
	
	/**
	 * 清空过期事件
	 */
	public void clearInValideEvent(){
		long current=System.currentTimeMillis();
		List<Event> toRemovedEvents =new ArrayList<Event>();
		/***
		 * 清除超过有效个数的事件
		 */
		for (int i = 0; i <events.size()-validSize; i++) {
			toRemovedEvents.add(events.get(i));
		}
		/**
		 * 清除过期事件
		 */
		if(validTime>0){
			for (int i = toRemovedEvents.size(); i < events.size(); i++) {
				Event event=events.get(i);
				if(current-event.getEventTime()>validTime){
					toRemovedEvents.add(event);
				}else{
					break;
				}
			}
		}
		if(toRemovedEvents!=null){
			for (Iterator<Event> iterator = toRemovedEvents.iterator(); iterator
					.hasNext();) {
				Event event = iterator.next();
				events.remove(event);
			}
		}
	}
	
	
}
