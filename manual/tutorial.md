
# ������Ŀ5
# WEB APIӦ��

# ��ʮ��������
## WEB API Ӧ��
---
## ����֪ʶ
### 1. CardView����
����CardView����
```java
compile 'com.android.support:cardview-v7:28.+'
```
�������ͬʹ����������һ��ʹ��CardView��
```xml
<android.support.v7.widget.CardView  
  app:cardCornerRadius="8dp"  
  android:layout_width="match_parent"  
  android:layout_height="wrap_content"  
  android:layout_margin="10dp"  
  app:contentPadding="5dp">
</android.support.v7.widget.CardView>
```

### 2. Progressbar����
��򵥵����ü��ɣ�����ֻ��Ҫ������visibility���Լ��ɣ�������Ҫʹ�õ����ܵ�֪ʶ�����̸߳���UI��
```xml
<!--����progressbar�ĸ�ʽ-->
style="?android:attr/progressBarStyleLarge"
<!--Ҳ����ʹ��������ʽ-->
style="?android:attr/progressBarStyleInverse" 
style="?android:attr/progressBarStyleLargeInverse" 
style="?android:attr/progressBarStyleSmall" 
style="?android:attr/progressBarStyleSmallInverse" 
style="?android:attr/progressBarStyleSmallTitle" 
```

### 3. ����ͼƬʹ��

ͼƬ�ĸ��·����ο��μ���ͼƬ��url��Ҫ��ǰͨ�������ӿڵõ�����Ҫ��ȷ���¼���
* �����̷߳���GET����
* GET�ɹ�������handler������Ϣ
* ���̸߳���UI������progressBar

## ��������֪ʶ
### 1. ѧϰʹ��HttpURLConnection
* �������̣߳����߳��н���������ʷ�ֹ�������߳�
* ����һ���µ�URL����
* Ϊ���URL��Դ��Connection
* ��ȡ����
* ��������ʱ��Ҫ�����쳣

���λ���������Ҫ���ʵĽӿ���
`https://space.bilibili.com/ajax/top/showTop?mid=<user_id>`
���ص����ݽṹ�����Լ���postman����������ʼ��ɻ�á�


### 2. �̸߳���UI
�̸߳���UI���ڶ��ַ�������򵥵���handler��
���⽨��ʹ��RxJava������ʹ�÷������ϴ���ҵ�Ѿ������ˡ�

### 3.Json����
��εõ����������£�
`json
 {
 	"status": true,
 	"data": {
 		"aid":30087657,
 		"state":0,
 		"cover":"����",
 		"title":"��ũ�ֵܣ���ֻ�����������ˣ�ũ��С��ֻ�ܰ������ˣ�ζ�����ۿɿ�"
 		����
 	}
 }
`
����������Ҫ����һ���࣬����������������Ҫ��ʾ����Ϣ������
`java
public class RecyclerObj {
    private Boolean status;
    private Data data;
	public static class Data  {
		private int aid;
		����//ʡ��get set��
	}
	����//ʡ��get��set
}
`
֮��ֱ��ʹ������������ɡ�
`java
RecyclerObj recyclerObj = new Gson().fromJson((String)jsonString, RecyclerObj.class);
`
������Ҫ�õ�Gson����Ҫ��������
`compile 'com.squareup.retrofit2:converter-gson:2.1.0'`

### 4.�ӷ���
���μӷ���ּ�������������Լ���Android�̻߳��Ƶ���֪��
��Ҫ��������������������Լ�����UI�Ĳ�����
��Ҫ���������ӿڵõ���aid��
`https://api.bilibili.com/pvideo?aid=<aid>`
�������ݣ���ʽ���£�������Ҫ���������image���߼򵥵�ʹ��pvdata�õ�Ԥ��ͼ����Ϣ��
`json
{
    "code": 0,
    "message": "0",
    "ttl": 1,
    "data": {
        "pvdata": "http://i3.hdslb.com/bfs/videoshot/3648617.bin?vsign=aa201bcb43f2eec2b7d4cc3a56f63e0252e6fca7&ver=31532705",
        "img_x_len": 10,
        "img_y_len": 10,
        "img_x_size": 160,
        "img_y_size": 90,
        "image": [
            "http://i3.hdslb.com/bfs/videoshot/3648617.jpg?vsign=2a581a23de86cfa280b00948f6c16e814f5e1c13&ver=31532705"
        ],
	}
}
`
��������Ԥ��ͼ�������ݼ��ɣ�������BitMap������ɡ�