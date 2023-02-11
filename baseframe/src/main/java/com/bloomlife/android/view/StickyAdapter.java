package com.bloomlife.android.view;

import android.widget.ListAdapter;

public interface StickyAdapter extends ListAdapter {
	int getStickyViewId();
	boolean haveDivider();
}
