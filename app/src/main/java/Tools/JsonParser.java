
// Copyright © 2018 - Fathalfath30.
// Created On 9/4/2018 
// Email : fathalfath30@gmail.com
// Follow me on GithHub : https://github.com/Fathalfath30
// 
// For the brave souls who get this far: You are the chosen ones,
// the valiant knights of programming who toil away, without rest,
// fixing our most awful code. To you, true saviors, kings of men,
//
// I say this: never gonna give you up, never gonna let you down,
// never gonna run around and desert you. Never gonna make you cry,
// never gonna say goodbye. Never gonna tell a lie and hurt you.
//
package Tools;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class JsonParser {
    static InputStream is = null;
    static JSONObject jObj ;
    static String json = "";

    public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {

        // Making HTTP request
        try {
            jObj = new JSONObject("{}");
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();


            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();

            jObj = new JSONObject(json);
        } catch (Exception e) {
            Log.e("VIEW HTMLNYA____", json);
            e.printStackTrace();
            try {
                jObj = new JSONObject("{\"code\":\"Telah Terjadi kesalahan pada http request\"}");
            }catch (Exception se){
                se.printStackTrace();
            }
        }
        json = "";
        return jObj;

    }

}
