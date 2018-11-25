
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
[�ο�](https://blog.csdn.net/qq_33200967/article/details/77263062?locationNum=1&fps=1)

��Ҫ��ȷ���¼���
* �����̷߳���GET����
* GET�ɹ�������handler������Ϣ
* ���̸߳���UI������progressBar

����UI�����������ֲ�������ҵ��

## ��������֪ʶ
### 1. ����

������������ʹ����OkHttp3��Retrofit2��RXJAVA�������Լ������������������֮����Ҫ���õ�json�����ȸ��ֹ��ߣ�����һ�����롣
```java
compile 'com.squareup.retrofit2:retrofit:2.0.2'  
compile 'com.squareup.retrofit2:converter-scalars:2.0.0'  
compile 'com.squareup.retrofit2:converter-gson:2.1.0'  
compile 'com.squareup.retrofit2:adapter-rxjava:2.1.0'  
compile 'io.reactivex:rxandroid:1.2.1'  
compile 'io.reactivex:rxjava:1.2.1'  
compile 'com.squareup.okhttp3:okhttp:3.2.0'
```

### 2.Retrofit+RxJava+OkHttp
����Retrofitʵ�ֽ�Ϊ�鷳����Ҫд����ļ���
����ͬѧ�Ƕ��ѯ������ϣ���������ʵ�ֵĿ���ʹ������ͼƬ��ȡ���ֵ����󷽷�����ֻʹ��Retrofit+OkHttp��
* **RecyclerObj.java**

*Model����*
��������չʾ���ݣ�ͬʱ��ΪRecyclerView��Ԫ�ء�
��ʹ��POSTMAN�����ģ�ⷢ�����󣬻�ֱ��ʹ�����������Ҳ�У������ʽ���£�
`https://space.bilibili.com/ajax/top/showTop?mid=250858633`
�õ����ص����ݽṹ���£�
	```json
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
	```
����JSON���ݽṹ�����Ӧ��JAVA�࣬��Ҫ��ȷָ�����ݶ�Ӧ��ϵ����Bool���ͣ�INT���͵ȣ���Ҫ����İ����¡�gsonΪ���ݽ�������������Ҫ��ȷ��˵��json���ֶ�����
`import com.google.gson.annotations.SerializedName;`
���ַ����ȽϷ������ӿڸ��ĵ�ʱ����������ɴ�����ĸĶ���
������ƽ�����£�
	```java
	public class RecyclerObj {  
	    @SerializedName("status")  
	    private Boolean status;  
	    @SerializedName("data")  
	    private data idata;  
 
	    public static class data {  
	        @SerializedName("aid")  
	        private int aid;
	        ����ʡ�������ֶ�
	        ����ʡ��get�����Լ�set����
	    }
	    ����ʡ��get�����Լ�set����
		
		//�ӷ���ʹ��
	    public List<Bitmap> bitmaps = new ArrayList<Bitmap>();  
	    public Bitmap bitmap;  
	}
	```
* **�½��ӿ��࣬DataService.java**
��Ҫ����retorfit����ذ�
���ڱ��λ�����ҵֻʹ����һ��API���ʿ��Խ�����URL���岿�ַָ�����@QueryΪ�������
`import retrofit2.http.GET;`  
`import retrofit2.http.Query;`
����RXJAVAʱ����Ҫ����
`import rx.Observable;`
���ս�����£�
	```java
	public interface DataService {  
    @GET("showTop")  
	    Observable<RecyclerObj> getUser(@Query("mid") String User);  
	}
	```
* **MainActivity���ýӿ�**
����ʹ����RXJAVA�Ĺ۲췢����ģʽ��
	```java
	dataService.getUser(text)  
        .subscribeOn(Schedulers.newThread())  
        .observeOn(AndroidSchedulers.mainThread())  
        .subscribe(new Subscriber<RecyclerObj>() {
			@Override  
			public void onCompleted() {}
			����onNext,���յ�����ʱ�����ݸ�ʽΪrecyclerObj
			����onError������ʱ�������������Ӵ����
			����oncompleted������ʱ
	});
	```
dataSerevice��Դ��retrofit,DataService��Ϊ�ӿ��ࡣ
	```java
		DataService dataService = retrofit.create(DataService.class);
	```
* **retrofit**����
	```java
	//������OkHttpClient����Ϊretrofitʱ����okhttp�ģ������������һЩ��ʱ������
	OkHttpClient build = new OkHttpClient.Builder()  
                        .connectTimeout(10, TimeUnit.SECONDS)  
                        .readTimeout(30, TimeUnit.SECONDS)  
                        .writeTimeout(10, TimeUnit.SECONDS)  
                        .build();  
	Retrofit retrofit = new Retrofit.Builder()
	// baseURL��Ϊ����ǰ׺��������ҵΪshowTop֮ǰ���ַ�  
                        .baseUrl(baseURL)  
	// ����json���ݽ�����
	                    .addConverterFactory(GsonConverterFactory.create())

	// RxJava��װOkHttp��Call���������ʻ�������OkHttp��������
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())  
                        .client(build)  
                        .build();
	```
	���ˣ����http���� ��������ݡ�
### 3.  DEMO�����ļ��ṹ��
```
	|-- JAVA
		|-- DataService.java
		|-- MainActivity.java
		|-- MyImageView.java
		|-- RecycleAdapter.java
		|-- RecyclerObj.java
	|-- XML
		|-- activity_main.xml
		|-- recycle.xml
	|--build.gradler
```

### 4. �ӷ�����
����һ���Ѷȣ��ɲ�����
��Ҫ����������api��ȡ���ݣ��������ΪshowTop�õ���aid
`https://api.bilibili.com/pvideo?aid=<aid>`
�õ������ݽṹ���£�
```json
{
	"code": 0,
	"message":"0",
	"ttl":1,
	"data": {
		"pvdata":"URL", //���������ͼƬ�Ķ������ļ���������ҵ������
		"img_x_len":10, //x�����ͼƬ��Ŀ
		"img_y_len":10, //y�����ͼƬ��Ŀ���ܼ���x*y��
		"img_x_size":160, //ÿ��Ԥ��ͼ��ȣ�����pxֵ
		"img_y_size":90, //ÿ��Ԥ��ͼ�߶�
		"image":["IMG_URL1","IMG_URL2"], //Ԥ��ͼ����
		"index":[0,0,8,16,����], //ǰ�˽������飬������ҵû���õ�
	}
} 
``` 
����õ��������У�image��������Ԥ��ͼ�����ͼƬ��
�õ���Ԥ��ͼʱ��������jpg��ʽ��ͼƬ���Ը�ͼƬ������������飬����seekBarλ�ñ任�����±꼴�ɡ�
<img src="/images/349.jpg" />

### 5.��ʮ����ʵ����ܣ�DEMO��δ���£�
�ڴ���Ŀ�����ϣ�ʵ��ҡһҡ���ֻ����������user_id�Ĺ��ܡ�
