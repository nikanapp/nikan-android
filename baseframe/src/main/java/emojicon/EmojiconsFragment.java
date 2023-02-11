/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package emojicon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.bloomlife.android.R;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.framework.AppContext;

import emojicon.emoji.Emojicon;
import emojicon.emoji.People;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com).
 */
public class EmojiconsFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private OnEmojiconBackspaceClickedListener mOnEmojiconBackspaceClickedListener;
    private int mEmojiTabLastSelectedIndex = -1;
    
    private  LinearLayout featureLayout ;
    
    private ImageView curPonit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.emojicons, container, false);
        final ViewPager emojisPager = (ViewPager) view.findViewById(R.id.emojis_pager);
        emojisPager.setOnPageChangeListener(this);
        
        
        int widthColumn = AppContext.deviceInfo.getScreenWidth()/UiUtils.dip2px(getActivity(), 45); //40列宽+5间距
        
        int heightColumn = 225 / 45 ;
        
        int eachPageEmojsNum = widthColumn*heightColumn-1; //有一个是删除按钮
        
        int pageNum  = People.DATA.length / eachPageEmojsNum; //总用更有多少页
       
        List<EmojiconGridFragment> gridFragmentList = new ArrayList<EmojiconGridFragment>();
        for (int i = 0; i < pageNum; i++) {
        	EmojiconGridFragment fragment =EmojiconGridFragment.newInstance(People.getEmojiGroup(i*eachPageEmojsNum, (i+1)*eachPageEmojsNum));
        	fragment.setEmojiconsFragment(this);
        	gridFragmentList.add(fragment);
		}
        
        EmojisPagerAdapter emojisAdapter = new EmojisPagerAdapter(getFragmentManager(),gridFragmentList );
        emojisPager.setAdapter(emojisAdapter);

        featureLayout = (LinearLayout) view.findViewById(R.id.feature_layout);
        for (int i = 0; i < pageNum; i++) {
			featureLayout.addView(makeFeatureImageView());
		}
        curPonit = (ImageView) featureLayout.getChildAt(0);
        curPonit.setImageResource(R.drawable.feature_point_active);
        
        onPageSelected(0);
        return view;
    }
    
    private ImageView makeFeatureImageView(){
    	ImageView imageView = new ImageView(getActivity());
    	LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    	layoutParams.leftMargin = 10;
    	layoutParams.rightMargin = 10 ;
    	imageView.setLayoutParams(layoutParams);
    	imageView.setImageResource(R.drawable.feature_point);
    	return imageView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof OnEmojiconBackspaceClickedListener) {
            mOnEmojiconBackspaceClickedListener = (OnEmojiconBackspaceClickedListener) getActivity();
        } else {
            throw new IllegalArgumentException(activity + " must implement interface " + OnEmojiconBackspaceClickedListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        mOnEmojiconBackspaceClickedListener = null;
        super.onDetach();
    }

    public static void input(EditText editText, Emojicon emojicon) {
        if (editText == null || emojicon == null) {
            return;
        }

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < 0) {
            editText.append(emojicon.getEmoji());
        } else {
            editText.getText().replace(Math.min(start, end), Math.max(start, end), emojicon.getEmoji(), 0, emojicon.getEmoji().length());
        }
    }

    public static void backspace(EditText editText) {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
    	
    	curPonit.setImageResource(R.drawable.feature_point);
    	curPonit = (ImageView) featureLayout.getChildAt(i);
    	curPonit.setImageResource(R.drawable.feature_point_active);
    	
        if (mEmojiTabLastSelectedIndex == i) {
            return;
        }
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
//            case 4:
//                if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mEmojiTabs.length) {
//                    mEmojiTabs[mEmojiTabLastSelectedIndex].setSelected(false);
//                }
//                mEmojiTabs[i].setSelected(true);
//                mEmojiTabLastSelectedIndex = i;
//                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private static class EmojisPagerAdapter extends FragmentStatePagerAdapter {
        private List<EmojiconGridFragment> fragments;

        public EmojisPagerAdapter(FragmentManager fm, List<EmojiconGridFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    /**
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     * <p/>
     * <p>Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     */
    public static class RepeatListener implements View.OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final View.OnClickListener clickListener;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }
                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                clickListener.onClick(downView);
            }
        };

        private View downView;

        /**
         * @param initialInterval The interval before first click event
         * @param normalInterval  The interval before second and subsequent click
         *                        events
         * @param clickListener   The OnClickListener, that will be called
         *                        periodically
         */
        public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
            }
            return false;
        }
    }

    public interface OnEmojiconBackspaceClickedListener {
        void onEmojiconBackspaceClicked(View v);
    }
    
    public void onBackspaceClick(final View v){
    	if (mOnEmojiconBackspaceClickedListener != null) {
          mOnEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);
      }
    }
}
