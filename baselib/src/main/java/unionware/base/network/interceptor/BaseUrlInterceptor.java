package unionware.base.network.interceptor;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BaseUrlInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {

        //获取request
        Request request = chain.request();
        //从request中获取原有的HttpUrl实例oldHttpUrl
        HttpUrl oldHttpUrl = request.url();

//        List<String> headerValues = request.headers("domain");
        //获取request的创建者builder
        Request.Builder builder = request.newBuilder();
        //从request中获取headers，通过给定的键url_name
        //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用

        //根据业务逻辑，动态获取的服务器地址

        String baseUrl = MMKV.mmkvWithID("app").decodeString("url", "http://192.168.0.71/K3Cloud/");
        if (baseUrl == null) {
            baseUrl = "http://192.168.0.71/K3Cloud/";
        }
        HttpUrl newHttpUrl = HttpUrl.parse(
                oldHttpUrl.toString().contains("MachineAuthor")
                        || oldHttpUrl.toString().contains("MachineAuthorizeOnline")
                        ? oldHttpUrl.toString() : baseUrl);
        if (newHttpUrl != null && !request.url().host().equals(newHttpUrl.host())) {
            //重建新的HttpUrl，修改需要修改的url部分
            HttpUrl.Builder newBuild = newHttpUrl.newBuilder();
            newBuild.addPathSegments(oldHttpUrl.pathSegments().get(oldHttpUrl.pathSegments().size() - 1));
            if (!oldHttpUrl.queryParameterNames().isEmpty()) {
                Object[] list = oldHttpUrl.queryParameterNames().toArray();
                for (int i = 0; i < list.length; i++) {
                    newBuild.setQueryParameter(list[i].toString(), oldHttpUrl.queryParameterValue(i));
                }
            }

            //重建这个request，通过builder.url(newFullUrl).build()；
            // 然后返回一个response至此结束修改
            return chain.proceed(builder.url(newBuild.build()).build());
        }
        return chain.proceed(chain.request());
    }
}

