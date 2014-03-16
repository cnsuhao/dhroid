package net.duohuo.dhroid.net.upload;

import java.io.File;

public 	class FileInfo{
	

	String name;
	File file;
	public FileInfo(String name, File file) {
		super();
		this.name = name;
		this.file = file;
	}
	public String getFileTextName() {
		return name;
	}
	public File getFile() {
		return file;
	}
	
}