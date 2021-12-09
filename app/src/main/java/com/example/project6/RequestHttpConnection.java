package com.example.project6;

import android.content.ContentValues;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class RequestHttpConnection {
    public String request(String _url, ContentValues params){
        HttpURLConnection urlConn= null;
        StringBuffer sbParams =new StringBuffer();
        /**
         *
         */
        if(params==null)
            sbParams.append("");
        else
        {
            boolean isAnd=false;
            String key;
            String value;
            for(Map.Entry<String,Object> parameter: params.valueSet())
            {
                key=parameter.getKey();
                value=parameter.getValue().toString();
                if(isAnd)
                    sbParams.append("&");

                sbParams.append(key).append("=").append(value);

                if(!isAnd)
                    if(params.size()>1)
                        isAnd=true;
            }
        }
        try {
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Accept-Charset", "UTP - 8");
            urlConn.setRequestProperty("ConText_Type", "application/x-www-form-urlencoded;charset=UTF-8");
            urlConn.setDoInput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;
            String line;
            String page = "";
            while ((line = br.readLine()) != null) {
                page += line;

            }
            return page;
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if(urlConn!=null)
                urlConn.disconnect();
        }
        return null;
    }

    public String buildChargerUrlWithId(String id){
        Uri.Builder builder= new Uri.Builder();
        builder.scheme("http")
                .authority("192.168.0.2")
                .path("8088")
                .appendPath("chargers")
                .appendPath(id)
                .build();
        String url=null;
        try {
            url=new URL(builder.toString()).toString();
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }


}
