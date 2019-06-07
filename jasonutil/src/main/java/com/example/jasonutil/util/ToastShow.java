package com.example.jasonutil.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * toast管理
 */
public class ToastShow {
	private static Map<String, Long> map = new HashMap<String, Long>();
	private static long MAX_SHOW_TIME = 3 * 1000;

	public static void showToast(final Context act, final String message, boolean isShowToast) {

		if(!ActivityUtil.isActivityOnTop(act)) {
            return;
        }
		if(StringUtils.isEmpty(message)) {
            return;
        }
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(act, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void showToast(final Context act, final String message) {
		if(!ActivityUtil.isActivityOnTop(act)) {
            return;
        }
		if(StringUtils.isEmpty(message)) {
            return;
        }
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(act, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void showToastLength(final Context act, final String message) {
		if(!ActivityUtil.isActivityOnTop(act)) {
            return;
        }
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(act, message, Toast.LENGTH_LONG).show();
			}
		});
	}
	public static void showToast2(final Context act, final String message) {
		if(!ActivityUtil.isActivityOnTop(act)) {
            return;
        }
		if(StringUtils.isEmpty(message)) {
            return;
        }
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				show(act, message);
			}
		});
	}
	public static void showToastShowCenter(final Context act, final String message) {
		if(!ActivityUtil.isActivityOnTop(act)) {
            return;
        }
		if(StringUtils.isEmpty(message)) {
            return;
        }
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				showTime(act, message);
			}
		});
	}
	private static boolean isShow(String message) {
		synchronized (map) {
			if (!map.containsKey(message)) {
				return true;
			}

			long lastShowTime = map.get(message);
			if (System.currentTimeMillis() - lastShowTime > MAX_SHOW_TIME) {
				return true;
			}

			return false;
		}
	}

	private static void show(Context act, String message) {
		if (!isShow(message)) {
			return;
		}
		synchronized (map) {
			map.put(message, System.currentTimeMillis());
			Toast.makeText(act, message, Toast.LENGTH_SHORT).show();
		}
	}

	private static void showTime(Context act, String message) {
		if (!isShow(message)) {
			return;
		}
		synchronized (map) {
			map.put(message, System.currentTimeMillis());
			Toast toast = Toast.makeText(act,message, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}


}
