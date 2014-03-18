/**
 * 
 */
package net.duohuo.dhroiddemos.eventbus;

import android.os.Bundle;
import android.view.View;
import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.eventbus.EventBus;
import net.duohuo.dhroid.eventbus.ann.OnEvent;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroiddemos.R;

/**
 *	
 * @author duohuo-jinghao
 * @date 2014-3-18
 */
public class EventBusSecondActivity extends BaseActivity{

	@InjectView(id=R.id.button1,click="fireEvent")
	View eventFire1;
	@InjectView(id=R.id.button2,click="fireEvent")
	View eventFire2;
	@InjectView(id=R.id.button3,click="fireEvent")
	View eventFire3;
	@InjectView(id=R.id.button4,click="fireEvent")
	View eventFire4;
	@Inject
	EventBus bus;
	@Inject
	IDialog dialoger;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_bus_second);
	}
	
	/**
	 * 
	 */
	public void fireEvent(View v) {
		switch (v.getId()) {
		case R.id.button1:
			bus.fireEvent(Events.event_test1, "事件1 这个是参数");
			dialoger.showToastShort(this, "");
			break;
		case R.id.button2:
			bus.fireEvent(Events.event_test2, "事件2 这个是参数","可以传多个参数");
			break;
		case R.id.button3:
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					bus.fireEvent(Events.event_test1, "事件1我不是在主线程触发的");
				}
			}).start();
			break;
		case R.id.button4:
		new Thread(new Runnable() {
				
				@Override
				public void run() {
					bus.fireEvent(Events.event_test2, "事件2我不是在主线程触发的","可以传多个参数");
				}
			}).start();
		
			break;
		default:
			break;
		}
		
	}
	
}
