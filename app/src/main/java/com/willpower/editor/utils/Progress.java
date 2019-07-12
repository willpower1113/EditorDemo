package com.willpower.editor.utils;

import android.content.Context;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.willpower.editor.R;

public class Progress {
    public static QMUIDialog showProgress(Context context) {
        return new QMUIDialog.CustomDialogBuilder(context)
                .setLayout(R.layout.dialog_progress)
                .create(R.style.qmui_dialog_wrap);
    }


}
