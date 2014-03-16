package net.duohuo.dhroid.adapter;

import net.duohuo.dhroid.net.Response;

/**
 * 网络adapter接口
 * @author Administrator
 *
 */
public interface INetAdapter {

	public String getTag();
	
	public void refresh();
	
	public void setOnLoadSuccess(LoadSuccessCallBack loadSuccessCallBack);
	
	public void removeOnLoadSuccess(LoadSuccessCallBack loadSuccessCallBack);
	
	public void setOnTempLoadSuccess(LoadSuccessCallBack loadSuccessCallBack);
	
	public Boolean hasMore();
	
	public void showNext();
	
//	public boolean isLoding();
	
	public void showNextInDialog();

	public interface LoadSuccessCallBack {
		public void callBack(Response response);
	}
}
