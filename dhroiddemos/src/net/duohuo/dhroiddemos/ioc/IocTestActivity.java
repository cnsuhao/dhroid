package net.duohuo.dhroiddemos.ioc;

import java.io.File;

import org.json.JSONObject;

import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.db.DhDB;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectAssert;
import net.duohuo.dhroid.ioc.annotation.InjectExtra;
import net.duohuo.dhroid.ioc.annotation.InjectResource;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.util.ViewUtil;
import net.duohuo.dhroiddemos.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

/**
 * 这是个IOC的demo IOC 需要在Application中进行简单配置
 * @author duohuo-jinghao
 *
 */
public class IocTestActivity extends BaseActivity{
	//获取assert中的文本
	@InjectAssert(path="testtext.json")
	String testassert;
	//获取assert中的json
	@InjectAssert(path="testtext.json")
	JSONObject jo;
	
	//注入文件,因为注入文件时是在新线程里,所以建议在之前的页面就注入一次,不然文件大了会在使用时还没拷贝完成
	@InjectAssert(path="ivory.apk")
	File apkFile;
	//注入视图
	@InjectView(id=R.id.asserttext)
	TextView testassertV;
	@InjectView(id=R.id.resstring)
	TextView resstrV;

	//注入视图,和视图事件//itemClick和itemLongClick时间可以参照
	//注入时建议向下兼容,如果你的layout中是一个button且不需要换文字,请注入View,这样可以在将布局修改为image时前台不会出错
	@InjectView(id=R.id.assertFile,click="toInstal")
	View instalApkV;
	@InjectView(id=R.id.child_layout)
	ViewGroup childLayoutV;
	//注入布局文件
	@InjectView(layout=R.layout.ioc_head)
	View headV;
	//在其他视图中查看
	@InjectView(id=R.id.intext,inView="headV")
	TextView childTextV;
	
	
	//注入字串
	@InjectResource(string=R.string.app_name)
	String appname;
	//注入颜色
	//这里不能为int因为int有默认值0 有值的属性会被忽略,防止重复注入
	@InjectResource(color=R.color.link)
	Integer colorLink;
	//注入图片
	@InjectResource(drawable=R.drawable.ic_launcher)
	Drawable icDraw;
	//注入dimen
	@InjectResource(dimen=R.dimen.testdimen)
	Float dime;
	
	
	//接受传入的字符串
	@InjectExtra(name="str",def="默认值")
	String extra;
	//接受传入的数字
	@InjectExtra(name="int",def="1")
	Integer extraint;
	//接受传入的json对象(传入时是已字符串传入的)这个默认值没用
	@InjectExtra(name="jo")
	JSONObject extrajo;
	
	
	//标准注入 单例  注入接口 需要在application中配置
	@Inject
	IDialog dialoger;
	
	//标准注入 单例  注入类
	@Inject
	DhDB db;
	
	//根据tag拿对象这里拿到的manager1和manager1copy是同一对象,manager2和manager2copy是同一对象
	@Inject(tag="manager1")
	TestManager manager1;
	@Inject(tag="manager1")
	TestManager manager1copy;
	@Inject(tag="manager2")
	TestManager manager2;
	@Inject(tag="manager2")
	TestManager manager2copy;
	
	
	//这个测试根据名字获取对象配置请看application
	@Inject(name="testmm")//这里获取到的对象是TestManagerMM
	TestManager managermm;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//在baseActivity 中 调用了	InjectUtil.inject(this); 来注入注解在任意任意类中都可调用
		setContentView(R.layout.ioc_test_activity);
		LayoutParams params=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		childLayoutV.addView(headV,params);
		childTextV.setText("我在注入的布局里");

		testassertV.setText("assert text: "+testassert+"assert jo:"+jo);
		resstrV.setTextColor(colorLink);
		resstrV.setText(appname+"  textsize:"+dime);
		resstrV.setTextSize(dime);
		ViewUtil.bindView(findViewById(R.id.imageView1), icDraw);
		ViewUtil.bindView(findViewById(R.id.extras), "extras str:"+extra+" int:"+extraint+" jo:"+extrajo);
		manager1.setName("第一个对象");
		manager2.setName("第二个对象");
		ViewUtil.bindView(findViewById(R.id.inject_stand), "manager1:"+manager1.getName()+" manager1copy:"+manager1copy.getName()+" manager2:"+manager2.getName()+" manager2copy:"+manager2copy.getName());
		//通过编码的方式获取对象
		TestManager testmanager=IocContainer.getShare().get(TestManager.class, "manager2");
		//通过接口 获取单例这个需要在
		IDialog d=IocContainer.getShare().get(IDialog.class);
		d.showToastShort(this, testmanager.getName());
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
//				也可以根据编码获取
//				IocContainer.getShare().get("testmm");
				dialoger.showToastShort(IocTestActivity.this, managermm.getName());
			}
		});
		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				TestDateHelper helper=IocContainer.getShare().get(TestDateHelper.class);
				dialoger.showToastShort(IocTestActivity.this, helper.getName());
			}
		});
	
	}	
	/**
	 * 视图事件,安装事件
	 * @param v
	 */
	public void toInstal(View v) {
		if(apkFile==null){
			dialoger.showToastLong(this, "文件拷贝中..");
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" +  apkFile.getAbsolutePath()),"application/vnd.android.package-archive");
		startActivity(i);
	}
}
