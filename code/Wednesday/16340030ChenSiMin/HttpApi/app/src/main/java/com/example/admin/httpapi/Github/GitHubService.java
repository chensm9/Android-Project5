package com.example.admin.httpapi.Github;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GitHubService {
    @Headers("Authorization: token fa6a0f3b4fd5f1a639972a5b7fff2a3a66e660ba")
    @GET("/users/{user_name}/repos")
    Observable<List<RepoItem>> getRepo(@Path("user_name") String user_name);

    @Headers("Authorization: token fa6a0f3b4fd5f1a639972a5b7fff2a3a66e660ba")
    @GET("/repos/{user_name}/{repo_name}/issues")
    Observable<List<IssueItem>> getIssue(@Path("user_name") String user_name,
                                   @Path("repo_name") String repo_name);

    @Headers("Authorization: token fa6a0f3b4fd5f1a639972a5b7fff2a3a66e660ba")
    @POST("/repos/{user_name}/{repo_name}/issues")
    Observable<IssueItem> postIssue(@Path("user_name") String user_name,
                                    @Path("repo_name") String repo_name,
                                    @Body RequestBody postMessage);
}

class PostMessage{
    private String title;
    private String body;
    public PostMessage(String title, String body) {
        this.title = title;
        this.body = body;
    }
}