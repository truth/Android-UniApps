package com.elevenzon.TextInputLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.Patterns;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import com.elevenzon.TextInputLayout.util.MyAsyncTask;
import com.smarx.notchlib.INotchScreen;
import com.smarx.notchlib.NotchScreenManager;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView register;
    boolean isEmailValid, isPasswordValid;
    TextInputLayout emailError, passError;
    private static final String TAG = "landscape_notch";
    private NotchScreenManager notchScreenManager = NotchScreenManager.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置Activity全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        // 隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // 支持显示到刘海区域
        notchScreenManager.setDisplayInNotch(this);
        // 获取刘海屏信息
        notchScreenManager.getNotchInfo(this, new INotchScreen.NotchScreenCallback() {
            @Override
            public void onResult(INotchScreen.NotchScreenInfo notchScreenInfo) {
                Log.i(TAG, "Is this screen notch? " + notchScreenInfo.hasNotch);
                if (notchScreenInfo.hasNotch) {
                    for (Rect rect : notchScreenInfo.notchRects) {
                        Log.i(TAG, "notch screen Rect =  " + rect.toShortString());
                        // 将被遮挡的TextView左移
//                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textView.getLayoutParams();
//                        layoutParams.leftMargin = rect.right;
//                        textView.setLayoutParams(layoutParams);
                    }
                }
            }
        });
        email = (EditText) findViewById(R.id.email);
        SharedPreferences sp = getApplicationContext().getSharedPreferences("private", Context.MODE_PRIVATE);
        String user = sp.getString("username","");
        String pwd = sp.getString("password","");
        if(user!=null && user.length()>0) {
            email.setText(user);
        }
        password = (EditText) findViewById(R.id.password);
        if(pwd!=null && pwd.length()>0) {
            password.setText(pwd);
        }
        login = (Button) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);
        emailError = (TextInputLayout) findViewById(R.id.emailError);
        passError = (TextInputLayout) findViewById(R.id.passError);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SetValidation()) {
                    //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    //startActivity(intent);
                    MyAsyncTask task = new MyAsyncTask(LoginActivity.this.getApplicationContext(),LoginActivity.this);
                    task.setUser(email.getText().toString());
                    task.setPassword(password.getText().toString());
                    task.execute(1000);
                }
            }
        });

        /*
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to RegisterActivity
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        */
    }

    public boolean SetValidation() {
        // Check for a valid email address.
        /**
         * else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
         *             emailError.setError(getResources().getString(R.string.error_invalid_email));
         *             isEmailValid = false;
         *         }
         */
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.name_error));
            isEmailValid = false;
        } else  {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (password.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (password.getText().length() < 6) {
            passError.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else  {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }

        if (isEmailValid && isPasswordValid) {
            Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
        }
        return isPasswordValid && isEmailValid;
    }

}