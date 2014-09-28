package net.duohuo.dhroid.db;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.text.format.DateFormat;

import net.duohuo.dhroid.util.BeanUtil;


/**
 * sql组装代理类
 */
public class SqlProxy {
	
	StringBuffer sql;
	List<Object> params;
	Class clazz;
	
	public Class getRelClass(){
		return clazz;
	}
	
	
	private SqlProxy() {
		sql=new StringBuffer();
		params=new ArrayList<Object>();
	}
	
	/**
	 * 插入数据
	 * @param obj
	 * @return
	 */
	public static SqlProxy insert(Object obj){
		SqlProxy	proxy=new SqlProxy();
		EntityInfo entity=EntityInfo.build(obj.getClass());
		proxy.sql.append("INSERT INTO ").append(entity.getTable()).append("(");
		Set<String> keys=entity.getColumns().keySet();
		StringBuffer p=new StringBuffer("(");
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			//自增主键不管
			if(key.equals(entity.pk)&&entity.pkAuto)continue;
			proxy.sql.append(entity.getColumns().get(key)).append(",");
			p.append("?,");
			proxy.params.add(BeanUtil.getProperty(obj, key));
		}
		proxy.sql.deleteCharAt(proxy.sql.length()-1);
		p.deleteCharAt(p.length()-1);
		proxy.	sql.append(")");
		p.append(")");
		proxy.sql.append(" VALUES ").append(p);
		proxy.clazz=obj.getClass();
		return proxy;
	}
	
	/**
	 * 更新数据
	 * @param obj
	 * @return
	 */
	public static SqlProxy update(Object obj){
		SqlProxy	proxy=new SqlProxy();
		EntityInfo entity=EntityInfo.build(obj.getClass());
		String pk=entity.getPk();
		if(TextUtils.isEmpty(pk)){
			throw new RuntimeException("主键不可为空");
		}
		proxy.sql.append("UPDATE ").append(entity.getTable()).append(" SET ");
		Set<String> keys=entity.getColumns().keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			proxy.sql.append(entity.getColumns().get(key)).append("=? ,");
			proxy.params.add(BeanUtil.getProperty(obj, key));
		}
		proxy.sql.deleteCharAt(proxy.sql.length()-1);
		proxy.sql.append(" WHERE ").append(entity.getColumns().get(pk)).append("=?");
		proxy.params.add(BeanUtil.getProperty(obj, pk));
		proxy.clazz=obj.getClass();
		return proxy;
	}
	
	/**
	 * 根据条件更新数据
	 * @param obj
	 * @param where
	 * @return
	 */
	public static SqlProxy update(Object obj,String where,Object... whereargs){
		SqlProxy	proxy=new SqlProxy();
		EntityInfo entity=EntityInfo.build(obj.getClass());
		String pk=entity.getPk();
		if(TextUtils.isEmpty(pk)){
			throw new RuntimeException("主键不可为空");
		}
		proxy.sql.append("UPDATE ").append(entity.getTable()).append("SET ");
		Set<String> keys=entity.getColumns().keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			proxy.sql.append(entity.getColumns().get(key)).append("=? ,");
			proxy.params.add(BeanUtil.getProperty(obj, key));
		}
		proxy.sql.deleteCharAt(proxy.sql.length()-1);
		proxy.clazz=obj.getClass();
		proxy.buildWhere(where, whereargs);
		return proxy;
	}
	
	private void buildWhere(String where,Object[] whereargs){
		if(TextUtils.isEmpty(where))return;
		sql.append(" WHERE ");
		EntityInfo entity=EntityInfo.build(clazz);
		Pattern pattern=Pattern.compile(":([[a-zA-Z]|\\.]*)");
		Matcher matcher=pattern.matcher(where);
		while (matcher.find()) {
			String key=	matcher.group();
			where=where.replace(key, entity.getColumns().get(matcher.group(1)));
		}
		sql.append(where);
		if(whereargs!=null){
			for (int i = 0; i < whereargs.length; i++) {
				params.add(whereargs[i]);
			}
		}
	}
	
	
	/**
	 * 更新条件更新部分数据
	 * @param values
	 * @param where
	 * @return
	 */
	public static SqlProxy update(Class clazz,Map<String,Object> values,String where,Object... whereargs){
		SqlProxy	proxy=new SqlProxy();
		EntityInfo entity=EntityInfo.build(clazz);
		String pk=entity.getPk();
		if(TextUtils.isEmpty(pk)){
			throw new RuntimeException("主键不可为空");
		}
		proxy.sql.append("UPDATE ").append(entity.getTable()).append("SET ");
		Set<String> keys=entity.getColumns().keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			if(values.get(key)!=null){
				proxy.sql.append(entity.getColumns().get(key)).append("=? ,");
				proxy.params.add(values.get(key));
			}
		}
		proxy.sql.deleteCharAt(proxy.sql.length()-1);
		proxy.clazz=clazz;
		proxy.buildWhere(where, whereargs);
		return proxy;
		
	}
	
	
	/**
	 * 删除数据
	 * @param obj
	 * @return
	 */
	public static SqlProxy delete(Object obj){
		SqlProxy	proxy=new SqlProxy();
		EntityInfo entity=EntityInfo.build(obj.getClass());
		String pk=entity.getPk();
		if(TextUtils.isEmpty(pk)){
			throw new RuntimeException("主键不可为空");
		}
		proxy.sql.append("DELETE FROM ").append(entity.getTable()).append(" WHERE ")
		.append(entity.getColumns().get(pk))
		.append("=?");
		proxy.params.add(BeanUtil.getProperty(obj, pk));
		proxy.clazz=obj.getClass();
		return proxy;
	}
	
	/**
	 * 根据条件删除数据
	 * @param where
	 * @return
	 */
	public static SqlProxy delete(Class clazz,String where,Object... whereargs){
		SqlProxy	proxy=new SqlProxy();
		EntityInfo entity=EntityInfo.build(clazz);
		proxy.sql.append("DELETE FROM ").append(entity.getTable());
		proxy.clazz=clazz;
		proxy.buildWhere(where, whereargs);
		return proxy;
	}
	
	/**
	 * 删除数据
	 * @param clazz
	 * @param pkvalue
	 * @return
	 */
	public static SqlProxy delete(Class clazz,Object pkvalue){
		SqlProxy	proxy=new SqlProxy();
		EntityInfo entity=EntityInfo.build(clazz);
		String pk=entity.getPk();
		if(TextUtils.isEmpty(pk)){
			throw new RuntimeException("主键不可为空");
		}
		proxy.sql.append("DELETE FROM ").append(entity.getTable()).append(" WHERE ")
		.append(entity.getColumns().get(pk))
		.append("=?");
		proxy.params.add(pkvalue);
		proxy.clazz=clazz;
		return proxy;
	}
	
	
	
	
	public static SqlProxy select(Class clazz,String where,Object... whereargs){
		SqlProxy	proxy=new SqlProxy();
		EntityInfo entity=EntityInfo.build(clazz);
		proxy.sql.append("SELECT * FROM ").append(entity.getTable());
		proxy.clazz=clazz;
		proxy.buildWhere(where, whereargs);
		return proxy;
	}
	
	public String getSql(){
		return sql.toString();
	}
	
	public String[] paramsArgs(){
		String[] args=new String[params.size()];
		for (int i = 0; i < args.length; i++) {
			Object obj=params.get(i);
			if(obj!=null){
				if(obj.getClass().equals(Date.class)){
					Date date=(Date) obj;
					args[i]=date.getTime()+"";
				}else{
					args[i]=params.get(i).toString();
				}
			
			}
		}
		return args;
	}
	
	
	
}
