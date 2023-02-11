/**************************************************************************************
* [Project]
*       MyProgressDialog
* [Package]
*       com.lxd.widgets
* [FileName]
*       CustomProgressDialog.java
* [Copyright]
*       Copyright 2012 LXD All Rights Reserved.
* [History]
*       Version          Date              Author                        Record
*--------------------------------------------------------------------------------------
*       1.0.0           2012-4-27         lxd (rohsuton@gmail.com)        Create
**************************************************************************************/
	
package com.bloomlife.android.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.bloomlife.android.R;
/***
 * 自定义进度提示dialog。登陆和刷新时提示的就是这个dialog
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-11-7  下午2:12:36
 */
public class CustomProgressDialog extends Dialog {
//	private  CustomProgressDialog customProgressDialog = null;
	
//	public CustomProgressDialog(Context context){
//		super(context);
//	}
	
	public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static  CustomProgressDialog createDialog(Context context){
		CustomProgressDialog customProgressDialog = new CustomProgressDialog(context,R.style.no_shadow_dialog);
		customProgressDialog.setContentView(R.layout.progressbar);
		customProgressDialog.getWindow().setGravity(Gravity.CENTER);
		return customProgressDialog;
	}
 
    public void onWindowFocusChanged(CustomProgressDialog customProgressDialog  ,boolean hasFocus){
    	if (customProgressDialog == null){
    		return;
    	}
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        Animatable animationDrawable = (Animatable) imageView.getBackground();
        animationDrawable.start();
    }
    
    public static CustomProgressDialog createViewDialog(Context context, View view){
    	CustomProgressDialog customProgressDialog = new CustomProgressDialog(context,R.style.CustomDialog);
		customProgressDialog.setContentView(view);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return customProgressDialog;
    }
 
//    /**
//     * 
//     * [Summary]
//     *       setTitile 标题
//     * @param strTitle
//     * @return
//     *
//     */
//    public CustomProgressDialog setTitile(String strTitle){
//    	return customProgressDialog;
//    }
//    
//    /**
//     * 
//     * [Summary]
//     *       setMessage 提示内容
//     * @param strMessage
//     * @return
//     *
//     */
//    public CustomProgressDialog setMessage(String strMessage){
//    	TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
//    	if (tvMsg != null){
//    		tvMsg.setText(strMessage);
//    	}
//    	return customProgressDialog;
//    }
}