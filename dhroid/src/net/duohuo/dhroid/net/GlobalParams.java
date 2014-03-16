package net.duohuo.dhroid.net;

import java.util.HashMap;
import java.util.Map;

public class GlobalParams {
	Map<String,String> params=new HashMap<String, String>();

	public Map<String,String> getGlobalParams() {
		return params;
	}
	
	public void setGlobalParam(String key,String value) {
		params.put(key, value);
	}
	
	public String  getGlobalParam(String key){
		return params.get(key);
	}
	
}
