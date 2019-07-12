//package com.willpower.editor.utils;
//
//import android.content.Context;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.util.Log;
//
//import com.jph.takephoto.uitl.TUriParse;
//import com.willpower.editor.entity.Project;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//
//public class FileUtils {
//
//    public static final String DATA_PATH = getRootPath() + "/Editor/Data";
//
//    public static String getRootPath() {
//        return Environment
//                .getExternalStorageDirectory().toString();
//    }
//
//    public static Uri getUriFromFile(Context context, File file) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            return TUriParse.getUriForFile(context, file);
//        } else {
//            return Uri.fromFile(file);
//        }
//    }
//
//    public static ArrayList<Project> getProjectList() {
//        File file = new File(DATA_PATH);
//        ArrayList<Project> projects = new ArrayList<>();
//        if (file.exists()) {
//            File[] files = file.listFiles();
//            Log.e("归档", "文件数量：" + files.length);
//            for (File f :
//                    files) {
//                Project project = readFileToProject(f.listFiles()[0]);
//                if (project != null) {
//                    projects.add(project);
//                }
//            }
//        }
//        Log.e("归档", "以创建项目数量：" + projects.size());
//        return projects;
//    }
//
//    /*
//    读物页面数据
//     */
//    public static Project readFileToProject(File file) {
//        InputStream inputStream = null;
//        ObjectInputStream stream = null;
//        Project project = null;
//        try {
//            inputStream = new FileInputStream(file);
//            stream = new ObjectInputStream(inputStream);
//            project = (Project) stream.readObject();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                stream.close();
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return project;
//    }
//
//    /*
//    将页面数据写入本地
//     */
//    public static boolean saveProjectToFile(Project project) {
//        File file = new File(DATA_PATH + "/" + project.getProjectName(), project.getProjectName() + "txt");
//        if (!file.getParentFile().exists()) {
//            file.getParentFile().mkdirs();
//        }
//        OutputStream outputStream = null;
//        ObjectOutputStream objectOutputStream = null;
//        try {
//            outputStream = new FileOutputStream(file);
//            objectOutputStream = new ObjectOutputStream(outputStream);
//            objectOutputStream.writeObject(project);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            try {
//                objectOutputStream.close();
//                outputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//        return true;
//    }
//
//
//}
