package com.example.qiitaapplication

import com.example.qiitaapplication.dataclass.QiitaResponse
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

object QiitaApi {

    val items: ItemsIF by lazy { retrofitApi().create(ItemsIF::class.java)}
    val tags: TagsIF by lazy { retrofitApi().create(TagsIF::class.java)}

    fun retrofitApi(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl("https://qiita.com/api/v2/")
            .addConverterFactory(create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    fun retrofitApi(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://qiita.com/api/v2/")
            .addConverterFactory(create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    interface ItemsIF {
        // @GET("items?page={page}&per_page=20")
        @GET("items")
        fun getItem(@Query("page") page: Int, @Query("per_page") perPage: Int = 20) : Observable<List<QiitaResponse>>

        //@GET("items?page={page}&per_page=20&query=body:{encodeQuery}")
        @GET("items")
        fun searchBody(@Query("page") page: Int, @Query("query") encodeQuery: String
                       , @Query("per_page") perPage: Int = 20) : Observable<List<QiitaResponse>>

    }

    interface TagsIF {
        @GET("tags/{encodeQuery}/items")
        fun searchTag(@Path("encodeQuery", encoded=true) encodeQuery: String, @Query("page") page: Int
                      , @Query("per_page") perPage: Int = 20): Observable<List<QiitaResponse>>
    }
}