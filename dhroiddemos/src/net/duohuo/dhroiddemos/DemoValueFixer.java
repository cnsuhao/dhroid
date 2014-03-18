package net.duohuo.dhroiddemos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;

import net.duohuo.dhroid.adapter.ValueFix;
import net.duohuo.dhroid.util.ImageUtil;

public class DemoValueFixer implements ValueFix {

	// 头像用圆角
	public static DisplayImageOptions optionsHeadRound;
	public static Map<String, DisplayImageOptions> imageOptions;

	public DemoValueFixer() {
		imageOptions = new HashMap<String, DisplayImageOptions>();
		DisplayImageOptions	optionsDefault = new DisplayImageOptions.Builder()
		.cacheInMemory()
		.cacheOnDisc().build();
		imageOptions.put("default", optionsDefault);
		optionsHeadRound = new DisplayImageOptions.Builder().cacheInMemory()
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showStubImage(R.drawable.ic_launcher)
				.displayer(new BitmapDisplayer() {

					@Override
					public Bitmap display(Bitmap bitmap, ImageView imageView) {
						bitmap = ImageUtil.zoomBitmap(bitmap, 200, 200);
						bitmap = ImageUtil.toRoundCorner(bitmap,
								bitmap.getWidth() / 6);
						imageView.setImageBitmap(bitmap);
						return bitmap;
					}

				}).cacheInMemory().cacheOnDisc().build();
		imageOptions.put("round", optionsHeadRound);
	}

	@Override
	public Object fix(Object o, String type) {
		if (o == null)
			return null;
		if ("time".equals(type)) {
			return getStandardTime(Long.parseLong(o.toString()) * 1000,
					"yyyy-MM-dd");
		}
		if ("sex".equals(type)) {
			if (o.toString().equals("1")) {
				return "男";
			} else {
				return "女";
			}
		}
		return o;
	}
	/**
	 * 时间转字符串
	 * @param timestamp
	 * @param pattern
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getStandardTime(long timestamp, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = new Date(timestamp);
		sdf.format(date);
		return sdf.format(date);
	}
	@Override
	public DisplayImageOptions imageOptions(String type) {
		DisplayImageOptions option=imageOptions.get(type);
		if(option==null){
			option=imageOptions.get("default");
		}
		return option;
	}

}
