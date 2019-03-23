package com.DDebbie.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by savera on 10/2/16.
 *
 * This class is created for adding Headers like Api-Key
 * Use this class for api call in whole project.
 *
 * In getBodyContentType() in this we pass Content-Type
 *
 * In getHeaders() in this we pass header like Api-Key
 *
 */
public class JsonObjectRequestWithHeader extends JsonObjectRequest {

    public JsonObjectRequestWithHeader(int method, String url, JSONObject requestBody, Response.Listener listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    public String getBodyContentType()
    {
        return "application/json";
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        Map<String, String> params = new HashMap<String, String>();
        // params.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_VALUE);
        params.put(Constants.API_KEY, Constants.API_KEY_VALUE);

        return params;
    }


}
