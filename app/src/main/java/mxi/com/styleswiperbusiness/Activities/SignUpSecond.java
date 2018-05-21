package mxi.com.styleswiperbusiness.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mxi.com.styleswiperbusiness.Models.UserInfo;
import mxi.com.styleswiperbusiness.Network.AppController;
import mxi.com.styleswiperbusiness.Network.CommanClass;
import mxi.com.styleswiperbusiness.Network.Constants;
import mxi.com.styleswiperbusiness.Network.WebserviceURL;
import mxi.com.styleswiperbusiness.R;
import mxi.com.styleswiperbusiness.gcm.ApplicationConstants;

public class SignUpSecond extends AppCompatActivity implements View.OnClickListener {

    int PLACE_PICKER_REQUEST = 1;
    private GoogleApiClient mClient;
    CommanClass cc;
    TextView tvFashionStyle, tvHairStyle, btnSignUp, btnSignIn, tvAddress;
    EditText etStore;
    ArrayList<UserInfo> userInfos;
    String selectedLat = "0";
    String selectedLong = "0";

    LinearLayout llControler;
    ProgressDialog progressDialog;

    String selectedBusiness = "Fashion";
    String name, email, phone, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_second);
        initUI();
    }

    private void initUI() {
        cc = new CommanClass(SignUpSecond.this);
        mClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        llControler = (LinearLayout) findViewById(R.id.ll_controller_signup_second);
        tvFashionStyle = (TextView) findViewById(R.id.tv_fashion_style_signup_second);
        tvHairStyle = (TextView) findViewById(R.id.tv_hair_style_signup_second);
        btnSignUp = (TextView) findViewById(R.id.tv_sign_up_second);
        btnSignIn = (TextView) findViewById(R.id.tv_have_account_sign_up_second);

        etStore = (EditText) findViewById(R.id.et_store_sign_up);
        tvAddress = (TextView) findViewById(R.id.tv_address_sign_up);
        setViewClickListners();
    }

    private void setViewClickListners() {
        tvFashionStyle.setOnClickListener(this);
        tvHairStyle.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
        setIntentData();
    }

    private void setIntentData() {
        Bundle data = getIntent().getExtras();
        name = data.getString(Constants.SignUp.key_name);
        email = data.getString(Constants.SignUp.key_email);
        phone = data.getString(Constants.SignUp.key_phone);
        pass = data.getString(Constants.SignUp.key_pass);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_fashion_style_signup_second:
                selectedBusiness = "Fashion";
                tvFashionStyle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_selected, 0, 0, 0);
                tvHairStyle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_normal, 0, 0, 0);
                break;
            case R.id.tv_hair_style_signup_second:
                selectedBusiness = "Style";
                tvHairStyle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_selected, 0, 0, 0);
                tvFashionStyle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_normal, 0, 0, 0);
                break;
            case R.id.tv_sign_up_second:
                try {
                    checkStore();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_address_sign_up:
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(SignUpSecond.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_have_account_sign_up_second:
                Intent mIntent = new Intent(SignUpSecond.this, Login.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mIntent);
                finish();
                break;
        }
    }

    private void checkStore() {
        if (etStore.getText().length() > 0) {
            checkAddress();
        } else {
            etStore.setError("Store Name Can not be blank");
            return;
        }
    }

    private void checkAddress() {
        if (tvAddress.getText().length() > 0 && (!selectedLat.equalsIgnoreCase("0")) && !selectedLong.equalsIgnoreCase("0")) {
            callSignUpWS();
        } else {
            return;
        }
    }

    private void callSignUpWS() {
        progressDialog = new ProgressDialog(SignUpSecond.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, WebserviceURL.url_registration,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("url_signup", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("status");
                            String msg = obj.getString("msg");
                            if (status.equalsIgnoreCase("200")) {
                                cc.showSnackbar(llControler, msg);
                                CallLoginWS();
//                                Intent mIntent = new Intent(SignUpSecond.this, Login.class);
//                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                startActivity(mIntent);
//                                finish();
                            } else {
                                progressDialog.dismiss();
                                cc.showSnackbar(llControler, msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Error:login", error.getMessage());
                progressDialog.dismiss();
                cc.showSnackbar(llControler, "Somthing went wrong");
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("name", name);
                params.put("email", email);
                params.put("phone", phone);
                params.put("password", pass);
                params.put("store", etStore.getText().toString());
                params.put("address", tvAddress.getText().toString());
                params.put("lat", selectedLat);
                params.put("lng", selectedLong);
                params.put("business", selectedBusiness);
                params.put("device_id", cc.loadPrefString(Constants.Login.deviceID));
                params.put("gcm_id", ApplicationConstants.GOOGLE_PROJ_ID);

                Log.i("email", params.toString());

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());

                selectedLat = String.valueOf(place.getLatLng().latitude);
                selectedLong = String.valueOf(place.getLatLng().longitude);

                String address = String.format("%s", place.getAddress());
                stBuilder.append("Address: ");
                stBuilder.append(address);
                tvAddress.setText(stBuilder.toString());
            }
        }
    }


    private void CallLoginWS() {
//        progressDialog = new ProgressDialog(SignUpSecond.this);
//        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, WebserviceURL.url_login,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("url_home", response);
                        parseUserInfo(response);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Error:login", error.getMessage());
                progressDialog.dismiss();
                cc.showSnackbar(llControler, "Invalid Username or Password");
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", email);
                params.put("password",pass);
                params.put("device_id", cc.loadPrefString(Constants.Login.deviceID));
                params.put("gcm_id", ApplicationConstants.GOOGLE_PROJ_ID);

                Log.i("email", params.toString());

                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                String stsw_token = response.headers.get("stsw-token ");
                Log.e("stsw-token", stsw_token);
                cc.savePrefString("stsw-token", stsw_token);
                return super.parseNetworkResponse(response);
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");
    }

    private void parseUserInfo(String response) {
        userInfos = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(response);
            String status = obj.get("status").toString();
            JSONObject data = obj.getJSONObject("data");
            if (status.equalsIgnoreCase("200")) {
                UserInfo info = new UserInfo();
                info.setName(data.getString("name"));
                info.setEmail(data.getString("email"));
                info.setPhone(data.getString("phone"));
                info.setPassword(pass);
                info.setStore(data.getString("store"));
                info.setBusiness(data.getString("business"));
                info.setAddress(data.getString("address"));
                info.setLat(data.getString("lat"));
                info.setLng(data.getString("lng"));
                info.save();
                userInfos.add(info);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                cc.savePrefBoolean("isFirstTime", true);
                cc.savePrefBoolean("isLoggedIn", true);
                Intent mIntent = new Intent(SignUpSecond.this, SelectGender.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mIntent);
                finish();
            } else {
                cc.showSnackbar(llControler, "Invalid Username or Password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
