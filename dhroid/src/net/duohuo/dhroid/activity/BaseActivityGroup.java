package net.duohuo.dhroid.activity;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
/***
 *  
 * @author duohuo-jinghao 
 *
 */
public  abstract class BaseActivityGroup extends ActivityGroup{
	
	static ActivityTack tack=ActivityTack.getInstanse();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		tack.addActivity(this);
	}

	@Override
	public void onResume() {			
		super.onResume();
	}

	
	@Override
	public void finish() {	
		tack.removeActivity(this);
		super.finish();
	}
	
	public  void exit(){
		tack.exit(this);
	}
	/**
	 * 
	 * @param it获取字activity
	 * @return
	 */
	public abstract  boolean callSubActivity(Intent it);
	
	/**
	 * 获取当前的view
	 * @return
	 */
	public abstract View getCurrentView();
	
	/**
	 * 移除view
	 * @param v
	 */
	public abstract void removeView(View v);
	/**
	 * 显示前一个
	 */
	public abstract void showPreview();
}
