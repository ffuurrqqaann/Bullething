package unicommsapp.application.com.unicomppsapp.Utils;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by furqan on 2/21/2016.
 */
public class WebservicePostUtil {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public String run(String url, String json, boolean other) throws IOException {

        RequestBody requestBody = null;

        if(other) {
            requestBody = RequestBody.create(JSON, json);
        } else {
            requestBody = new FormBody.Builder()
                    .add("data", json)
                    .build();
        }

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}
