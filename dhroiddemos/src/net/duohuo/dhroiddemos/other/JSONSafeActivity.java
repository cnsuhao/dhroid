/**
 * 
 */
package net.duohuo.dhroiddemos.other;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.ioc.annotation.InjectAssert;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.util.ViewUtil;
import net.duohuo.dhroiddemos.R;

/**
 * 安全高效的处理json,防止数据格式异常
 * 
 * @author duohuo-jinghao
 * @date 2014-3-18
 */
public class JSONSafeActivity extends BaseActivity {
	@InjectAssert(path = "jsontest.json")
	JSONObject jo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_json);
		boolean isok = JSONUtil.getBoolean(jo, "data.isok");
		boolean isgood = JSONUtil.getBoolean(jo, "data.isgood");
		Long time = JSONUtil.getLong(jo, "data.pubtime");
		JSONObject data = JSONUtil.getJSONObject(jo, "data");
		String uname = JSONUtil.getString(jo, "data.user.name");
		JSONUtil.getString(data, "user.name");
		JSONArray array = JSONUtil.getJSONArray(data, "attrs");
		JSONObject o = JSONUtil.getJSONObjectAt(array, 0);
		ViewUtil.bindView(findViewById(R.id.textView1), isok, "sex");
		ViewUtil.bindView(findViewById(R.id.textView2), isgood);
		ViewUtil.bindView(findViewById(R.id.textView3), time, "time");
		ViewUtil.bindView(findViewById(R.id.textView4), uname);
		ViewUtil.bindView(findViewById(R.id.textView5), o);
	}

}
