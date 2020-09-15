package com.telephone.coursetable.Update;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.telephone.coursetable.BuildConfig;
import com.telephone.coursetable.Http.HttpConnectionAndCode;
import com.telephone.coursetable.Https.Get;
import com.telephone.coursetable.MyApp;

import java.util.List;

public class Update {

    public static void whatIsNew(Context c, Runnable error, Runnable new_version, Runnable no_new_version) {
        final String NAME = "whatIsNew()";
        class Release{
            class Asset{
                private String name;
                private String browser_download_url;

                public String getName() {
                    return name;
                }

                public String getBrowser_download_url() {
                    return browser_download_url;
                }
            }
            private String html_url;
            private String tag_name;
            private String name;
            private String body;
            private List<Asset> assets;

            public String getHtml_url() {
                return html_url;
            }

            public String getTag_name() {
                return tag_name;
            }

            public String getName() {
                return name;
            }

            public String getBody() {
                return body;
            }

            public List<Asset> getAssets() {
                return assets;
            }
        }
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(c);
        String url ="https://api.github.com/repos/Telephone2019/CourseTable/releases/latest";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Log.e(NAME, "response: " + response);
                    if (response.isEmpty()) {
                        error.run();
                    } else {
                        Release latest = new Gson().fromJson(response, Release.class);
                        String version = "v" + BuildConfig.VERSION_NAME;
                        String latest_tag = latest.getTag_name();
                        if (!latest_tag.equals(version)) {
                            new_version.run();
                        } else {
                            no_new_version.run();
                        }
                    }
                },
                net_error -> {
                    Log.e(NAME, "error: " + net_error);
                    error.run();
                }
        );
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
