package com.example.lab8.Serives;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GHNRequest {
    public final static String SHOPID = "195457";
    public final static String TokenGHN = "2ca77665-acd1-11ef-accc-c6f6f22065b5";
    private GHNServices ghnRequestInterface;

    public GHNRequest(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("ShopId", SHOPID)
                        .addHeader("Token", TokenGHN)
                        .build();
                return chain.proceed(request);
            }
        });

        ghnRequestInterface = new Retrofit.Builder()
                .baseUrl(GHNServices.GHN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build().create(GHNServices.class);
    }

    public GHNServices callAPI(){
        return ghnRequestInterface;
    }

//    curl --location --request POST 'https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/create' \
//            --header 'Content-Type: application/json' \
//            --header 'ShopId: 195457' \
//            --header 'Token: 2ca77665-acd1-11ef-accc-c6f6f22065b5' \
//            --data-raw '{
//            "payment_type_id": 2,
//            "note": "Tintest 123",
//            "required_note": "KHONGCHOXEMHANG",
//            "from_name": "TinTest124",
//            "from_phone": "0987654321",
//            "from_address": "72 Thành Thái, Phường 14, Quận 10, Hồ Chí Minh, Vietnam",
//            "from_ward_name": "Phường 14",
//            "from_district_name": "Quận 10",
//            "from_province_name": "HCM",
//            "return_phone": "0332190444",
//            "return_address": "39 NTT",
//            "return_district_id": null,
//            "return_ward_code": "",
//            "client_order_code": "",
//            "to_name": "TinTest124",
//            "to_phone": "0987654321",
//            "to_address": "72 Thành Thái, Phường 14, Quận 10, Hồ Chí Minh, Vietnam",
//            "to_ward_code": "20308",
//            "to_district_id": 1444,
//            "cod_amount": 200000,
//            "content": "Theo New York Times",
//            "weight": 200,
//            "length": 1,
//            "width": 19,
//            "height": 10,
//            "pick_station_id": 1444,
//            "deliver_station_id": null,
//            "insurance_value": 10000,
//            "service_id": 0,
//            "service_type_id":2,
//            "coupon":null,
//            "pick_shift":[2],
//            "items": [
//    {
//        "name":"Áo Polo",
//            "code":"Polo123",
//            "quantity": 1,
//            "price": 200000,
//            "length": 12,
//            "width": 12,
//            "height": 12,
//            "weight": 1200,
//            "category":
//        {
//            "level1":"Áo"
//        }
//    }
//
//                                     ]
//}'

}
