package net.duohuo.dhroid.view;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * 一直跑马灯的textView
 * @author Administrator
 *
 */
public class MarqueeTextView extends TextView {

	@Override
	public boolean isFocused() {
		return true;
	}
	public MarqueeTextView(Context context) {
		super(context);
		setEllipsize(TruncateAt.MARQUEE);
	}
	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEllipsize(TruncateAt.MARQUEE);
	}
	
	
}
