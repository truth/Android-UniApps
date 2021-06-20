package com.elevenzon.TextInputLayout.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elevenzon.TextInputLayout.LoginActivity;
import com.elevenzon.TextInputLayout.MainActivity;
import com.elevenzon.TextInputLayout.TinyApp;

import java.io.IOException;
import java.util.List;

import dc.squareup.okhttp3.Call;
import dc.squareup.okhttp3.OkHttpClient;
import dc.squareup.okhttp3.Request;
import dc.squareup.okhttp3.Response;

public class MyAsyncTask extends AsyncTask<Integer, String, String> {
    private static String TAG="TinyAPP";
    private static String url="http://122.225.91.190:8088/jeecg-boot//sys/apiLogin";
    private Context ctx;
    private LoginActivity activity;
    private String user;
    private String password;

    public MyAsyncTask(Context context,LoginActivity activity) {
        super();
        this.ctx = context;
        this.activity = activity;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected String doInBackground(Integer... integers) {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url+"?username="+user+"&password="+password)
                .build();
        final Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String content = response.body().string();
            JSONObject json = JSONObject.parseObject(content);
            if(json!=null && json.getBoolean("success")) {
                SharedPreferences sp = ctx.getSharedPreferences("private",Context.MODE_PRIVATE);
                sp.edit().putString("user",content).commit();
                JSONObject result = json.getJSONObject("result");
                if(result!=null) {
                    JSONObject user = result.getJSONObject("userInfo");
                    if(user!=null) {
                        GlobalCache.put("userId",user.getString("id"));
                        GlobalCache.put("orgCode",user.getString("orgCode"));
                    }
                }
                GlobalCache.put("token",json.getJSONObject("result").getString("token"));
            }else {
                return "error";
            }
            Log.d(TAG, "uni-apps: " +content );
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
        return "ok";
    }
    /**
     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
     */
    @Override
    protected void onPostExecute(String result) {
        //btn.setText("线程结束" + result);
        if(result.equals("ok")) {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        }else {

        }
    }
    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPreExecute() {
        //btn.setText("开始执行异步线程");
    }
    /**
     * 这里的Intege参数对应AsyncTask中的第二个参数
     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
     * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
     */
    @Override
    protected void onProgressUpdate(String... values) {

    }
}