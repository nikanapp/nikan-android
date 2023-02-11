package com.bloomlife.android.common.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

import com.bloomlife.android.bean.PhoneNumber;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/7.
 */
public class ContactUtils {

    public static Set<PhoneNumber> getContactList(Context context) {
        Set<PhoneNumber> numbers = new HashSet<PhoneNumber>();
        ContentResolver resolver = context.getContentResolver();
        // 获得所有的联系人
        Cursor cur = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur == null)
            return numbers;
        // 循环遍历
        while (cur.moveToNext()) {
            int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);
            // 获得联系人的ID号
            String contactId = cur.getString(idColumn);
            // 查看该联系人有多少个电话号码。如果没有这返回值为0
            int phoneCount = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if (phoneCount > 0) {
                // 获得联系人的电话号码
                Cursor phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                if (phones == null)
                    return numbers;
                while (phones.moveToNext()) {
                    // 遍历所有的电话号码
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String sortKey = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        sortKey = phones.getString(phones.getColumnIndex("phonebook_label"));
                    else
                        sortKey = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY));
                    String number = phoneNumber.replace(" ", "").trim(); // 去掉空格
                    // 检查是否是手机号码
                    if (isPhoneNumber(number)) {
                        PhoneNumber pn = new PhoneNumber();
                        pn.setNumber(number);
                        pn.setName(name);
                        pn.setAlphabetic(sortKey);
                        numbers.add(pn);
                    }
                }
                phones.close();
            }
        }
        cur.close();
        return numbers;
    }

    public static boolean isPhoneNumber(String str){
        return Pattern.matches("\\d{11}", str);
    }
}
