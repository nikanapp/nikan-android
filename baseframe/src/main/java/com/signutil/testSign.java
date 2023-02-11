package com.signutil;

import com.signutil.BaseRequest;
import com.signutil.Signature;


public class testSign {
	
	public static void main(String[] args) {
		BaseRequest request= new BaseRequest();
		Signature sign=new Signature();
		request.setMethod("me.highand.gs.calltheme");
		request.setSession("9870CBA3BCF017844BA28DDFC1777E6A");
		request.setLoginuserid("1");
		request.addApplicationParams("themeid", "44"); 
		request.addApplicationParams("pageNum", "1");
		request.addApplicationParams("pagesize", "10");
		System.out.println("sign:"+sign.getSign(request));
	}

}
