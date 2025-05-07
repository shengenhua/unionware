package unionware.base.network.module;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.GsonBuilder;
import com.tencent.mmkv.MMKV;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import unionware.base.network.converter.GsonConverterFactory;
import unionware.base.network.cookie.UnionwareCookieJar;
import unionware.base.network.interceptor.BaseUrlInterceptor;
import unionware.base.network.interceptor.HeadInterceptor;

@Module
@InstallIn(SingletonComponent.class)
public class RetrofitModule {
    @Singleton
    @Provides
    Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    @Provides
    @Singleton
    //  http://112.74.102.13/K3Cloud/ http://clouddemo3.kingdeedemo.com:88/k3cloud/
    public Retrofit provideRetrofit(OkHttpClient client) {
        String url = MMKV.mmkvWithID("app").decodeString("url", "https://www.kingdee.com/");
        return new Retrofit.Builder()
                .baseUrl(url != null ? url : "https://www.kingdee.com/")
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();
    }

    @Singleton
    @Provides
    OkHttpClient.Builder provideOkHttpBuilder() {
        return new OkHttpClient.Builder();
    }


    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient(@ApplicationContext Context context, OkHttpClient.Builder builder) {
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        builder.addInterceptor(new BaseUrlInterceptor()); // 动态URL
        builder.addNetworkInterceptor(new HeadInterceptor()); // 自定义Head
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();    /*网络日志*/
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        builder.cookieJar(UnionwareCookieJar.getInstance().create(context));
        builder.cookieJar(cookieJar);
        builder.addInterceptor(logging);
        builder.connectTimeout(5, TimeUnit.MINUTES);  // 设置超时
        builder.readTimeout(5, TimeUnit.MINUTES);
        builder.writeTimeout(5, TimeUnit.MINUTES);
        builder.retryOnConnectionFailure(true); // 设置断线重连
        return builder.build();
    }
}
