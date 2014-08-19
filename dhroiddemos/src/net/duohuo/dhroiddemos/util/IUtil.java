/**
 * 
 */
package net.duohuo.dhroiddemos.util;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.Display;

/**
 * 
 * @author duohuo-jinghao
 * @date 2014-8-19
 */
public class IUtil {
	private static 	int displaywidth;
	private static 	int displayHeight;
	
	public static void init(Activity activity) {
		// 获取屏幕的宽度
		Display display = activity.getWindowManager().getDefaultDisplay();
		displaywidth = display.getWidth();
		displayHeight = display.getHeight();
	}

	public static int getDisplaywidth() {
		return displaywidth;
	}

	public static int getDisplayHeight() {
		return displayHeight;
	}
	
	
}
