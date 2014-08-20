/**
 * 
 */
package net.duohuo.dhroiddemos.eventbus;

import net.duohuo.dhroid.eventbus.Event;
import net.duohuo.dhroid.eventbus.EventBus;
import net.duohuo.dhroid.eventbus.EventInjectUtil;
import net.duohuo.dhroid.eventbus.OnEventListener;
import net.duohuo.dhroid.eventbus.ann.OnEvent;
import net.duohuo.dhroid.ioc.InjectUtil;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroiddemos.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * @author duohuo-jinghao
 * @date 2014-8-20
 */
public class MyFragmentActivity extends FragmentActivity {
	@Inject
	EventBus bus ;
	@InjectView(id = R.id.button1, click = "toFire")
	View fireV;
	@InjectView(id = R.id.textView1)
	TextView textV;
	@InjectView(id = R.id.button2, click = "toReset")
	View resetV;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.event_bus_main_frag);
		InjectUtil.inject(this);
		Fragment frag = new MyFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.frag, frag)
				.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventInjectUtil.inject(this);
		bus.registerListener(Events.event_reset, getClass().getSimpleName(),
				new OnEventListener() {
					@Override
					public Boolean doInUI(Event event) {
						textV.setText("这里显示文本");
						count=0;
						return super.doInUI(event);
					}
				});
	}

	@Override
	protected void onPause() {
		super.onPause();
		EventInjectUtil.unInject(this);
		bus.unregisterListener(Events.event_reset, getClass().getSimpleName());
	}

	public void toReset() {
		bus.fireEvent(Events.event_reset);
	}

	int count=0;
	/**
	 * activity触发事件
	 */
	public void toFire() {
		count++;
		bus.fireEvent(Events.event_test3, "我是MyFragmentActivity触发的"+count);
	}

	@OnEvent(name = Events.event_test4)
	public boolean onTest4(String str) {
		textV.setText(str);
		return false;
	}

}
