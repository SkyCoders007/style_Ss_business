package mxi.com.styleswiperbusiness.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mxi.com.styleswiperbusiness.Models.SavedStylesInfo;
import mxi.com.styleswiperbusiness.Models.StylesRepositoryInfo;
import mxi.com.styleswiperbusiness.Models.TagColors;
import mxi.com.styleswiperbusiness.Models.TagLenghts;
import mxi.com.styleswiperbusiness.Models.TagStyles;
import mxi.com.styleswiperbusiness.Models.UserInfo;
import mxi.com.styleswiperbusiness.Network.AndroidMultiPartEntity;
import mxi.com.styleswiperbusiness.Network.AppController;
import mxi.com.styleswiperbusiness.Network.CommanClass;
import mxi.com.styleswiperbusiness.Network.Constants;
import mxi.com.styleswiperbusiness.Network.WebserviceURL;
import mxi.com.styleswiperbusiness.R;
import mxi.com.styleswiperbusiness.widget.CustomTFSpan;

public class AddStyles extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog pDialog;
    ImageView ivSelectedImage,iv_back_list_styles;
    Bitmap selectedImage;
    TextView tvGender, tvColor, tvStyle, tvLength, tvSave, tvImage;
    EditText etPrice;
    CommanClass cc;
    ProgressDialog progressDialog;
    LinearLayout llController;
    List<TagColors> colors;
    List<TagStyles> styles;
    List<TagLenghts> lengths;

    ArrayList<String> strColor;
    ArrayList<String> strStyle;
    ArrayList<String> strLength;

    String[] gender = {"Male", "Female", "Unisex"};
    String genderTitle = "Select Gender";
    String[] colorArray;
    String colorTitle = "Select Color";
    String[] stylerArray;
    String styleTitle = "Select Style";
    String[] lengthArray;
    String lengthTitle = "Select Length";
    StringBuffer sbColor;
    StringBuffer sbStyle;
    StringBuffer sbLength;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath = "";
    String groupFilePath = "";
    long totalSize = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_styles);
        initUI();
    }

    private void initUI() {
        cc = new CommanClass(AddStyles.this);
        llController = (LinearLayout) findViewById(R.id.ll_controller_add_style);
        callTagWS();

        etPrice = (EditText) findViewById(R.id.et_cost_add_style);
        ivSelectedImage = (ImageView) findViewById(R.id.iv_selected_image_add_style);
        iv_back_list_styles = (ImageView) findViewById(R.id.iv_back_list_styles);
        tvGender = (TextView) findViewById(R.id.tv_gender_add_style);
        tvColor = (TextView) findViewById(R.id.tv_color_add_style);
        tvStyle = (TextView) findViewById(R.id.tv_style_add_style);
        tvLength = (TextView) findViewById(R.id.tv_length_add_style);
        tvSave = (TextView) findViewById(R.id.tv_save_add_style);
        tvImage = (TextView) findViewById(R.id.tv_image_button_add_style);
        setViewClickListners();
    }

    private void setViewClickListners() {
        tvGender.setOnClickListener(this);
        tvColor.setOnClickListener(this);
        tvStyle.setOnClickListener(this);
        tvLength.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        tvImage.setOnClickListener(this);
        iv_back_list_styles.setOnClickListener(this);
//        fillTags();
    }

    private void fillTags() {
        colors = TagColors.listAll(TagColors.class);
        styles = TagStyles.listAll(TagStyles.class);
        lengths = TagLenghts.listAll(TagLenghts.class);

        strColor = new ArrayList<>();
        for (int i = 0; i < colors.size(); i++) {
            strColor.add(colors.get(i).getColorTag());
        }
        colorArray = new String[strColor.size()];
        colorArray = strColor.toArray(colorArray);

        strStyle = new ArrayList<>();
        for (int i = 0; i < styles.size(); i++) {
            strStyle.add(styles.get(i).getStyleTag());
        }
        stylerArray = new String[strStyle.size()];
        stylerArray = strStyle.toArray(stylerArray);

        strLength = new ArrayList<>();
        for (int i = 0; i < lengths.size(); i++) {
            strLength.add(lengths.get(i).getLenghtsTag());
        }
        lengthArray = new String[strLength.size()];
        lengthArray = strLength.toArray(lengthArray);
    }

    private void callTagWS() {
        progressDialog = new ProgressDialog(AddStyles.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, WebserviceURL.url_getStyleTags,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("url_home", response);
                        parseTags(response);
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

    private void parseTags(String response) {

        try {
            JSONObject obj = new JSONObject(response);
            String status = obj.get("status").toString();
            if (status.equalsIgnoreCase("200")) {
                JSONObject dataObj = obj.getJSONObject("data");
                JSONArray colorArray = dataObj.getJSONArray("colors");
                for (int i = 0; i < colorArray.length(); i++) {
                    JSONObject colorObj = colorArray.getJSONObject(i);
                    TagColors clr = new TagColors();
                    clr.setId(Long.parseLong(colorObj.getString("id")));
                    clr.setColorID(colorObj.getString("id"));
                    clr.setColorTag(colorObj.getString("tag"));
                    clr.save();
                }
                JSONArray styleArray = dataObj.getJSONArray("styles");
                for (int i = 0; i < styleArray.length(); i++) {
                    JSONObject styleObj = styleArray.getJSONObject(i);
                    TagStyles style = new TagStyles();
                    style.setId(Long.parseLong(styleObj.getString("id")));
                    style.setStyleID(styleObj.getString("id"));
                    style.setStyleTag(styleObj.getString("tag"));
                    style.save();
                }

                JSONArray lengthArray = dataObj.getJSONArray("lenghts");
                for (int i = 0; i < lengthArray.length(); i++) {
                    JSONObject lengthObj = lengthArray.getJSONObject(i);
                    TagLenghts length = new TagLenghts();
                    length.setId(Long.parseLong(lengthObj.getString("id")));
                    length.setLenghtsID(lengthObj.getString("id"));
                    length.setLenghtsTag(lengthObj.getString("tag"));
                    length.save();
                }
                fillTags();
            }else if (status.equalsIgnoreCase("498")){
                invalidTokenDialogs();
            }
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void invalidTokenDialogs() {

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/AGENCYB.TTF");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddStyles.this);
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
                        Intent mIntent = new Intent(AddStyles.this,Login.class);
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

    private void showPoupupMenu(final String popupDialogType, final String title, final String[] items) {
//        Log.e("Dhaval ", "items = " + items.toString());

        //List of items to be show in  alert Dialog are stored in array of strings/char sequences
        AlertDialog.Builder builder = new AlertDialog.Builder(AddStyles.this);
        //set the title for alert dialog
        builder.setTitle(title);
        for (int i = 0; i < items.length; i++) {
            Log.e("show tag popup", "items array = " + items[i]);
        }
        //set items to alert dialog. i.e. our array , which will be shown as list view in alert dialog
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // setting the button text to the selected itenm from the list
                if (popupDialogType.equalsIgnoreCase(Constants.AddStyle.popupGender)) {
                    tvGender.setText(items[item]);
                } else if (popupDialogType.equalsIgnoreCase(Constants.AddStyle.popupColors)) {
                    tvColor.setText(items[item]);
                } else if (popupDialogType.equalsIgnoreCase(Constants.AddStyle.popupStyle)) {
                    tvStyle.setText(items[item]);
                } else if (popupDialogType.equalsIgnoreCase(Constants.AddStyle.popupLength)) {
                    tvLength.setText(items[item]);
                }
            }
        });
        //Creating CANCEL button in alert dialog, to dismiss the dialog box when nothing is selected
        builder.setCancelable(false)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //When clicked on CANCEL button the dalog will be dismissed
                        dialog.dismiss();
                    }
                });
        // Creating alert dialog
        AlertDialog alert = builder.create();
        //Showing alert dialog
        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_gender_add_style:
                showPoupupMenu(Constants.AddStyle.popupGender, genderTitle, gender);
                break;
            case R.id.tv_color_add_style:
                showPoupupMenu(Constants.AddStyle.popupColors, colorTitle, colorArray);
                break;
            case R.id.tv_style_add_style:
                showPoupupMenu(Constants.AddStyle.popupStyle, styleTitle, stylerArray);
                break;
            case R.id.tv_length_add_style:
                showPoupupMenu(Constants.AddStyle.popupLength, lengthTitle, lengthArray);
                break;
            case R.id.tv_save_add_style:
                String strCost = "";
                strCost = etPrice.getText().toString();
                if (strCost.equalsIgnoreCase("")) {
                    etPrice.setError("Please enter Style price");
                } else {
                    if(selectedImagePath.equalsIgnoreCase("")){
                        cc.showSnackbar(llController, "Please select Image");
                    } else {
                        new UploadFileToServer(tvGender.getText().toString(), tvColor.getText().toString(), tvStyle.getText().toString(), tvLength.getText().toString(), strCost).execute();
                    }
                }
                break;
            case R.id.tv_image_button_add_style:
                selectfile();
                break;
            case R.id.iv_back_list_styles:
                onBackPressed();
                break;
        }
    }

    private void selectfile() {
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, SELECT_PICTURE);


        } catch (Exception e) {
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == AddStyles.this.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                try {
                    selectedImagePath = getPath(selectedImageUri);
                    Log.e("Selected File", selectedImagePath);
                    Uri uri = Uri.fromFile(new File(selectedImagePath));
                    ivSelectedImage.setImageURI(uri);

                    ExifInterface ei = null;
                    Bitmap mybitmap = null;
                    Bitmap retVal = null;
                    try {
                        ei = new ExifInterface(selectedImagePath);
                        mybitmap = BitmapFactory.decodeFile(selectedImagePath);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Matrix matrix = new Matrix();
                    int orientation = ei.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    Log.e("Oriention", orientation + "");

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_NORMAL:
                            matrix.postRotate(0);
                            retVal = Bitmap.createBitmap(mybitmap, 0, 0,
                                    mybitmap.getWidth(), mybitmap.getHeight(),
                                    matrix, true);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_90:

                            matrix.postRotate(90);
                            retVal = Bitmap.createBitmap(mybitmap, 0, 0,
                                    mybitmap.getWidth(), mybitmap.getHeight(),
                                    matrix, true);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:

                            matrix.postRotate(180);
                            retVal = Bitmap.createBitmap(mybitmap, 0, 0,
                                    mybitmap.getWidth(), mybitmap.getHeight(),
                                    matrix, true);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:

                            matrix.postRotate(270);
                            retVal = Bitmap.createBitmap(mybitmap, 0, 0,
                                    mybitmap.getWidth(), mybitmap.getHeight(),
                                    matrix, true);
                            break;

                    }

                    File file = new File(selectedImagePath);
                    long fileSizeInBytes = file.length();

                    long fileSizeInKB = fileSizeInBytes / 1024;

                    long fileSizeInMB = fileSizeInKB / 1024;

                    if (fileSizeInMB > 10) {
                        selectedImagePath = "";
                        new AlertDialog.Builder(AddStyles.this)
                                .setMessage("You can't upload more than 10 MB file")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })

                                .show();
                    }
                } catch (URISyntaxException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    public String getPath(Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = AddStyles.this.getContentResolver().query(uri,
                        projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        HttpClient httpclient;
        HttpPost httppost;
        String gender;
        String color;
        String style;
        String length;
        String price;

        public UploadFileToServer(String gender, String color, String style, String length, String price) {
            this.gender = gender;
            this.color = color;
            this.style = style;
            this.length = length;
            this.price = price;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddStyles.this);
            pDialog.show();
            pDialog.setCancelable(false);

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pDialog.setMessage(String.valueOf("Loading..." + progress[0])
                    + " %");

        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(WebserviceURL.url_createStyles);
            String accessToken = cc.loadPrefString("stsw-token");
            httppost.addHeader("stsw-token", accessToken);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                entity.addPart("gender", new StringBody(gender));
                entity.addPart("color", new StringBody(color));
                entity.addPart("style", new StringBody(style));
                entity.addPart("length", new StringBody(length));
                entity.addPart("price", new StringBody(price));

                if (selectedImagePath != null || !selectedImagePath.equals("")) {
                    File sourceFile = new File(selectedImagePath);
                    entity.addPart("styleimg", new FileBody(sourceFile));

                }

                totalSize = entity.getContentLength();

                Log.e("dhaval", "entity = " + entity);
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    cc.showSnackbar(llController, "New style created successfully");
                    return responseString;
                } else if (statusCode == 498){
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                    invalidTokenDialogs();
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Register: result", "Response from server: " + result);
            try {
                pDialog.dismiss();
                JSONObject jObject = new JSONObject(result);
                if (jObject.getString("status").equals("200")) {

                    cc.showToast(jObject.getString("message"));
                    finish();

                } else {
                    cc.showToast(jObject.getString("message"));
                }

            } catch (JSONException e) {
                Log.e("Error : Exception", e.getMessage());
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(AddStyles.this,
                StyleSwiper.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mIntent);
        finish();
    }
}
