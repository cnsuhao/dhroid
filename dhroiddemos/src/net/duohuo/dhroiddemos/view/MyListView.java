/**
 * 
 */
package net.duohuo.dhroiddemos.view;


import net.duohuo.dhroid.adapter.BeanAdapter;
import net.duohuo.dhroid.adapter.INetAdapter;
import net.duohuo.dhroid.adapter.INetAdapter.LoadSuccessCallBack;
import net.duohuo.dhroid.adapter.NetJSONAdapter;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.util.DhUtil;
import net.duohuo.dhroid.util.ViewUtil;
import net.duohuo.dhroid.view.RefreshAndMoreListView;
import net.duohuo.dhroid.view.RefreshGridView;
import net.duohuo.dhroiddemos.R;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

public class MyListView extends RefreshAndMoreListView {
	private Context mContext;
	View refreshheadView;
	public View footView;

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		if (isInEditMode())
			return;
		init();
	}


	public INetAdapter getNetAdapter() {
		if (getAdapter() instanceof HeaderViewListAdapter) {
			HeaderViewListAdapter headadapter = (HeaderViewListAdapter) getAdapter();
			if (headadapter.getWrappedAdapter() instanceof INetAdapter) {
				INetAdapter trueAdapter = (INetAdapter) headadapter
						.getWrappedAdapter();
				return trueAdapter;
			}
		}
		return null;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (adapter instanceof INetAdapter) {
			INetAdapter netAfapter = (INetAdapter) adapter;
			netAfapter.setOnLoadSuccess(new LoadSuccessCallBack() {
				@Override
				public void callBack(Response response) {
					
				}
			});
		}
		if (adapter instanceof BeanAdapter) {
			BeanAdapter beanadapter = (BeanAdapter) adapter;
			if (beanadapter.getJumpClazz() != null) {
				setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						NetJSONAdapter adapter = (NetJSONAdapter) getNetAdapter();
						Intent it = new Intent(getContext(),
								adapter.getJumpClazz());
						adapter.getJumpKey();
						if (position - getHeaderViewsCount() + 1 > 0) {
							JSONObject jo = adapter.getTItem(position
									- getHeaderViewsCount());
							it.putExtra("listTemp",jo.toString());
							try {
								it.putExtra(
										adapter.getJumpAs(),
										JSONUtil.getString(jo,
												adapter.getJumpKey()));
							} catch (Exception e) {
							}
							getContext().startActivity(it);
						}
					}
				});
			}
		}
	}	

	private void init() {
		refreshheadView = LayoutInflater.from(mContext).inflate(
				R.layout.list_refresh_head, null);
		this.setRefreshView(refreshheadView);
		footView = LayoutInflater.from(mContext).inflate(
				R.layout.list_more_view, null);
		footView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				INetAdapter trueAdapter = getNetAdapter();
				if (trueAdapter != null && trueAdapter.hasMore()) {
					trueAdapter.showNext();
				} else {
					footView.findViewById(R.id.root).setVisibility(View.GONE);
					ViewUtil.bindView(
							footView.findViewById(R.id.tips),
							"没有更多数据");
					IocContainer.getShare().get(IDialog.class).showToastShort(mContext,"没有更多数据");
				}
			}
		});
		setRefreshHeight(DhUtil.dip2px(mContext, 60));
		this.setMoreView(footView, -2);
		this.setOnStateChangeListener(new OnStateChangeListener() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void StateChange(int state, View view) {
				switch (state) {
				case RefreshAndMoreListView.STATE_RELEASE_To_REFRESH: {
					ViewUtil.bindView(refreshheadView.findViewById(R.id.tips), "松开刷新");
					break;
				}
				case RefreshAndMoreListView.STATE_PULL_To_REFRESH: {
					ViewUtil.bindView(refreshheadView.findViewById(R.id.tips), "下拉刷新");
					break;
				}
				case RefreshAndMoreListView.STATE_DONE: {
				}
				case RefreshAndMoreListView.STATE_LOADING: {
					break;
				}
				case RefreshAndMoreListView.STATE_REFRESHING: {
				}
				case RefreshGridView.STATE_RELEASE_To_More: {
					ViewUtil.bindView(footView.findViewById(R.id.tips),
							"松开加载更多");
					break;
				}
				case RefreshGridView.STATE_PULL_To_More: {
					ViewUtil.bindView(footView.findViewById(R.id.tips),
							"上拉加载更多");
					break;
				}
				case RefreshGridView.STATE_MORE_LOADING: {
					ViewUtil.bindView(footView.findViewById(R.id.tips), "正在努力加载...");
					break;
				}
				case RefreshGridView.STATE_MORE_OK: {
					ViewUtil.bindView(footView.findViewById(R.id.tips), "加载完成");
					break;
				}
				default:
					break;
				}
			}
		});
	}



}

