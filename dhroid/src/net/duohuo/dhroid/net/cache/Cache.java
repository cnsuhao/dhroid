package net.duohuo.dhroid.net.cache;

import java.util.Date;

import net.duohuo.dhroid.db.ann.Column;

import android.os.Parcel;



/**
 * 缓存对象
 * @author duohuo-jinghao
 *
 */
public class Cache{
	public Cache() {
	}

	@Column(pk=true)
	public Integer id;
	public String key;
	public String result;
	public Long updateTime;
	
	

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
