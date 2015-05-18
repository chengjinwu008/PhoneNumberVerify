package com.cjq.PhoneNumberVerify.util;

import android.content.Context;
import android.util.Log;
import com.cjq.PhoneNumberVerify.R;
import com.cjq.StreamUtil.StreamUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by android on 2015/5/18.
 */
public class Validator {
    public interface VerifyListener {
        void verifyFinished(String state);
    }

    public static void verify(Context context,String num,VerifyListener listener){
        new Thread(){
            @Override
            public void run() {
                //拼装字符串
                InputStream stream = context.getResources().openRawResource(R.raw.soap12);
                String res=null;
                try {
                    String oString = StreamUtil.readStreamToString(stream);
                    String pat = "\\$mobile";
                    Pattern pattern = Pattern.compile(pat);
                    Matcher matcher = pattern.matcher(oString);
                    while (matcher.find()){
                        res = matcher.replaceAll(num);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        //关闭失败
                        e.printStackTrace();
                    }
                }

                //替换完成，用这个xml进行请求
                try {
                    String code = ValidatorHttpRequestUtil.request(res);
                    if(listener!=null)
                        listener.verifyFinished(code);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void verify(String num,VerifyListener listener){

        new Thread(){
            @Override
            public void run() {
                JSONObject code = null;
                try {
                    code = ValidatorHttpRequestUtil.requestTaobaoGET(num);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(code!=null){
                    if(listener!=null)
                        try {
                            listener.verifyFinished((String) code.get("telString"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }else{
                    if(listener!=null)
                        listener.verifyFinished(null);
                }
            }
        }.start();
    }
}
