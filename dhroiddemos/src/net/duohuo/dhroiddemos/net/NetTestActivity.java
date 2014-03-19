package net.duohuo.dhroiddemos.net;

import java.io.File;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectAssert;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.net.cache.CachePolicy;
import net.duohuo.dhroiddemos.R;
import net.duohuo.dhroiddemos.net.bean.AdBean;
import net.duohuo.dhroiddemos.net.bean.AdUrl;

public class NetTestActivity extends BaseActivity{
	@InjectView(id=R.id.gettest,click="onTestGet")
	View getTestV;
	@InjectView(id=R.id.getdialogtest,click="onTestGetDialoge")
	View getDialogTestV;
	@InjectView(id=R.id.posttest,click="onTestPost")
	View postTestV;
	@InjectView(id=R.id.postdialogtest,click="onTestPost")
	View postDialogeTestV;
	
	@InjectView(id=R.id.cache_only,click="onTestCache")
	View cacheOnlyTestV;
	@InjectView(id=R.id.cache_refresh,click="onTestCache")
	View cacheRefreshTestV;
	@InjectView(id=R.id.cache_net_error,click="onTestCache")
	View cacheNetErrorTestV;
	@InjectView(id=R.id.cache_b_a,click="onTestCache")
	View cacheBaTestV;
	@InjectView(id=R.id.to_json,click="onTrans")
	View beanTransV;
	@InjectView(id=R.id.to_bean,click="onTrans")
	View JSONTranV;
	@InjectView(id=R.id.upload,click="onUpload")
	View upload;
	
	
	@InjectView(id=R.id.result)
	TextView resultV;
	@Inject
	IDialog dialoger;
	JSONObject jodate;
	
	//注入文件,因为注入文件时是在新线程里,所以建议在之前的页面就注入一次,不然文件大了会在使用时还没拷贝完成
	@InjectAssert(path="ivory.apk")
	File apkFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.net_test_activyty);
	}

	public NetTestActivity getActivity(){
		return this;
	}
	/**
	 * get测试
	 * @param v
	 */
	public void onTestGet(View v) {
		DhNet net=new DhNet("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63");
		//添加参数
		net.addParam("key1", "key1");
		net.doGet(new NetTask(this) {
			@Override
			public void onErray(Response response) {
				super.onErray(response);
				//错误处理,出错后会先关闭对话框然后调用这个方法,默认不处理
			}
			@Override
			public void doInBackground(Response response) {
				super.doInBackground(response);
				//后台处理信息
				//可以向UI层传递数据
				response.addBundle("keyBundle", "传递的数据");
				transfer(response, 100);
			}
			@Override
			public void doInUI(Response response, Integer transfer) {
				if(transfer==100){
					dialoger.showToastShort(getActivity(), response.getBundle("keyBundle").toString());
				}else{
					resultV.setText(response.plain());
				}
			
			}
		});
	}
	/**
	 * get测试同时打开进度
	 * @param v
	 */
	public void onTestGetDialoge(View v) {
		DhNet net=new DhNet("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63");
//		net.doGet(jodate==null, new NetTask(this) {
		net.doGetInDialog(new NetTask(this) {
			@Override
			public void doInUI(Response response, Integer transfer) {
					resultV.setText(response.plain());
			}
		});
	}
	
	/**
	 * post测试
	 * @param v
	 */
	public void onTestPost(View v) {
		DhNet net=new DhNet("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63");
		NetTask task=new NetTask(this) {
			@Override
			public void doInUI(Response response, Integer transfer) {
					resultV.setText(response.plain());
			}
		};
		if(v.getId()==R.id.posttest){
			net.doPost(task);
		}else if(v.getId()==R.id.postdialogtest){
			net.doPostInDialog(task);
		}
	}
	
	public void onTestCache(View v) {
		
		switch (v.getId()) {
			case R.id.cache_only:{
				DhNet net=new DhNet();
				net.setUrl("http://youxianpei.c.myduohuo.com/mobile_picker_getareacode");
				net.useCache(CachePolicy.POLICY_CACHE_ONLY);
				net.doGet(new NetTask(getActivity()) {
					@Override
					public void doInUI(Response response, Integer transfer) {
						resultV.setText(response.plain());
						if (!response.isCache()) {
							dialoger.showToastShort(getActivity(), "这次访问还没有缓存,访问了网络");
						}else{
							dialoger.showToastShort(getActivity(), "这次访问已有缓存,只用了缓存");
						}
					}
				});
			}
			break;
			case R.id.cache_net_error:{
				DhNet net=new DhNet();
				net.setUrl("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63&temp=cache_net_error");
				net.useCache(CachePolicy.POLICY_ON_NET_ERROR);
				net.doGet(new NetTask(getActivity()) {
					@Override
					public void doInUI(Response response, Integer transfer) {
						resultV.setText(response.plain());
						if (!response.isCache()) {
							dialoger.showToastShort(getActivity(), "这次访问不是使用的缓存,断开网络试试看");
						}else{
							dialoger.showToastShort(getActivity(), "网络访问失败,这次使用的是缓存");
						}
					}
				});
			}
			break;
			case R.id.cache_refresh:{
				DhNet net=new DhNet();
				net.setUrl("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63&temp=cache_refresh");
				net.useCache(CachePolicy.POLICY_CACHE_AndRefresh);
				net.doGet(new NetTask(getActivity()) {
					@Override
					public void doInUI(Response response, Integer transfer) {
						resultV.setText(response.plain());
						if (!response.isCache()) {
							dialoger.showToastShort(getActivity(), "这次访问还没有缓存");
						}else{
							dialoger.showToastShort(getActivity(), "现在使用的是缓存,并尝试更新缓存");
						}
					}
				});
			}
			break;
			case R.id.cache_b_a:{
				DhNet net=new DhNet();
				net.setUrl("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63&temp=cache_b_a");
				net.useCache(CachePolicy.POLICY_BEFORE_AND_AFTER_NET);
				net.doGet(new NetTask(getActivity()) {
					@Override
					public void doInUI(Response response, Integer transfer) {
						resultV.setText(response.plain());
						if (!response.isCache()) {
							dialoger.showToastShort(getActivity(), "第二网络访问结果");
						}else{
							dialoger.showToastShort(getActivity(), "第一次是用的是缓存");
						}
					}
				});
			}
			break;
		default:
			break;
		}
	}
	
	
	public void onTrans(final View v) {
		DhNet net=new DhNet();
		net.setUrl("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63&temp=trans");
		net.doGet(new NetTask(getActivity()) {
			@Override
			public void doInUI(Response response, Integer transfer) {
				//结果转JOSN
				if(v.getId()==R.id.to_json){
				JSONArray array=response.jSONArrayFromData();
				resultV.setText(array.toString());
				//获取某个节点下的json
//				JSONObject urlinfo=response.jSONFrom("xxx.xxx");
//				dialoger.showToastLong(getActivity(), urlinfo.toString());
				//这是结果的纯文本
//				response.plain();
				//获取某个节点下的jsonarray
//				response.jSONArrayFrom("xxx.xxx");
				//将整个节点转为json对象
//				response.jSON();
				}else if(v.getId()==R.id.to_bean){
					//
				List<AdBean> ads=response.listFrom(AdBean.class, "data");
				resultV.setText(ads.toString());
				//获取某个节点下的对象
//				AdBean bean=response.modelFrom("xxx.xxx");
//				dialoger.showToastLong(getActivity(), bean.toString());
				}
			}
		});
	}
	
	/**
	 * 
	 */
	public void onUpload() {
		DhNet net=new DhNet("http://www.duohuo.net");
		net.addParam("key1", "参数1")
		.addParam("key2", "参数1").upload("fileName", apkFile, new NetTask(this) {
			@Override
			public void doInUI(Response response, Integer transfer) {
				if (response.isSuccess()) {
					Boolean uploading = response.getBundle("uploading");
					if (!uploading) {
						//上传完成
					}else{
						//已上传大小
						long length= response.getBundle("length");
						//文件总大小
						long total=  response.getBundle("total");
					}
				}
			}
		});
		
		
	}
	
	
}
