package net.duohuo.dhroid.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.duohuo.dhroid.ioc.annotation.FieldsInjectable;

import android.content.Context;

/**
 * 容器中的对象
 * 
 * @author duohuo-jinghao
 * 
 * @param
 */

@SuppressWarnings("rawtypes")
public class Instance {
	
	/**
	 * 对象作用域
	 * 
	 * @author Administrator
	 */
	public enum InstanceScope {

		// 应用中单例
		SCOPE_SINGLETON,
		// 每次创建一个
		SCOPE_PROTOTYPE;
	}
	
	//别名
	public AsAlians asAlians;
	
	//对应的类
	public Class clazz;
	
	//名
	public String name;
	
	//对象的作用域
	public InstanceScope scope;
	
	//绑定到对象
	public Class toClazz;
	
	//保存的对象
	public Object obj;
	
	//初始化构造
	public PerpareAction perpare;
	
	//保存context时的对象
	public Map<String,Object> objs;
	
	public static Stack<FieldsInjectable> injected=new Stack<FieldsInjectable>();
	
	public Object get(Context context) {
		//获取单例
		if (scope == InstanceScope.SCOPE_SINGLETON) {
			if (obj == null) {
				obj=bulidObj(context);
				injectChild(obj);
			}
			//这里需要先保证自己可以在容器中拿到然后才能注入
			
			return obj;
		//获取context类型的对象
		}else if (scope == InstanceScope.SCOPE_PROTOTYPE) {
			Object obj=bulidObj(context);
			//这里需要先保证自己可以在容器中拿到然后才能注入
			injectChild(obj);
			return obj;
		}
		return null;
	}
	
	private void injectChild(Object obj){
		if(obj instanceof FieldsInjectable){
			FieldsInjectable f=(FieldsInjectable) obj;
			injected.push(f);
			InjectUtil.inject(obj);
			if(injected.get(0)==f){
				while (!injected.isEmpty()) {
					FieldsInjectable popCall=injected.pop();
					popCall.injected();
				}	
			}
		}
	}
	
	
	/**
	 * 获取一个 tag相同的对象 如果不存在就创建,存在就获取
	 * @param clazz
	 * @param tag
	 * @return
	 */
	public Object get(Context context,String tag){
		if(objs==null){
			objs=new HashMap<String, Object>();
		}
		Object obj=objs.get(tag);
		if(obj==null){
			obj=bulidObj(context);
			objs.put(tag, obj);
			//这里需要先保证自己可以在容器中拿到然后才能注入
			injectChild(obj);
		}
		return obj;
	}
	
	
	/**
	 * 构建对象<br/>
	 * 如果传入的context 不为空会尝试用context构建对象 否者会调用默认构造函数
	 * @param context
	 * @return
	 */
	public Object bulidObj(Context context) {
		Object obj = null;
		Constructor construstor = null;
		if(context!=null){
			Constructor[] constructors = clazz.getDeclaredConstructors();
			for (int i = 0; i < constructors.length; i++) {
				Constructor ctr = constructors[i];
				Type[] types = ctr.getParameterTypes();
				if (types != null && types.length == 1
						&& types[0].equals(Context.class)) {
					construstor = ctr;
				}
			}
		}
		try {
			if (construstor != null) {
				obj= construstor.newInstance(context);
			} else {
				obj= clazz.newInstance();
			}
		} catch (IllegalArgumentException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		if(obj!=null&&perpare!=null){
			perpare.perpare(obj);
		}
		return obj;

	}

	
	public Instance(Class clazz) {
		this.clazz = clazz;
	}

	public Instance name(String name) {
		this.name = name;
		if (this.asAlians != null) {
			this.asAlians.as(this, name, null);
		}
		return this;
	}

	/**
	 * 需要注入的类型
	 * @param clazz
	 * @return
	 */
	public Instance to(Class clazz) {
		this.toClazz = clazz;
		if (this.asAlians != null) {
			this.asAlians.as(this, null, clazz);
		}
		return this;
	}
	/**
	 * 作用域
	 * @param scope
	 * @return
	 */
	public Instance scope(InstanceScope scope) {
		this.scope = scope;
		return this;
	}
	
	
	public void perpare(PerpareAction perpare){
		this.perpare=perpare;
	}
	
	/**
	 * 
	 * @param asAlians
	 */
	public void setAsAlians(AsAlians asAlians) {
		this.asAlians = asAlians;
	}

	public interface AsAlians {
		public void as(Instance me, String name, Class toClazz);
	}

	public interface PerpareAction{
		public void perpare(Object o);
	}
	
}