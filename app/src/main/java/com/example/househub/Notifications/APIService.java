package com.example.househub.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAM92szB0:APA91bEjlz2E-xOUwcap6k4FDiotM4eP-sMp3rBIAjMo2rFM_bKcWxA373Uhahfbl-ICUbMuEVhnvnHx1GJxoXwIqqGs3cEUmZ2tTUaC2SydcaC-5Jy9NIZC1PvzHu9lfD8-Mla3jhC3"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
