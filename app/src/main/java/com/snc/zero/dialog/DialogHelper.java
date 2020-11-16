package com.snc.zero.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.app.AlertDialog;

import com.snc.zero.log.Logger;

/**
 * Dialog Helper
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class DialogHelper {

	public static void alert(Activity activity, String message) {
		alert(activity, "", message, android.R.string.ok,
				(dialog, which) -> {

				});
	}

	public static void alert(Activity activity, String title, String message, int resId, DialogInterface.OnClickListener listener) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle(title);
			builder.setMessage(message);
			builder.setView(null);
			builder.setNegativeButton(activity.getResources().getString(resId), listener);
			builder.setCancelable(true);

			Dialog dialog = builder.show();
			dialog.setCanceledOnTouchOutside(false);

		} catch (android.view.WindowManager.BadTokenException e) {
			Logger.e(activity.getClass().getSimpleName(), e);
		}
	}

}
