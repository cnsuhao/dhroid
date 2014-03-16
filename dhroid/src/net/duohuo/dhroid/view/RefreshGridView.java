package net.duohuo.dhroid.view;

import net.duohuo.dhroid.adapter.INetAdapter;
import net.duohuo.dhroid.adapter.INetAdapter.LoadSuccessCallBack;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.view.RefreshAndMoreListView.OnRefreshListener;
import net.duohuo.dhroid.view.RefreshAndMoreListView.OnStateChangeListener;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.AbsListView.OnScrollListener;

public class RefreshGridView extends GridView implements OnScrollListener {
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
	private boolean isBottom;
	private boolean isBottomRecord;
	private int headContentHeight;
	private int footContentHeight;
	private int startY;
	private int firstItemIndex;
	private int state;
	private OnRefreshListener refreshListener;
	private boolean isRefreshable;

	OnStateChangeListener onStateChangeListener;

	LoadSuccessCallBack moreLoadSuccessCallBack;

	int refreshHeight;
	int moreHeight;

	// 剩下多少刷新
	Integer leaveCount;

	public void setRefreshHeight(int refreshHeight) {
		this.refreshHeight = refreshHeight;
	}

	// 是否在加载更多
	Boolean isLoadingMore = false;

	public RefreshGridView(Context context) {
		super(context);
	}

	public RefreshGridView(Context context, AttributeSet attrs) {
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
		refreshHeight = headContentHeight;
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		// addHeaderView(headView, null, false);
		setOnScrollListener(this);
		state = STATE_DONE;
		isRefreshable = true;
	}

	public View getHeaderView() {
		return this.headView;
	}

	public void onScroll(AbsListView view, int firstVisiableItem,
			int allVisiable, int allItems) {
		firstItemIndex = firstVisiableItem;
		isBottom=firstItemIndex+allVisiable==allItems;
		if (this.footView != null) {
			synchronized (isLoadingMore) {
				int left=allItems - (firstVisiableItem + allVisiable);
				if (left<=leaveCount
						&& !isLoadingMore) {
					if (getAdapter() instanceof INetAdapter) {
						INetAdapter trueAdapter = (INetAdapter) getAdapter();
						if (trueAdapter.hasMore()) {
							onStateChange(STATE_MORE_LOADING, footView);
							if (moreLoadSuccessCallBack == null) {
								this.moreLoadSuccessCallBack = new LoadSuccessCallBack() {
									public void callBack(Response rs) {
										onStateChange(STATE_MORE_OK, footView);
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

	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}

	int lastheadContentHeight = 0;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (lastheadContentHeight == headView.getPaddingTop()) {
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
			}
			;
			lastheadContentHeight = headView.getPaddingTop();
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
						&& state != STATE_LOADING&&!isBottomRecord) {
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
				
				if (state != STATE_MORE_LOADING && isBottomRecord
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
				break;
			}
		}
		super.onTouchEvent(event);
		return true;
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
	
	/**
	 * 刷新完成时调用
	 */
	public void onMoreComplete() {
		state = STATE_MORE_OK;
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
		if (this.getAdapter() instanceof INetAdapter) {
			INetAdapter trueAdapter = (INetAdapter) this.getAdapter();
			trueAdapter.setOnTempLoadSuccess(new LoadSuccessCallBack() {
				public void callBack(Response rss) {
					onRefreshComplete();
				}
			});
			trueAdapter.refresh();
		} else if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}
	
	public void onMore(){
		if (this.getAdapter() instanceof INetAdapter) {
			INetAdapter trueAdapter = (INetAdapter) this.getAdapter();
			trueAdapter.setOnTempLoadSuccess(new LoadSuccessCallBack() {
				public void callBack(Response rss) {
					onMoreComplete();
				}
			});
			trueAdapter.showNext();
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
	}

	public View getMoreView() {
		return this.footView;
	}

	public void setAutoLoadCount(Integer leaveCount) {
		this.leaveCount = leaveCount;
	}
}
