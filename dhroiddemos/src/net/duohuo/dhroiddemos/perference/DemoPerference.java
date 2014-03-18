/**
 * 
 */
package net.duohuo.dhroiddemos.perference;

import net.duohuo.dhroid.util.Perference;
import net.duohuo.dhroiddemos.db.bean.Student;

/**
 *	
 * @author duohuo-jinghao
 * @date 2014-3-18
 */
public class DemoPerference extends Perference{
	//必须是public的属性不然不会赋值的
	public	String username;
	public int uid;
	public	Student student;
	
	/**
	 *自己写的刷新方法 
	 */
	public void refresh() {
		//基本是访问网络,然后赋值,最后提交
		username="用户名";
		uid=1212;
		student=new Student();
		student.setName("学生姓名");
		//提交
		commit();
	}
	
}
