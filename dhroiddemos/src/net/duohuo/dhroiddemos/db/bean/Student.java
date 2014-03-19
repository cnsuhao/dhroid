package net.duohuo.dhroiddemos.db.bean;

import java.util.Date;

import net.duohuo.dhroid.db.ann.Column;
import net.duohuo.dhroid.db.ann.NoColumn;

public class Student {
	
	@Column(pk=true)
	public Long id;
	public String name;
	@Column(name="num_no")
	public String num;
	@Column(name="create_time")
	public Date createTime;
	public int age;
	public int sex;
	public boolean dangyuang;
	
	@NoColumn
	public String temp;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public boolean isDangyuang() {
		return dangyuang;
	}

	public void setDangyuang(boolean dangyuang) {
		this.dangyuang = dangyuang;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}
	
	
	
	
}
