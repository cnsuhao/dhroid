package net.duohuo.dhroid.adapter;

import android.view.View;

/**
 * 
 * 用于netAdapter的数据绑定
 * @author duohuo-jinghao
 *
 */
public abstract class FieldMap {

	protected FieldMap(String key, Integer refId) {
		super();
		this.key = key;
		this.refId = refId;
	}

	protected FieldMap(String key, Integer refId, String type) {
		super();
		this.key = key;
		this.refId = refId;
		this.type = type;
	}
	
	String key;
	Integer refId;
	String type;
	
	public abstract Object fix(View itemV,Integer position,Object o,Object jo);
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Integer getRefId() {
		return refId;
	}
	public void setRefId(Integer refId) {
		this.refId = refId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}