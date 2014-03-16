package net.duohuo.dhroid.net.download;

import java.io.File;
import java.util.Map;

import android.os.Handler;

/**
 * 文件下载任务
 * @author Administrator
 *
 */
public class DownloadTask {
	private final static int BUFFER = 1024; 
	/**
	 * 任务编码
	 */
	public String code;
	
	/**
	 * 下载路径
	 */
	public String url;
	
	/**
	 * 下载附加参数
	 */
	private Map<String,Object> params;
	
	/**
	 * 保存文件到
	 */
	private File file;


	
	public DownloadTask(String code,String url, Map<String, Object> params, File file) {
		super();
		this.code=code;
		this.url = url;
		this.params = params;
		this.file = file;
		hasDown=0;
		fileSize=0;
		isStop=false;
	}

	/**
	 *已下载长度
	 */
	private long hasDown;
	
	/**
	 * 文件大小
	 */
	private long fileSize;
	
	/**
	 * 是否已停止
	 */
	private boolean isStop;
	
	
	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isStop() {
		return isStop;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public float getPersent() {
		return hasDown/(float)fileSize;
	}

	public long getHasDown() {
		return hasDown;
	}

	public void setHasDown(long hasDown) {
		this.hasDown = hasDown;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
	
	
}
