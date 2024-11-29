package com.example.lab8.Serives;

import com.example.lab8.Models.Create_Product;
import com.example.lab8.Models.District;
import com.example.lab8.Models.DistrictRequest;
import com.example.lab8.Models.GHNOrderRespone;
import com.example.lab8.Models.Province;
import com.example.lab8.Models.ResponseGHN;
import com.example.lab8.Models.Ward;
import com.example.lab8.Models.sendOrderRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GHNServices {
    public static String GHN_URL = "https://dev-online-gateway.ghn.vn/";

    @GET("/shiip/public-api/master-data/province")
    Call<ResponseGHN<ArrayList<Province>>> getListProvince();

    @POST("/shiip/public-api/master-data/district")
    Call<ResponseGHN<ArrayList<District>>> getListDistrict(@Body DistrictRequest districtRequest);

    @GET("/shiip/public-api/master-data/ward")
    Call<ResponseGHN<ArrayList<Ward>>> getListWard(@Query("district_id") int district_id);

    @POST("/shiip/public-api/master-data/province")
    Call<ResponseGHN<Create_Product>> createShippingOrder(@Body Create_Product createProduct);
    @POST("shiip/public-api/v2/shipping-order/create")
    Call<ResponseGHN<GHNOrderRespone>> GHNOrder(@Body sendOrderRequest ghnOrderRequest);

}
