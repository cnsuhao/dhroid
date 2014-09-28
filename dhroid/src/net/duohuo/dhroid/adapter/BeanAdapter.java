package net.duohuo.dhroid.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.view.HorizontalListView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * 
 * 用bean绑定的adapter
 * 
 * @author duohuo-jinghao
 * 
 */
public abstract class BeanAdapter<T> extends BaseAdapter {

	public List<T> mVaules = null;

	private final Object mLock = new Object();

	protected int mResource;

	protected int mDropDownResource;

	protected boolean mNotifyOnChange = true;

	private LayoutInflater mInflater;

	public Map<Integer, InViewClickListener> canClickItem;

	public boolean isReuse = true;

	public Context mContext;

	public ValueFix fixer;

	public Class jumpClazz;
	public String jumpKey;
	public String jumpAs;

	public Class getJumpClazz() {
		return jumpClazz;
	}

	public String getJumpKey() {
		return jumpKey;
	}

	public String getJumpAs() {
		return jumpAs;
	}

	public void setJump(Class jumpClazz, String jumpkey, String as) {
		this.jumpClazz = jumpClazz;
		this.jumpKey = jumpkey;
		this.jumpAs = as;
	}

	public BeanAdapter(Context context, int mResource, boolean isViewReuse) {
		super();
		this.mResource = mResource;
		isReuse = isViewReuse;
		this.mDropDownResource = mResource;
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mVaules = new ArrayList<T>();
		fixer = IocContainer.getShare().get(ValueFix.class);
	}

	public BeanAdapter(Context context, int mResource) {
		this(context, mResource, true);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getValues() {
		return (List<T>) mVaules;
	}

	public void add(T one) {
		synchronized (mLock) {
			mVaules.add(one);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void addAll(List<T> ones) {
		synchronized (mLock) {
			mVaules.addAll(ones);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void insert(int index, T one) {
		synchronized (mLock) {
			if(index<0)index=0;
			mVaules.add(index, one);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void remove(int index) {
		synchronized (mLock) {
			if (index < mVaules.size()&&index>=0) {
				mVaules.remove(index);
			}
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void clear() {
		synchronized (mLock) {
			mVaules.clear();
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void setNotifyOnChange(boolean notifyOnChange) {
		mNotifyOnChange = notifyOnChange;
	}

	public int getCount() {
		return mVaules.size();
	}

	public Object getItem(int position) {
		if (position < mVaules.size()&&position>=0) {
			return	mVaules.get(position);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T getTItem(int position) {
		if (position < mVaules.size()&&position>=0){
			return (T) mVaules.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public String getTItemId(int position) {

		return position + "";
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (isReuse) {
			if (convertView == null) {
				view = mInflater.inflate(mResource, parent, false);
			} else {
				view = convertView;
			}

		} else {
			if (convertView != null) {
				parent.removeView(convertView);
			}
			view = mInflater.inflate(mResource, parent, false);
		}
		bindView(view, position, mVaules.get(position));
		bindInViewListener(view, position, mVaules.get(position));
		return view;
	}

	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView == null) {
			view = mInflater.inflate(mDropDownResource, parent, false);
		} else {
			view = convertView;
		}
		bindView(view, position, mVaules.get(position));
		bindInViewListener(view, position, mVaules.get(position));
		return view;
	}

	public abstract void bindView(View itemV, int position, T jo);

	/**
	 * 将值和控件绑定 可以防止图片的移位
	 * 
	 * @param position
	 * @param v
	 * @param o
	 */
	public void bindValue(final Integer position, View v, Object o,
			DisplayImageOptions options) {
		if (o == null)
			o = "";
		if (v instanceof ImageView) {
			ImageView imagev = (ImageView) v;
			if (o instanceof Drawable) {
				imagev.setImageDrawable((Drawable) o);
			} else if (o instanceof Bitmap) {
				imagev.setImageBitmap((Bitmap) o);
			} else if (o instanceof Integer) {
				imagev.setImageResource((Integer) o);
			} else if (o instanceof String) {
				ImageLoader.getInstance().displayImage((String) o,
						(ImageView) v, options);
			}
		} else if (v instanceof TextView) {
			if (o instanceof CharSequence) {
				((TextView) v).setText((CharSequence) o);
			} else {
				((TextView) v).setText(o.toString());
			}
		}
	}

	private void bindInViewListener(final View itemV, final Integer position,
			final Object valuesMap) {
		if (canClickItem != null) {
			for (Integer key : canClickItem.keySet()) {
				View inView = itemV.findViewById(key);
				final InViewClickListener inviewListener = canClickItem
						.get(key);
				if (inView != null && inviewListener != null) {
					inView.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							inviewListener.OnClickListener(itemV, v, position,
									valuesMap);
						}
					});
				}
			}
		}
	}

	public void setOnInViewClickListener(Integer key,
			InViewClickListener inViewClickListener) {
		if (canClickItem == null)
			canClickItem = new HashMap<Integer, InViewClickListener>();
		canClickItem.put(key, inViewClickListener);
	}

	public void setmDropDownResource(int mDropDownResource) {
		this.mDropDownResource = mDropDownResource;
	}

	public int getmDropDownResource() {
		return mDropDownResource;
	}

	public interface InViewClickListener {
		public void OnClickListener(View parentV, View v, Integer position,
				Object values);
	}

	/**
	 * 
	 * 大家都用的viewholder
	 * 
	 */
	public static class ViewHolder {
		Map<Integer, View> views;
		View itemV;

		public static ViewHolder getHolder(View itemV) {
			ViewHolder viewHolder = (ViewHolder) itemV.getTag();
			if (viewHolder == null) {
				viewHolder = new ViewHolder();
				itemV.setTag(viewHolder);
				viewHolder.itemV = itemV;
			}
			return viewHolder;
		}

		public View getView(Integer id) {
			View v = views.get(id);
			if (v == null) {
				v = itemV.findViewById(id);
				if (v != null) {
					views.put(id, v);
				}
			}
			return v;
		}

		private ViewHolder() {
			super();
			views = new HashMap<Integer, View>();
		}

	}

}
