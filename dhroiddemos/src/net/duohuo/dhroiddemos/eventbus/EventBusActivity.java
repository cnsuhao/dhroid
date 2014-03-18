/**
 * 
 */
package net.duohuo.dhroiddemos.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroiddemos.R;

/**
 *	
 * @author duohuo-jinghao
 * @date 2014-3-18
 */
public class EventBusActivity extends BaseActivity{

	@InjectView(id=R.id.button1,click="toTest")
	View toTest;
	@InjectView(id=R.id.button2,click="toTest")
	View toTest2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventbus_test_main);
	}
	
	public void toTest(View v) {
		Intent it=new Intent();
		switch (v.getId()) {
		case R.id.button1:
			it.setClass(this, EventBusOneActivity.class);
			break;
		case R.id.button2:
			it.setClass(this, EventBusAnnActivity.class);
			break;
		default:
			break;
		}
		startActivity(it);
		
	}
	
}
