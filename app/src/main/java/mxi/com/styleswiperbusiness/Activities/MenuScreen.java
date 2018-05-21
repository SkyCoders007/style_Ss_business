package mxi.com.styleswiperbusiness.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class MenuScreen extends Activity implements View.OnClickListener {

    ProgressDialog progressDialog;
    CommanClass cc;
    ImageView ivClose;
    TextView tvChangePass, tvStyleSwiper, tvAddStyle, tvListStyles, tvLogout;
    TextView tv_dashboard, tv_billing, tv_my_location,tvMyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);
        initUI();
    }

    private void initUI(){
        cc = new CommanClass(MenuScreen.this);
        ivClose = (ImageView) findViewById(R.id.iv_close_menu);
        tvMyAccount = (TextView) findViewById(R.id.tv_my_account_menu);
        tvChangePass = (TextView) findViewById(R.id.tv_change_pass_menu);
        tvStyleSwiper = (TextView) findViewById(R.id.tv_style_swiper_menu);
        tvAddStyle = (TextView) findViewById(R.id.tv_add_style_menu);
        tvListStyles = (TextView) findViewById(R.id.tv_list_style_menu);
        tvLogout = (TextView) findViewById(R.id.tv_logout_menu);
        tv_dashboard = (TextView) findViewById(R.id.tv_dashboard);
        tv_billing = (TextView) findViewById(R.id.tv_billing);
        tv_my_location = (TextView) findViewById(R.id.tv_my_location);
        setViewClickListners();
    }

    private void setViewClickListners(){
        ivClose.setOnClickListener(this);
        tvMyAccount.setOnClickListener(this);
        tvChangePass.setOnClickListener(this);
        tvStyleSwiper.setOnClickListener(this);
        tvAddStyle.setOnClickListener(this);
        tvListStyles.setOnClickListener(this);
        tvLogout.setOnClickListener(this);

        tv_dashboard.setOnClickListener(this);
        tv_billing.setOnClickListener(this);
        tv_my_location.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_my_account_menu:
//                startActivity(new Intent(MenuScreen.this, MyAccount.class));
//                finish();
                callWebview("myAccount");
                break;
            case R.id.iv_close_menu:
                finish();
                break;
            case R.id.tv_change_pass_menu:
                startActivity(new Intent(MenuScreen.this, ChangePassword.class));
                finish();
                break;
            case R.id.tv_style_swiper_menu:
                startActivity(new Intent(MenuScreen.this, StyleSwiper.class));
                finish();
                break;
            case R.id.tv_add_style_menu:
                startActivity(new Intent(MenuScreen.this, AddStyles.class));
                finish();
                break;
            case R.id.tv_list_style_menu:
                startActivity(new Intent(MenuScreen.this, ListStyles.class));
                finish();
                break;
            case R.id.tv_dashboard:
                callWebview("dashboard");
                break;

/*            case R.id.tv_style_swiper_history:
                callWebview("history");
                break;*/

            case R.id.tv_billing:
                callWebview("billing");
                break;

            case R.id.tv_my_location:
                callWebview("location");
                break;

            case R.id.tv_logout_menu:
                callWsLogout();
                break;
        }
    }

    private void callWebview(String activitycall) {

        Intent intent=new Intent(MenuScreen.this,WebViewActivity.class);
        intent.putExtra("webservice",activitycall);
        startActivity(intent);
        finish();

    }

    private void callWsLogout() {
        progressDialog = new ProgressDialog(MenuScreen.this);
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
                            if(status.equalsIgnoreCase("200") || status.equalsIgnoreCase("498")){
                                cc.savePrefBoolean("isLoggedIn", false);
                                cc.showToast(msg);
                                UserInfo.deleteAll(UserInfo.class);
                                StylesRepositoryInfo.deleteAll(StylesRepositoryInfo.class);
                                SavedStylesInfo.deleteAll(SavedStylesInfo.class);
                                TagColors.deleteAll(TagColors.class);
                                TagLenghts.deleteAll(TagLenghts.class);
                                TagStyles.deleteAll(TagStyles.class);
                                Intent mIntent = new Intent(MenuScreen.this,
                                        Login.class);
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(mIntent);
                                finish();
                            }else {
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
}
