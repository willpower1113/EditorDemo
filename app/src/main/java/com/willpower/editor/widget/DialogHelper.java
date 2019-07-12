package com.willpower.editor.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class DialogHelper {

    static Handler handler = new Handler(Looper.getMainLooper());

    public static void showTipDialog(Context context, @QMUITipDialog.Builder.IconType int iconType) {
        showTipDialog(context, iconType, null);
    }

    public static void showTipDialog(Context context, @QMUITipDialog.Builder.IconType int iconType, String words) {
        showTipDialog(context, iconType, words, 2000);
    }

    public static void showTipDialog(Context context, @QMUITipDialog.Builder.IconType int iconType, String words, long duration) {
        final QMUITipDialog dialog = new QMUITipDialog
                .Builder(context)
                .setIconType(iconType)
                .setTipWord(words)
                .create();
        dialog.show();
        handler.postDelayed(() -> dialog.dismiss(), duration);
    }

    public static QMUITipDialog showLoading(Context context,String msg){
        return new QMUITipDialog
                .Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(msg)
                .create();
    }

}
