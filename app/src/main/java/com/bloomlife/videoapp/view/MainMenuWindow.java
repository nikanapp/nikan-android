package com.bloomlife.videoapp.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.activity.HtmlActivity;
import com.bloomlife.videoapp.activity.InviteActivity;
import com.bloomlife.videoapp.activity.MyVideoActivity;
import com.bloomlife.videoapp.activity.NotificationStoryPlayActivity;
import com.bloomlife.videoapp.adapter.MenuAdapter;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.util.Utils;
import com.bloomlife.videoapp.dialog.DialogUtils;
import com.bloomlife.videoapp.manager.BackgroundManager;
import com.bloomlife.videoapp.model.CacheMenus;
import com.bloomlife.videoapp.model.DymainicMenu;
import com.bloomlife.videoapp.model.Menu;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/3.
 */
public class MainMenuWindow extends FrameLayout {

    private FragmentActivity mActivity;
    private MenuAdapter mAdapter;
    private ListView mButtonList;
    private OnDismissListener mOnDismissListener;
    private List<DymainicMenu> mCacheMenuList;

    public MainMenuWindow(Context context) {
        super(context);
        init(context);
    }

    public MainMenuWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainMenuWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MainMenuWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        View layout = inflate(context, R.layout.layout_main_menu, this);
        if (isInEditMode()) return;
        mActivity = (FragmentActivity) context;
        mButtonList = (ListView) layout.findViewById(R.id.main_menu_button_list);
        mAdapter = new MenuAdapter(mActivity, getButtonList());
        mButtonList.setAdapter(mAdapter);
        mButtonList.setOnItemClickListener(mBtnItemClickListener);
        mCacheMenuList = CacheMenus.get(mActivity);
    }

    private List<DymainicMenu> getButtonList(){
        List<DymainicMenu> menus = new ArrayList<>();
        if (!Utils.isEmpty(AppContext.getSysCode().getDynamicmenus())){
            menus.addAll(AppContext.getSysCode().getDynamicmenus());
        }
        menus.add(0, DymainicMenu.inviteFriend(getContext()));
        menus.add(0, DymainicMenu.myPage(getContext()));
        return menus;
    }

    private AdapterView.OnItemClickListener mBtnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DymainicMenu m = (DymainicMenu) parent.getAdapter().getItem(position);
            if (0 == position){
                if (!isLogin()){
                    dismiss();
                    return;
                }
                mActivity.startActivity(new Intent(mActivity, MyVideoActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_bottom_in, 0);
                BackgroundManager.getInstance().capture(mActivity);
                dismiss();
                return;
            }
            if (1 == position){
                if (!isLogin()){
                    dismiss();
                    return;
                }
                mActivity.startActivity(new Intent(mActivity, InviteActivity.class));
                mActivity.overridePendingTransition(R.anim.activity_bottom_in, 0);
                BackgroundManager.getInstance().capture(mActivity);
                dismiss();
                return;
            }
            switch (m.getBehavior()){
                case DymainicMenu.BEHAVIOR_STORY_ID:
                    // 弹出Story视频页面
                    String[] msgs = m.getContent().split(",");
                    if (msgs != null && msgs.length > 1){
                        Intent intent = new Intent(mActivity, NotificationStoryPlayActivity.class);
                        intent.putExtra(NotificationStoryPlayActivity.INTENT_STORY_ID, msgs[0]);
                        intent.putExtra(NotificationStoryPlayActivity.INTENT_USER_ID, msgs[1]);
                        mActivity.startActivity(intent);
                    }
                    break;

                case DymainicMenu.BEHAVIOR_USER_ID:
                    // 弹出用户卡牌
                    DialogUtils.showUserInfo(mActivity, m.getContent());
                    break;

                case DymainicMenu.BEHAVIOR_WEB_URL:
                    Intent intent = new Intent(mActivity, HtmlActivity.class);
                    intent.putExtra(HtmlActivity.INTENT_URL, m.getContent());
                    mActivity.startActivity(intent);
                    break;
            }
            if (m.isNewBtn()){
                m.setNewBtn(false);
                mCacheMenuList.add(m);
                // 缓存当前获取的动态菜单
                CacheMenus.set(mActivity, mCacheMenuList);
            }
            dismiss();
            mAdapter.notifyDataSetChanged();
        }
    };

    private boolean isLogin(){
        return Utils.isLogin(mActivity, true);
    }

    public void dismiss() {
        if (!isShown()) return;
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.window_menu_out);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(anim);
    }

    public void show(){
        setVisibility(View.VISIBLE);
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.window_menu_in));
    }

    public void setOnDismissListener(OnDismissListener l){
        mOnDismissListener = l;
    }

    public interface OnDismissListener{
        void onDismiss();
    }
}
