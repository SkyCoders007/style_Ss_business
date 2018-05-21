package mxi.com.styleswiperbusiness.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mxi.com.styleswiperbusiness.Network.CommanClass;
import mxi.com.styleswiperbusiness.Network.Constants;
import mxi.com.styleswiperbusiness.R;

public class SignUpFirst extends AppCompatActivity implements View.OnClickListener{

    CommanClass cc;
    TextView btnNext, btnSignIn;
    EditText etName, etEmail, etPhone, etPassword, etConfirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_first);
        initUI();
    }

    private void initUI(){
        cc = new CommanClass(SignUpFirst.this);
        btnNext = (TextView) findViewById(R.id.tv_next_sign_up);
        btnSignIn = (TextView) findViewById(R.id.tv_have_account_sign_up_first);

        etName = (EditText) findViewById(R.id.et_Name);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPhone = (EditText) findViewById(R.id.et_phone_number);
        etPassword = (EditText) findViewById(R.id.et_password_sign_up);
        etConfirmPass = (EditText) findViewById(R.id.et_confirm_pass);
        setViewClickListners();
    }

    private void setViewClickListners(){
        btnNext.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next_sign_up:
                checkName();
                break;
            case R.id.tv_have_account_sign_up_first:
                startActivity(new Intent(SignUpFirst.this, Login.class));
                break;
        }
    }

    private void checkName(){
        if (etName.getText().length() > 0) {
            checkEmail();
        } else {
            etName.setError("Name can not be blank");
            return;
        }
    }
    private void checkEmail(){
        if (etEmail.getText().length() > 0) {
            if(!isValidEmail(etEmail.getText().toString())) {
                etEmail.setError("Invalid Email");
                return;
            } else {
                checkPhone();
            }
        } else {
            etEmail.setError("Email can not be blank");
            return;
        }
    }
    private void checkPhone(){
        if (etPhone.getText().length() > 0) {
            checkPassword();
        } else {
            etPhone.setError("Phone number can not be blank");
            return;
        }
    }
    private void checkPassword(){
        if (etPassword.getText().length() > 0) {
            checkConfirmPassword();
        } else {
            etPassword.setError("Password can not be blank");
            return;
        }
    }
    private void checkConfirmPassword(){
        if (etConfirmPass.getText().length() > 0) {
            if(etPassword.getText().toString().equalsIgnoreCase(etConfirmPass.getText().toString())) {
                Intent intent = new Intent(SignUpFirst.this, SignUpSecond.class);
                intent.putExtra(Constants.SignUp.key_name, etName.getText().toString());
                intent.putExtra(Constants.SignUp.key_email, etEmail.getText().toString());
                intent.putExtra(Constants.SignUp.key_phone, etPhone.getText().toString());
                intent.putExtra(Constants.SignUp.key_pass, etPassword.getText().toString());
                intent.putExtra(Constants.SignUp.key_confirm_pass, etConfirmPass.getText().toString());
                startActivity(intent);
            } else {
                etConfirmPass.setError("Invalid Password");
                return;
            }
        } else {
            etConfirmPass.setError("Confirm Password can not be blank");
            return;
        }
    }

    private boolean isValidEmail(String email) {

        String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
