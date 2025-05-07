package unionware.base.network.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * HEAD捕获器
 */
public class HeadInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .header("Content-Type", chain.request().toString().contains("Kingdee") ? "application/json" : "text/plain")
                .build();
        return chain.proceed(request);
    }
}
