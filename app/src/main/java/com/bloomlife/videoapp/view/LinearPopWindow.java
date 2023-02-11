/**
 * 
 */
package com.bloomlife.videoapp.view;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloomlife.videoapp.R;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>
 * 
 * @notice 这个弹出框和所处于的activity的样式有关。如果在mainfest中使用了theme，会使样式有变化的。需要注意。
 * 
 * @date 2013-11-29 上午10:12:23
 */
public class LinearPopWindow implements OnClickListener {

	private Dialog dialog;
	private TextView titleView;
	private ImageView line;
	
	private TextView first;
	private TextView second;
	private TextView third;

	public LinearPopWindow(Activity ctx) {
		initPop(ctx, null);
	}

	public LinearPopWindow(Activity ctx, String title) {
		initPop(ctx, null, title);
	}

	public LinearPopWindow(Activity ctx, List<String> names) {
		initPop(ctx, names);
	}

	public LinearPopWindow(Activity ctx, List<String> names, String title) {

		initPop(ctx, names, title);
	}

	private void initPop(Activity ctx, List<String> names) {
		initPop(ctx, names, "");
	}

	private void initPop(Activity ctx, List<String> names, String title) {
		dialog = new AlertDialog.Builder(ctx).create();
		View view = LayoutInflater.from(ctx).inflate(
				R.layout.picture_popwindow, null);

		titleView = (TextView) view.findViewById(R.id.title);
		line = (ImageView) view.findViewById(R.id.line);
		if (title == null || title.isEmpty()) {
			titleView.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		} else {
			titleView.setText(title);
		}

		first = (TextView) view.findViewById(R.id.first);
		first.setOnClickListener(this);
		second = (TextView) view.findViewById(R.id.second);
		second.setOnClickListener(this);
		third = (TextView) view.findViewById(R.id.third);
		third.setOnClickListener(this);

		// Button cancel = (Button) dlgView.findViewById(R.id.cancel);
		// cancel.setOnClickListener(this);

		ImageView line1 = (ImageView) view.findViewById(R.id.line1);
		ImageView line2 = (ImageView) view.findViewById(R.id.line2);

		if (names != null && !names.isEmpty()) {
			int size = names.size();
			if (names.size() > 3)
				throw new IllegalArgumentException("输入的控件数量不能大于3个");
			first.setText(names.get(0));
			switch (size) {
			case 1:
				second.setVisibility(View.GONE);
				line1.setVisibility(View.INVISIBLE);
				break;
			case 2:
				second.setText(names.get(1));
				break;
			case 3:
				second.setText(names.get(1));
				third.setVisibility(View.VISIBLE);
				third.setText(names.get(2));
				line2.setVisibility(View.VISIBLE);
				break;
			}

		}

		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.PicturePopWindowStyle); // 添加动画
		dialog.show();
		dialog.setContentView(view);

	}
	
	public void setFirstText(CharSequence text) {
		first.setText(text);
	}

	public void setSecondText(CharSequence text) {
		second.setText(text);
	}

	public void setThirdText(CharSequence text) {
		third.setText(text);
	}

	public void dismiss() {
		this.dialog.dismiss();
	}

	public void show() {
		this.dialog.show();
	}

	public void show(String title) {
		if (title == null || title.isEmpty()) {
			titleView.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		} else {
			titleView.setText(title);
		}
		show();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.first:
			dialog.dismiss();
			listener.actionFirst();
			break;
		case R.id.second:
			dialog.dismiss();
			listener.actionSecond();
			break;
		case R.id.third:
			dialog.dismiss();
			listener.actionThird();
			break;
		// case R.id.cancel:
		// dialog.dismiss();
		default:
			break;
		}

	}

	public interface LinearPopListener {
		void actionFirst();

		void actionSecond();

		void actionThird();
	}

	private LinearPopListener listener;

	public void setPopListener(LinearPopListener listener) {
		this.listener = listener;
	}
}
