package net.duohuo.dhroiddemos.net.bean;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class AdBean {

	public String des;
	public String title;
	public String width;
	public String height;
	public String src;
	public AdUrl url;
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public AdUrl getUrl() {
		return url;
	}
	public void setUrl(AdUrl url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "AdBean [des=" + des + ", title=" + title + ", width=" + width
				+ ", height=" + height + ", src=" + src + ", url=" + url + "]";
	}
	
	
}
