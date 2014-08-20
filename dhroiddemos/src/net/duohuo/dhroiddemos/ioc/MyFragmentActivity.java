/**
 * 
 */
package net.duohuo.dhroiddemos.ioc;

import net.duohuo.dhroiddemos.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * 
 * @author duohuo-jinghao
 * @date 2014-8-20
 */
public class MyFragmentActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.main_frag);
		Fragment frag1=new MyFragment();
		Fragment frag2=new MyFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frag1, frag1)
				.replace(R.id.frag2, frag2).commit();
	}

}
