package net.duohuo.dhroid.activity;
import net.duohuo.dhroid.Const;
import net.duohuo.dhroid.ioc.InjectUtil;
import android.app.Activity;
import android.app.PendingIntent.OnFinished;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
/***
 *  
 * @author duohuo-jinghao 
 *
 */
public  class BaseActivity extends Activity   {	
	
	private ActivityTack tack=ActivityTack.getInstanse();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tack.addActivity(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	
	@Override
	public void finish() {
		super.finish();
		tack.removeActivity(this);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		if(Const.auto_inject){
			InjectUtil.inject(this);
		}
	}
}
