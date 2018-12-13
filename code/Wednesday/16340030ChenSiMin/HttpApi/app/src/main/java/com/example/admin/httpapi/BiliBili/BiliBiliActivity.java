package com.example.admin.httpapi.BiliBili;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.httpapi.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class BiliBiliActivity extends AppCompatActivity {
    final MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter<RecyclerObj>(BiliBiliActivity.this, R.layout.bilibili_item, new ArrayList<RecyclerObj>()) {
        @Override
        public void convert(MyViewHolder holder, RecyclerObj recyclerObj) {
            ((TextView)holder.getView(R.id.play_times)).setText("播放："+recyclerObj.getData().play);
            ((TextView)holder.getView(R.id.comment_num)).setText("评论："+recyclerObj.getData().video_review);
            ((TextView)holder.getView(R.id.duration)).setText("时长：" + recyclerObj.getData().duration);
            ((TextView)holder.getView(R.id.create_at)).setText("创建时间：" + recyclerObj.getData().create);
            ((TextView)holder.getView(R.id.title)).setText(recyclerObj.getData().title);
            ((TextView)holder.getView(R.id.content)).setText(recyclerObj.getData().content);
            getOriginBitmapByUrl(holder, recyclerObj);
            getPvideoBimapByUrl(holder, recyclerObj);
            setSeekBar(holder, recyclerObj);
        }
    };
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bilibili_activity);
        Init();
    }

    private void Init() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(BiliBiliActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 0:
                        Toast.makeText(BiliBiliActivity.this, "数据库中不存在记录", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        String response = msg.getData().getString("response");
                        RecyclerObj recyclerObj = new Gson().fromJson(response, RecyclerObj.class);
                        myRecyclerViewAdapter.addItem(recyclerObj);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }

    public void SearchButtonOnClick(View target) {
        EditText input = findViewById(R.id.input);
        final String mid = input.getText().toString();
        if (!isNumeric(mid)) {
            Toast.makeText(BiliBiliActivity.this, "请输入用户ID（整数类型）", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://space.bilibili.com/ajax/top/showTop?mid="+mid);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");//设置HTTP请求方式为GET
                    connection.setConnectTimeout(1000);//设置连接超时的毫秒数
                    connection.setReadTimeout(1000);//设置读取超时的毫秒数

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = connection.getInputStream();
                        //下面对获取到的输入流进行读取
                        reader = new BufferedReader(new InputStreamReader(in));

                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        String response = URLDecoder.decode(stringBuilder.toString());

                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("response", response);
                        if (response.indexOf("false") != -1)
                            msg.what = 0;
                        else {
                            msg.what = 1;
                            msg.setData(bundle);
                        }
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = -1;
                    mHandler.sendMessage(msg);
                } finally {
                    if(reader != null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
