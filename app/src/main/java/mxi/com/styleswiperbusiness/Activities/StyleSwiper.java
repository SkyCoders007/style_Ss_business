package mxi.com.styleswiperbusiness.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
import mxi.com.styleswiperbusiness.Network.Constants;
import mxi.com.styleswiperbusiness.Network.WebserviceURL;
import mxi.com.styleswiperbusiness.R;
import mxi.com.styleswiperbusiness.tindercard.FlingCardListener;
import mxi.com.styleswiperbusiness.tindercard.SwipeFlingAdapterView;
import mxi.com.styleswiperbusiness.widget.CustomTFSpan;

public class StyleSwiper extends AppCompatActivity implements FlingCardListener.ActionDownInterface, View.OnClickListener {

    String Tag = "StyleSwiperBusiness";
    int i = 0;
    int PAGE_COUNT = 1;
    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;
    private ArrayList<StylesRepositoryInfo> stylesRepositoryInfos;
    private SwipeFlingAdapterView flingContainer;
    private ImageView ivNope, ivLike, ivMenu;
    EditText etCurrentPass;
    EditText etNewPass;
    EditText etConfirmPass;

    LinearLayout llController;
    CommanClass cc;
    ProgressDialog progressDialog;
    ImageView ivEmpty;
    String strGender;
    boolean isActivityCreated = false;
    String strCurrentPass = "", strNewPass = "", strConfirmPass = "";

    public static void removeBackground() {


        viewHolder.background.setVisibility(View.GONE);
        myAppAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_style_swiper);

        cc = new CommanClass(StyleSwiper.this);
        llController = (LinearLayout) findViewById(R.id.ll_controller_style_swiper);
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        ivNope = (ImageView) findViewById(R.id.iv_nope);
        ivLike = (ImageView) findViewById(R.id.iv_like);
        ivMenu = (ImageView) findViewById(R.id.iv_menu_style_swiper);
        ivEmpty = (ImageView) findViewById(R.id.empty_view);

        if (!cc.loadPrefBoolean("isFirstTimeInStSw")) {
            openWelcomeDialogs();
        }

        if (cc.loadPrefBoolean("isFirstTime")) {
            callStylesRepositoryWS("1");
        } else {
            if (Constants.SelectedGender.isGenderChanged) {
                StylesRepositoryInfo.deleteAll(StylesRepositoryInfo.class);
                callStylesRepositoryWS("1");
                Constants.SelectedGender.isGenderChanged = false;
            } else {
                callStylesRepositoryWS(cc.loadPrefString("currentPage"));
            }
        }

        setViewClickListners();

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {

            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                deleteStylesToDataBase(stylesRepositoryInfos.get(0).getId());
                stylesRepositoryInfos.remove(0);
                myAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                openDialogs(stylesRepositoryInfos.get(0).getStyle(), stylesRepositoryInfos.get(0).getId());
                stylesRepositoryInfos.remove(0);
                myAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                Log.e(Tag, "itemsInAdapter = " + itemsInAdapter);
                if (itemsInAdapter == 0) {
                    int currentPage = Integer.parseInt(cc.loadPrefString("currentPage"));
                    int totalPage = Integer.parseInt(cc.loadPrefString("total_number"));
                    if (currentPage == totalPage) {
                        Log.e(Tag, "In if currentPage == totalPage ");
                        callStylesRepositoryWS("1");
                    } else {
                        currentPage++;
                        Log.e(Tag, "In else currentPage == " + currentPage);
                        callStylesRepositoryWS("" + currentPage);
                    }
                }
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);

                myAppAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityCreated = true;
    }

    private void setViewClickListners() {
        ivNope.setOnClickListener(this);
        ivLike.setOnClickListener(this);
        ivMenu.setOnClickListener(this);
    }

    @Override
    public void onActionDownPerform() {
        Log.e("action", "bingo");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_nope:
                Log.d(Tag, "onClick: iv_nope = " + flingContainer.getCount());
                if (stylesRepositoryInfos.size() > 0) {
                    flingContainer.getTopCardListener().selectLeft();
                }

                break;

            case R.id.iv_like:
                Log.d(Tag, "onClick: iv_like = " + flingContainer.getCount());
                if (stylesRepositoryInfos.size() > 0) {
                    flingContainer.getTopCardListener().selectRight();
                }

                break;
            case R.id.iv_menu_style_swiper:
                startActivity(new Intent(StyleSwiper.this, MenuScreen.class));
                break;
        }
    }

    public static class ViewHolder {
        public static FrameLayout background;
        public TextView DataText;
        public ImageView cardImage;
        public ProgressBar mProgressBar;
    }

    public class MyAppAdapter extends BaseAdapter {

        //        public ArrayList<StylesRepositoryInfo> parkingList;
        public Context context;

        private MyAppAdapter(Context context) {
            this.context = context;
        }

        //        private MyAppAdapter(ArrayList<StylesRepositoryInfo> apps, Context context) {
//            this.parkingList = null;
//            this.parkingList = apps;
//            this.context = context;
//        }
        @Override
        public int getCount() {
            return stylesRepositoryInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;

            if (rowView == null) {

                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.item, parent, false);
                // configure view holder
                viewHolder = new ViewHolder();
                viewHolder.DataText = (TextView) rowView.findViewById(R.id.bookText);
                viewHolder.background = (FrameLayout) rowView.findViewById(R.id.background);
                viewHolder.cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
                viewHolder.mProgressBar = (ProgressBar) rowView.findViewById(R.id.pgb_image_loading_style_swiper);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            Picasso.with(StyleSwiper.this).load(R.drawable.no_media).memoryPolicy(MemoryPolicy.NO_CACHE);
            if (stylesRepositoryInfos.size() != 0) {
                viewHolder.DataText.setText(stylesRepositoryInfos.get(position).getStyleId() + " store");

                Picasso.with(StyleSwiper.this).load(stylesRepositoryInfos.get(position).getImage()).memoryPolicy(MemoryPolicy.NO_CACHE).into(viewHolder.cardImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                    }
                });
            } else {
                Picasso.with(StyleSwiper.this).load(R.drawable.no_media).into(viewHolder.cardImage);
                viewHolder.mProgressBar.setVisibility(View.GONE);
            }

            return rowView;
        }
    }

    private void openDialogs(String imageName, final long id) {

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/AGENCYB.TTF");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StyleSwiper.this);
        CustomTFSpan tfSpan = new CustomTFSpan(face);

//        SpannableString spannableString = new SpannableString(imageName);
        SpannableString spannableString = new SpannableString("Let nearby customers know this style is available here!");
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertDialog.setTitle(spannableString);

        SpannableString spannableMsg = new SpannableString("Enter Cost");
        spannableMsg.setSpan(tfSpan, 0, spannableMsg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertDialog.setMessage(spannableMsg);

        final EditText input = new EditText(StyleSwiper.this);
        input.setTypeface(face);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        lp.setMargins(20, 5, 20, 5);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        SpannableString spannableYes = new SpannableString("Yes");
        spannableYes.setSpan(tfSpan, 0, spannableYes.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertDialog.setPositiveButton(spannableYes,
                new DialogInterface.OnClickListener()

                {
                    public void onClick(DialogInterface dialog, int which) {
                        callAddStyleCostWS(stylesRepositoryInfos.get(0).getStyleId(), input.getText().toString(), id);
                        dialog.dismiss();
                    }
                });

        SpannableString spannableNo = new SpannableString("No");
        spannableNo.setSpan(tfSpan, 0, spannableNo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertDialog.setNegativeButton(spannableNo,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        myAppAdapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void deleteStylesToDataBase(long styleID) {
        try {
            StylesRepositoryInfo mStyleInfo = StylesRepositoryInfo.findById(StylesRepositoryInfo.class, styleID);
            if (mStyleInfo != null) {
                boolean isDelete = mStyleInfo.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openWelcomeDialogs() {

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/AGENCYB.TTF");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StyleSwiper.this);
        CustomTFSpan tfSpan = new CustomTFSpan(face);

        String data = "Welcome to Style Swiper! As a business let customer know what styles you offer by swiping on a style and setting its price! Also add your own styles!";
        SpannableString spannableString = new SpannableString("Welcome to Style Swiper! As a business let customer know what styles you offer by swiping on a style and setting its price! Also add your own styles!");
        spannableString.setSpan(tfSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        alertDialog.setTitle(spannableString);
//        alertDialog.setTitle(data);
        alertDialog.setMessage(spannableString);


        SpannableString spannableYes = new SpannableString("Ok");
        spannableYes.setSpan(tfSpan, 0, spannableYes.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertDialog.setPositiveButton(spannableYes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        cc.savePrefBoolean("isFirstTimeInStSw", true);
    }

    private void invalidTokenDialogs() {

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/AGENCYB.TTF");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StyleSwiper.this);
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
                        Intent mIntent = new Intent(StyleSwiper.this,Login.class);
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

    private void callStylesRepositoryWS(final String pageNumber) {
        progressDialog = new ProgressDialog(StyleSwiper.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (cc.loadPrefBoolean(Constants.SelectedGender.isManSelected)) {
            strGender = "Male";
        } else {
            strGender = "Female";
        }

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, WebserviceURL.url_getStylesRepository,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("url_home", response);

                        parseStylesRepositoryInfos(response);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Error:login", error.getMessage());
                progressDialog.dismiss();
                setEmptyView();
                cc.showSnackbar(llController, "Something went wrong");
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                Log.e("Gender", strGender);
                params.put("gender", strGender);
                params.put("page", pageNumber);

                Log.i("email", params.toString());

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

    private void parseStylesRepositoryInfos(String response) {
        try {
            stylesRepositoryInfos = null;
            stylesRepositoryInfos = new ArrayList<>();
            JSONObject obj = new JSONObject(response);
            String status = obj.get("status").toString();
            if (status.equalsIgnoreCase("200")) {
                StylesRepositoryInfo.deleteAll(StylesRepositoryInfo.class);
                String currentPage = obj.getString("page");
                String totalPage = obj.getString("totalpage");
                cc.savePrefString("currentPage", currentPage);
                cc.savePrefString("total_number", totalPage);

                JSONArray data = obj.getJSONArray("data");

                if (cc.loadPrefBoolean("isFirstTime")) {
                    cc.savePrefBoolean("isFirstTime", false);
                }

                for (int i = 0; i < data.length(); i++) {
                    JSONObject dataObj = data.getJSONObject(i);
                    StylesRepositoryInfo mStyleInfo = StylesRepositoryInfo.findById(StylesRepositoryInfo.class, Long.parseLong(dataObj.getString("style_id")));
                    if (mStyleInfo == null) {
                        StylesRepositoryInfo info = new StylesRepositoryInfo();
                        info.setId(Long.parseLong(dataObj.getString("style_id")));
                        info.setStyleId(dataObj.getString("style_id"));
                        info.setStyle(dataObj.getString("style"));
                        info.setLength(dataObj.getString("length"));
                        info.setColor(dataObj.getString("color"));
                        String imagePath = dataObj.getString("image").replace("ws/../", "");
                        Log.e("styleSwiper adapter", "Image path = " + imagePath);
                        info.setImage(imagePath);
                        info.save();
                    }
                }
            } else if (status.equalsIgnoreCase("498")) {
                setEmptyView();
                invalidTokenDialogs();
            } else {
                setEmptyView();
                cc.showSnackbar(llController, "No Style Available!");
            }

            stylesRepositoryInfos.clear();
            stylesRepositoryInfos = (ArrayList<StylesRepositoryInfo>) StylesRepositoryInfo.listAll(StylesRepositoryInfo.class);
            Log.e("Style swiper", "total item = " + stylesRepositoryInfos.size());
            if (stylesRepositoryInfos.size() > 0) {
                myAppAdapter = new MyAppAdapter(StyleSwiper.this);
                flingContainer.setAdapter(myAppAdapter);
            }
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                myAppAdapter.notifyDataSetChanged();
            }
//---------------------------------------
        } catch (Exception e) {
            setEmptyView();
            e.printStackTrace();
        }
    }

    public void setEmptyView() {
        ivEmpty.setVisibility(View.VISIBLE);
        flingContainer.setEmptyView(ivEmpty);
    }

    private void callAddStyleCostWS(final String styleID, final String cost, final long id) {
        progressDialog = new ProgressDialog(StyleSwiper.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, WebserviceURL.url_addStyles,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("url_addStyles", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("200")) {
                                deleteStylesToDataBase(id);
                                cc.showSnackbar(llController, "Style added successfully");
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
                cc.showSnackbar(llController, "Something went wrong");
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("style_id", styleID);
                params.put("price", cost);

                Log.i("email", params.toString());

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

   /* private void showChangePasswordDialogs() {
        final Dialog dialog = new Dialog(StyleSwiper.this);
        dialog.setContentView(R.layout.activity_change_password);

        etCurrentPass = (EditText) dialog.findViewById(R.id.et_current_pass_change_pass);
        etNewPass = (EditText) dialog.findViewById(R.id.et_new_pass_change_pass);
        etConfirmPass = (EditText) dialog.findViewById(R.id.et_confirm_pass_change_pass);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tvChangePass = (TextView) dialog.findViewById(R.id.tv_change_pass);
        tvChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.isChangePass = false;
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
                                dialog.cancel();
                                callChangePassWS(strCurrentPass, strNewPass);
                            } else {
                                etConfirmPass.setError("Password not match");
                            }
                        }
                    }
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.isChangePass = false;
                dialog.cancel();
            }
        });
        dialog.show();
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        /*if (Constants.isChangePass) {
            showChangePasswordDialogs();
        } else if (Constants.SelectedGender.isGenderChanged) {
            Log.e(Tag, "isGenderChanged = " + Constants.SelectedGender.isGenderChanged);
            Constants.SelectedGender.isGenderChanged = false;
            if (isActivityCreated) {
                Log.e(Tag, "isActivityCreated = " + isActivityCreated);
                callStylesRepositoryWS("1");
            }
        }*/


        if (Constants.SelectedGender.isGenderChanged) {
            Log.e(Tag, "isGenderChanged = " + Constants.SelectedGender.isGenderChanged);
            Constants.SelectedGender.isGenderChanged = false;
            if (isActivityCreated) {
                Log.e(Tag, "isActivityCreated = " + isActivityCreated);
                callStylesRepositoryWS("1");
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }
}