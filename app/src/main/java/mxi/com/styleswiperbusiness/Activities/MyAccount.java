package mxi.com.styleswiperbusiness.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mxi.com.styleswiperbusiness.Models.UserInfo;
import mxi.com.styleswiperbusiness.Network.CommanClass;
import mxi.com.styleswiperbusiness.Network.Constants;
import mxi.com.styleswiperbusiness.R;

public class MyAccount extends AppCompatActivity implements View.OnClickListener {

    CommanClass cc;
    ImageView ivProfile, ivBack;
    TextView tvStore, tvOwnerName, tvEmail, tvPhone, tvBusiness, tvMen, tvWomen;
    String selectedGender = "Style";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initUI();
    }

    private void initUI() {
        cc = new CommanClass(MyAccount.this);
        ivBack = (ImageView) findViewById(R.id.iv_back_my_account);
        ivProfile = (CircleImageView) findViewById(R.id.iv_image_profile);
        tvStore = (TextView) findViewById(R.id.tv_store_profile);
        tvOwnerName = (TextView) findViewById(R.id.tv_owner_name_profile);
        tvEmail = (TextView) findViewById(R.id.tv_email_profile);
        tvPhone = (TextView) findViewById(R.id.tv_phone_profile);
        tvBusiness = (TextView) findViewById(R.id.tv_business_profile);
        tvMen = (TextView) findViewById(R.id.tv_male_my_account);
        tvWomen = (TextView) findViewById(R.id.tv_female_my_account);

        fillProfileData();
    }

    private void fillProfileData() {
        UserInfo userInfo = UserInfo.first(UserInfo.class);
        tvStore.setText(userInfo.getStore());
        tvOwnerName.setText(userInfo.getName());
        tvEmail.setText(userInfo.getEmail());
        tvPhone.setText(userInfo.getPhone());
        tvBusiness.setText(userInfo.getBusiness());
        if(cc.loadPrefBoolean(Constants.SelectedGender.isManSelected)){
            tvMen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_selected, 0, 0, 0);
            tvWomen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_normal, 0, 0, 0);
        } else {
            tvWomen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_selected, 0, 0, 0);
            tvMen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_normal, 0, 0, 0);
        }
        setViewClickListners();
        tempp();
    }

    private void setViewClickListners(){
        ivBack.setOnClickListener(this);
        tvMen.setOnClickListener(this);
        tvWomen.setOnClickListener(this);
    }

    private void tempp(){
        ArrayList<UserInfo> info = (ArrayList<UserInfo>)UserInfo.listAll(UserInfo.class);
        for (int i = 0; i < info.size(); i++) {
            Log.e("MyAccount", "Username = " + info.get(i).getEmail());
            Log.e("MyAccount", "Password = " + info.get(i).getPassword());
            Log.e("MyAccount", "-------------------------------");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_male_my_account:
                Constants.SelectedGender.isGenderChanged = isGenderChange("male");
                Log.e("SelectedGender", "isGenderChanged = " + isGenderChange("male"));
                cc.savePrefBoolean(Constants.SelectedGender.isManSelected, true);
                tvMen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_selected, 0, 0, 0);
                tvWomen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_normal, 0, 0, 0);
                break;
            case R.id.tv_female_my_account:
                Log.e("SelectedGender", "isGenderChanged = " + isGenderChange("female"));
                Constants.SelectedGender.isGenderChanged = isGenderChange("female");
                cc.savePrefBoolean(Constants.SelectedGender.isManSelected, false);
                tvWomen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_selected, 0, 0, 0);
                tvMen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_normal, 0, 0, 0);
                break;
            case R.id.iv_back_my_account:
                onBackPressed();
                break;
        }
    }
    private boolean isGenderChange(String gender){
        if(cc.loadPrefBoolean(Constants.SelectedGender.isManSelected) && gender.equalsIgnoreCase("female")){
            return true;
        } else if(!cc.loadPrefBoolean(Constants.SelectedGender.isManSelected) && gender.equalsIgnoreCase("male")){
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(Constants.SelectedGender.isGenderChanged){
            Intent mIntent = new Intent(MyAccount.this,
                    StyleSwiper.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(mIntent);
            finish();
        }
    }
}
