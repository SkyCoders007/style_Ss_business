package mxi.com.styleswiperbusiness.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import mxi.com.styleswiperbusiness.Network.CommanClass;
import mxi.com.styleswiperbusiness.Network.Constants;
import mxi.com.styleswiperbusiness.R;

public class StyleDetail extends AppCompatActivity {

    CommanClass cc;
    ImageView ivBack, ivStyle;
    TextView tvColor, tvStyle, tvCost, tvLength,tv_like_style_detail,tv_visit_style_detail;
    String notAvailable = "N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_detail);
        initUI();
    }

    private void initUI() {
        cc = new CommanClass(StyleDetail.this);
        ivBack = (ImageView) findViewById(R.id.iv_back_style_detail);
        ivStyle = (ImageView) findViewById(R.id.iv_image_style_detail);
        tvColor = (TextView) findViewById(R.id.tv_color_style_detail);
        tvStyle = (TextView) findViewById(R.id.tv_style_style_detail);
        tvCost = (TextView) findViewById(R.id.tv_cost_style_detail);
        tvLength = (TextView) findViewById(R.id.tv_length_style_detail);
        tv_like_style_detail = (TextView) findViewById(R.id.tv_like_style_detail);
        tv_visit_style_detail = (TextView) findViewById(R.id.tv_visit_style_detail);
        fillData();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fillData() {
        Bundle data = getIntent().getExtras();
        long styleID = data.getLong(Constants.StyleDetails.StyleID);
        String style = data.getString(Constants.StyleDetails.Style);
        String styleImage = data.getString(Constants.StyleDetails.StyleImage);
        String color = data.getString(Constants.StyleDetails.Color);
        String length = data.getString(Constants.StyleDetails.Length);
        String cost = data.getString(Constants.StyleDetails.Cost);
        String visited = data.getString("StyleVisited");
        String liked= data.getString("StyleLiked");

        if (style.length() > 0) {
            tvStyle.setText("Style :  " + style);
        } else {
            tvStyle.setText("Style :  " + notAvailable);
        }

        if (styleImage.length() > 0) {
            Picasso.with(StyleDetail.this).load(styleImage).into(ivStyle);
        } else {
            Picasso.with(StyleDetail.this).load(R.mipmap.not_available).into(ivStyle);
        }

        if (color.length() > 0) {
            tvColor.setText("Color :  " + color);
        } else {
            tvColor.setText("Color :  " + notAvailable);
        }

        if (length.length() > 0) {
            tvLength.setText("Length :  " + length);
        } else {
            tvLength.setText("Length :  " + notAvailable);
        }

        if (cost.length() > 0) {
            tvCost.setText("Cost :  " + cost);
        } else {
            tvCost.setText("Cost :  " + notAvailable);
        }

        if (liked.length() > 0) {
            tv_like_style_detail.setText("Times Liked :  " +liked);
        } else {
            tv_like_style_detail.setText("Times Liked :  " + notAvailable);
        }


        if (visited.length() > 0) {
            tv_visit_style_detail.setText("Times Redeemed:  " +visited);
        } else {
            tv_visit_style_detail.setText("Times Redeemed:  " + notAvailable);
        }

    }

}
