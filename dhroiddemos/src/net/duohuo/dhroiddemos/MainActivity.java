package net.duohuo.dhroiddemos;

import org.json.JSONObject;

import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.eventbus.EventBus;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroiddemos.adapter.AdapterTestMainActivity;
import net.duohuo.dhroiddemos.adapter.ListTestActivity;
import net.duohuo.dhroiddemos.db.DbStudentListActivity;
import net.duohuo.dhroiddemos.eventbus.EventBusActivity;
import net.duohuo.dhroiddemos.eventbus.EventBusOneActivity;
import net.duohuo.dhroiddemos.ioc.IocTestActivity;
import net.duohuo.dhroiddemos.ioc.TestManager;
import net.duohuo.dhroiddemos.net.NetTestActivity;
import net.duohuo.dhroiddemos.other.OtherMain;
import net.duohuo.dhroiddemos.perference.PerferenceTestActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends BaseActivity {
	@InjectView(id=R.id.ioctest,click="toTest")
	View toIocTest;
	@InjectView(id=R.id.dbtest,click="toTest")
	View toDbTest;
	@InjectView(id=R.id.nettest,click="toTest")
	View toNetTest;
	@InjectView(id=R.id.adaptertest,click="toTest")
	View toAdapterTest;
	@InjectView(id=R.id.eventtest,click="toTest")
	View toeventTest;
	@InjectView(id=R.id.perferencetest,click="toTest")
	View toperferenceTest;
	@InjectView(id=R.id.othertest,click="toTest")
	View tootherTest;
	
	@Inject(tag="manager2")//这里获取到的对象是TestManagerMM
	TestManager managermm;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
	}
	
	public void toTest(View v) {
		Intent it=new Intent();
		switch (v.getId()) {
		case R.id.ioctest:
			it.setClass(this, IocTestActivity.class);
			//传递数据
			it.putExtra("str", "这段文本来自"+this.getClass().getSimpleName());
			it.putExtra("int", 1000);
			JSONObject jo=new JSONObject();
			JSONUtil.put(jo, "name", "tengzhinei");
			it.putExtra("jo", jo.toString());
			break;
		case R.id.dbtest:
			it.setClass(this, DbStudentListActivity.class);
			break;
		case R.id.nettest:
			it.setClass(this, NetTestActivity.class);
			break;
		case R.id.adaptertest:
			it.setClass(this, AdapterTestMainActivity.class);
			break;
		case R.id.eventtest:{
			it.setClass(this, EventBusActivity.class);
			break;
		}
		case R.id.perferencetest:{
			it.setClass(this, PerferenceTestActivity.class);
			break;
		}
		case R.id.othertest:{
			it.setClass(this, OtherMain.class);
			break;
		}
		default:
			break;
		}
		startActivity(it);
		
		
	}

}
