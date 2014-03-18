/**
 * 
 */
package net.duohuo.dhroiddemos.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroiddemos.R;

/**
 * 
 * @author duohuo-jinghao
 * @date 2014-3-18
 */
public class OtherMain extends BaseActivity {

	@InjectView(id=R.id.test_JSON,click="toDetail")
	View toJSON;
	@InjectView(id=R.id.asy_thread,click="toDetail")
	View toThread;
	@InjectView(id=R.id.photo,click="toDetail")
	View toUploadPic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_other);
		
	}

	/**
	 * 
	 */
	public void toDetail(View v) {
		Intent it=new Intent();
		switch (v.getId()) {
		case R.id.test_JSON:
			it.setClass(this, JSONSafeActivity.class);
			break;
		case R.id.asy_thread:
			it.setClass(this, ThreadTest.class);
			break;
		case R.id.photo:
			it.setClass(this, PicUploadActivity.class);
			break;	
		default:
			break;
		}
		startActivity(it);
	}
	
}
