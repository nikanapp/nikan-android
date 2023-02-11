package com.bloomlife.videoapp.dialog;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewAnimator;

import com.android.volley.toolbox.Volley;
import com.bloomlife.videoapp.R;

import net.tsz.afinal.FinalActivity;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/7/23.
 */
public abstract class BaseDialog extends DialogFragment implements View.OnClickListener {

    private Context mApplicationContext;
    private Object mFragmentTag;
    private OnDismissListener mOnDismissListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentTag = Volley.getTag();
        FrameLayout layout = new FrameLayout(getActivity());
        inflater.inflate(getLayoutResId(), layout, true);
        FinalActivity.initInjectedView(this, layout);
        initLayout(layout);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Volley.cancelAll(mFragmentTag);
    }

    protected abstract int getLayoutResId();

    protected abstract void initLayout(View layout);

    public void show(Activity activity){
        show(activity.getFragmentManager());
    }

    public void show(FragmentManager fragmentManager){
        show(fragmentManager, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mApplicationContext = activity.getApplicationContext();
    }

    public Context getApplicationContext(){
        return mApplicationContext;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null){
            mOnDismissListener.onDismiss();
        }
    }

    public void setOnDismissListener(OnDismissListener l){
        mOnDismissListener = l;
    }

    public interface OnDismissListener{
        void onDismiss();
    }

}
