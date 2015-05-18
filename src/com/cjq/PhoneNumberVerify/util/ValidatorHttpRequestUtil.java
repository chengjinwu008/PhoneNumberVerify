package com.cjq.PhoneNumberVerify.util;

import android.util.Log;
import com.cjq.StreamUtil.StreamUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by android on 2015/5/18.
 */
public class ValidatorHttpRequestUtil {

    public static String request(String out) throws IOException,XmlPullParserException {
        //转换字符串到字节数组
        byte[] param = out.getBytes();
        URL url1 = new URL("http://webservice.36wu.com/MobilePhoneService.asmx");//todo 修改肥皂协议主机地址
        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
//        connection.setConnectTimeout(5000);
//        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
        connection.setRequestProperty("Content-Length", String.valueOf(param.length));
        connection.setDoInput(true);
        connection.setDoOutput(true);
        BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
        bos.write(param);
        bos.flush();
        bos.close();
        String res = null;
        if (connection.getResponseCode() == 200) {
            InputStream in = connection.getInputStream();
            //已经取得了返回值
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(in, "UTF-8");
            int tag = parser.next();
            while (tag != XmlPullParser.END_DOCUMENT) {
                if (tag == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    if ("Mobile".equals(tagName)) {
                        res = parser.nextText();
                        break;
                    }
                }
                tag = parser.next();
            }
            in.close();
            connection.disconnect();
        }
        return res;
    }

    /**
     * mts:'xxxxxxxx',
     province:'四川',
     catName:'中国移动',
     telString:'xxxxxxxx',
     areaVid:'xxxx',
     ispVid:'xxxxx',
     carrier:'四川移动'
     */
    public static JSONObject requestTaobaoGET(String out) throws IOException, JSONException {
        String url_str = "http://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel="+out;
        URL url = new URL(url_str);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoInput(true);
        if(connection.getResponseCode()==200){
            InputStream in =connection.getInputStream();
            String res=StreamUtil.readStreamToString(in);
//            Log.e("xxx",res);
            Pattern pattern = Pattern.compile("__.*_.*=\\s");
            Matcher matcher = pattern.matcher(res);
            if(matcher.find()){
                res=matcher.replaceAll("");
            }
            in.close();
            connection.disconnect();
            return new JSONObject(res);
        }
        connection.disconnect();
        return null;
    }
}
