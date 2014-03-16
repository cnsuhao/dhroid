package net.duohuo.dhroiddemos;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import net.duohuo.dhroid.adapter.ValueFix;

public class DemoValueFixer implements ValueFix{

	@Override
	public Object fix(Object o, String type) {
		if(o==null) return null;
		if(type.equals("sex")){
			if(o.toString().equals("1")){
				return "男";
			}else{
				return "女";
			}
		}
		return o;
	}

	@Override
	public DisplayImageOptions imageOptions(String type) {
		return null;
	}

	
	
}
