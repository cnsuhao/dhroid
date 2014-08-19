package net.duohuo.dhroid.util;

import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.ioc.annotation.FieldsInjectable;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Parcel;
import android.text.TextUtils;
import com.google.gson.Gson;

/**
 * 
 * 用于存Preferences的获取与存储
 * 
 * @author duohuo
 * 
 */
@SuppressLint("ParcelCreator")
public class Perference implements FieldsInjectable, android.os.Parcelable {
	private static final DataSetObservable mDataSetObservable = new DataSetObservable();
	// 用户的用户名
	public String account;
	// 默认账户
	private static final String perference_def_account = "_per_def_account";

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * 
	 */
	public Perference load() {
		Context context = IocContainer.getShare().getApplicationContext();
		SharedPreferences p = context.getSharedPreferences(
				getClass().getName(), Context.MODE_WORLD_READABLE);
		if (TextUtils.isEmpty(account)) {
			account = p.getString("account", perference_def_account);
		}
		if (!TextUtils.isEmpty(account)) {
			String infos = p.getString(account, "");
			Gson gson = new Gson();
			Perference perference = gson.fromJson(infos, getClass());
			if (perference != null) {
				BeanUtil.copyBeanWithOutNull(perference, this);
			}
		}
		return this;
	}

	/**
	 * 
	 * @return
	 */
	public Perference commit() {
		Context context = IocContainer.getShare().getApplicationContext();
		SharedPreferences p = context.getSharedPreferences(
				getClass().getName(), Context.MODE_WORLD_READABLE);
		String json = new Gson().toJson(this);
		p.edit().putString(account, json).putString("account", account)
				.commit();
		notifyDataSetChanged();
		return this;
	}

	/**
	 * 通知数据修改
	 */
	public void notifyDataSetChanged() {
		mDataSetObservable.notifyChanged();
	}

	/**
	 * 注册数据观察者
	 * 
	 * @param observer
	 */
	public void registerDataSetObserver(DataSetObserver observer) {
		mDataSetObservable.registerObserver(observer);
	}

	/**
	 * 取消监听
	 * 
	 * @param observer
	 */
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mDataSetObservable.unregisterObserver(observer);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	}

	/**
	 * 清空当前用户的信息,同时会将数据重置为默认数据
	 */
	public void reset() {
		String maccount = account;
		Context context = IocContainer.getShare().getApplicationContext();
		SharedPreferences p = context.getSharedPreferences(
				getClass().getName(), Context.MODE_WORLD_READABLE);
		Perference perference = null;
		try {
			perference = this.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if (perference != null) {
			BeanUtil.copyBean(perference, this);
		}
		account = maccount;
		commit();
	}

	/**
	 * 清空所有用户的信息
	 */
	public void clearAll() {
		SharedPreferences p = IocContainer
				.getShare()
				.getApplicationContext()
				.getSharedPreferences(getClass().getName(),
						Context.MODE_WORLD_READABLE);
		p.edit().clear().commit();
		notifyDataSetChanged();
	}

	@Override
	public void injected() {
		// 创建时会自己加载
		load();
	}
}
