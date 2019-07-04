package com.willpower.editor;

import android.os.Environment;

import com.willpower.editor.entity.Page;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class FileUtils {

    public static final String DATA_PATH = getRootPath() + "/Editor/Data";

    public static String getRootPath() {
        return Environment
                .getExternalStorageDirectory().toString();
    }

    /*
    将页面数据写入本地
     */
    public static void saveDataToFile(Page page) {
        File file = new File(DATA_PATH, page.getPageID() + "txt");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            OutputStream outputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(page);
            objectOutputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
