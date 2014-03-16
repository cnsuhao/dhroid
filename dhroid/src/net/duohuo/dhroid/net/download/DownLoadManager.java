package net.duohuo.dhroid.net.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.HttpManager;
import net.duohuo.dhroid.net.NetUtil;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.os.Handler;
import android.os.Message;

public class DownLoadManager {
	
	private final static int BUFFER = 1024; 
	Map<String,DownLoadCallBack> callbacks;
	Map<String,DownloadTask> tasks;
	
	public DownLoadManager() {
		super();
		this.callbacks=new HashMap<String, DownLoadManager.DownLoadCallBack>();
		this.tasks=new HashMap<String, DownloadTask>();
	}

	
	/**
	 * 下载
	 * @param url
	 * @param params
	 * @param path
	 */
	public void download(final String code,final String fileurl, final Map<String, Object> params, final String path){
		new Thread(new Runnable() {
			@Override
			public void run() {
				File file=new File(path+"_.temp");
				String dir=path.substring(0,path.lastIndexOf("/"));
				new File(dir).mkdirs();
				try {
					if(file.exists()) file.delete();
					file.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				DownloadTask task=new DownloadTask(code,fileurl,params,file);
				tasks.put(task.getCode(), task);
				String url=fileurl;
				if (!url.contains("?")) {
					url += "?";
				}else{
					if(!url.endsWith("&")){
						url+="&";
					}
				}
				HttpGet httpGet = new HttpGet(url + NetUtil.encodeUrl(task.getParams()));
				try {
					 HttpResponse response =getResponse(httpGet);
					long fileSize =  response.getEntity().getContentLength();
					task.setFileSize(fileSize);
					long  hasDown=0;
					task.setHasDown(hasDown);
					 InputStream in=response.getEntity().getContent();
					 FileOutputStream out = new FileOutputStream(task.getFile());
		             byte[] b = new byte[BUFFER];  
		             int len = 0;  
		             while((len=in.read(b))!= -1){  
		                 out.write(b,0,len); 
		                 hasDown+=len;
		             	 task.setHasDown(hasDown);
		             	if(task.isStop()){
		             		handler.sendMessage(handler.obtainMessage(onStop, task));
		             		 break;
		             	}else{
		             		handler.sendMessage(handler.obtainMessage(onPersent, task));
		             	}
		               
		             }  
		         	 handler.sendMessage(handler.obtainMessage(onEnd, task));
		         	 file.renameTo(new File(path));
		             in.close();  
		             out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}).start();
	
	}
	
	public HttpResponse getResponse(HttpGet httpGet ){
		try {
		 HttpResponse response = HttpManager.execute(httpGet);
			int statusCode=response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY ||
					statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
					    // 从头中取出转向的地址
					    Header[] locationHeader = response.getHeaders("location");
					    String location = null;
					    if (locationHeader != null) {
					     location = locationHeader[0].getValue();
					    } 
						httpGet = new HttpGet(location);
					 return getResponse(httpGet);
			}
			return response;
			} catch (Exception e) {
			}
		return null;
	}
	
	public void onStop(DownloadTask task){
		DownLoadCallBack callback=callbacks.get(task.getCode());
		if(callback!=null){
			callback.onStop(task);
		}
	}
	
	public void onPersent(DownloadTask task){
		DownLoadCallBack callback=callbacks.get(task.getCode());
		if(callback!=null){
			callback.onPersent(task, task.getPersent());
		}
	}
	
	public void onEnd(DownloadTask task){
		DownLoadCallBack callback=callbacks.get(task.getCode());
		if(callback!=null){
			callback.onEnd(task);
		}
		callbacks.remove(task.getCode());
		tasks.remove(task.getCode());
	}
	
	/**
	 * 检测是否含任务
	 * @param code
	 * @return
	 */
	public boolean hasTask(String code){
		return	tasks.containsKey(code);
	}
	
	/**
	 * 停止下载任务
	 * @param taskCode
	 */
	public void stopTask(String taskCode){
		DownloadTask task=tasks.get(taskCode);
		if(task!=null){
			task.setStop(true);
		}
	}
	
	/**
	 * 注册任务回调
	 * @param code
	 * @param callback
	 */
	public void regeisterCallBack(String code,DownLoadCallBack callback){
		callbacks.put(code, callback);
	}
	
	/**
	 * 取消任务回调
	 * @param code
	 */
	public void unregeisterCallBack(String code){
		callbacks.remove(code);
	}
	
	public interface DownLoadCallBack{

		public void	onStop(DownloadTask task);
		public void onPersent(DownloadTask task,float persent);
		public void	onEnd(DownloadTask task);
		
	}
	
	private static final int onStop=0x111;
	private static final int onPersent=0x222;
	private static final int onEnd=0x333;
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			DownloadTask task=(DownloadTask) msg.obj;
			switch (msg.what) {
			case onStop:
				onStop(task);
				break;
			case onPersent:{
				onPersent(task);
				break;
			}
			case onEnd:{
				onEnd(task);
				break;
			}
			default:
				break;
			}
		}
	};	
	
}
