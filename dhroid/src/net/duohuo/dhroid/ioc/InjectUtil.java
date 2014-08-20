package net.duohuo.dhroid.ioc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.duohuo.dhroid.Const;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectAssert;
import net.duohuo.dhroid.ioc.annotation.InjectExtra;
import net.duohuo.dhroid.ioc.annotation.InjectResource;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.thread.Task;
import net.duohuo.dhroid.thread.ThreadWorker;
import net.duohuo.dhroid.util.FileUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;

/**
 * 注入工具类
 * 
 * @author duohuo-jinghao
 * 
 */
@SuppressWarnings("rawtypes")
public class InjectUtil {

	public static final String LOG_TAG = "duohuo_InjectUtil";

	/**
	 * 在activity中注入 在activity setContext 后调用 其他的类中可以初始化时可调用
	 * 
	 * @param activity
	 * @param layoutResId
	 */
	public static void inject(Object obj) {
		if (obj == null)
			return;
		// 本类中的所有属性
		Field[] fields = getDeclaredFields(obj.getClass());
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				field.setAccessible(true);
				try {
					if (field.get(obj) != null)
						continue;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				// 注入view
				InjectView viewInject = field.getAnnotation(InjectView.class);
				if (viewInject != null) {
					indectView(obj, field, viewInject);
				}
				// 标准ioc注入
				Inject inject = field.getAnnotation(Inject.class);
				if (inject != null) {
					injectStand(obj, field, inject);
				}

				if (obj instanceof Activity || obj instanceof Fragment) {
					// extra
					InjectExtra extra = field.getAnnotation(InjectExtra.class);
					if (extra != null) {
						getExtras(obj, field, extra);
					}
				}

				InjectResource resource = field
						.getAnnotation(InjectResource.class);
				if (resource != null) {
					getResource(obj, field, resource);
				}
				InjectAssert as = field.getAnnotation(InjectAssert.class);
				if (as != null) {
					getAssert(obj, field, as);
				}
			}

		}
	}

	private static void getResource(Object obj, Field field,
			InjectResource resource) {
		Resources res = IocContainer.getShare().getApplication().getResources();
		Object value = null;
		if (resource.color() != 0) {
			value = res.getColor(resource.color());
		} else if (resource.drawable() != 0) {
			value = res.getDrawable(resource.drawable());
		} else if (resource.string() != 0) {
			value = res.getString(resource.string());
		} else if (resource.dimen() != 0) {
			value = res.getDimensionPixelSize(resource.dimen());
		}
		if (value != null) {
			try {
				field.set(obj, value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static void unInjectView(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				field.setAccessible(true);
				// 注入view
				InjectView viewInject = field.getAnnotation(InjectView.class);
				if (viewInject != null) {
					try {
						field.set(obj, null);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 注入
	 * 
	 * @param obj
	 * @param field
	 * @param injectview
	 */
	public static void indectView(Object obj, Field field, InjectView injectview) {
		// view
		InjectView viewInject = field.getAnnotation(InjectView.class);
		View view = null;

		int layout = viewInject.layout();
		// layout中获取
		if (layout != 0) {
			view = LayoutInflater.from(
					IocContainer.getShare().getApplicationContext()).inflate(
					layout, null);
		} else {
			// 在其他view中的view
			String inView = viewInject.inView();
			if (!TextUtils.isEmpty(inView)) {
				try {
					Field inViewField = obj.getClass().getDeclaredField(inView);
					inViewField.setAccessible(true);
					View parentView = (View) inViewField.get(obj);
					if (parentView == null) {
						indectView(obj, inViewField,
								inViewField.getAnnotation(InjectView.class));
						parentView = (View) inViewField.get(obj);
					}
					view = parentView.findViewById(viewInject.id());
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else {
				// 在activity中的view
				if (obj instanceof Activity) {
					Activity act = (Activity) obj;
					view = act.findViewById(viewInject.id());
				}
				if (obj instanceof Dialog) {
					Dialog act = (Dialog) obj;
					view = act.findViewById(viewInject.id());
				}
				if (obj instanceof Fragment) {
					Fragment act = (Fragment) obj;
					view = act.getView().findViewById(viewInject.id());
				} else if (obj instanceof View) {
					View vtemp = (View) obj;
					view = vtemp.findViewById(viewInject.id());
				} else if (obj instanceof ContentView) {
					ContentView vtemp = (ContentView) obj;
					view = vtemp.getContentView().findViewById(viewInject.id());
				}
			}
		}

		try {
			field.set(obj, view);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		// 事件绑定
		String clickMethod = viewInject.click();
		if (!TextUtils.isEmpty(clickMethod))
			setViewClickListener(obj, field, clickMethod);

		String longClickMethod = viewInject.longClick();
		if (!TextUtils.isEmpty(longClickMethod))
			setViewLongClickListener(obj, field, longClickMethod);

		String itemClickMethod = viewInject.itemClick();
		if (!TextUtils.isEmpty(itemClickMethod))
			setItemClickListener(obj, field, itemClickMethod);

		String itemLongClickMethod = viewInject.itemLongClick();
		if (!TextUtils.isEmpty(itemLongClickMethod))
			setItemLongClickListener(obj, field, itemLongClickMethod);

	}

	/**
	 * 注入ioc容器中的对象
	 * 
	 * @param obj
	 * @param field
	 * @param inject
	 */
	@SuppressWarnings({ "unchecked" })
	public static void injectStand(Object obj, Field field, Inject inject) {
		try {

			String name = inject.name();
			Object value = null;
			if (!TextUtils.isEmpty(name)) {
				value = IocContainer.getShare().get(name);

			} else {
				Class clazz = field.getType();
				String tag = inject.tag();
				if (!TextUtils.isEmpty(tag)) {
					value = IocContainer.getShare().get(clazz, tag);
				} else {
					value = IocContainer.getShare().get(clazz);
				}
			}
			field.set(obj, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void getExtras(Object activity, Field field, InjectExtra extra) {
		Bundle bundle = null;
		if (activity instanceof Activity) {
			Activity ac = (Activity) activity;
			bundle = ac.getIntent().getExtras();
		} else if (activity instanceof Fragment) {
			Fragment fg = (Fragment) activity;
			bundle = fg.getArguments();
		}
		if (bundle == null)
			bundle = new Bundle();

		try {
			Object obj = null;
			Class clazz = field.getType();
			if (clazz.equals(Integer.class)) {
				if (!TextUtils.isEmpty(extra.def())) {
					obj = bundle.getInt(extra.name(),
							Integer.parseInt(extra.def()));
				} else {
					obj = bundle.getInt(extra.name(), 0);
				}
			} else if (clazz.equals(String.class)) {
				obj = bundle.getString(extra.name());
				if (obj == null) {
					if (!TextUtils.isEmpty(extra.def())) {
						obj = extra.def();
					}
				}
			} else if (clazz.equals(Long.class)) {
				if (!TextUtils.isEmpty(extra.def())) {
					obj = bundle.getLong(extra.name(),
							Long.parseLong(extra.def()));
				} else {
					obj = bundle.getLong(extra.name(), 0);
				}
			} else if (clazz.equals(Float.class)) {
				if (!TextUtils.isEmpty(extra.def())) {
					obj = bundle.getFloat(extra.name(),
							Float.parseFloat(extra.def()));
				} else {
					obj = bundle.getFloat(extra.name(), 0);
				}
			} else if (clazz.equals(Double.class)) {
				if (!TextUtils.isEmpty(extra.def())) {
					obj = bundle.getDouble(extra.name(),
							Double.parseDouble(extra.def()));
				} else {
					obj = bundle.getDouble(extra.name(), 0);
				}
			} else if (clazz.equals(Boolean.class)) {
				if (!TextUtils.isEmpty(extra.def())) {
					obj = bundle.getBoolean(extra.name(),
							Boolean.parseBoolean(extra.def()));
				} else {
					obj = bundle.getBoolean(extra.name(), true);
				}

			} else if ((clazz.equals(JSONObject.class))) {
				String objstr = bundle.getString(extra.name());
				if (!TextUtils.isEmpty(objstr)) {
					obj = new JSONObject(objstr);
				}
			} else if (clazz.equals(JSONArray.class)) {
				String objstr = bundle.getString(extra.name());
				if (!TextUtils.isEmpty(objstr)) {
					obj = new JSONArray(objstr);
				}
			} else {
				String objstr = bundle.getString(extra.name());
				if (!TextUtils.isEmpty(objstr)) {
					try {
						obj = new Gson().fromJson(objstr, clazz);
					} catch (Exception e) {
					}
				}
			}
			if (obj != null) {
				field.set(activity, obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取assert
	 * 
	 * @param activity
	 * @param field
	 * @param as
	 */
	private static void getAssert(final Object activity, final Field field,
			final InjectAssert as) {
		AssetManager manager = IocContainer.getShare().getApplication()
				.getAssets();
		Class clazz = field.getType();
		Object value = null;
		try {
			final InputStream in = manager.open(as.path());
			if (clazz.equals(InputStream.class)) {
				value = in;
			}
			if (clazz.equals(String.class) || clazz.equals(JSONObject.class)
					|| clazz.equals(JSONArray.class)) {
				if (in != null) {
					Scanner scanner = new Scanner(in);
					StringBuffer sb = new StringBuffer();
					while (scanner.hasNext()) {
						sb.append(scanner.nextLine());
					}
					in.close();
					scanner.close();
					value = sb.toString();
				}
				if (clazz.equals(JSONArray.class)) {
					value = new JSONArray(value.toString());
				} else if (clazz.equals(JSONObject.class)) {
					value = new JSONObject(value.toString());
				}
			} else if (clazz.equals(File.class)) {
				File dir = FileUtil.getDir();
				final File file = new File(dir, as.path());
				if (!file.exists()) {
					ThreadWorker.execuse(false,new Task(Ioc.getApplicationContext()) {
						@Override
						public void doInBackground() {
							super.doInBackground();
								FileUtil.write(in, file);
							try {
								field.set(activity, file);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
						@Override
						public void doInUI(Object obj, Integer what) {
							if(!TextUtils.isEmpty(as.fileInjected()) ){
								try {
									Method method=activity.getClass().getDeclaredMethod(as.fileInjected());
									method.invoke(activity);
								} catch (NoSuchMethodException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
							}
						}
					});
					return;
				}else{
					if(!TextUtils.isEmpty(as.fileInjected()) ){
						try {
							Method method=activity.getClass().getDeclaredMethod(as.fileInjected());
							method.invoke(activity);
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
				value = file;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (value != null) {
			try {
				field.set(activity, value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private static void setViewClickListener(Object activity, Field field,
			String clickMethod) {
		try {
			Object obj = field.get(activity);
			if (obj instanceof View) {
				((View) obj).setOnClickListener(new EventListener(activity)
						.click(clickMethod));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setViewLongClickListener(Object activity, Field field,
			String clickMethod) {
		try {
			Object obj = field.get(activity);
			if (obj instanceof View) {
				((View) obj).setOnLongClickListener(new EventListener(activity)
						.longClick(clickMethod));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setItemClickListener(Object activity, Field field,
			String itemClickMethod) {
		try {
			Object obj = field.get(activity);
			if (obj instanceof AdapterView) {
				((AdapterView) obj).setOnItemClickListener(new EventListener(
						activity).itemClick(itemClickMethod));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setItemLongClickListener(Object activity, Field field,
			String itemClickMethod) {
		try {
			Object obj = field.get(activity);
			if (obj instanceof AdapterView) {
				((AdapterView) obj)
						.setOnItemLongClickListener(new EventListener(activity)
								.itemLongClick(itemClickMethod));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Field[] getDeclaredFields(Class<?> clazz) {
		List<Field> result = new ArrayList<Field>();
		try {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isFinal(field.getModifiers())) {
					continue;
				}
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				if ("serialVersionUID".equals(field.getName())) {// ingore
					continue;
				}
				result.add(field);
			}
		} catch (Exception e) {
		}
		clazz = clazz.getSuperclass();
		for (; isCommonClazz(clazz); clazz = clazz.getSuperclass()) {
			try {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (Modifier.isFinal(field.getModifiers())) {
						continue;
					}
					if (Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					if ("serialVersionUID".equals(field.getName())) {// ingore
						continue;
					}
					result.add(field);
				}
			} catch (Exception e) {
			}
		}
		return result.toArray(new Field[0]);
	}

	private static boolean isCommonClazz(Class<?> clazz) {
		String pkg = clazz.getPackage().getName();
		boolean isok = pkg.startsWith("net.duohuo.dhroid")
				|| pkg.startsWith(IocContainer.getShare()
						.getApplicationContext().getPackageName());
		if (isok)
			return true;
		if (Const.ioc_instal_pkg != null) {
			for (int i = 0; i < Const.ioc_instal_pkg.length; i++) {
				if (pkg.startsWith(Const.ioc_instal_pkg[i]))
					return true;
			}
		}
		return false;
	}
}
