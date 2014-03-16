package net.duohuo.dhroid.util;

import com.google.gson.Gson;

import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.util.BeanUtil;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Parcel;
import android.text.TextUtils;


/**
 * 
 * 用于存Preferences的获取与存储
 * @author duohuo
 *
 */
public class Perference implements android.os.Parcelable{
	
	private  static final DataSetObservable mDataSetObservable = new DataSetObservable();
	
	
	
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	//用户的用户名
	public String account;
	
	/**
	 * 
	 */
	public Perference load(){
	   Context context=IocContainer.getShare().getApplicationContext();
		SharedPreferences p =context.getSharedPreferences(getClass().getName(),
				Context.MODE_WORLD_READABLE);
		if(TextUtils.isEmpty(account)){
			account= p.getString("account", "duohuodefault");
		}
		if(TextUtils.isEmpty(account)){
			account="duohuodefault";
		}
		if(!TextUtils.isEmpty(account)){
			String infos= p.getString(account, "");
			Gson gson=new Gson();
			Perference perference=gson.fromJson(infos, getClass());
			if(perference!=null){
				BeanUtil.copyBeanWithOutNull(perference, this);
			}
		}
		return this;
	}

	/**
	 * 
	 * @return
	 */
	public Perference commit(){
		   Context context=IocContainer.getShare().getApplicationContext();
			SharedPreferences p =context.getSharedPreferences(getClass().getName(),
					Context.MODE_WORLD_READABLE);
			String json=new Gson().toJson(this);
			p.edit().putString(account, json).putString("account", account).commit();
		return this;
	}	
	
	/**
	 * 通知数据修改
	 */
	public void notifyDataSetChanged(){
		mDataSetObservable.notifyChanged();
	}
	/**
	 * 注册数据观察者
	 * @param observer
	 */
	public void registerDataSetObserver(DataSetObserver observer){
		mDataSetObservable.registerObserver(observer);
	}
	/**
	 * 取消监听
	 * @param observer
	 */
	public void unregisterDataSetObserver(DataSetObserver observer){
		mDataSetObservable.unregisterObserver(observer);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
