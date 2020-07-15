package com.snc.sample.webview.network;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

/**
 * Dynamic URLs for Requests
 * @author Aaron Jo.
 * @since 2016
 */
public interface DynamicUrlService {

    /*
        -Example

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://your.api.url/")   // Base URL required.
                    .build();

            DynamicService service = retrofit.create(DynamicService.class);
            Call<ResponseBody> call = service.post("https://t1.kakaocdn.net/kakao_biz_common/public/docs/카카오싱크%20개발가이드_Ver1.1_20190411.pdf", new HashMap<String, String>());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (null == response) {
                            Logger.d(TAG, "onResponse() = response is null.");
                            return;
                        }

                        Logger.d(TAG, "onResponse() = response code is " + response.code());
                        ResponseBody body;
                        if (response.code() < 400) {
                            body = response.body();
                        } else {
                            body = response.errorBody();
                        }
                        Logger.d(TAG, "onResponse() = " + response + " \n\n " + body.string());

                    } catch (Exception e) {
                        Logger.d(TAG, "onResponse() = " + response + " \n\n Exception : \n" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Logger.e(TAG, "onFailure() = " + t);
                }
            });
     */

    /**
     * Dynamic URL GET 요청
     *
     * -Example
     * Retrofit retrofit = Retrofit.Builder()
     *     .baseUrl("https://your.api.url/");
     *     .build();
     *
     * DynamicService service = retrofit.create(DynamicService.class);
     * service.get("https://s3.amazon.com/profile-picture/path");
     *
     * @param url - URL 경로
     * @return - 응답
     */
    @GET
    Call<ResponseBody> get(@Url String url);

    /**
     * Dynamic URL POST 요청
     *
     * -Example
     *  Retrofit retrofit = Retrofit.Builder()
     *     .baseUrl("https://your.api.url/")   // Base URL required.
     *     .build();
     *
     *  DynamicService service = retrofit.create(DynamicService.class);
     *  service.get("https://s3.amazon.com/profile-picture/path");
     *
     * @param url - URL 경로
     * @param params - request parameter
     * @return - 응답
     */
    @FormUrlEncoded
    @POST
    Call<ResponseBody> post(@Url String url, @FieldMap Map<String, String> params);

    /**
     * Dynamic URL POST Multipart 요청
     * @param url - URL 경로
     * @param params - request parameter
     * @return - 응답
     */
    @Multipart
    @POST
    Call<ResponseBody> multipart(@Url String url, @PartMap Map<String, RequestBody> params);

}
