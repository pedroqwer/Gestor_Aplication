package Aplicacion.api.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import Aplicacion.api.Interfaz.IControllers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://3.91.9.12/api/";
    private static final String BASE_URL_user = "http://3.91.9.12/auth/";

    private static Retrofit retrofitApi = null;
    private static Retrofit retrofitAuth = null;

    public static Retrofit getClient() {
        if (retrofitApi == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofitApi = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofitApi;
    }

    public static Retrofit getLogin() {
        if (retrofitAuth == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofitAuth = new Retrofit.Builder()
                    .baseUrl(BASE_URL_user)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofitAuth;
    }

    public static IControllers getAPI() {
        return getClient().create(IControllers.class); // usa /api/
    }

    public static IControllers getAPILog() {
        return getLogin().create(IControllers.class); // usa /auth/
    }
}