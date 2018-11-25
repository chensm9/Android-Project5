
# 个人项目5
# WEB API应用

# 第十三周任务
## WEB API 应用
---
## 布局知识
### 1. CardView介绍
引入CardView依赖
```java
compile 'com.android.support:cardview-v7:28.+'
```
便可以如同使用正常属性一样使用CardView了
```xml
<android.support.v7.widget.CardView  
  app:cardCornerRadius="8dp"  
  android:layout_width="match_parent"  
  android:layout_height="wrap_content"  
  android:layout_margin="10dp"  
  app:contentPadding="5dp">
</android.support.v7.widget.CardView>
```

### 2. Progressbar介绍
最简单的设置即可，我们只需要更新其visibility属性即可，这里需要使用到上周的知识，子线程更新UI。
```xml
<!--设置progressbar的格式-->
style="?android:attr/progressBarStyleLarge"
<!--也可以使用其他格式-->
style="?android:attr/progressBarStyleInverse" 
style="?android:attr/progressBarStyleLargeInverse" 
style="?android:attr/progressBarStyleSmall" 
style="?android:attr/progressBarStyleSmallInverse" 
style="?android:attr/progressBarStyleSmallTitle" 
```

### 3. 网络图片使用
[参考](https://blog.csdn.net/qq_33200967/article/details/77263062?locationNum=1&fps=1)

需要明确以下几点
* 开新线程发送GET请求
* GET成功后利用handler传递消息
* 主线程更新UI，隐藏progressBar

更新UI方法见《音乐播放器作业》

## 网络请求知识
### 1. 依赖

网络请求我们使用了OkHttp3，Retrofit2，RXJAVA，根据自己环境引入相关依赖，之后需要利用到json解析等各种工具，在这一并引入。
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
本次Retrofit实现较为麻烦，需要写多个文件。
建议同学们多查询相关资料，觉得难以实现的可以使用网络图片获取部分的请求方法，或只使用Retrofit+OkHttp。
* **RecyclerObj.java**

*Model定义*
不仅用于展示数据，同时作为RecyclerView的元素。
先使用POSTMAN等软件模拟发送请求，或直接使用浏览器访问也行，请求格式如下：
`https://space.bilibili.com/ajax/top/showTop?mid=250858633`
得到返回的数据结构如下：
	```json
	{
		"status": true,
		"data": {
			"aid":30087657,
			"state":0,
			"cover":"……",
			"title":"华农兄弟：这只竹鼠打架受内伤，农村小伙只能把它煮了，味道鲜嫩可口"
			……
		}
	}
	```
根据JSON数据结构设计相应的JAVA类，需要明确指出数据对应关系，如Bool类型，INT类型等，需要引入的包如下。gson为数据解析器，我们需要明确的说明json的字段名。
`import com.google.gson.annotations.SerializedName;`
这种方法比较繁琐，接口更改的时候往往会造成大面积的改动。
最终设计结果如下：
	```java
	public class RecyclerObj {  
	    @SerializedName("status")  
	    private Boolean status;  
	    @SerializedName("data")  
	    private data idata;  
 
	    public static class data {  
	        @SerializedName("aid")  
	        private int aid;
	        ……省略其他字段
	        ……省略get函数以及set函数
	    }
	    ……省略get函数以及set函数
		
		//加分项使用
	    public List<Bitmap> bitmaps = new ArrayList<Bitmap>();  
	    public Bitmap bitmap;  
	}
	```
* **新建接口类，DataService.java**
需要引入retorfit的相关包
由于本次基本作业只使用了一个API，故可以将请求URL主体部分分隔开，@Query为请求参数
`import retrofit2.http.GET;`  
`import retrofit2.http.Query;`
利用RXJAVA时还需要引入
`import rx.Observable;`
最终结果如下：
	```java
	public interface DataService {  
    @GET("showTop")  
	    Observable<RecyclerObj> getUser(@Query("mid") String User);  
	}
	```
* **MainActivity调用接口**
这里使用了RXJAVA的观察发布者模式。
	```java
	dataService.getUser(text)  
        .subscribeOn(Schedulers.newThread())  
        .observeOn(AndroidSchedulers.mainThread())  
        .subscribe(new Subscriber<RecyclerObj>() {
			@Override  
			public void onCompleted() {}
			……onNext,接收到数据时，数据格式为recyclerObj
			……onError，出错时，包括网络连接错误等
			……oncompleted，结束时
	});
	```
dataSerevice来源于retrofit,DataService即为接口类。
	```java
		DataService dataService = retrofit.create(DataService.class);
	```
* **retrofit**声明
	```java
	//先声明OkHttpClient，因为retrofit时基于okhttp的，在这可以设置一些超时参数等
	OkHttpClient build = new OkHttpClient.Builder()  
                        .connectTimeout(10, TimeUnit.SECONDS)  
                        .readTimeout(30, TimeUnit.SECONDS)  
                        .writeTimeout(10, TimeUnit.SECONDS)  
                        .build();  
	Retrofit retrofit = new Retrofit.Builder()
	// baseURL即为请求前缀，本次作业为showTop之前的字符  
                        .baseUrl(baseURL)  
	// 设置json数据解析器
	                    .addConverterFactory(GsonConverterFactory.create())

	// RxJava封装OkHttp的Call函数，本质还是利用OkHttp请求数据
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())  
                        .client(build)  
                        .build();
	```
	至此，完成http请求 并获得数据。
### 3.  DEMO最终文件结构：
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

### 4. 加分项简介
存在一定难度，可不做。
需要先利用以下api获取数据，传入参数为showTop得到的aid
`https://api.bilibili.com/pvideo?aid=<aid>`
得到的数据结构如下：
```json
{
	"code": 0,
	"message":"0",
	"ttl":1,
	"data": {
		"pvdata":"URL", //浏览器解析图片的二进制文件，本次作业不适用
		"img_x_len":10, //x轴包含图片数目
		"img_y_len":10, //y轴包含图片数目，总计有x*y张
		"img_x_size":160, //每张预览图宽度，像素px值
		"img_y_size":90, //每张预览图高度
		"image":["IMG_URL1","IMG_URL2"], //预览图数组
		"index":[0,0,8,16,……], //前端解析数组，本次作业没有用到
	}
} 
``` 
上面得到的数据中，image即保存了预览图的相关图片。
得到的预览图时下面这种jpg格式的图片，对该图片解析保存成数组，根据seekBar位置变换数组下标即可。
<img src="/images/349.jpg" />

### 5.第十四周实验介绍（DEMO暂未更新）
在此项目基础上，实现摇一摇震动手机并随机搜索user_id的功能。
