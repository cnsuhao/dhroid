package net.duohuo.dhroid.net;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.duohuo.dhroid.Const;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.net.cache.CacheManager;
import net.duohuo.dhroid.net.cache.CachePolicy;
import net.duohuo.dhroid.net.upload.CancelException;
import net.duohuo.dhroid.net.upload.ProgressMultipartEntity;
import net.duohuo.dhroid.net.upload.ProgressMultipartEntity.ProgressListener;
import net.duohuo.dhroid.util.NetworkUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import android.app.Dialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

/**
 * 网络任务处理 默认有的 code <br/>
 * netErrorButCache 网络访问超时,但是使用了缓存 <br/>
 * netCanceled 长传文件时取消了任务<br/>
 * noNetError 没有可用的网络<br/>
 * netError 其他网络故障
 * 
 * @author duohuo-jinghao
 */
public class DhNet {
	
	private String url = null;
	private Map<String, Object> params = new HashMap<String, Object>();
	private Map<String, File> files = new HashMap<String, File>();
	
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	
	private String method = "POST";
	
	Boolean isCanceled = false;
	
	Future<?> feture;
	
	NetTask task;
	//对话框内容
	String progressMsg;
	
	CacheManager cacheManager;
	
	CachePolicy cachePolicy = CachePolicy.POLICY_NOCACHE;

	// 最后一次访问网络花费的时间
	private static int lastSpeed = 10;

	GlobalParams globalParams;
	
	public DhNet() {
		this(null);
	}
	
	public DhNet(String url) {
		this(url, null);
	}

	public DhNet(String url, Map<String, Object> params) {
		super();
		if (url != null) {
			this.url = url.trim();
		}
		if(params!=null){
			this.params.putAll(params);
		}
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 清空
	 */
	public void clear() {
		params.clear();
		if(globalParams!=null){
			Map<String,String> globalparams=globalParams.getGlobalParams();
			this.params.putAll(globalparams);
		}
		files.clear();
		
		
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	/**
	 * 使用缓存
	 * 
	 * @param policy
	 */
	public void useCache(CachePolicy policy) {
		this.cachePolicy = policy;
		if (cachePolicy != CachePolicy.POLICY_NOCACHE) {
			if (cacheManager == null) {
				cacheManager = IocContainer.getShare().get(CacheManager.class);
			}
		}
	}

	/**
	 * 使用緩存
	 */
	public void useCache(Boolean userCache) {
		if (userCache) {
			this.cachePolicy = CachePolicy.POLICY_ON_NET_ERROR;
			if (cachePolicy != CachePolicy.POLICY_NOCACHE) {
				if (cacheManager == null) {
					cacheManager = IocContainer.getShare().get(
							CacheManager.class);
				}
			}
		} else {
			this.cachePolicy = CachePolicy.POLICY_NOCACHE;
		}
	}

	/**
	 * 使用緩存
	 */
	public void useCache() {
		this.cachePolicy = CachePolicy.POLICY_ON_NET_ERROR;
		if (cachePolicy != CachePolicy.POLICY_NOCACHE) {
			if (cacheManager == null) {
				cacheManager = IocContainer.getShare().get(CacheManager.class);
			}
		}
	}

	

	public DhNet fixURl(String tag, Object value) {
		if (value != null) {
			this.url = this.url.replace("<" + tag + ">", value.toString());
		}
		return this;
	}

	/**
	 * 添加参数
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public DhNet addParam(String key, Object value) {
		if (value instanceof TextView) {
			TextView text = (TextView) value;
			this.params.put(key.trim(), text.getText().toString());
		} else {
			this.params.put(key.trim(), value);
		}
		return this;
	}

	/**
	 * 添加参数
	 * 
	 * @param params
	 * @return
	 */
	public DhNet addParams(Map<String, Object> params) {
		this.params.putAll(params);
		return this;
	}

	/**
	 * 设置方法 GET POST
	 * 
	 * @param mehtod
	 * @return
	 */
	public DhNet setMethod(String mehtod) {
		this.method = mehtod;
		return this;
	}

	/**
	 * get方法访问
	 * 
	 * @param task
	 * @return
	 */
	public DhNet doGet(NetTask task) {
		this.method = METHOD_GET;
		execuse(task);
		return this;
	}

	/**
	 * get方法访问
	 * 
	 * @param task
	 * @return
	 */
	public DhNet doGet(boolean dialog,NetTask task) {
		this.method = METHOD_GET;
		return dialog?execuseInDialog(task):execuse(task);
	}
	
	/**
	 * get方法访问
	 * @param task
	 * @return
	 */
	public DhNet doGet(boolean dialog,String msg,NetTask task) {
		this.method = METHOD_GET;
		if(!TextUtils.isEmpty(msg)){
			setProgressMsg(msg);
		}
		return dialog?execuseInDialog(task):execuse(task);
	}
	
	/**
	 * get方法访问 ,同时打开对话框
	 * 
	 * @param task
	 * @return
	 */
	public DhNet doGetInDialog(NetTask task) {
		this.method = METHOD_GET;
		execuseInDialog(task);
		return this;
	}

	/**
	 * post方法访问
	 * 
	 * @param task
	 * @return
	 */
	public DhNet doPost(NetTask task) {
		this.method = METHOD_POST;
		execuse(task);
		return this;
	}

	/**
	 * post方法访问 ,同时打开对话框
	 * 
	 * @param task
	 * @return
	 */
	public DhNet doPostInDialog(NetTask task) {
		this.method = METHOD_POST;
		execuseInDialog(task);
		return this;
	}
	
	/**
	 * post方法访问 ,同时打开对话框
	 * 
	 * @param task
	 * @return
	 */
	public DhNet doPostInDialog(String msg,NetTask task) {
		if(!TextUtils.isEmpty(msg)){
			setProgressMsg(msg);
		}
		this.method = METHOD_POST;
		execuseInDialog(task);
		return this;
	}

	static ExecutorService executorService;

	/**
	 * 执行网络访问
	 * 
	 * @param task
	 * @return
	 */
	public DhNet execuse(NetTask task) {
		this.task = task;
		boolean isCacheOk = false;
		//添加全局参数
		globalParams=IocContainer.getShare().get(GlobalParams.class);
		if(globalParams!=null){
			Map<String,String> globalparams=globalParams.getGlobalParams();
			params.putAll(globalparams);
		}
		// 在加载前加载缓存
		if (cachePolicy == CachePolicy.POLICY_CACHE_ONLY
				|| cachePolicy == CachePolicy.POLICY_CACHE_AndRefresh
				|| cachePolicy == CachePolicy.POLICY_BEFORE_AND_AFTER_NET) {
			if (cacheManager != null) {
				String result = cacheManager.get(url, params);
				if (result != null) {
					Response response = new Response(result);
					response.isCache(true);
					try {
						DhNet.this.task.doInBackground(response);
						DhNet.this.task.doInUI(response,
								NetTask.TRANSFER_DOUI_ForCache);
						// 缓存有数据就返回
						isCacheOk = true;
						if (DhNet.this.task.dialog != null) {
							DhNet.this.task.dialog.dismiss();
						}
					} catch (Exception e) {
					}
					
					if (cachePolicy == CachePolicy.POLICY_CACHE_ONLY) {
						return this;
					}
				}
			}
		}

		final boolean isCacheOkf = isCacheOk;// 是否使用了缓存
		this.feture = executeRunalle(new Runnable() {
			public void run() {
				boolean hasNet = NetworkUtils.isNetworkAvailable();
				// 有网直接访问
				if (hasNet) {
					// 网络状态良好
					if (lastSpeed < HttpManager.DEFAULT_SOCKET_TIMEOUT) {
						HttpManager.longTimeOut();
					} else {
						// 网络不好
						HttpManager.shortTimeOut();
					}
					String url = DhNet.this.url;
					Map<String, Object> params = DhNet.this.params;
					try {
						long begin = System.currentTimeMillis();
						String result = NetUtil.sync(url, DhNet.this.method,
								params);
						long end = System.currentTimeMillis();
						lastSpeed = (int) ((end - begin) / 1000);
						Log.d("duohuo_DhNet", DhNet.this.url + " method: "
								+ method + " params: " + params + " result: "
								+ result);

						
						Response response = new Response(result);
						response.isCache(false);
						String code = response.getCode();
						
						DhNet.this.task.transfer(response,
									NetTask.TRANSFER_CODE);
						try {
						DhNet.this.task.doInBackground(response);
						}catch (Exception e) {
							e.printStackTrace();
						}
						if (code == null) {
							// 没有错误
							// 需要保存缓存
							if (cacheManager != null
									&& cachePolicy != CachePolicy.POLICY_NOCACHE) {
								if(response.jo!=null){
									cacheManager.creata(url, params,
											response.result);
								}
							}
						}
						if (!isCanceled) {
							// 当没有使用缓存或者缓存策略是网后更新
							if (!isCacheOkf
									|| cachePolicy == CachePolicy.POLICY_BEFORE_AND_AFTER_NET) {
								DhNet.this.task.transfer(response,
										NetTask.TRANSFER_DOUI);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						onNetError(e, isCacheOkf);
					}
				} else {
					onNoNet(isCacheOkf);
				}
			}
		});
		return this;
	}

	/**
	 * 在没有网的时候的处理
	 */
	private void onNoNet(Boolean hasUserCache) {

		if (cachePolicy == CachePolicy.POLICY_ON_NET_ERROR) {
			if (cacheManager != null) {
				String result = cacheManager.get(url, params);
				if (result != null) {
					Response response = new Response(result);
					response.isCache(true);
					DhNet.this.task.doInBackground(response);
					DhNet.this.task.transfer(response,
							NetTask.TRANSFER_DOUI_ForCache);
				}
			}
		}

		String errorjson = "{'success':false,'msg':'没有可用的网络','code':'noNetError'}";
		Response response = new Response(errorjson);
		DhNet.this.task.transfer(response, NetTask.TRANSFER_DOERROR);
	}

	/**
	 * 处理网路异常
	 * 
	 * @param e
	 */
	private void onNetError(Exception e, Boolean hasUserCache) {
		lastSpeed = HttpManager.DEFAULT_SOCKET_TIMEOUT + 1;
		// 网络访问出错
		if (e instanceof UnknownHostException) {
			Log.e("duohuo_Dhnet", "域名不对可能是没有配置网络权限");
		}
		boolean isFromCache = false;
		if (cacheManager != null
				&& cachePolicy == CachePolicy.POLICY_ON_NET_ERROR) {
			String result = cacheManager.get(url, params);
			if (result != null) {
				isFromCache = true;
				Response response = new Response(result);
				response.isCache(true);
				DhNet.this.task.doInBackground(response);
				DhNet.this.task.transfer(response,
						NetTask.TRANSFER_DOUI_ForCache);
			}
		}
		// 同时提示网络问题
		String errorjson;
		if (isFromCache) {
			errorjson = "{'success':false,'msg':'当前网络信号不好,使用缓存数据','code':'netErrorButCache'}";
		} else {
			errorjson = "{'success':false,'msg':'当前网络信号不好','code':'netError'}";
		}
		Response response = new Response(errorjson);
		response.addBundle("e", e);
		DhNet.this.task.transfer(response, NetTask.TRANSFER_DOERROR);
	}

	/**
	 * 执行同时打开对话框
	 * 
	 * @param task
	 * @return
	 */
	public DhNet execuseInDialog(NetTask task) {
		String msg=progressMsg;
		if(TextUtils.isEmpty(msg)){
			msg=method.toUpperCase().equals(METHOD_GET)?"加载中...":"提交中...";
		}
		IDialog dialoger = IocContainer.getShare().get(IDialog.class);
		if (dialoger != null) {
			Dialog dialog = dialoger
					.showProgressDialog(task.mContext, msg);
			task.dialog = dialog;
		}
		execuse(task);
		return this;
	}

	/**
	 * 线程池里跑runnable
	 * 
	 * @param runnable
	 * @return
	 */
	public static Future<?> executeRunalle(Runnable runnable) {
		if (executorService == null) {
			executorService = Executors.newFixedThreadPool(Const.net_pool_size);
		}
		return executorService.submit(runnable);
	}

	/**
	 * 取消访问 如果访问没有开始就永远不会启动访问<br/>
	 * 如果访问已经启动 如果isInterrupt 为 true 则访问会被打断 , 否则 会线程继续运行 取消时必定会调用 task
	 * 的onCancel方法
	 * 
	 * @return
	 */
	public Boolean cancel(Boolean isInterrupt) {
		this.isCanceled = true;
		if (feture != null) {
			feture.cancel(isInterrupt);
		}
		if (task != null) {
			task.onCancelled();
		}
		return true;
	}

	/**
	 * 当网络访问没启动或被取消都返回 false
	 * 
	 * @return
	 */
	public Boolean isCanceled() {
		if (isCanceled != null) {
			return isCanceled;
		}
		return false;
	}

	/**
	 * 获取全部的cookie
	 * 
	 * @return
	 */
	public static List<Cookie> getCookies() {
		return HttpManager.getCookieStore().getCookies();
	}

	public int TRANSFER_UPLOADING = -40000;

	
	public DhNet addFile( String name,  File file){
		files.put(name, file);
		return this;
	}
	
	public void upload(NetTask task) {
		this.task = task;
		this.feture = executeRunalle(new Runnable() {
			public void run() {
				String url=DhNet.this.url;
				HttpPost httpPost = new HttpPost(url);
				long fileLen=0;
				ProgressMultipartEntity mulentity = new ProgressMultipartEntity();
				for (String key : files.keySet()) {
					File file=files.get(key);
					if(file==null)continue;
					fileLen+= file.length();
					FileBody filebody = new FileBody(file);
					mulentity.addPart(key, filebody);
				}
				final long total=fileLen;
				mulentity.setProgressListener(new ProgressListener() {

					public void transferred(long num) {
						Response response = new Response("{success:true}");
						response.addBundle("uploading", true);
						response.addBundle("length", num);
						response.addBundle("total", total);
						DhNet.this.task.transfer(response, TRANSFER_UPLOADING);
					}
					public boolean isCanceled() {
						return DhNet.this.isCanceled();
					}
				});
				try {
					if (params != null) {
						for (String key : params.keySet()) {
							if (params.get(key) != null) {
								mulentity.addPart(
										key,
										new StringBody(params.get(key)
												.toString(), Charset
												.forName("UTF-8")));
							}
						}
					}
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				httpPost.setEntity(mulentity);
				HttpResponse response;
				try {
					response = HttpManager.execute(httpPost);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity rentity = response.getEntity();
						String result = EntityUtils.toString(rentity);
						Response myresponse = new Response(result);
						myresponse.addBundle("uploading", false);
						myresponse.addBundle("proccess", 100);
						DhNet.this.task.transfer(myresponse,
								NetTask.TRANSFER_DOUI);
					}
				} catch (Exception e) {
					if (e.getCause() instanceof UnknownHostException) {
						Log.e("duohuo_Dhnet", "域名不对可能是没有配置网络权限");
					}
					if (e instanceof CancelException) {
						String errorjson = "{'success':false,'msg':'上传任务已被取消','code':'netCanceled'}";
						Response myresponse = new Response(errorjson);
						myresponse.addBundle("e", e);
						DhNet.this.task.transfer(myresponse,
								NetTask.TRANSFER_DOERROR);
					} else {
						String errorjson = "{'success':false,'msg':'上传失败','code':'netError'}";
						Response myresponse = new Response(errorjson);
						myresponse.addBundle("e", e);
						DhNet.this.task.transfer(myresponse,
								NetTask.TRANSFER_DOERROR);
					}
				}
			}
		});
	}
	
	
	
	/**
	 * 文件上传, 支持大文件的上传 和文件的上传进度更新 task inui response 的bundle参数 uploading true
	 * 上传中,false 上传完毕 ; process 上传进度 0-100 cancel 方法可以取消上传
	 * @param url
	 * @param name
	 * @param file
	 * @param task
	 */
	public void upload(final String name, final File file, NetTask task) {
		addFile(name, file);
		upload(task);
	}
	

	/**
	 * 获取cookie的值
	 * 
	 * @param key
	 * @return
	 */
	public static String getCookie(String key) {
		List<Cookie> cookies = getCookies();
		for (Iterator<Cookie> iterator = cookies.iterator(); iterator.hasNext();) {
			Cookie cookie = iterator.next();
			if (cookie.getName().equals(key)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	/**
	 * 清空cookie
	 */
	public static void clearCookies() {
		HttpManager.getCookieStore().clear();
	}
	public void setProgressMsg(String progressMsg) {
		this.progressMsg = progressMsg;
	}
}
