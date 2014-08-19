package net.duohuo.dhroid.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.duohuo.dhroid.Const;
import net.duohuo.dhroid.adapter.INetAdapter.LoadSuccessCallBack;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.net.cache.CachePolicy;
import net.duohuo.dhroid.util.BeanUtil;
import net.duohuo.dhroid.util.MD5;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * 
 * 将网络数据封装为bean然后绑定,默认使用缓存机制
 * 
 * @author duohuo-jinghao
 * 
 */
public class NetBeanAdapter extends BeanAdapter implements INetAdapter {
	public List<FieldMap> fields;
	public DhNet dhnet;
	public int pageNo = 0;
	private int step = Const.netadapter_step_default;
	public String timeline = null;
	public boolean hasMore = true;
	public Integer total = 0;
	IDialog dialoger;
	private List<LoadSuccessCallBack> loadSuccessCallBackList;
	private LoadSuccessCallBack tempLoadSuccessCallBack;
	BuildList buildList;

	Class mClazz;
	String fromWhat;
	Dialog progressDialoger;
	public String pageParams = Const.netadapter_page_no;
	public String stepParams = Const.netadapter_step;
	Boolean isLoading = false;
	public boolean showProgressOnLoadFrist = true;

	public void setPageParams(String pageParams) {
		this.pageParams = pageParams;
	}

	public void setStepParams(String stepParams) {
		this.stepParams = stepParams;
	}

	public NetBeanAdapter(Class clazz, String url, Context context,
			int mResource) {
		super(context, mResource);
		dhnet = new DhNet(url);
		dhnet.setMethod(DhNet.METHOD_GET);
		fields = new ArrayList<FieldMap>();
		dialoger = IocContainer.getShare().get(IDialog.class);
		mClazz = clazz;
		useCache();
	}

	public void fromWhat(String fromWhat) {
		this.fromWhat = fromWhat;
	}

	public NetBeanAdapter addField(String key, Integer refid) {
		FieldMap bigMap = new FieldMapImpl(key, refid);
		fields.add(bigMap);
		return this;
	}

	public NetBeanAdapter addField(String key, Integer refid, String type) {
		FieldMap bigMap = new FieldMapImpl(key, refid, type);
		fields.add(bigMap);
		return this;
	}

	public NetBeanAdapter addField(FieldMap fieldMap) {
		fields.add(fieldMap);
		return this;
	}

	public void cleanParams() {
		dhnet.clear();
	}

	public DhNet fixURl(String tag, Object value) {
		return dhnet.fixURl(tag, value);
	}

	public DhNet addparam(String key, Object value) {
		return dhnet.addParam(key, value);
	}

	public DhNet addparams(Map<String, Object> params) {
		return dhnet.addParams(params);
	}

	public DhNet setMethod(String mehtod) {
		return dhnet.setMethod(mehtod);
	}

	public Boolean cancel(Boolean isInterrupt) {
		return dhnet.cancel(isInterrupt);
	}

	public void refresh() {
		if (!isLoading) {
			pageNo = 0;
			showNext();
		}
	}

	public void setOnLoadSuccess(LoadSuccessCallBack loadSuccessCallBack) {
		if (this.loadSuccessCallBackList == null) {
			this.loadSuccessCallBackList = new ArrayList<INetAdapter.LoadSuccessCallBack>();
		}
		this.loadSuccessCallBackList.add(loadSuccessCallBack);

	}

	public void setOnTempLoadSuccess(LoadSuccessCallBack loadSuccessCallBack) {
		this.tempLoadSuccessCallBack = loadSuccessCallBack;
	}

	public Boolean hasMore() {
		return this.hasMore;
	}

	NetTask nettask = new NetTask(mContext) {
		@Override
		public void doInBackground(Response response) {
			List<Object> list = null;
			if (buildList != null) {
				list = buildList.build(response);
			} else {
				if (fromWhat == null) {
					list = response.listFromData(mClazz);
				} else {
					list = response.listFrom(mClazz, fromWhat);
				}
			}
			response.addBundle("list", list);
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
				if ("noNetError".equals(response.code)) {
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
			dialoger.showToastShort(mContext, "取消加载");
		}

		@Override
		public void doInUI(Response response, Integer transfer) {
			if (progressDialoger != null) {
				progressDialoger.dismiss();
				progressDialoger = null;
			}
			List<Object> list = response.getBundle("list");
			if (list != null && list.size() == 0) {
				if (dialoger != null) {
					if (!response.isCache()) {
						if (pageNo > 1) {
							dialoger.showToastShort(mContext, "无更多数据");
						}
					}
				}
				hasMore = false;
			}

			if (pageNo == 1) {
				clear();
			}
			if (list != null) {
				addAll(list);
			}

			isLoading = false;
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

	public interface BuildList {
		public List build(Response response);
	}

	public BuildList getBuildList() {
		return buildList;
	}

	public void setBuildList(BuildList buildList) {
		this.buildList = buildList;
	}

	public void showNext() {
		synchronized (isLoading) {
			if (isLoading)
				return;
			isLoading = true;
		}
		if (showProgressOnLoadFrist && pageNo == 0) {
			progressDialoger = dialoger.showProgressDialog(mContext, "加载中");
		}
		pageNo++;
		dhnet.addParam(pageParams, pageNo);
		dhnet.addParam(stepParams, step);
		dhnet.addParam(Const.netadapter_timeline, timeline);
		dhnet.execuse(nettask);
	}

	public void showProgressOnFrist(boolean isShow) {
		showProgressOnLoadFrist = isShow;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public void showNextInDialog() {
		pageNo++;
		dhnet.addParam(Const.netadapter_page_no, pageNo);
		dhnet.addParam(Const.netadapter_step, step);
		dhnet.addParam(Const.netadapter_timeline, timeline);
		dhnet.execuseInDialog(nettask);
	}

	@Override
	public void bindView(View itemV, int position, Object jo) {
		for (Iterator<FieldMap> iterator = fields.iterator(); iterator
				.hasNext();) {
			FieldMap fieldMap = iterator.next();
			View v = itemV.findViewById(fieldMap.getRefId());
			String value = null;
			Object valueobj = BeanUtil.getProperty(jo, fieldMap.getKey());
			if (valueobj != null) {
				value = valueobj.toString();
			}
			if (fieldMap instanceof FieldMapImpl && fixer != null) {
				Object gloValue = fixer.fix(value, fieldMap.getType());
				bindValue(position, v, gloValue,fixer.imageOptions(fieldMap.getType()));
			} else {
				Object ovalue = fieldMap.fix(itemV, position, value, jo);
				DisplayImageOptions options=null;
				if(fixer!=null){
					options=fixer.imageOptions(fieldMap.getType());
				}
				bindValue(position, v, ovalue,options);
			}
		}
	}

	public void userCache(boolean userCache) {
		dhnet.useCache(userCache);

	}

	public void useCache() {
		dhnet.useCache(true);
	}

	public void useCache(CachePolicy policy) {
		dhnet.useCache(policy);
	}

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

	@Override
	public void removeOnLoadSuccess(LoadSuccessCallBack loadSuccessCallBack) {
		if(loadSuccessCallBackList!=null){
			loadSuccessCallBackList.remove(loadSuccessCallBack);
		}
	}
}
