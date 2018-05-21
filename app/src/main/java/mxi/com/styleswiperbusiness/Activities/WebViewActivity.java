package mxi.com.styleswiperbusiness.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import mxi.com.styleswiperbusiness.Models.SavedStylesInfo;
import mxi.com.styleswiperbusiness.Models.StylesRepositoryInfo;
import mxi.com.styleswiperbusiness.Models.TagColors;
import mxi.com.styleswiperbusiness.Models.TagLenghts;
import mxi.com.styleswiperbusiness.Models.TagStyles;
import mxi.com.styleswiperbusiness.Models.UserInfo;
import mxi.com.styleswiperbusiness.Network.CommanClass;
import mxi.com.styleswiperbusiness.Network.WebserviceURL;
import mxi.com.styleswiperbusiness.R;
import mxi.com.styleswiperbusiness.widget.CustomTFSpan;

public class WebViewActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    String activity_text = "";
    String call_url = "";
    WebView wv_main;
    CommanClass cc;
    ImageView iv_menu_style_swiper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_web_view);
//        final Activity activity = this;
        cc=new CommanClass(this);
        wv_main=(WebView)findViewById(R.id.wv_main);
        iv_menu_style_swiper=(ImageView)findViewById(R.id.iv_menu_style_swiper);
        activity_text= getIntent().getStringExtra("webservice");
        progressDialog = new ProgressDialog(WebViewActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        if (activity_text.equals("myAccount")) {
            call_url = WebserviceURL.url_wv_edit_profile;
        } else if (activity_text.equals("dashboard")) {
            call_url = WebserviceURL.url_wv_dashbord;
        } else if (activity_text.equals("history")) {
            call_url = WebserviceURL.url_wv_style_history;
        } else if (activity_text.equals("billing")) {
            call_url = WebserviceURL.url_wv_billing;
        } else if (activity_text.equals("location")) {
            call_url = WebserviceURL.url_wv_my_location;
        }
        call_url=call_url+cc.loadPrefString("stsw-token");

        wv_main.getSettings().setJavaScriptEnabled(true);


        wv_main.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.e("@@@onReceivedHttpError",errorResponse.getStatusCode()+"");
                    if(errorResponse.getStatusCode() == 404){
                        invalidTokenDialogs();
                    }
                }
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e("Page"," Started");
                progressDialog.show();

                HttpGet req = new HttpGet(url);
                DefaultHttpClient client = new DefaultHttpClient();

                try {
                    HttpResponse res = client.execute(req);
                    Log.e("@@@+++++++++",res.getStatusLine().getStatusCode()+"");

                    if (HttpStatus.SC_OK == res.getStatusLine().getStatusCode()) {

                    }
                } catch (Exception e) {
                    String msg = e.getMessage();
                    Log.e(this.getClass().getSimpleName(), (null != msg) ? msg : "");
                } finally {
                    req.abort();
                    client.getConnectionManager().shutdown();
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("Page"," Finished");
                progressDialog.dismiss();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                cc.showToast("Oh no! " + description);
            }
        });
        wv_main.loadUrl(call_url);

        iv_menu_style_swiper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WebViewActivity.this, MenuScreen.class));
            }
        });
    }

    private void invalidTokenDialogs() {

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/AGENCYB.TTF");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WebViewActivity.this);
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
                        Intent mIntent = new Intent(WebViewActivity.this,Login.class);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(WebViewActivity.this,
                StyleSwiper.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mIntent);
        finish();
    }
}
