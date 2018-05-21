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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mxi.com.styleswiperbusiness.Models.ListStyleInfo;
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
import mxi.com.styleswiperbusiness.adapters.CustomGridAdapter;
import mxi.com.styleswiperbusiness.widget.CustomTFSpan;

public class ListStyles extends AppCompatActivity {

    GridView grvListStyles;
    RelativeLayout rlController;
    TextView tvEmpty;
    ImageView ivBack;
    CustomGridAdapter adapter;
    CommanClass cc;
    ProgressDialog progressDialog;
//    ArrayList<SavedStylesInfo> savedStylesInfos;
    ArrayList<ListStyleInfo> savedStylesInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_styles);
        initUI();
    }

    private void initUI(){
        cc = new CommanClass(ListStyles.this);
        grvListStyles = (GridView) findViewById(R.id.grv_list_styles);
        tvEmpty = (TextView) findViewById(R.id.tv_empty_view_List_styles);
        ivBack = (ImageView) findViewById(R.id.iv_back_list_styles);
        rlController = (RelativeLayout) findViewById(R.id.rl_controller_list_styles);
        callGetMyStylesWS();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void callGetMyStylesWS() {
        progressDialog = new ProgressDialog(ListStyles.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, WebserviceURL.url_getMyStyles,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("url_home", response);

                        parseMyStyles(response);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Error:login", error.getMessage());
                progressDialog.dismiss();
                cc.showSnackbar(rlController, "Something went wrong");
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

    private void parseMyStyles(String response) {
        savedStylesInfos = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(response);
            String status = obj.get("status").toString();
            if (status.equalsIgnoreCase("200")) {

                JSONArray data = obj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject dataObj = data.getJSONObject(i);
                    Log.e("ListStyle Screen", "dataObj = " + dataObj.toString());
//                    SavedStylesInfo datainfo = new SavedStylesInfo();
                    ListStyleInfo datainfo = new ListStyleInfo();
                    datainfo.setStyleId(Long.parseLong(dataObj.getString("style_id")));
                    datainfo.setStyle(dataObj.getString("style"));
                    datainfo.setLength(dataObj.getString("length"));
                    datainfo.setColor(dataObj.getString("color"));
                    datainfo.setVisited(dataObj.getString("visited"));
                    datainfo.setLiked(dataObj.getString("liked"));
                    String imagePath = dataObj.getString("image").replace("ws/../", "");
                    Log.e("styleSwiper adapter", "Image path = " + imagePath);
                    datainfo.setImage(imagePath);
                    datainfo.setPrice(dataObj.getString("price"));

                    savedStylesInfos.add(datainfo);
                }
            }else if (status.equalsIgnoreCase("498")){
                invalidTokenDialogs();
            }

//            savedStylesInfos = new ArrayList<>();
//            savedStylesInfos = (ArrayList<SavedStylesInfo>) SavedStylesInfo.listAll(SavedStylesInfo.class);

            Log.e("@@@Liked Style List",savedStylesInfos.size()+"");

            if(savedStylesInfos.size() > 0){
                tvEmpty.setVisibility(View.GONE);
                adapter = new CustomGridAdapter(ListStyles.this, savedStylesInfos);
                grvListStyles.setAdapter(adapter);
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
                grvListStyles.setEmptyView(tvEmpty);
            }

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(ListStyles.this,
                StyleSwiper.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mIntent);
        finish();
    }
    private void invalidTokenDialogs() {

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/AGENCYB.TTF");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ListStyles.this);
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
                        Intent mIntent = new Intent(ListStyles.this,Login.class);
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
