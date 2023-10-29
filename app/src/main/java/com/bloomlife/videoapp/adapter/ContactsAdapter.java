package com.bloomlife.videoapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Cache;
import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.CacheKeyConstants;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.common.util.ShareSDKUtil;
import com.bloomlife.videoapp.common.util.UIHelper;
import com.bloomlife.videoapp.model.Contact;
import com.bloomlife.videoapp.model.ContactInviteStatusList;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/6.
 */
public class ContactsAdapter extends BaseAdapter<Contact> implements StickyGridHeadersSimpleAdapter, View.OnClickListener {

    class Holder{
        TextView name;
        TextView number;
        TextView invite;
    }

    private List<String> mStatusList;

    public ContactsAdapter(FragmentActivity activity, List<Contact> dataList) {
        super(activity, dataList);
        mStatusList = ContactInviteStatusList.get(activity);
        if (mStatusList == null){
            mStatusList = new ArrayList<>();
        }
    }

    @Override
    protected View initItemView(int position, ViewGroup parent, LayoutInflater inflater) {
        Holder h = new Holder();
        View layout = inflater.inflate(R.layout.item_contacts, parent, false);
        h.name = (TextView) layout.findViewById(R.id.item_contact_name);
        h.number = (TextView) layout.findViewById(R.id.item_contact_number);
        h.invite = (TextView) layout.findViewById(R.id.item_contact_invite);
        h.invite.setOnClickListener(this);
        layout.setTag(h);
        return layout;
    }

    @Override
    protected void setViewContent(int position, View convertView, Contact item) {
        Holder h = (Holder) convertView.getTag();
        h.name.setText(item.getName());
        h.number.setText(item.getNumber());
        h.invite.setTag(R.id.position, position);
        if (item.isInvite()){
            h.invite.setText("Re-Invite");
            h.invite.setTextColor(activity.getResources().getColor(R.color.dialog_edit_line_yellow));
        } else {
            h.invite.setText("Invite");
            h.invite.setTextColor(activity.getResources().getColor(R.color.activity_invite_red));
        }
    }

    @Override
    public long getHeaderId(int position) {
        return getDataList().get(position).getAlphabetic().charAt(0);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        TextView title;
        if (convertView == null){
            title = UIHelper.getInviteTitle(activity);
        } else {
            title = (TextView) convertView;
        }
        title.setText(getDataList().get(position).getAlphabetic());
        return title;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_contact_invite:
                int position = (Integer) v.getTag(R.id.position);
                Contact contact = getDataList().get(position);
                String number = contact.getNumber().replace(" ", "");
                ShareSDKUtil.shareToMessage(activity, number, activity.getString(R.string.App_Share_Url));
                if (!contact.isInvite()){
                    mStatusList.add(number);
                    ContactInviteStatusList.set(activity, mStatusList);
                }
                contact.setInvite(true);
                notifyDataSetChanged();
                break;
        }
    }
}
