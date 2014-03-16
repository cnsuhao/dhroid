package net.duohuo.dhroid.view;

import java.security.acl.LastOwnerException;

import net.duohuo.dhroid.adapter.INetAdapter;
import net.duohuo.dhroid.adapter.INetAdapter.LoadSuccessCallBack;
import net.duohuo.dhroid.net.Response;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 添加了下拉刷新功能 和 自动添加下一页的ListView <br/>
 * 主要需要实现方法 public void setHeadView(View headView) <br/>
 * public void setMoreView(View footView,Integer leaveCount)<br/>
 * public void setOnStateChangeListener(OnStateChangeListener
 * onStateChangeListener)<br/>
 * 
 * @author duohuo-jinghao
 * 
 */
public class RefreshAndMoreListView extends ListView implements
		OnScrollListener {
	public final static int STATE_RELEASE_To_REFRESH = 0;
	public final static int STATE_PULL_To_REFRESH = 1;
	public final static int STATE_RELEASE_To_More = 7;
	public final static int STATE_PULL_To_More = 8;
	// 正在刷新
	public final static int STATE_REFRESHING = 2;
	// 刷新完成
	public final static int STATE_DONE = 3;
	// 加载中
	public final static int STATE_LOADING = 4;
	// 加载更多中
	public final static int STATE_MORE_LOADING = 5;
	// 加载更多完成
	public final static int STATE_MORE_OK = 6;
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;
	boolean isBack;
	private View headView;
	private View footView;
	private boolean isRecored;
	private int headContentHeight;
	private int startY;
	private int firstItemIndex;
	private int state;
	private OnRefreshListener refreshListener;
	private boolean isRefreshable;

	OnStateChangeListener onStateChangeListener;

	LoadSuccessCallBack moreLoadSuccessCallBack;
	
	int refreshHeight;
	
	// 剩下多少刷新
	Integer leaveCount;
	
	int moreHeight;
	private int footContentHeight;
	private boolean isBottomRecord;
	private boolean isBottom;
	public void setRefreshHeight(int refreshHeight) {
		this.refreshHeight = refreshHeight;
	}
	// 是否在加载更多
	Boolean isLoadingMore = false;

	public RefreshAndMoreListView(Context context) {
		super(context);
	}

	public RefreshAndMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setIsRefreshable(boolean isRefreshable) {
		this.isRefreshable = isRefreshable;
	}

	/**
	 * 设置 RefreshView 需要在其他addHeadView 之前调用
	 * 
	 * @param headView
	 */
	public void setRefreshView(View headView) {
		this.headView = headView;
		headView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		headContentHeight = headView.getMeasuredHeight();
		refreshHeight=headContentHeight;
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		addHeaderView(headView, null, false);
		setOnScrollListener(this);
		state = STATE_DONE;
		isRefreshable = true;
	}

	public void onScroll(AbsListView view, int firstVisiableItem,
			int allVisiable, int allItems) {
		firstItemIndex = firstVisiableItem;
		isBottom=firstItemIndex+allVisiable==allItems;
		if (this.footView != null) {
			synchronized (isLoadingMore) {
				if (allItems - (firstVisiableItem + allVisiable) <=leaveCount
						&& !isLoadingMore) {
					if (this.getAdapter() instanceof HeaderViewListAdapter) {
						HeaderViewListAdapter headadapter = (HeaderViewListAdapter) this
								.getAdapter();
						if (headadapter.getWrappedAdapter() instanceof INetAdapter) {
							INetAdapter trueAdapter = (INetAdapter) headadapter
									.getWrappedAdapter();
							if (trueAdapter.hasMore()) {
								onStateChange(STATE_MORE_LOADING, footView);
								if (moreLoadSuccessCallBack == null) {
									this.moreLoadSuccessCallBack = new LoadSuccessCallBack() {

										public void callBack(Response rs) {
											onStateChange(STATE_MORE_OK,
													footView);
											isLoadingMore = false;
										}
									};
									trueAdapter
											.setOnLoadSuccess(this.moreLoadSuccessCallBack);
								}
								trueAdapter.showNext();
								isLoadingMore = true;
							}
						}
					}
				}
			}
		}
	}
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}
	int lastheadContentHeight =0;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if(lastheadContentHeight==headView.getPaddingTop()){
				if (state != STATE_REFRESHING && state != STATE_LOADING) {
					if (state == STATE_DONE) {
					}
					if (state == STATE_PULL_To_REFRESH) {
						state = STATE_DONE;
						changeHeaderViewByState();
					}
					if (state == STATE_RELEASE_To_REFRESH) {
						state = STATE_REFRESHING;
						changeHeaderViewByState();
						onRefresh();
					}
				}
				isRecored = false;
				isBack = false;
			} ;
			lastheadContentHeight=headView.getPaddingTop();
		}
	};

	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != STATE_REFRESHING && state != STATE_LOADING&&state!=STATE_MORE_LOADING) {
					if (state == STATE_DONE) {
					}
					if (state == STATE_PULL_To_REFRESH) {
						state = STATE_DONE;
						changeHeaderViewByState();
					}
					if (state == STATE_RELEASE_To_REFRESH) {
						state = STATE_REFRESHING;
						changeHeaderViewByState();
						onRefresh();
					}
					if (state == STATE_PULL_To_More) {
						state = STATE_MORE_OK;
						changeHeaderViewByState();
					}
					if(state==STATE_RELEASE_To_More){
						state=STATE_MORE_LOADING;
						changeHeaderViewByState();
						onMore();
					}
				}
				isBottomRecord=false;
				isRecored = false;
				isBack = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (!isRecored && firstItemIndex == 0) {
					isRecored = true;
					startY = tempY;
				}
				if(!isBottomRecord&&isBottom){
					isBottomRecord=true;
					startY = tempY;
				}
				if (state != STATE_REFRESHING && isRecored
						&& state != STATE_LOADING) {
					if(state==STATE_MORE_OK )state=STATE_DONE;
					if (state == STATE_RELEASE_To_REFRESH) {
						setSelection(0);
						if (((tempY - startY) / RATIO < refreshHeight)
								&& (tempY - startY) > 0) {
							state = STATE_PULL_To_REFRESH;
							changeHeaderViewByState();
						} else if (tempY - startY <= 0) {
							state = STATE_DONE;
							changeHeaderViewByState();
						}
					}
					if (state == STATE_PULL_To_REFRESH) {
						if ((tempY - startY) / RATIO >= refreshHeight) {
							state = STATE_RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
						} else if (tempY - startY <= 0) {
							state = STATE_DONE;
							changeHeaderViewByState();
						}
					}
					if (state == STATE_DONE) {
						if (tempY - startY > 0) {
							state = STATE_PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}
					if (state == STATE_PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);

					}
					if (state == STATE_RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}
				}else
				if (footView!=null&&state != STATE_MORE_LOADING && isBottomRecord
						&& state != STATE_LOADING) {
					if(state==STATE_DONE)state=STATE_MORE_OK;
					if (state == STATE_RELEASE_To_More) {
						setSelection(getAdapter().getCount()-1);
						if (((startY - tempY) / RATIO < moreHeight)
								&& (startY - tempY) > 0) {
							state = STATE_PULL_To_More;
							changeHeaderViewByState();
						} else if (startY - tempY <= 0) {
							state = STATE_MORE_OK;
							changeHeaderViewByState();
						}
					}
					if (state == STATE_PULL_To_More) {
						if ((startY - tempY) / RATIO >= moreHeight) {
							state = STATE_RELEASE_To_More;
							isBack = true;
							changeHeaderViewByState();
						} else if (startY - tempY <= 0) {
							state = STATE_MORE_OK;
							changeHeaderViewByState();
						}
					}
					if (state == STATE_MORE_OK) {
						if (startY - tempY > 0) {
							state = STATE_PULL_To_More;
							changeHeaderViewByState();
						}
					}
					if (state == STATE_PULL_To_More) {
						footView.setPadding(0, 0, 0, 1 * footContentHeight
								+ ( startY  - tempY  ) / RATIO);

					}
					if (state == STATE_RELEASE_To_More) {
						footView.setPadding(0, 0, 0, 1 * footContentHeight
								+ ( startY  - tempY  ) / RATIO);
					}
				}
				
				// handler.sendEmptyMessageDelayed(0, 2000);
				break;
			}
		}
		super.onTouchEvent(event);
		return true ;
	}

	private void changeHeaderViewByState() {
		switch (state) {
		case STATE_RELEASE_To_REFRESH:
			onStateChange(state, headView);
			break;
		case STATE_PULL_To_REFRESH:
			onStateChange(state, headView);
			break;
		case STATE_RELEASE_To_More:
			onStateChange(state, footView);
			break;
		case STATE_PULL_To_More:
			onStateChange(state, footView);
			break;	
		// 加载中的
		case STATE_REFRESHING:
			headView.setPadding(0, -(headContentHeight - refreshHeight), 0, 0);
			onStateChange(state, headView);
			break;
		case STATE_MORE_LOADING:
			footView.setPadding(0, 0, 0, (footContentHeight - moreHeight));
			onStateChange(state, headView);
			break;	
		// 完成加载
		case STATE_DONE:
			onStateChange(state, headView);
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			break;
		case STATE_MORE_OK:
			onStateChange(state, footView);
			footView.setPadding(0, 0, 0, -1 * footContentHeight);
			break;
		}	
	}
	public void onMore(){
		if (this.getRealAdapter() instanceof INetAdapter) {
			INetAdapter trueAdapter = (INetAdapter) this.getRealAdapter();
			if(trueAdapter.hasMore()){
				trueAdapter.setOnTempLoadSuccess(new LoadSuccessCallBack() {
					public void callBack(Response rss) {
						onMoreComplete();
					}
				});
				trueAdapter.showNext();
			}else{
				onMoreComplete();
			}
		}else{
				onMoreComplete();
		}
		
	}
	
	/**
	 * 刷新完成时调用
	 */
	public void onMoreComplete() {
		state = STATE_MORE_OK;
		changeHeaderViewByState();
	}
	/**
	 * 
	 * @param state
	 * @param view
	 *            当 refresh 时view 为headview 当 加载下页时 view 为headView
	 */
	private void onStateChange(int state, View view) {
		if (onStateChangeListener != null) {
			onStateChangeListener.StateChange(state, view);
		}
	}

	public interface OnStateChangeListener {
		/**
		 * 
		 * @param state
		 * @param view
		 *            当 refresh 时view 为headview 当 加载下页时 view 为headView
		 */
		public void StateChange(int state, View view);
	}

	/**
	 * 获取实际的adapter
	 * 
	 * @return
	 */
	public ListAdapter getRealAdapter() {
		if (this.getAdapter() instanceof HeaderViewListAdapter) {
			HeaderViewListAdapter headadapter = (HeaderViewListAdapter) this
					.getAdapter();
			if (headadapter.getWrappedAdapter() instanceof INetAdapter) {
				ListAdapter adapter = headadapter.getWrappedAdapter();
				return adapter;
			}
		}
		return this.getAdapter();
	}

	/**
	 * 
	 * @param onStateChangeListener
	 */
	public void setOnStateChangeListener(
			OnStateChangeListener onStateChangeListener) {
		this.onStateChangeListener = onStateChangeListener;
	}

	/**
	 * 刷新完成时调用
	 */
	public void onRefreshComplete() {
		state = STATE_DONE;
		changeHeaderViewByState();
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	/**
	 * 
	 */
	public void onRefresh() {
		if (this.getAdapter() instanceof HeaderViewListAdapter) {
			HeaderViewListAdapter headadapter = (HeaderViewListAdapter) this
					.getAdapter();
			if (headadapter.getWrappedAdapter() instanceof INetAdapter) {
				INetAdapter trueAdapter = (INetAdapter) headadapter
						.getWrappedAdapter();
				trueAdapter.setOnTempLoadSuccess(new LoadSuccessCallBack() {
					public void callBack(Response rss) {
						onRefreshComplete();
					}
				});
				trueAdapter.refresh();
			}
		} else if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	/**
	 * 设置自动加载功能 调用后请不要addFootView
	 * 
	 * @param footView
	 * @param leaveCount
	 *            剩下多少时开始自动加载
	 */
	public void setMoreView(View footView, Integer leaveCount) {
		footView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		footContentHeight = footView.getMeasuredHeight();
		moreHeight = footContentHeight;
		footView.setPadding(0, 0, 0, -1 * footContentHeight);
		this.footView = footView;
		this.leaveCount = leaveCount;
		this.addFooterView(footView);
	}
	
	public void setAutoLoadCount(Integer leaveCount ){
		this.leaveCount = leaveCount;
	}
}
