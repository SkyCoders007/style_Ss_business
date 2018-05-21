package mxi.com.styleswiperbusiness.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mxi.com.styleswiperbusiness.Models.UserInfo;
import mxi.com.styleswiperbusiness.Network.AppController;
import mxi.com.styleswiperbusiness.Network.CommanClass;
import mxi.com.styleswiperbusiness.Network.Constants;
import mxi.com.styleswiperbusiness.Network.WebserviceURL;
import mxi.com.styleswiperbusiness.R;
import mxi.com.styleswiperbusiness.gcm.ApplicationConstants;

public class Login extends AppCompatActivity implements View.OnClickListener{

    LinearLayout llControler;
    TextView btnSignIn, btnSignUp, btnForgotPass;
    EditText etUsername, etPassword, etEmail;
    Dialog dialog;
    ProgressDialog progressDialog;
    CommanClass cc;
    String deviceID, strEmail = "";
    ArrayList<UserInfo> userInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cc = new CommanClass(Login.this);
        initUI();
    }

    private void initUI(){
        llControler = (LinearLayout) findViewById(R.id.ll_controller);
        btnSignIn = (TextView) findViewById(R.id.tv_sign_in);
        btnSignUp = (TextView) findViewById(R.id.tv_sign_up);
        btnForgotPass = (TextView) findViewById(R.id.tv_forgot_password);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_pass);

        deviceID = Secure.getString(Login.this.getContentResolver(), Secure.ANDROID_ID);
        cc.savePrefString(Constants.Login.deviceID, deviceID);
        setViewClickListners();
    }

    private void setViewClickListners() {
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sign_in:
                if(cc.isConnectingToInternet()){
                    checkUserValidation();
                } else {
                    cc.showSnackbar(llControler, "Please Check Internet connection");
                }
                break;
            case R.id.tv_sign_up:
                startActivity(new Intent(Login.this, SignUpFirst.class));
                finish();
                break;
            case R.id.tv_forgot_password:
                showForgotPasswordDialogs();
                break;
        }
    }

    private void checkUserValidation(){
        if(etUsername.length() > 0){
            checkPassValidation();
        } else {
            cc.showSnackbar(llControler, "Invalid Username");
            return;
        }
    }

    private void checkPassValidation() {
        if(etPassword.length() > 0){
            CallLoginWS();
        } else {
            cc.showSnackbar(llControler, "Invalid Password");
            return;
        }
    }

    private void CallLoginWS() {
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Please wait...");
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
                error.printStackTrace();
                cc.showSnackbar(llControler, "Invalid Username or Password");
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", etUsername.getText().toString());
                params.put("password", etPassword.getText().toString());
                params.put("device_id", deviceID);
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

    private void parseUserInfo(String response){
        userInfos = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(response);
            String status = obj.get("status").toString();
            JSONObject data = obj.getJSONObject("data");
            if(status.equalsIgnoreCase("200")){
                UserInfo info = new UserInfo();
                info.setName(data.getString("name"));
                info.setEmail(data.getString("email"));
                info.setPhone(data.getString("phone"));
                info.setPassword(etPassword.getText().toString());
                info.setStore(data.getString("store"));
                info.setBusiness(data.getString("business"));
                info.setAddress(data.getString("address"));
                info.setLat(data.getString("lat"));
                info.setLng(data.getString("lng"));
                info.save();
                userInfos.add(info);
                if(progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                cc.savePrefBoolean("isFirstTime", true);
                cc.savePrefBoolean("isLoggedIn", true);
                startActivity(new Intent(Login.this, SelectGender.class));
                finish();
            } else {
                cc.showSnackbar(llControler, "Invalid Username or Password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showForgotPasswordDialogs() {
        dialog = new Dialog(Login.this);
        dialog.setContentView(R.layout.dialog_forgot_password);

        etEmail = (EditText) dialog.findViewById(R.id.et_email_forgot_pass);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel_forgot_pass);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tv_submit_forgot_pass);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = etEmail.getText().toString();

                if(strEmail.equalsIgnoreCase("")){
                    etEmail.setError("Please insert Email ID");
                } else {
                    if(isValidEmail(strEmail)){
                        callForgotPassWS(strEmail);
                    } else {
                        etEmail.setError("Invalid Email");
                    }
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private boolean isValidEmail(String email) {

        String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void callForgotPassWS(final String email) {
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, WebserviceURL.url_forgotpassword,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("url_addStyles", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("status");
                            String msg = obj.getString("msg");
                            if (status.equalsIgnoreCase("200")) {
                                dialog.dismiss();
                                cc.showSnackbar(llControler, msg);
                            } else {
                                etEmail.setError(msg);
                            }
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
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
                cc.showSnackbar(llControler, "Something went wrong");
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("email", email);

                Log.i("email", params.toString());

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");
    }
}
