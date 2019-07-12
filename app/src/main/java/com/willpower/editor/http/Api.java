package com.willpower.editor.http;

import com.willpower.editor.entity.Project;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface Api {
    String baseUrl = "http://192.168.1.155:5888/";

    //获取项目列表
    @POST("editor/getProjectList")
    Call<String> getProjectList();

    //创建项目
    @POST("editor/createProject")
    Call<String> createProject(@Body RequestBody project);

    //新增页面
    @POST("editor/createPage")
    Call<String> createPage(@Body RequestBody project);

    //修改项目名称
    @POST("editor/updateProjectName")
    Call<String> updateProjectName(@Query("projectId") long projectId,@Query("projectName") String projectName);

    //更新页面
    @POST("editor/updatePage")
    Call<String> updatePage(@Body RequestBody requestBody);

    //新增Frame
    @POST("editor/addFrame")
    Call<String> addFrame(@Body RequestBody requestBody);

    //删除Frames
    @POST("editor/deleteFrames")
    Call<String> deleteFrames(@Query("frameIds") long[] frameIds);

    //获取界面列表
    @POST("editor/getProjectInfo")
    Call<String> getProjectInfo(@Query("projectId") long projectId);

    //上传页面背景图
    @Multipart
    @POST("/editor/uploadPageThumbnail")
    Call<String> uploadPageThumbnail(@Part MultipartBody.Part file,@Query("pageId")long pageId,@Query("projectId")long projectId,@Query("projectName")String projectName);

}
