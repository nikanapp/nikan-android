package com.bloomlife.videoapp.activity.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.bean.PhoneNumber;
import com.bloomlife.android.common.CacheKeyConstants;
import com.bloomlife.android.common.util.ContactUtils;
import com.bloomlife.android.executor.AsyncTask;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.adapter.ContactsAdapter;
import com.bloomlife.videoapp.model.Contact;
import com.bloomlife.videoapp.model.ContactInviteStatusList;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/6.
 */
public class ContactsFragment extends Fragment {

    @ViewInject(id = R.id.fragment_contacts_list)
    private StickyGridHeadersGridView mContactList;

    private ContactsAdapter mAdapter;

    private GetContactListTask mTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_contacts, container, false);
        FinalActivity.initInjectedView(this, layout);
        initStickyGridView();
        mTask = new GetContactListTask(getActivity());
        mTask.execute();
        return layout;
    }

    private void initStickyGridView(){
        mContactList.setNumColumns(1);
        mContactList.setVerticalScrollBarEnabled(false);
        mContactList.setAreHeadersSticky(false);
    }

    @Override
    public void onDestroyView() {
        mTask.cancel(true);
        super.onDestroyView();
    }

    class GetContactListTask extends AsyncTask<String, Integer, List<Contact>>{

        public GetContactListTask(Activity activity, boolean showDialog) {
            super(activity, showDialog);
        }

        public GetContactListTask(Activity activity) {
            super(activity);
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            mAdapter = new ContactsAdapter(getActivity(), contacts);
            mContactList.setAdapter(mAdapter);
        }

        @Override
        protected List<Contact> doInBackground(String... params) {
            List<String> cacheList = ContactInviteStatusList.get(getActivity());
            Set<PhoneNumber> contactSet = ContactUtils.getContactList(getActivity());
            List<Contact> contactList = new ArrayList<>(contactSet.size());
            for (PhoneNumber p:contactSet){
                Contact cnt = new Contact();
                cnt.setName(p.getName());
                cnt.setNumber(p.getNumber().substring(0, 1) + " " + p.getNumber().substring(1, 4) + " " + p.getNumber().substring(4, p.getNumber().length()));
                cnt.setAlphabetic(p.getAlphabetic());
                if (cacheList != null){
                    if (cacheList.contains(p.getNumber())) {
                        cnt.setInvite(true);
                    }
                }
                contactList.add(cnt);
            }
            Collections.sort(contactList, new ContactComparator());
            return contactList;
        }

        class ContactComparator implements Comparator<Contact> {

            @Override
            public int compare(Contact lhs, Contact rhs) {
                if (lhs.getAlphabetic().charAt(0) < rhs.getAlphabetic().charAt(0)){
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    }

}
