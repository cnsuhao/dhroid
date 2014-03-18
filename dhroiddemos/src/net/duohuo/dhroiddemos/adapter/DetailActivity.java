/**
 * 
 */
package net.duohuo.dhroiddemos.adapter;

import android.os.Bundle;
import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.ioc.annotation.InjectExtra;
import net.duohuo.dhroid.util.ViewUtil;
import net.duohuo.dhroiddemos.R;

/**
 *	
 * @author duohuo-jinghao
 * @date 2014-3-18
 */
public class DetailActivity extends BaseActivity{
	
	@InjectExtra(name="uid")
	String uid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		ViewUtil.bindView(findViewById(R.id.textView1), "传入的ID是"+uid);
	
	}
	
}
