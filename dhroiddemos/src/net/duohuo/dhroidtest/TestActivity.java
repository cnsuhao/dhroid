/**
 * 
 */
package net.duohuo.dhroidtest;

import net.duohuo.dhroid.ioc.InjectUtil;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroiddemos.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 *	
 * @author duohuo-jinghao
 * @date 2014-8-19
 */
public class TestActivity extends Activity{
	@InjectView(id=R.id.textView1)	
	TextView text1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		InjectUtil.inject(this);
		text1.setText("TestActivity的包名和项目的包名可以不同了现在");
	}

	
	
}
