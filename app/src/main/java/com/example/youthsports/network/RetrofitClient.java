package com.example.youthsports.network;

import android.content.Context;
import android.util.Log;

import com.example.youthsports.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    private static Retrofit retrofit = null;

    private static OkHttpClient buildClient(Context context) {
        JwtTokenService jwtTokenService = new JwtTokenService(context);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, "HTTP: " + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();

                    if (!original.url().encodedPath().contains("/auth")) {
                        String token = jwtTokenService.getToken();
                        requestBuilder.header("Authorization", "Bearer " + token);
                        Log.d(TAG, "Adding Authorization token for request to " + original.url());
                    }

                    Request request = requestBuilder.method(original.method(), original.body()).build();
                    Response response = chain.proceed(request);

                    String authHeader = response.header("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        jwtTokenService.storeToken(authHeader.substring(7));
                        Log.d(TAG, "New JWT token stored from response");
                    }

                    return response;
                })
                .addInterceptor(loggingInterceptor)
                .build();
    }

    public static synchronized ApiService getApiService(Context context) {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            OkHttpClient client = buildClient(context);
            String baseUrl = context.getString(R.string.backend_url);

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
