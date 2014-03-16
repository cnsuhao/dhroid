package net.duohuo.dhroid.db;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.util.BeanUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * 数据库助手类  android的数据库实现了单表的增删改查就够了
 * @author duohuo-jinghao
 */
public class DhDB {

	SQLiteDatabase db;

	public DhDB() {
		super();

	}

	/**
	 * 在应用内创建数据库
	 * 
	 * @param dbname
	 * @param dbversion
	 */
	public void init(String dbname, int dbversion) {
		this.db = new SqliteDbHelper(IocContainer.getShare()
				.getApplicationContext(), dbname, dbversion)
				.getWritableDatabase();
	};

	/**
	 * 在SD中创建数据库
	 * 
	 * @param sdcardPath
	 * @param dbname
	 * @param dbversion
	 */
	public void initInSD(String sdcardPath, String dbname, int dbversion) {
		this.db = createDbFileOnSDCard(sdcardPath, dbname);
		if (db == null) {
			init(dbname, dbversion);
		}
	};
	
	/**
	 * 保存
	 * @param obj
	 */
	public void save(Object obj) {
		if (obj == null)
			return;
		checkOrCreateTable(obj.getClass());
		SqlProxy proxy = SqlProxy.insert(obj);
		db.execSQL(proxy.getSql(), proxy.paramsArgs());
	}

	/**
	 * 更新
	 * @param obj
	 */
	public void update(Object obj) {
		if (obj == null)
			return;
		checkOrCreateTable(obj.getClass());
		SqlProxy proxy = SqlProxy.update(obj);
		db.execSQL(proxy.getSql(), proxy.paramsArgs());
	}

	/**
	 * 删除
	 * @param obj
	 */
	public void delete(Object obj) {
		if (obj == null)
			return;
		checkOrCreateTable(obj.getClass());
		SqlProxy proxy = SqlProxy.delete(obj);
		db.execSQL(proxy.getSql(), proxy.paramsArgs());
	}
	/**
	 * 执行
	 * @param proxy
	 */
	public void execProxy(SqlProxy proxy) {
		db.execSQL(proxy.getSql(), proxy.paramsArgs());
	}
	
	/***
	 * 加载
	 * @param clazz
	 * @param id
	 * @return
	 */
	public <T> T  load(Class<T> clazz,Object id){
		EntityInfo info=EntityInfo.build(clazz);
		return	queryFrist(clazz, info.getPk()+"=?", id);
	}
	
	
	/**
	 * 查询
	 * @param proxy
	 * @return
	 */
	public <T> T queryFrist(Class<T> clazz, String where, Object... whereargs) {
		if (where.indexOf("limit") < -1) {
			where += " limit 0,1";
		}
		List<T> list=	queryList(clazz, where, whereargs);
		if(list==null||list.size()==0)return null;
		return list.get(0);
	}
	
	
	
	/**
	 * 查询
	 * @param proxy
	 * @return
	 */
	public <T> T queryFrist(SqlProxy proxy) {
		String sql=proxy.getSql();
		if (sql.indexOf("limit") < -1) {
			sql += " limit 0,1";
			proxy=SqlProxy.select(proxy.getRelClass(), sql, proxy.paramsArgs());
		}
		List<T> list=queryList(proxy);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	
	
	
	
	
	
	

	/**
	 * 查询
	 * @param proxy
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> queryList(SqlProxy proxy) {
		Cursor cursor = db.rawQuery(proxy.getSql(), proxy.paramsArgs());
		try {
			List<T> list = new ArrayList<T>();
			while (cursor.moveToNext()) {
				T t = (T) cursorToBean(cursor, proxy.getRelClass());
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			cursor = null;
		}
		return null;
	}

	/**
	 * 通过sql查询
	 * @param clazz
	 * @param sql
	 * @param args
	 * @return
	 */
	public <T> List<T> queryList(Class<T> clazz, String where, Object... whereargs) {
		checkOrCreateTable(clazz);
		SqlProxy proxy=SqlProxy.select(clazz, where, whereargs);
		return queryList(proxy);
	}
	
	public <T> List<T> queryAll(Class<T> clazz) {
		checkOrCreateTable(clazz);
		SqlProxy proxy=SqlProxy.select(clazz, null, null);
		return queryList(proxy);
	}
	
	
	/**
	 * 对象封装
	 * @param cursor
	 * @param clazz
	 * @return
	 */
	private <T> T cursorToBean(Cursor cursor, Class<T> clazz) {
		EntityInfo entity = EntityInfo.build(clazz);
		Set<String> keys = entity.getColumns().keySet();
		T obj = null;
		try {
			obj = clazz.newInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String column = entity.getColumns().get(key);
			Field field = BeanUtil.getDeclaredField(clazz, key);
			if (field.getType().equals(Integer.class)
					|| field.getType().equals(int.class)) {
				BeanUtil.setProperty(obj, key,
						cursor.getInt(cursor.getColumnIndex(column)));
			} else if (field.getType().equals(Long.class)
					|| field.getType().equals(long.class)) {
				BeanUtil.setProperty(obj, key,
						cursor.getLong(cursor.getColumnIndex(column)));
			} else if (field.getType().equals(Double.class)
					|| field.getType().equals(double.class)) {
				BeanUtil.setProperty(obj, key,
						cursor.getDouble(cursor.getColumnIndex(column)));
			} else if (field.getType().equals(Float.class)
					|| field.getType().equals(float.class)) {
				BeanUtil.setProperty(obj, key,
						cursor.getFloat(cursor.getColumnIndex(column)));
			} else if (field.getType().equals(String.class)) {
				BeanUtil.setProperty(obj, key,
						cursor.getString(cursor.getColumnIndex(column)));
			} else if (field.getType().equals(Date.class)) {
				try {
					BeanUtil.setProperty(obj, key, new Date(cursor
							.getLong(cursor.getColumnIndex(column))));
				} catch (Exception e) {
				}
			} else if (field.getType().equals(Boolean.class)
					|| field.getType().equals(boolean.class)) {
				BeanUtil.setProperty(obj, key, cursor.getInt(cursor
						.getColumnIndex(column)) == 0 ? false : true);
			}
		}

		return obj;
	}

	/**
	 * 检查表
	 * 
	 * @param clazz
	 */
	public void checkOrCreateTable(Class clazz) {
		EntityInfo info = EntityInfo.build(clazz);
		if (info.isChecked()) {
			return;
		} else {
			boolean isexit = checkTable(info.table);
			if (!isexit) {
				String sql = getCreatTableSQL(clazz);
				db.execSQL(sql);
			}
			info.setChecked(true);
		}
	}

	/**
	 * 检查表是否存在
	 * 
	 * @param table
	 * @return
	 */
	private boolean checkTable(String table) {
		Cursor cursor = null;
		try {
			String sql = "SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='"
					+ table + "' ";
			log(sql);
			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			cursor = null;
		}
		return false;
	}

	private void log(String msg) {
		Log.i(getClass().getSimpleName(), msg);
	}

	/**
	 * 创建本地数据库
	 * 
	 * @param sdcardPath
	 * @param dbfilename
	 * @return
	 */
	private SQLiteDatabase createDbFileOnSDCard(String sdcardPath,
			String dbfilename) {
		File dbf = new File(sdcardPath, dbfilename);
		if (!dbf.exists()) {
			try {
				if (dbf.createNewFile()) {
					return SQLiteDatabase.openOrCreateDatabase(dbf, null);
				}
			} catch (IOException e) {

			}
		} else {
			return SQLiteDatabase.openOrCreateDatabase(dbf, null);
		}

		return null;
	}
	private static String getCreatTableSQL(Class<?> clazz){
		EntityInfo info=EntityInfo.build(clazz);
		StringBuffer sql = new StringBuffer();
		sql.append("CREATE TABLE IF NOT EXISTS ");
		sql.append(info.getTable());
		sql.append(" ( ");
		Map<String, String> propertys = info.getColumns();
		Set<String> keys=propertys.keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			sql.append(propertys.get(key));
			Class<?> dataType = BeanUtil.getDeclaredField(clazz, key).getType(); 
			if( dataType== int.class || dataType == Integer.class 
			   || dataType == long.class || dataType == Long.class){
				sql.append(" INTEGER");
			}else if(dataType == float.class ||dataType == Float.class 
					||dataType == double.class || dataType == Double.class){
				sql.append(" REAL");
			}else if (dataType == boolean.class || dataType == Boolean.class) {
				sql.append(" NUMERIC");
			}
			if(key.equals(info.pk)){
				sql.append(" PRIMARY KEY");
				if(info.pkAuto){
					sql.append(" AUTOINCREMENT");
				}
			}
			sql.append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" )");
		return sql.toString();
	}
	/**
	 * 删除所有数据表
	 */
	public void dropDb() {
		Cursor cursor = db
				.rawQuery(
						"SELECT name FROM sqlite_master WHERE type ='table' AND name != 'sqlite_sequence'",
						null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				db.execSQL("DROP TABLE " + cursor.getString(0));
			}
		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}

	class SqliteDbHelper extends SQLiteOpenHelper {

		public SqliteDbHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			dropDb();
		}

	}

}
