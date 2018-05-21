package mxi.com.styleswiperbusiness.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import mxi.com.styleswiperbusiness.Network.CommanClass;
import mxi.com.styleswiperbusiness.Network.Constants;
import mxi.com.styleswiperbusiness.R;

public class SelectGender extends AppCompatActivity implements View.OnClickListener{

    ImageView ivMale, ivFemale;
    CommanClass cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gender);
        initUI();
    }

    private void initUI(){
        cc = new CommanClass(SelectGender.this);
        ivMale = (ImageView) findViewById(R.id.iv_male);
        ivFemale = (ImageView) findViewById(R.id.iv_female);
        setClickListners(ivMale);
        setClickListners(ivFemale);
    }

    private void setClickListners(View view){
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_male:
                Constants.SelectedGender.isGenderChanged = isGenderChange("male");
                Log.e("SelectedGender", "isGenderChanged = " + isGenderChange("male"));
                cc.savePrefBoolean(Constants.SelectedGender.isManSelected, true);
                startActivity(new Intent(SelectGender.this, StyleSwiper.class));
//                Intent intent=new Intent(SelectGender.this, WebViewActivity.class);
//                intent.putExtra("webservice","dashboard");
//                startActivity(intent);
//                finish();
                break;
            case R.id.iv_female:
                Log.e("SelectedGender", "isGenderChanged = " + isGenderChange("female"));
                Constants.SelectedGender.isGenderChanged = isGenderChange("female");
                cc.savePrefBoolean(Constants.SelectedGender.isManSelected, false);
                startActivity(new Intent(SelectGender.this, StyleSwiper.class));
//                Intent intent1=new Intent(SelectGender.this, WebViewActivity.class);
//                intent1.putExtra("webservice","dashboard");
//                startActivity(intent1);
                finish();
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
}