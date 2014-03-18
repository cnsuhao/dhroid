/**
 * 
 */
package net.duohuo.dhroiddemos.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.adapter.BeanAdapter;
import net.duohuo.dhroid.adapter.FieldMap;
import net.duohuo.dhroid.adapter.NetJSONAdapter;
import net.duohuo.dhroid.adapter.INetAdapter.LoadSuccessCallBack;
import net.duohuo.dhroid.adapter.NetJSONAdapter.DataBulider;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroiddemos.R;

/**
 *	
 * @author duohuo-jinghao
 * @date 2014-3-18
 */
public class ListRefreshActivity extends BaseActivity{

	
	@InjectView(id=R.id.listView1)
	ListView listV;
	NetJSONAdapter adapter;
	@Inject
	IDialog dialoger;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adapter_list_refresh_more);
		adapter=new NetJSONAdapter("http://shishangquan.017788.com/mobile_ordermeal_jujiList", this, R.layout.adapter_item);
		
		
		//这个方法没有具体实现,要要在自己实现的listview中处理
		adapter.setJump(DetailActivity.class, "id", "uid");
		//这里可以进行覆盖DataBulider   需要返回有效的JSONArray
		adapter.setDataBulider(new DataBulider() {
			@Override
			public JSONArray onDate(Response response) {
				return response.jSONArrayFromData();
			}
		});
		
		
		
		
		
		
		
		
		
		
		//数据绑定
		adapter.addField("username", R.id.name);
		adapter.addField("title", R.id.title);
		//数据绑定 进行文本修饰
		adapter.addField("pubdate", R.id.time,"time");
		//数据绑定 进行图片修饰
		adapter.addField("user_faceimg", R.id.pic,"round");
		
		adapter.addField(new FieldMap("activeaddress", R.id.content) {
			@Override
			public Object fix(View itemV, Integer po, Object o, Object jo) {
				JSONObject joo=(JSONObject) jo;
				//这里可以做一些额外的工作
				itemV.findViewById(R.id.icon).setVisibility(JSONUtil.getInt(joo, "status")==1?View.VISIBLE:View.INVISIBLE);
				return o;
			}
		});
		//内部点击事件
		adapter.setOnInViewClickListener(R.id.pic, new BeanAdapter.InViewClickListener() {

			@Override
			public void OnClickListener(View itemV, View v, Integer po,
					Object jo) {
				JSONObject joo=(JSONObject) jo;
				dialoger.showToastLong(getActivity(), JSONUtil.getString(joo, "username"));
			}
		});

		//加载成功后回调
		adapter.setOnLoadSuccess(new LoadSuccessCallBack() {
			@Override
			public void callBack(Response response) {
				if(response.isSuccess()){
					dialoger.showToastShort(getActivity(), "加载成功");
					if(adapter.getPageNo()==1){
						listV.setSelection(0);
					}
					
				}
			}
		});
		
		adapter.refresh();
		adapter.showProgressOnFrist(true);
		listV.setAdapter(adapter);
		
		
	}
	
	public Activity getActivity(){
		return this;
	}
	
}
