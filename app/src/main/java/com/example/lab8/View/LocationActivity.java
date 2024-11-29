package com.example.lab8.View;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab8.Adapter.Adapter_Item_District_Select_GHN;
import com.example.lab8.Adapter.Adapter_Item_Province_Select_GHN;
import com.example.lab8.Adapter.Adapter_Item_Ward_Select_GHN;
import com.example.lab8.Models.District;
import com.example.lab8.Models.DistrictRequest;
import com.example.lab8.Models.GHNItem;
import com.example.lab8.Models.GHNOrderRespone;
import com.example.lab8.Models.Province;
import com.example.lab8.Models.ResponseGHN;
import com.example.lab8.Models.Ward;
import com.example.lab8.Models.sendOrderRequest;
import com.example.lab8.R;
import com.example.lab8.Serives.GHNRequest;
import com.example.lab8.databinding.ActivityLocationBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationActivity extends AppCompatActivity {
    private ActivityLocationBinding binding;
    private GHNRequest request;
    private String productId, productTypeId, productName, description, WardCode;
    private double rate, price;
    private int image, DistrictID, ProvinceID;
    Button btnOder;
    private Adapter_Item_Province_Select_GHN adapter_item_province_select_ghn;
    private Adapter_Item_District_Select_GHN adapter_item_district_select_ghn;
    private Adapter_Item_Ward_Select_GHN adapter_item_ward_select_ghn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        request = new GHNRequest();
        // bat gl
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productId = bundle.getString("productId");
            productTypeId = bundle.getString("productTypeId");
            productName = bundle.getString("productName");
            description = bundle.getString("description");
            rate = bundle.getDouble("rate");
            price = bundle.getDouble("price");
            image = bundle.getInt("image");
        }

        btnOder = findViewById(R.id.btn_order);
        TextInputLayout locationInputLayout = findViewById(R.id.location_input_layout);

        btnOder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Province selectedProvince = (Province) binding.spProvince.getSelectedItem();
                District selectedDistrict = (District) binding.spDistrict.getSelectedItem();
                Ward selectedWard = (Ward) binding.spWard.getSelectedItem();
                String location = locationInputLayout.getEditText().getText().toString();
                if (selectedProvince != null && selectedDistrict != null && selectedWard != null) {
                    ArrayList<GHNItem> items = new ArrayList<>();
                    items.add(new GHNItem(productName, productId, 1, (int) price, 1000));
                    String to_address = location+", " + selectedWard.getWardName() + ", " +
                            selectedDistrict.getDistrictName() + ", " + selectedProvince.getProvinceName()+ ", Vietnam" ;
                    sendOrderRequest order = new sendOrderRequest(
                            "Người nhận",
                            "0332190158",
                            to_address,
                            selectedWard.getWardCode(),
                            selectedDistrict.getDistrictID(),
                            items
                    );

                    order.setFrom_name("TinTest124");
                    order.setFrom_phone("0987654321");
                    order.setFrom_address("72 Thành Thái, Phường 14, Quận 10, Hồ Chí Minh, Vietnam");
                    order.setFrom_ward_name("Phường 14");
                    order.setFrom_district_name("Quận 10");
                    order.setFrom_province_name("HCM");

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(order);
                    Log.d("GHNOrderRequest", json);
                    Call<ResponseGHN<GHNOrderRespone>> call = request.callAPI().GHNOrder(order);
                    call.enqueue(new Callback<ResponseGHN<GHNOrderRespone>>() {
                        @Override
                        public void onResponse(Call<ResponseGHN<GHNOrderRespone>> call, Response<ResponseGHN<GHNOrderRespone>> response) {
                            if (response.isSuccessful()) {
                                Log.d("GHNOrder", "Tạo đơn hàng thành công: " + response.body());
                            } else {
                                try {
                                    Log.e("GHNOrder", "Lỗi tạo đơn hàng: " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseGHN<GHNOrderRespone>> call, Throwable t) {
                            Log.e("GHNOrder", "Lỗi kết nối: " + t.getMessage());
                        }
                    });
                } else {
                    Log.d("SelectedLocation", "Province, district or ward is not selected.");
                }
            }
        });

        request.callAPI().getListProvince().enqueue(responseProvince);
        binding.spProvince.setOnItemSelectedListener(onItemSelectedListener);
        binding.spDistrict.setOnItemSelectedListener(onItemSelectedListener);
        binding.spWard.setOnItemSelectedListener(onItemSelectedListener);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getId() == R.id.sp_province) {
                ProvinceID = ((Province) parent.getAdapter().getItem(position)).getProvinceID();
                DistrictRequest districtRequest = new DistrictRequest(ProvinceID);
                request.callAPI().getListDistrict(districtRequest).enqueue(responseDistrict);
            } else if (parent.getId() == R.id.sp_district) {
                DistrictID = ((District) parent.getAdapter().getItem(position)).getDistrictID();
                request.callAPI().getListWard(DistrictID).enqueue(responseWard);
            } else if (parent.getId() == R.id.sp_ward) {
                WardCode = ((Ward) parent.getAdapter().getItem(position)).getWardCode();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    Callback<ResponseGHN<ArrayList<Province>>> responseProvince = new Callback<ResponseGHN<ArrayList<Province>>>() {
        @Override
        public void onResponse(Call<ResponseGHN<ArrayList<Province>>> call, Response<ResponseGHN<ArrayList<Province>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getCode() == 200) {
                    ArrayList<Province> ds = new ArrayList<>(response.body().getData());
                    SetDataSpinProvince(ds);
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseGHN<ArrayList<Province>>> call, Throwable t) {
            Toast.makeText(LocationActivity.this, "Lấy dữ liệu bị lỗi", Toast.LENGTH_SHORT).show();
        }
    };

    Callback<ResponseGHN<ArrayList<District>>> responseDistrict = new Callback<ResponseGHN<ArrayList<District>>>() {
        @Override
        public void onResponse(Call<ResponseGHN<ArrayList<District>>> call, Response<ResponseGHN<ArrayList<District>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getCode() == 200) {
                    ArrayList<District> ds = new ArrayList<>(response.body().getData());
                    SetDataSpinDistrict(ds);
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseGHN<ArrayList<District>>> call, Throwable t) {

        }
    };

    Callback<ResponseGHN<ArrayList<Ward>>> responseWard = new Callback<ResponseGHN<ArrayList<Ward>>>() {
        @Override
        public void onResponse(Call<ResponseGHN<ArrayList<Ward>>> call, Response<ResponseGHN<ArrayList<Ward>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getCode() == 200) {

                    if (response.body().getData() == null)
                        return;

                    ArrayList<Ward> ds = new ArrayList<>(response.body().getData());

                    ds.addAll(response.body().getData());
                    SetDataSpinWard(ds);
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseGHN<ArrayList<Ward>>> call, Throwable t) {
            Toast.makeText(LocationActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
        }
    };

    private void SetDataSpinProvince(ArrayList<Province> ds) {
        adapter_item_province_select_ghn = new Adapter_Item_Province_Select_GHN(this, ds);
        binding.spProvince.setAdapter(adapter_item_province_select_ghn);
    }

    private void SetDataSpinDistrict(ArrayList<District> ds) {
        adapter_item_district_select_ghn = new Adapter_Item_District_Select_GHN(this, ds);
        binding.spDistrict.setAdapter(adapter_item_district_select_ghn);
    }

    private void SetDataSpinWard(ArrayList<Ward> ds) {
        adapter_item_ward_select_ghn = new Adapter_Item_Ward_Select_GHN(this, ds);
        binding.spWard.setAdapter(adapter_item_ward_select_ghn);
    }
}