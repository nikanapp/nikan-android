package com.bloomlife.android.common.util;

import java.io.UnsupportedEncodingException;

import android.text.InputFilter;
import android.text.Spanned;

public class GsInputFilter implements InputFilter{
	
	private int maxLength = 100; 
	
	public GsInputFilter(int maxlth){
		
		maxLength = maxlth;
	}
	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest,int dstart, int dend) {
        // TODO Auto-generated method stub
        
        try {

            //转换成中文字符集的长度
            int destLen = dest.toString().getBytes("GB18030").length;
            int sourceLen = source.toString().getBytes("GB18030").length;

            //如果超过100个字符
            if (destLen + sourceLen > maxLength) {
                return "";
            }

           //如果按回退键
            if (source.length() < 1 && (dend - dstart >= 1)) {
                return dest.subSequence(dstart, dend - 1);
            }

            //其他情况直接返回输入的内容
            return source;
            
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
}

