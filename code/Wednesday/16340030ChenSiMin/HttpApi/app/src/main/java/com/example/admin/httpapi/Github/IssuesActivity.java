package com.example.admin.httpapi.Github;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.httpapi.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class IssuesActivity extends AppCompatActivity {
    private String user_name;
    private String repo_name;
    private static String BASE_URL = "https://api.github.com";

    private final MyRecyclerViewAdapter myRecyclerViewAdapter =
            new MyRecyclerViewAdapter<IssueItem>(IssuesActivity.this, R.layout.github_issue_item, new ArrayList<IssueItem>()){
        @Override
        public void convert(MyViewHolder holder, IssueItem issue_item) {
            ((TextView)holder.getView(R.id.issue_title)).setText("Title：" + issue_item.getTitle());
            ((TextView)holder.getView(R.id.created_at)).setText("创建时间：" + issue_item.getCreated_at());
            ((TextView)holder.getView(R.id.issue_state)).setText("问题状态：" + issue_item.getState());
            ((TextView)holder.getView(R.id.issue_body)).setText("问题描述：" + issue_item.getBody());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github_issue_activity);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user_name = bundle.getString("user_name");
        repo_name = bundle.getString("repo_name");

        InitRecyclerView();
    }

    protected void InitRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myRecyclerViewAdapter);
        getAllIssues();
    }

    public void AddIssuesOnClick(View target) {
        if (!isNetworkAvailable()) {
            Toast.makeText(IssuesActivity.this, "当前网络不可用，请检查网络情况", Toast.LENGTH_SHORT).show();
            return;
        }
        findViewById(R.id.recyclerView).setVisibility(View.GONE);
        findViewById(R.id.linear2).setVisibility(View.VISIBLE);
        final String title = ((EditText)findViewById(R.id.issue_title_input)).getText().toString();
        final String body = ((EditText)findViewById(R.id.issue_body_input)).getText().toString();
        try {
            OkHttpClient build = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .readTimeout(2, TimeUnit.SECONDS)
                    .writeTimeout(2, TimeUnit.SECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) //设置网络请求的Url地址
                    .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(build)
                    .build();
            String message = new Gson().toJson(new PostMessage(title, body));
            RequestBody req_body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=utf-8"), message);
            GitHubService request = retrofit.create(GitHubService.class);
            request.postIssue(user_name, repo_name, req_body)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<IssueItem>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(IssueItem issueItem) {
                            Toast.makeText(IssuesActivity.this, "提交issue成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(IssuesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            getAllIssues();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAllIssues() {
        if (!isNetworkAvailable()) {
            Toast.makeText(IssuesActivity.this, "当前网络不可用，请检查网络情况", Toast.LENGTH_SHORT).show();
            return;
        }
        findViewById(R.id.recyclerView).setVisibility(View.GONE);
        findViewById(R.id.linear2).setVisibility(View.VISIBLE);
        try {
            OkHttpClient build = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .readTimeout(2, TimeUnit.SECONDS)
                    .writeTimeout(2, TimeUnit.SECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) //设置网络请求的Url地址
                    .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(build)
                    .build();
            GitHubService request = retrofit.create(GitHubService.class);
            request.getIssue(user_name, repo_name)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<List<IssueItem>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<IssueItem> list) {
                            // 按照创建日期排序
                            list.sort(new Comparator<IssueItem>() {
                                @Override
                                public int compare(IssueItem o1, IssueItem o2) {
                                    return o2.getCreated_at().compareTo(o1.getCreated_at());
                                }
                            });
                            myRecyclerViewAdapter.refresh(list);
                            if (list.size() == 0) {
                                Toast.makeText(IssuesActivity.this,
                                        "该项目暂无相应的issues", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            List<IssueItem> list = new ArrayList<>();
                            myRecyclerViewAdapter.refresh(list);
                            Toast.makeText(IssuesActivity.this,
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                            findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
                            findViewById(R.id.linear2).setVisibility(View.GONE);
                        }
                    });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) IssuesActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
