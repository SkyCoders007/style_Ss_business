package mxi.com.styleswiperbusiness.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mxi.com.styleswiperbusiness.Models.SavedStylesInfo;
import mxi.com.styleswiperbusiness.Models.StylesRepositoryInfo;
import mxi.com.styleswiperbusiness.Models.TagColors;
import mxi.com.styleswiperbusiness.Models.TagLenghts;
import mxi.com.styleswiperbusiness.Models.TagStyles;
import mxi.com.styleswiperbusiness.Models.UserInfo;
import mxi.com.styleswiperbusiness.Network.AppController;
import mxi.com.styleswiperbusiness.Network.CommanClass;
import mxi.com.styleswiperbusiness.Network.WebserviceURL;
import mxi.com.styleswiperbusiness.R;
import mxi.com.styleswiperbusiness.widget.CustomTFSpan;

public class ChangePassword extends AppCompatActivity {

    CommanClass cc;
    EditText etCurrentPass;
    EditText etNewPass;
    EditText etConfirmPass;
    ProgressDialog progressDialog;
    ImageView ic_cancel;
    String strCurrentPass = "", strNewPass = "", strConfirmPass = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        cc=new CommanClass(this);

        ic_cancel=(ImageView)findViewById(R.id.ic_cancel);
        etCurrentPass = (EditText) findViewById(R.id.et_current_pass_change_pass);
        etNewPass = (EditText) findViewById(R.id.et_new_pass_change_pass);
        etConfirmPass = (EditText) findViewById(R.id.et_confirm_pass_change_pass);
//        TextView tvCancel = (TextView) findViewById(R.id.tv_cancel);
        TextView tvChangePass = (TextView) findViewById(R.id.tv_change_pass);
        tvChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strCurrentPass = etCurrentPass.getText().toString();
                strNewPass = etNewPass.getText().toString();
                strConfirmPass = etConfirmPass.getText().toString();

                if (strCurrentPass.equalsIgnoreCase("")) {
                    etCurrentPass.setError("Please insert Current Password");
                } else {
                    if (strNewPass.equalsIgnoreCase("")) {
                        etNewPass.setError("Please insert New Password");
                    } else {
                        if (strConfirmPass.equalsIgnoreCase("")) {
                            etConfirmPass.setError("Please insert Confirm Password");
                        } else {
                            if (strNewPass.equals(strConfirmPass)) {
                                callChangePassWS(strCurrentPass, strNewPass);
                            } else {
                                etConfirmPass.setError("Password not match");
                            }
                        }
                    }
                }
            }
        });

/*        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        ic_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    private void callWsLogout() {
        progressDialog = new ProgressDialog(ChangePassword.this);
        progressDialog.setMessage("Please wait..."); ;
        progressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, WebserviceURL.url_logout,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("url_home", response);
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        try {
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("status");
                            String msg = obj.getString("msg");
                            if(status.equalsIgnoreCase("200")){
                                cc.savePrefBoolean("isLoggedIn", false);
                                cc.showToast(msg);
                                UserInfo.deleteAll(UserInfo.class);
                                StylesRepositoryInfo.deleteAll(StylesRepositoryInfo.class);
                                SavedStylesInfo.deleteAll(SavedStylesInfo.class);
                                TagColors.deleteAll(TagColors.class);
                                TagLenghts.deleteAll(TagLenghts.class);
                                TagStyles.deleteAll(TagStyles.class);
                                Intent mIntent = new Intent(ChangePassword.this,
                                        Login.class);
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(mIntent);
                                finish();
                            } else {
                                cc.showToast(msg);
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
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                String accessToken = cc.loadPrefString("stsw-token");
                headers.put("stsw-token", accessToken);
                Log.e("ManageEvents...", "headers = " + headers.toString());
                return headers;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");
    }


    private void callChangePassWS(final String currentPass, final String newPass) {
        progressDialog = new ProgressDialog(ChangePassword.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, WebserviceURL.url_changepassword,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("url_addStyles", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("status");
                            String msg = obj.getString("msg");
                            if (status.equalsIgnoreCase("200")) {
//                                cc.showSnackbar(llController, msg);
                                cc.showToast(msg);
                            } else if (status.equalsIgnoreCase("498")){
//                                cc.showSnackbar(llController, msg);
                                invalidTokenDialogs();
                                cc.showToast(msg);
                            }
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            callWsLogout();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Error:login", error.getMessage());
                progressDialog.dismiss();
                cc.showToast("Something went wrong");
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("password", currentPass);
                params.put("new_password", newPass);

                Log.i("params", params.toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                String accessToken = cc.loadPrefString("stsw-token");
                headers.put("stsw-token", accessToken);
                Log.e("ManageEvents...", "headers = " + headers.toString());
                return headers;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(ChangePassword.this,
                StyleSwiper.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mIntent);
        finish();
    }


    private void invalidTokenDialogs() {

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/AGENCYB.TTF");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangePassword.this);
        CustomTFSpan tfSpan = new CustomTFSpan(face);

        SpannableString spannableString = new SpannableString("Sorry! Your session is expired, please logout and then login again to continue.");
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        alertDialog.setTitle(spannableString);
//        alertDialog.setTitle(data);
        alertDialog.setMessage(spannableString);
        alertDialog.setCancelable(false);

        SpannableString spannableYes = new SpannableString("Logout");
        spannableYes.setSpan(tfSpan, 0, spannableYes.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertDialog.setPositiveButton(spannableYes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cc.savePrefBoolean("isLoggedIn", false);
                        UserInfo.deleteAll(UserInfo.class);
                        StylesRepositoryInfo.deleteAll(StylesRepositoryInfo.class);
                        SavedStylesInfo.deleteAll(SavedStylesInfo.class);
                        TagColors.deleteAll(TagColors.class);
                        TagLenghts.deleteAll(TagLenghts.class);
                        TagStyles.deleteAll(TagStyles.class);
                        Intent mIntent = new Intent(ChangePassword.this,Login.class);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent);
                        finish();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
