/**
 * 
 */
package net.duohuo.dhroiddemos.ioc;

import net.duohuo.dhroid.ioc.InjectUtil;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroiddemos.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author duohuo-jinghao
 * @date 2014-8-20
 */
public class MyFragment extends Fragment {
	
	@InjectView(id = R.id.textView1)
	TextView textV;
	@InjectView(id = R.id.button1, click = "onButton")
	Button buttonV;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_view, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		InjectUtil.inject(this);
		textV.setText("我是fragment");
	}

	public void onButton() {
		Toast.makeText(getActivity(), "onButtonClick", Toast.LENGTH_LONG)
				.show();
	}

}
