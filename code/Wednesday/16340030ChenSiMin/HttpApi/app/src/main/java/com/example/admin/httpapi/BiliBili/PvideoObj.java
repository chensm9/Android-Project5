package com.example.admin.httpapi.BiliBili;

import java.util.List;

public class PvideoObj {
    public int code;
    public String message;
    private Data data;
    public static class Data {
        public String pvdata;
        public int img_x_len;
        public int img_y_len;
        public int img_x_size;
        public int img_y_size;
        public List<String> image;
        public int[] index;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
