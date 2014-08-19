package net.duohuo.dhroid.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.duohuo.dhroid.Const;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.NetUtil;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.net.cache.CachePolicy;
import net.duohuo.dhroid.util.MD5;
import net.duohuo.dhroid.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

/**
 * 通过网络json的adapter ,默认使用缓存机制
 * 
 * @author duohuo-jinghao
 * 
 */
public class NetJSONAdapter extends BeanAdapter<JSONObject> implements
		INetAdapter {

	public List<FieldMap> fields;
	public DhNet dhnet;
	private int pageNo = 0;
	private int step = Const.netadapter_step_default;

	private boolean hasMore = true;
	public Integer total = 0;
	IDialog dialoger;
	private List<LoadSuccessCallBack> loadSuccessCallBackList;
	private LoadSuccessCallBack tempLoadSuccessCallBack;
	String fromWhat;
	public String pageParams = Const.netadapter_page_no;
	public String stepParams = Const.netadapter_step;

	Boolean isLoading = false;
	Dialog progressDialoger;
	// 第一页加载时显示对话框
	public boolean showProgressOnLoadFrist = true;

	private String timeline = null;

	private String timelineParam = Const.netadapter_timeline;

	private String timelineinjson = Const.netadapter_json_timeline;

	DataBulider dataBulider;

	public DataBulider getDataBulider() {
		return dataBulider;
	}

	public void setDataBulider(DataBulider dataBulider) {
		this.dataBulider = dataBulider;
	}

	/**
	 * 设置时间线的参数
	 * 
	 * @param timelineParam
	 * @param timelineinjson
	 */
	public void setTimelineParams(String timelineParam, String timelineinjson) {
		this.timelineParam = timelineParam;
		this.timelineinjson = timelineinjson;
	}

	public void addAll(JSONArray ones) {
		if (ones == null)
			return;
		List<JSONObject> list = new ArrayList<JSONObject>();
		for (int i = 0; i < ones.length(); i++) {
			try {
				list.add(ones.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		addAll(list);
	}

	/**
	 * 设置分页参数 page
	 * 
	 * @param pageParams
	 */
	public void setPageParams(String pageParams) {
		this.pageParams = pageParams;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getTotal() {
		return total;
	}

	/**
	 * 设置分页参数 step
	 * 
	 * @param stepParams
	 */
	public void setStepParams(String stepParams) {
		this.stepParams = stepParams;

	}

	/**
	 * 清空参数
	 */
	public void cleanParams() {
		dhnet.clear();
	}

	/**
	 * list加载段
	 * 
	 * @param fromWhat
	 */
	public void fromWhat(String fromWhat) {
		this.fromWhat = fromWhat;
	}

	public NetJSONAdapter(String api, Context context, int mResource) {
		super(context, mResource);
		dhnet = new DhNet(api);
		dhnet.setMethod(DhNet.METHOD_GET);
		fields = new ArrayList<FieldMap>();
		dialoger = IocContainer.getShare().get(IDialog.class);
		useCache(CachePolicy.POLICY_BEFORE_AND_AFTER_NET);
	}

	public NetJSONAdapter(String api, Context context, int mResource,
			boolean isViewReuse) {
		super(context, mResource, isViewReuse);
		dhnet = new DhNet(api);
		dhnet.setMethod(DhNet.METHOD_GET);
		fields = new ArrayList<FieldMap>();
		dialoger = IocContainer.getShare().get(IDialog.class);
		useCache(CachePolicy.POLICY_BEFORE_AND_AFTER_NET);
	}

	/**
	 * 修改url中的参数
	 * 
	 * @param tag
	 * @param value
	 * @return
	 */
	public DhNet fixURl(String tag, Object value) {
		return dhnet.fixURl(tag, value);
	}

	/**
	 * 添加参数
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public DhNet addparam(String key, Object value) {
		return dhnet.addParam(key, value);
	}

	/**
	 * 添加参数
	 * 
	 * @param params
	 * @return
	 */
	public DhNet addparams(Map<String, Object> params) {
		return dhnet.addParams(params);
	}
	
	/**
	 * 添加参数
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public DhNet addParam(String key, Object value) {
		return dhnet.addParam(key, value);
	}

	/**
	 * 添加参数
	 * 
	 * @param params
	 * @return
	 */
	public DhNet addParams(Map<String, Object> params) {
		return dhnet.addParams(params);
	}

	/**
	 * 网络访问方法
	 * 
	 * @param mehtod
	 * @return
	 */
	public DhNet setMethod(String mehtod) {
		return dhnet.setMethod(mehtod);
	}

	/**
	 * 取消网络访问
	 * 
	 * @param isInterrupt
	 * @return
	 */
	public Boolean cancel(Boolean isInterrupt) {
		return dhnet.cancel(isInterrupt);
	}

	/**
	 * 添加Field
	 * 
	 * @param key
	 * @param refid
	 * @return
	 */
	public NetJSONAdapter addField(String key, Integer refid) {
		FieldMap bigMap = new FieldMapImpl(key, refid);
		fields.add(bigMap);
		return this;
	}

	/**
	 * 添加Field
	 * 
	 * @param key
	 * @param refid
	 * @param type
	 * @return
	 */
	public NetJSONAdapter addField(String key, Integer refid, String type) {
		FieldMap bigMap = new FieldMapImpl(key, refid, type);
		fields.add(bigMap);
		return this;
	}

	/**
	 * 添加Field
	 * 
	 * @param fieldMap
	 * @return
	 */
	public NetJSONAdapter addField(FieldMap fieldMap) {
		fields.add(fieldMap);
		return this;
	}

	@Override
	public String getTItemId(int position) {
		JSONObject jo = getTItem(position);
		String key = getJumpKey();
		if (TextUtils.isEmpty(key)) {
			key = "id";
		}
		String id = JSONUtil.getString(jo, key);
		if (TextUtils.isEmpty(id)) {
			id = position + "";
		}
		return id;
	}

	@Override
	public long getItemId(int position) {
		JSONObject jo = getTItem(position);
		if (jo != null && jo.has("id")) {
			try {
				return jo.getInt("id");
			} catch (Exception e) {
				return position;
			}
		}
		return position;
	}

	/**
	 * 数据绑定
	 */
	@Override
	public void bindView(View itemV, int position, JSONObject item) {
		// 使用大家的viewholder模式
		ViewHolder viewHolder = ViewHolder.getHolder(itemV);
		JSONObject jo = (JSONObject) item;
		for (Iterator<FieldMap> iterator = fields.iterator(); iterator
				.hasNext();) {
			FieldMap fieldMap = iterator.next();
			View v = viewHolder.getView(fieldMap.getRefId());
			String value = JSONUtil.getString(jo, fieldMap.getKey());
			if (fieldMap instanceof FieldMapImpl && fixer != null) {
				Object gloValue = fixer.fix(value, fieldMap.getType());
				bindValue(position, v, gloValue,
						fixer.imageOptions(fieldMap.getType()));
			} else {
				Object ovalue = fieldMap.fix(itemV, position, value, jo);
				DisplayImageOptions options=null;
				if(fixer!=null){
					options=fixer.imageOptions(fieldMap.getType());
				}
				bindValue(position, v, ovalue,
						options);
			}
		}
	}

	/**
	 * 刷新
	 */
	public void refresh() {
		if (!isLoading) {
			hasMore = true;
			pageNo = 0;
			timeline = null;
			showNext();
		}
	}

	public void refreshDialog() {
		if (!isLoading) {
			hasMore = true;
			pageNo = 0;
			timeline = null;
			showNextInDialog();
		}
	}

	/**
	 * 加载成功后的回调
	 */
	public void setOnLoadSuccess(LoadSuccessCallBack loadSuccessCallBack) {
		if (this.loadSuccessCallBackList == null) {
			this.loadSuccessCallBackList = new ArrayList<INetAdapter.LoadSuccessCallBack>();
		}
		this.loadSuccessCallBackList.add(loadSuccessCallBack);
	}

	/**
	 * 只用一次的加载成功后的回调
	 */
	public void setOnTempLoadSuccess(LoadSuccessCallBack loadSuccessCallBack) {
		this.tempLoadSuccessCallBack = loadSuccessCallBack;

	}

	/**
	 * 是否有更多数据
	 */
	public Boolean hasMore() {
		return this.hasMore;
	}

	/**
	 * 网络任务处理
	 */
	NetTask nettask = new NetTask(mContext) {
		@Override
		public void doInBackground(Response response) {
			// 后台处理主要是数据封装
			JSONArray array = null;
			if (dataBulider != null) {
				array = dataBulider.onDate(response);
			} else if (fromWhat == null) {
				array = response.jSONArrayFromData();
			} else {
				array = response.jSONArrayFrom(fromWhat);
			}
			List<Object> jos = new ArrayList<Object>();
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					try {
						JSONObject jo = array.getJSONObject(i);
						jos.add(jo);
						if (i == array.length() - 1) {
							timeline = JSONUtil.getString(jo, timelineinjson);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			response.addBundle("list" + response.isCache(), jos);
			super.doInBackground(response);
		}

		@Override
		public void onErray(Response response) {
			super.onErray(response);
			isLoading = false;
			pageNo--;
			if (progressDialoger != null && progressDialoger.isShowing()) {
				progressDialoger.dismiss();
				progressDialoger = null;
			}
			if (dialoger != null) {
				if ("noNetError".equals(response.code)
						|| "netErrorButCache".equals(response.code)) {
				} else if ("netError".equals(response.code)) {
					if (pageNo > 1) {
						dialoger.showToastShort(mContext, response.msg);
					}
				} else {
					dialoger.showToastShort(mContext, response.msg);
				}
			}

			if (tempLoadSuccessCallBack != null) {
				tempLoadSuccessCallBack.callBack(response);
				tempLoadSuccessCallBack = null;
			}
			if (loadSuccessCallBackList != null) {
				for (Iterator iterator = loadSuccessCallBackList.iterator(); iterator
						.hasNext();) {
					LoadSuccessCallBack loadSuccessCallBack = (LoadSuccessCallBack) iterator
							.next();
					loadSuccessCallBack.callBack(response);
				}
			}
		}

		@Override
		public void onCancelled() {
			super.onCancelled();
			if (progressDialoger != null && progressDialoger.isShowing()) {
				progressDialoger.dismiss();
				progressDialoger = null;
			}
		}

		@Override
		public void doInUI(Response response, Integer transfer) {
			if (!response.isSuccess() && !response.isCache()) {
				if (!TextUtils.isEmpty(response.msg)) {
					dialoger.showToastShort(mContext, response.msg);
				}
			}
			List<JSONObject> list = response.getBundle("list"
					+ response.isCache());
			if (list.size() == 0) {
				if (dialoger != null) {
					if (!response.isCache()) {
						if (pageNo > 1) {
							dialoger.showToastShort(mContext,
									Const.netadapter_no_more);
						}
					}
				}
				hasMore = false;
			}
			if (pageNo == 1) {
				clear();
			}
			addAll(list);
			// 如果加载的收据不是来自缓存那么就不在使用缓存否者一直使用缓存
			if (!response.isCache) {
				useCache(CachePolicy.POLICY_NOCACHE);
			}
			isLoading = false;
			if (progressDialoger != null && progressDialoger.isShowing()) {

				progressDialoger.dismiss();
				progressDialoger = null;
			}
			if (tempLoadSuccessCallBack != null) {
				tempLoadSuccessCallBack.callBack(response);
				tempLoadSuccessCallBack = null;
			}
			if (loadSuccessCallBackList != null) {
				for (Iterator iterator = loadSuccessCallBackList.iterator(); iterator
						.hasNext();) {
					LoadSuccessCallBack loadSuccessCallBack = (LoadSuccessCallBack) iterator
							.next();
					loadSuccessCallBack.callBack(response);
				}
			}
		}
	};

	/**
	 * 加载下一页
	 */
	public void showNext() {
		synchronized (isLoading) {
			if (isLoading)
				return;
			isLoading = true;
		}
		if (showProgressOnLoadFrist && pageNo == 0) {
			if (NetworkUtils.isNetworkAvailable()) {
				progressDialoger = dialoger.showProgressDialog(mContext, "加载中");
			}
		}
		pageNo++;
		// dialoger.showToastLong(mContext, "page:"+pageNo);
		dhnet.addParam(pageParams, pageNo);
		dhnet.addParam(stepParams, step);
		dhnet.addParam(timelineParam, timeline);
		dhnet.execuse(nettask);
	}

	/**
	 * 第一也加载是否显示进度框
	 * 
	 * @param isShow
	 */
	public void showProgressOnFrist(boolean isShow) {
		showProgressOnLoadFrist = isShow;
	}

	/**
	 * 设置分页数
	 * 
	 * @param step
	 */
	public void setStep(int step) {
		this.step = step;
	}

	/**
	 * 加载下一页同时显示进度
	 */
	public void showNextInDialog() {
		synchronized (isLoading) {
			if (isLoading)
				return;
			isLoading = true;
		}
		pageNo++;
		dhnet.addParam(pageParams, pageNo);
		dhnet.addParam(stepParams, step);
		dhnet.addParam(timelineParam, timeline);
		dhnet.execuseInDialog(nettask);
	}

	/**
	 * 是否可以在加载
	 * 
	 * @param hasmore
	 */
	public void hasMore(boolean hasmore) {
		this.hasMore = hasmore;
	}

	/**
	 * 使用默认缓存
	 */
	public void useCache() {
		dhnet.useCache(true);
	}

	/**
	 * 使用缓存策略
	 * 
	 * @param policy
	 */
	public void useCache(CachePolicy policy) {
		dhnet.useCache(policy);
	}

	/**
	 * 获取一个独特的tag
	 */
	public String getTag() {
		String tag = dhnet.getUrl();
		Set<String> keys = dhnet.getParams().keySet();
		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			if (!string.equals(pageParams) && !string.equals(stepParams)
					&& !string.equals(Const.netadapter_timeline)) {
				tag += string + ":" + dhnet.getParams().get(string) + ";";
			}
		}
		try {
			return MD5.encryptMD5(tag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 移除成功回调
	 */
	@Override
	public void removeOnLoadSuccess(LoadSuccessCallBack loadSuccessCallBack) {
		if (loadSuccessCallBackList != null) {
			loadSuccessCallBackList.remove(loadSuccessCallBack);
		}
	}

	public interface DataBulider {
		public JSONArray onDate(Response response);
	}

}
