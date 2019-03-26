//      ____  __________   __  ______  __ 
//     / __ \/_  __/  _/  / / / / __ )/ / 
//    / / / / / /  / /   / / / / __  / /  
//   / /_/ / / / _/ /   / /_/ / /_/ / /___
//  /_____/ /_/ /___/   \____/_____/_____/
//                                        
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
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
    static JSONObject jObj = null;
    static String json = "";

    public JSONObject getJSONListfromURL(String url, List<NameValuePair> params) {
        //initialize
        InputStream is = null;
        String result = "";
        JSONObject jArray = null;

        //http post
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            //Log.e("log_tag", "Error in http connection " + e.toString());
        }

        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            //Log.e("log_tag", "Error converting result " + e.toString());
        }

        //try parse the string to a JSON object
        try {
            jArray = new JSONObject(result);
        } catch (Exception e) {
            //Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jArray;
    }

    public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            //Log.e("JSON", json);
        } catch (Exception e) {
            //Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            //Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }

    public void SendJSONToURL(String url, List<NameValuePair> params) {

        //http post
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params));
            httpclient.execute(httppost);

        } catch (Exception e) {
            //Log.e("log_tag", "Error in http connection " + e.toString());
        }
    }

    public JSONObject GetJSONObjectEncrypted(String url, List<NameValuePair> params) {

        // Making HTTP request
        try {
            jObj = null;
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        } catch (ClientProtocolException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            //Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        try {
            jObj = new JSONObject(json);
            jObj = new JSONObject((new appKey()).decode(jObj.getString("xdata")));

        } catch (JSONException e) {
            //Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return jObj;
    }
}
