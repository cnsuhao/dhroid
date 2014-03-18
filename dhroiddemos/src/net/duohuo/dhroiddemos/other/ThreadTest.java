/**
 * 
 */
package net.duohuo.dhroiddemos.other;

import android.os.Bundle;
import android.view.View;
import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.thread.Task;
import net.duohuo.dhroid.thread.ThreadWorker;
import net.duohuo.dhroiddemos.R;
import net.duohuo.dhroiddemos.db.bean.Student;

/**
 *	
 * @author duohuo-jinghao
 * @date 2014-3-18
 */
public class ThreadTest extends BaseActivity{

	@Inject
	IDialog dialoger;
	@InjectView(id=R.id.button1,click="onTest")
	View test;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_thread);
	}
	
	/**
	 * 
	 */
	public void onTest() {
		ThreadWorker.execuse(false, new Task(this) {
			Student student;
			@Override
			public void doInBackground() {
				super.doInBackground();
				//后台处理
				student=new Student();
				//
				
				transfer("线程间的交互", 100);
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
			}
			
			@Override
			public void doInUI(Object obj, Integer what) {
				if(student!=null){
					if(what==100){
						dialoger.showToastShort(ThreadTest.this, obj.toString());
					}else{
						dialoger.showToastShort(ThreadTest.this, "处理完成");
					}
				}
			}
		});
	}
	
}
