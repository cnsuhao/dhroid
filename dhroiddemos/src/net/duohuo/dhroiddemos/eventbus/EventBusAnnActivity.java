/**
 * 
 */
package net.duohuo.dhroiddemos.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.eventbus.EventBus;
import net.duohuo.dhroid.eventbus.EventInjectUtil;
import net.duohuo.dhroid.eventbus.ann.OnEvent;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.util.ViewUtil;
import net.duohuo.dhroiddemos.R;

/**
 *	
 * @author duohuo-jinghao
 * @date 2014-3-18
 */
public class EventBusAnnActivity extends BaseActivity{
	@Inject
	EventBus bus;
	@Inject
	IDialog dialoger;
	@InjectView(id=R.id.button1,click="toFire")
	View toFire;
	@InjectView(id=R.id.button2,click="toFire")
	View toFire2;
	public static final String log_tag="EVENT_DEMO";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_bus_one);
		//EventBusOneActivity中没有以下清空操作,这里发生的事件,EventBusOneActivity中也是可以监听到的
		//清空之前发生过的事件
		bus.clearEvents(Events.event_test1);
		//也可以重置单个监听器最新监听到的时间
//		bus.clearEventTime(Events.event_test1, "EventBusAnnActivity.onEvent1");
		bus.clearEvents(Events.event_test1);
		ViewUtil.bindView(findViewById(R.id.tips), "这是通过注解方式的");
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		EventInjectUtil.inject(this);
	}
	@Override
	protected void onStop() {
		super.onStop();
		EventInjectUtil.unInject(this);
	}
	
	//在UI线程监听事件一注解的返回类型可以是boolean 或空(默认true),
	//如果为true,会继续处理前面没处理的事件,false不处理
	@OnEvent(name=Events.event_test1,ui=false)
	public boolean onEvent1(String p1){
		Log.v(log_tag,"事件1触发参数:"+p1);
//		return true;
		return false;
	
	}
	
	//
	@OnEvent(name=Events.event_test2)
	public void onEvent2(String p1,String p2){
		dialoger.showToastShort(this, "事件2触发参数1:"+p1+"参数2:"+p2 );
	}
	
	
	/**
	 * 
	 */
	public void toFire(View v) {
		switch (v.getId()) {
		case R.id.button1:{
			Intent it=new Intent(this,EventBusSecondActivity.class);
			startActivity(it);
		}
			break;
		case R.id.button2:{
			bus.fireEvent(Events.event_test1, "我是在本界面触发的");
		}
			break;
		default:
			break;
		}
	}
}
