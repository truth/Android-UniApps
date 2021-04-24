package com.elevenzon.TextInputLayout;

import android.app.Application;
import android.content.Context;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.elevenzon.TextInputLayout.util.FileUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dc.squareup.okhttp3.Call;
import dc.squareup.okhttp3.OkHttpClient;
import dc.squareup.okhttp3.Request;
import dc.squareup.okhttp3.Response;
import dc.squareup.okhttp3.ResponseBody;
import dc.squareup.okio.BufferedSink;
import dc.squareup.okio.BufferedSource;
import dc.squareup.okio.Okio;
import io.dcloud.common.DHInterface.ICallBack;
import io.dcloud.feature.sdk.DCUniMPSDK;

public class MiniAppManager {
    private static String TAG="TinyAPP";
    private static String [] names = {"04E3A11","2108B0A","AB47F19"};
    public final  static String serverUrl = "http://s1.wangzhenli.net";
    private static Map<String,TinyApp> apps = new HashMap<>();

    public static String get(int i) {
        return names[i%3];
    }
    public static Collection<TinyApp> getApps() {
        return apps.values();
    }
    public static void init() {
        String url = serverUrl+"/uni-app/apps.json";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    String content = response.body().string();
                    Log.d(TAG, "run: " +content );
                    List<TinyApp> list = JSON.parseArray(content,TinyApp.class);
                    for (TinyApp app :list) {
                        apps.put(app.getName(),app);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void start(Context context,int index) {
        start(context,"__UNI__"+MiniAppManager.get(index));
    }
    private static boolean exist(Context context,String name) {
        String wgtPath = context.getExternalCacheDir().getPath()+"/"+name+".wgt";
        return FileUtil.fileIsExists(wgtPath);
    }
    public static boolean startApp(final Context context,String name) {
        final  TinyApp app = apps.get(name);
        if(app!=null && exist(context,app.getApp())) {
            startExt(context,app);
            return true;
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String wgtPath = context.getExternalCacheDir().getPath() + "/"+app.getApp()+".wgt";
                    if(DownloadSmallFile(app.getUrl(),wgtPath))
                    {
                        startExt(context,app);
                    }
                }
            }).start();
            return false;
        }
    }
    public static boolean DownloadSmallFile(final String uri, final String filePath) {
        OkHttpClient client = new OkHttpClient();
        String url = uri;
        if(!uri.equals("http")){
            url=MiniAppManager.serverUrl+url;
        }
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return false;
            }

            ResponseBody body = response.body();
            long contentLength = body.contentLength();
            BufferedSource source = body.source();
            File file = new File(filePath);
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(source);
            sink.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    public  static void startExt(final  Context context,TinyApp app) {
        String wgtPath = context.getExternalCacheDir().getPath() + "/"+app.getApp()+".wgt";
        final String name = app.getApp();
        if(DCUniMPSDK.getInstance().isExistsApp(name)){
            try {
                DCUniMPSDK.getInstance().startApp(context, name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        DCUniMPSDK.getInstance().releaseWgtToRunPathFromePath(name, wgtPath, new ICallBack() {
            @Override
            public Object onCallBack(int code, Object pArgs) {
                if (code == 1) {//释放wgt完成
                    try {
                        DCUniMPSDK.getInstance().startApp(context, name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//释放wgt失败
                    Toast.makeText(context, "程序释放失败!", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });
    }
    public static void start(Context context, String name) {
        boolean innerApp = false;
        final Context ctx = context;
        for(String n:names) {
            if(name.equals("__UNI__"+n)) {
                innerApp = true;
                break;
            }
        }
        if(innerApp) {
            try {
                DCUniMPSDK.getInstance().startApp(context, name);
            } catch (Exception e) {
                e.printStackTrace();//
            }
        }else {
            if(!startApp(ctx,name)) {

            }
        }
    }
}
