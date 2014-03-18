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
import net.duohuo.dhroid.eventbus.Event;
import net.duohuo.dhroid.eventbus.EventBus;
import net.duohuo.dhroid.eventbus.OnEventListener;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.util.ViewUtil;
import net.duohuo.dhroiddemos.R;

/**
 *	
 * @author duohuo-jinghao
 * @date 2014-3-18
 */
public class EventBusOneActivity extends BaseActivity{
	@Inject
	EventBus bus;
	public static final String log_tag="EVENT_DEMO";
	

	@InjectView(id=R.id.button1,click="toFire")
	View toFire;
	@InjectView(id=R.id.button2,click="toFire")
	View toFire2;
	@Inject
	IDialog dialoger;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_bus_one);
		//手动,注册事件1我在onCreate中注册需要在finish取消注册
		bus.registerListener(Events.event_test1, EventBusOneActivity.class.getSimpleName(), new OnEventListener(){
			@Override
			public Boolean doInBg(Event event) {
				 super.doInBg(event);
				 Log.v(log_tag, "我是在后台线程处理的请勿操作UI,我接受到的参数是"+event.getParams()[0]);
				return false;
			}
		});
		ViewUtil.bindView(findViewById(R.id.tips), "这是通过编码方式的");
	}

	@Override
	protected void onResume() {
		super.onResume();
		//手动,注册事件2,我在onResume中注册需要在onStop取消注册
		bus.registerListener(Events.event_test2, EventBusOneActivity.class.getSimpleName(), new OnEventListener(){
			@Override
			public Boolean doInUI(Event event) {
				 super.doInUI(event);
     			dialoger.showToastShort(EventBusOneActivity.this, "我可以接受到先前的用户,我是在UI线程处理的,我接受到的参数1是"+event.getParams()[0]+"我接受到的参数2是"+event.getParams()[1]);
				//返回值表示是否继续迭代事件
     			//如果为true,会继续处理前面没处理的事件,false不处理
//     			return true;
     			return false;
			}
		});
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		//这里取消注册事件
		bus.unregisterListener(Events.event_test2, EventBusOneActivity.class.getSimpleName());
	}
	
	
	/* (non-Javadoc)
	 * @see net.duohuo.dhroid.activity.BaseActivity#finish()
	 */
	@Override
	public void finish() {
		super.finish();
		//这里取消注册事件
		bus.unregisterListener(Events.event_test1, EventBusOneActivity.class.getSimpleName());
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
