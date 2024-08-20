package com.tennisdc.tln.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.WindowManager;

import com.tennisdc.tln.R;
import com.tennisdc.tln.interfaces.OnDialogButtonClickListener;
import com.tennisdc.tln.interfaces.OnDialogButtonNuteralClickListener;

/**
 * contains Dialogs to be used in the application
 */
public class DialogsUtil {

    /**
     * Return an alert dialog
     *
     * @param message  message for the alert dialog
     * @param listener listener to trigger selection methods
     */
    public void openAlertDialog(Context context, String message, String positiveBtnText, String negativeBtnText,
                                final OnDialogButtonClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.onPositiveButtonClicked();

            }
        });

        builder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.onNegativeButtonClicked();

            }
        });
        builder.setTitle(context.getResources().getString(R.string.app_name));
        builder.setMessage(message);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);
        builder.create().show();
    }

    /**
     * Return an alert dialog
     *
     * @param message  message for the alert dialog
     * @param listener listener to trigger selection methods
     */
    public void openAlertDialogWithCustomTitle(Context context, String title, String message, String positiveBtnText, String negativeBtnText,
                                               String nuteralBtnText, final OnDialogButtonNuteralClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.onPositiveButtonClicked();

            }
        });

        builder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.onNegativeButtonClicked();

            }
        });

        builder.setNeutralButton(nuteralBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.onNuteralButtonClicked();

            }
        });
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);
        builder.create().show();
    }


    public Dialog showDialog(Context context, int layout){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(layout);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(true);
       return dialog;
    }

    public Dialog showDialogFix(Context context, int layout){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(layout);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
    public Dialog showDialogFull(Context context, int layout){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(layout);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}