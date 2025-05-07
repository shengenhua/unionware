package unionware.base.network.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Converter;
import unionware.base.network.response.BaseResponse;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        try {
            T result;

            BufferedSource source = value.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.getBuffer();
            String content = buffer.clone().readString(StandardCharsets.UTF_8);
            if (content.equals("403 Forbidden ByRspRetStatusCode -- N001: Unexpectable request.")) {
                BaseResponse<Object> baseResponse = new BaseResponse<>(504);
                baseResponse.setMessage("用户信息过期，请重新登录~");
                String test = gson.toJson(baseResponse);
                result = adapter.read(gson.newJsonReader(ResponseBody.create(test, MEDIA_TYPE).charStream()));
            } else {
                result = adapter.read(jsonReader);
            }
            return result;
        } finally {
            value.close();
        }
    }
}
