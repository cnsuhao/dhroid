package net.duohuo.dhroid.adapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.view.Display;

/**
 * 
 * 全局的网络数据修复
 * 应用中基本需要实现的
 * 
 * @author duohuo-jinghao
 *
 */
public interface ValueFix {
	
	public Object fix(Object o,String type);
	public DisplayImageOptions imageOptions(String type);
	
}
