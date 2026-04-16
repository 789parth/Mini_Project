package com.example.miniproject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    // Send OTP request
    @FormUrlEncoded
    @POST("Services/{serviceSid}/Verifications")
    Call<Map<String, Object>> sendOtp(
            @Header("Authorization") String authHeader,
            @Path("serviceSid") String serviceSid,
            @Field("To") String mobileNumber,
            @Field("Channel") String channel
    );

    // Verify OTP request
    @FormUrlEncoded
    @POST("Services/{serviceSid}/VerificationCheck")
    Call<Map<String, Object>> verifyOtp(
            @Header("Authorization") String authHeader,
            @Path("serviceSid") String serviceSid,
            @Field("To") String mobileNumber,
            @Field("Code") String otpCode
    );
}