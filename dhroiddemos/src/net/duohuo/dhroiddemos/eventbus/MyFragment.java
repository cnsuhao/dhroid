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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author duohuo-jinghao
 * @date 2014-8-20
 */
public class MyFragment extends Fragment {

	@InjectView(id = R.id.textView1)
	TextView textV;
	@InjectView(id = R.id.button1, click = "toFire")
	Button buttonV;
	@Inject
	EventBus bus;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.eventbus_frag_view, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		InjectUtil.inject(this);

	}

	@Override
	public void onResume() {
		super.onResume();
		// 也有可能在onActivityCreated 中添加监听 在onDestroy中移除监听,根据自己的Fragment的生命周期
		bus.registerListener(Events.event_test3, "MyFragment",
				new OnEventListener() {
					@Override
					public Boolean doInUI(Event event) {
						String str = (String) event.getParams()[0];
						textV.setText(str);
						return super.doInUI(event);
					}

				});
		EventInjectUtil.inject(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 注意监听器有注入就有移除,自己判定自己的监听器的移除位置
		bus.unregisterListener(Events.event_test3, "MyFragment");
		EventInjectUtil.unInject(this);
	}

	int count=0;
	/**
	 * fragment触发事件
	 */
	public void toFire() {
		count++;
		bus.fireEvent(Events.event_test4, "我是MyFragment触发的"+count);
	}

	@OnEvent(name = Events.event_reset)
	public void onReset() {
		textV.setText("这里显示文本");
		count=0;
	}
}
