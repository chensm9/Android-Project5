package com.example.admin.httpapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public abstract class MyRecyclerViewAdapter<T> extends RecyclerView.Adapter<MyViewHolder> {
    final private List<T> data;
    private Context context;
    private int layoutId;
    private  Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public MyRecyclerViewAdapter(Context _context, int _layoutId, List<T> _data) {
        data = _data;
        layoutId = _layoutId;
        context = _context;
    }

    public abstract void convert(MyViewHolder holder, T t);

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = MyViewHolder.get(context, parent, layoutId);
        return holder;
    }

    public interface OnItemClickListener {
        void onClick(int position);
        void onLongClick(int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener _onItemClickListener) {
        this.onItemClickListener = _onItemClickListener;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        convert(holder, data.get(position)); // convert函数需要重写
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.onLongClick(holder.getAdapterPosition());
                    return true;
                }
            });
        }
    }

    public void addItem(T item) {
        data.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position < data.size()) {
            data.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void getOriginBitmapByUrl(final MyViewHolder viewHolder, final RecyclerObj recyclerObj) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(recyclerObj.getData().cover);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ((ImageView)viewHolder.getView(R.id.image)).setImageBitmap(bitmap);
                                viewHolder.getView(R.id.progress_bar).setVisibility(View.GONE);
                                viewHolder.getView(R.id.image).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = -1;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }


    public void getPvideoBimapByUrl(final MyViewHolder viewHolder, final RecyclerObj recyclerObj) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int aid = recyclerObj.getData().aid;
                    URL url = new URL("https://api.bilibili.com/pvideo?aid="+aid);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");//设置HTTP请求方式为GET
                    connection.setConnectTimeout(1000);//设置连接超时的毫秒数
                    connection.setReadTimeout(1000);//设置读取超时的毫秒数

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = connection.getInputStream();
                        //下面对获取到的输入流进行读取
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        String response = URLDecoder.decode(stringBuilder.toString());
                        final PvideoObj pvideoObj = new Gson().fromJson(response, PvideoObj.class);

                        URL pv_url = new URL(pvideoObj.getData().image.get(0));
                        HttpURLConnection conn = (HttpURLConnection) pv_url.openConnection();
                        conn.setConnectTimeout(1000);
                        conn.setRequestMethod("GET");
                        if (conn.getResponseCode() == 200) {
                            InputStream inputStream = conn.getInputStream();
                            final Bitmap totalBitmap = BitmapFactory.decodeStream(inputStream);
                            List<Bitmap> list = new ArrayList<>();
                            for (int i = 0; i < pvideoObj.getData().img_x_len; i++) {
                                for (int j = 0; j < pvideoObj.getData().img_y_len; j++) {
                                    int xValue = j * pvideoObj.getData().img_x_size;
                                    int yValue = i * pvideoObj.getData().img_y_size;
                                    Bitmap t_bitmap = Bitmap.createBitmap(totalBitmap, xValue, yValue,
                                            pvideoObj.getData().img_x_size,
                                            pvideoObj.getData().img_y_size);
                                    list.add(t_bitmap);
                                    list.add(t_bitmap);
                                    list.add(t_bitmap);
                                }
                            }
                            recyclerObj.setBitmapList(list);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setSeekBar(final MyViewHolder viewHolder, final RecyclerObj recyclerObj) {
//        if (recyclerObj.getData().aid == 6845155) {
//            viewHolder.seekBar.setClickable(false);
//            viewHolder.seekBar.setEnabled(false);
//            viewHolder.seekBar.setSelected(false);
//            viewHolder.seekBar.setFocusable(false);
//        }
        SeekBar seekBar = viewHolder.getView(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (recyclerObj.getBitmapList() != null && progress < recyclerObj.getBitmapList().size()) {
                    ((ImageView)viewHolder.getView(R.id.image))
                            .setImageBitmap(recyclerObj.getBitmapList().get(progress));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(0);
                getOriginBitmapByUrl(viewHolder, recyclerObj);
            }
        });
    }
}
