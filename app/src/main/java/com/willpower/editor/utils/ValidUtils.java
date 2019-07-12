package com.willpower.editor.utils;

import com.willpower.editor.entity.Frame;

import java.util.List;

public class ValidUtils {

    public static boolean isEmpty(Object obj) {
        if (obj == null) return true;
        if (obj instanceof List) {
            return ((List) obj).size() == 0;
        } else if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        return true;
    }


    public static String getFrameText(Frame frame, boolean isInfo) {
        if (frame == null) return "";
        if (frame.getMode() == Frame.MODE_BUTTON) {
            return "Button";
        } else {
            if (isInfo) {
                switch (frame.getAction()) {
                    case 0:
                        return "默认";
                    case 1:
                        return "眼跳";
                    case 2:
                        return "回视,组ID：" + frame.getGroupId();
                }
            }
        }
        return "";
    }

}
