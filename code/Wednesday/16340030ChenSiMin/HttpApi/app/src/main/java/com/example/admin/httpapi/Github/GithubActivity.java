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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GithubActivity extends AppCompatActivity {

    private static String BASE_URL = "https://api.github.com";
    private final MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter<RepoItem>(GithubActivity.this, R.layout.github_repo_item, new ArrayList<RepoItem>()){
        @Override
        public void convert(MyViewHolder holder, RepoItem repo_item) {
            ((TextView)holder.getView(R.id.repo_name)).setText("项目名：" + repo_item.getName());
            ((TextView)holder.getView(R.id.repo_id)).setText("项目id：" + repo_item.getId());
            ((TextView)holder.getView(R.id.create_at)).setText("创建时间：" + repo_item.getCreated_at());
            ((TextView)holder.getView(R.id.issue_num)).setText("存在问题：" + repo_item.getOpen_issues());
            ((TextView)holder.getView(R.id.description)).setText("项目描述：" + repo_item.getDescription());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github_repo_activity);
        Init();
    }

    public void SearchRepoOnClick(View target) {
        if (!isNetworkAvailable()) {
            Toast.makeText(GithubActivity.this, "当前网络不可用，请检查网络情况", Toast.LENGTH_SHORT).show();
            return;
        }
        String username = ((EditText)findViewById(R.id.input)).getText().toString();
        findViewById(R.id.recyclerView).setVisibility(View.GONE);
        findViewById(R.id.linear2).setVisibility(View.VISIBLE);
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
        request.getRepo(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<RepoItem>>() {
                       @Override
                       public void onSubscribe(Disposable d) {

                       }

                       @Override
                       public void onNext(List<RepoItem> list) {
                           list.removeIf(new Predicate<RepoItem>() {
                               @Override
                               public boolean test(RepoItem repoItem) {
                                   return !repoItem.getHas_issues();
                               }
                           });
                           // 按照创建日期排序
                           list.sort(new Comparator<RepoItem>() {
                               @Override
                               public int compare(RepoItem o1, RepoItem o2) {
                                   return o2.getCreated_at().compareTo(o1.getCreated_at());
                               }
                           });
                           myRecyclerViewAdapter.refresh(list);
                           if (list.size() == 0) {
                               Toast.makeText(GithubActivity.this,
                                       "该用户不存在可提交issue的项目", Toast.LENGTH_SHORT).show();
                           }
                       }

                       @Override
                       public void onError(Throwable e) {
                           List<RepoItem> list = new ArrayList<>();
                           myRecyclerViewAdapter.refresh(list);
                           Toast.makeText(GithubActivity.this,
                                   "该用户不存在", Toast.LENGTH_SHORT).show();
                           findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
                           findViewById(R.id.linear2).setVisibility(View.GONE);
                       }

                       @Override
                       public void onComplete() {
                           findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
                           findViewById(R.id.linear2).setVisibility(View.GONE);
                       }
                   });
    }

    public void Init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myRecyclerViewAdapter);

        myRecyclerViewAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int i) {
                Intent intent = new Intent(GithubActivity.this, IssuesActivity.class);
                Bundle bundle = new Bundle();
                RepoItem item = (RepoItem)myRecyclerViewAdapter.getItem(i);
                bundle.putSerializable("repo_name", item.getName());
                bundle.putSerializable("user_name", item.getUserName());
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {

            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) GithubActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
