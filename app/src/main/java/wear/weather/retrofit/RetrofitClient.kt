package wear.weather.retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    companion object {
        private val retrofitClient: RetrofitClient = RetrofitClient()
        fun getInstance(): RetrofitClient = retrofitClient
    }

    fun buildRetrofit(url: String): RetrofitService {
        val gson = GsonBuilder().setLenient().create()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit: Retrofit? = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


        return retrofit!!.create(RetrofitService::class.java)
    }
}