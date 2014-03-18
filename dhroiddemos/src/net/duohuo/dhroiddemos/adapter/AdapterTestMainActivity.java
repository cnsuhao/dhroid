/**
 * 
 */
package net.duohuo.dhroiddemos.adapter;

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
public class AdapterTestMainActivity extends BaseActivity{
	@InjectView(id=R.id.button1,click="toListTest")
	View toCommon;
	@InjectView(id=R.id.button2,click="toListTest")
	View toRefresh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adapter_main);
		
	}
	
	/**
	 * 
	 */
	public void toListTest(View v) {
	Intent it=new Intent();
		switch (v.getId()) {
		case R.id.button1:
			it.setClass(this, ListTestActivity.class);
			break;
		case R.id.button2:
			it.setClass(this, ListRefreshActivity.class);
			break;
		default:
			break;
		}
		startActivity(it);
	}
	
	
}
