package com.example.admin.httpapi.BiliBili;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class RecyclerObj {
    private Boolean status;
    private Data data;
    public List<Bitmap> BitmapList = null;
    public static class Data  {
        public int aid;
        public int state;
        public String cover;
        public String title;
        public String content;
        public int play;
        public String duration;
        public int video_review;
        public String create;
        //省略get set等
    }

    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }

    public List<Bitmap> getBitmapList() {
        return BitmapList;
    }

    public void setBitmapList(List<Bitmap> bitmapList) {
        BitmapList = bitmapList;
    }
}
