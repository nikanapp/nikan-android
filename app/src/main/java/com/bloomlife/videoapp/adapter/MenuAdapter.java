package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.model.DymainicMenu;
import com.bloomlife.videoapp.model.Menu;

import java.util.List;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/4.
 */
public class MenuAdapter extends BaseAdapter<DymainicMenu> {

    class Holder {
        ImageView nativeIcon;
        ImageView networkIcon;
        TextView name;
        View redDot;
    }

    public MenuAdapter(Activity activity, List<DymainicMenu> dataList) {
        super(activity, dataList);
    }

    @Override
    protected View initItemView(int position, ViewGroup parent, LayoutInflater inflater) {
        Holder h = new Holder();
        View layout = inflater.inflate(R.layout.item_main_menu, parent, false);
        h.nativeIcon = (ImageView) layout.findViewById(R.id.item_main_menu_native_icon);
        h.networkIcon = (ImageView) layout.findViewById(R.id.item_main_menu_network_icon);
        h.name = (TextView) layout.findViewById(R.id.item_main_menu_name);
        h.redDot = layout.findViewById(R.id.item_main_menu_red_dot);
        layout.setTag(h);
        return layout;
    }

    @Override
    protected void setViewContent(int position, View convertView, DymainicMenu item) {
        Holder h = (Holder) convertView.getTag();
        h.name.setText(item.getName());
        h.redDot.setVisibility(item.isNewBtn() ? View.VISIBLE : View.GONE);
        if (TextUtils.isEmpty(item.getIconurl())){
            h.nativeIcon.setVisibility(View.VISIBLE);
            h.networkIcon.setVisibility(View.INVISIBLE);
            h.nativeIcon.setImageResource(item.getIconResId());
        } else {
            h.nativeIcon.setVisibility(View.INVISIBLE);
            h.networkIcon.setVisibility(View.VISIBLE);
            mImageLoader.displayImage(item.getIconurl(), h.networkIcon, mOption);
        }
    }
}
