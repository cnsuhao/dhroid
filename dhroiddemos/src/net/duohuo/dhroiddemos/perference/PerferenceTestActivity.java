/**
 * 
 */
package net.duohuo.dhroiddemos.perference;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.util.ViewUtil;
import net.duohuo.dhroiddemos.R;
import net.duohuo.dhroiddemos.db.bean.Student;

/**
 *	
 * @author duohuo-jinghao
 * @date 2014-3-18
 */
public class PerferenceTestActivity extends BaseActivity{
	@InjectView(id=R.id.account)
	EditText accountV;
	@InjectView(id=R.id.name)
	EditText nameV;
	@InjectView(id=R.id.stuname)
	EditText stuNameV;
	@InjectView(id=R.id.uid)
	EditText uidV;
	@InjectView(id=R.id.load,click="onLoad")
	View loadV;
	@InjectView(id=R.id.refresh,click="onRefresh")
	View refreshV;
	@InjectView(id=R.id.commit,click="onCommit")
	View commitV;
	@Inject
	DemoPerference demoPerference;
	IDialog dialoger;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.perference_test);
		//首次加载这个
		demoPerference.load();
		bindDate();
	}
	
	/**
	 * 数据绑定
	 */
	void bindDate(){
		ViewUtil.bindView(nameV, demoPerference.username);
		ViewUtil.bindView(uidV, demoPerference.uid+"");
		if(demoPerference.student!=null){
			ViewUtil.bindView(stuNameV, demoPerference.student.getName());
		}
		ViewUtil.bindView(accountV, demoPerference.account);
		
		
	}
	
	
	/**
	 * 加载
	 */
	public void onLoad() {
		demoPerference.account=accountV.getText().toString();
		demoPerference.load();
		bindDate();
	}
	/**
	 * 更新
	 */
	public void onRefresh() {
		//这里是模拟的用的同步
		demoPerference.refresh();
		bindDate();
	}
	/**
	 * 提交
	 */
	public void onCommit() {
		demoPerference.account=accountV.getText().toString();
		Student student=new Student();
		student.setName(stuNameV.getText().toString());;
		demoPerference.student=student;
		demoPerference.uid=Integer.parseInt(uidV.getText().toString());
		demoPerference.username=nameV.getText().toString();
		demoPerference.commit();
		dialoger.showToastShort(this, "提交成功,换个account试试");
	}
}
