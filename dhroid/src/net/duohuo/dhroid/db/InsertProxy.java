/**
 * 
 */
package net.duohuo.dhroid.db;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import net.duohuo.dhroid.util.BeanUtil;
import android.content.ContentValues;

/**
 * 
 * @author duohuo-jinghao
 * @date 2014-8-20
 */
public class InsertProxy {
	String table;
	ContentValues values;

	public static InsertProxy insert(Object obj) {
		InsertProxy proxy = new InsertProxy();
		EntityInfo entity = EntityInfo.build(obj.getClass());
		proxy.table = entity.getTable();
		Set<String> keys = entity.getColumns().keySet();
		ContentValues values = new ContentValues();
		proxy.values = values;
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			// 自增主键不管
			if (key.equals(entity.pk) && entity.pkAuto)
				continue;
			Object value = BeanUtil.getProperty(obj, key);
			if (value == null)
				continue;
			if (value instanceof String) {
				String new_name = (String) value;
				values.put(entity.getColumns().get(key), new_name);
			}
			if (value.getClass() == Integer.class
					|| value.getClass() == int.class) {
				Integer new_name = (Integer) value;
				values.put(entity.getColumns().get(key), new_name);
			}
			if (value.getClass() == Long.class
					|| value.getClass() == long.class) {
				Long new_name = (Long) value;
				values.put(entity.getColumns().get(key), new_name);
			}
			if (value.getClass() == Float.class
					|| value.getClass() == float.class) {
				Float new_name = (Float) value;
				values.put(entity.getColumns().get(key), new_name);
			}
			if (value.getClass() == Double.class
					|| value.getClass() == double.class) {
				Double new_name = (Double) value;
				values.put(entity.getColumns().get(key), new_name);
			}
			if (value.getClass() == Boolean.class
					|| value.getClass() == boolean.class) {
				Boolean new_name = (Boolean) value;
				values.put(entity.getColumns().get(key), new_name);
			}
			if (value.getClass() == Date.class) {
				Date new_name = (Date) value;
				values.put(entity.getColumns().get(key), new_name.getTime());
			}
		}
		return proxy;
	}

}
