package com.bloomlife.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
/**
 * 分隔条置顶不动的ListView
 * @author <a href="mailto:lan4627@gmail.com">zxt</a>
 *
 * @date 2014年9月5日 下午6:35:28
 */
public class StickyListView extends ListView {

	private OnScrollListener l;
	private ParentTopListener listener;
	private HeaderViewListener headerViewListener;
	private boolean isSticky;
	private int id;
	private int scroll;
	private int visibleItemCount;
	
	private StickyAdapter adapter;
	private boolean haveDivider;
	private int stickyViewId;

	public StickyListView(Context context) {
		super(context);
		init(context);
	}

	public StickyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public StickyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context){
		super.setOnScrollListener(scrollListener);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (isInEditMode()) {return;}
		this.haveDivider  = adapter.haveDivider();
		this.stickyViewId = adapter.getStickyViewId();
		super.onLayout(changed, l, t, r, b);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (headerViewListener != null)
			headerViewListener.headerView(this);
		if (listener == null || !haveDivider)
			return;
		if (isSticky && id >= 0 && stickyViewId < scroll){										// 如果在屏幕的显示范围内，则传要置顶的View目前到顶部还有多少距离。
			listener.parentTop(id, getViewTop(id));
		} else if (isSticky && (id - visibleItemCount) > 0 ){									// 如果要置顶的View还在屏幕下方未显示出来，则传最大值。
			listener.parentTop(id, Integer.MAX_VALUE);
		} else {
			listener.parentTop(id, 0);
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}
	
	public void setStickyAdapter(StickyAdapter adapter) {
		this.adapter = adapter;
		super.setAdapter(adapter);
	}
	
	public void setSticky(boolean isSticky){
		this.isSticky = isSticky;
	}
	
	@Override
	public void setOnScrollListener(OnScrollListener l) {
		this.l = l;
	}
	
	public void setParentTopListener(ParentTopListener listener){
		this.listener = listener;
	}
	
	public interface ParentTopListener{
		void parentTop(int id, int top);
	}
	
	public void setHeaderViewListener(HeaderViewListener headerViewListener){
		this.headerViewListener = headerViewListener;
	}
	
	public interface HeaderViewListener{
		void headerView(View v);
	}
	
	private OnScrollListener scrollListener = new OnScrollListener(){
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (adapter != null){
				id = stickyViewId - firstVisibleItem;
				scroll = firstVisibleItem + visibleItemCount;
			}
			if (l != null)
				l.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {			
			if (l != null)
				l.onScrollStateChanged(view, scrollState);
		}
		
	};
	
	public int getViewTop(int id){
		View v = getChildAt(id);
		if (v != null){
			return v.getTop();
		}else{
			return 0;
		}
	}
	
	public static int getScrollY(AbsListView lv) {
        View c = lv.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = lv.getFirstVisiblePosition();
        int top = c.getTop();

        int scrollY = -top + firstVisiblePosition * c.getHeight();
        return scrollY;
    }

}
