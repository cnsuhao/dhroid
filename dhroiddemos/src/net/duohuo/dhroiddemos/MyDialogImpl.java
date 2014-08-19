/**
 * 
 */
package net.duohuo.dhroiddemos;

import net.duohuo.dhroid.dialog.DialogImpl;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.util.DhUtil;
import net.duohuo.dhroiddemos.util.IUtil;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 覆盖toast玩玩     项目开始时可以使用DialogImpl最后进行覆盖
 * @author duohuo-jinghao
 * @date 2014-8-19
 */
public class MyDialogImpl extends DialogImpl {

	@Override
	public void showToastShort(Context context, String msg) {
		if (!TextUtils.isEmpty(msg)) {
			super.showToastLong(context, msg);
			Toast toast = IocContainer.getShare().get(Toast.class);
			toast.setDuration(Toast.LENGTH_SHORT);
			View view = toast.getView();
			TextView text = (TextView) view.findViewById(R.id.ivory_toast_text);
			if (text == null) {
				View toastview = LayoutInflater.from(context).inflate(
						R.layout.toast_view, null);
				text = (TextView) toastview.findViewById(R.id.ivory_toast_text);
				LayoutParams params = text.getLayoutParams();
				params.width = IUtil.getDisplaywidth();
				text.setLayoutParams(params);
				toast.setView(toastview);
				toast.setGravity(Gravity.TOP, 0, DhUtil.dip2px(context, 48));
			}
			text.setText(msg);
			toast.show();
		}
	}

	@Override
	public void showToastLong(Context context, String msg) {
		showToastShort(context, msg);
	}

	@Override
	public void showToastType(Context context, String msg, String type) {
		showToastShort(context, msg);
	}

}
