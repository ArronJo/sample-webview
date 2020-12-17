package com.snc.sample.webview.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Dynamic URLs for Requests
 *
 * @author Aaron Jo.
 * @since 2020
 */
public interface DynamicUrlService {

    @SuppressWarnings({"unused", "RedundantSuppression"})
    @GET
    Call<ResponseBody> get(@Url String url);

}
