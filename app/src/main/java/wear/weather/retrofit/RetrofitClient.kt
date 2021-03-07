package wear.weather.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    companion object {
        private val retrofitClient: RetrofitClient = RetrofitClient()
        fun getInstance(): RetrofitClient = retrofitClient
    }

    fun buildRetrofit(url: String): RetrofitService {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit: Retrofit? = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        return retrofit!!.create(RetrofitService::class.java)
    }
}