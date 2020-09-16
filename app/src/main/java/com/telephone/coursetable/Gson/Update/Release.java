package com.telephone.coursetable.Gson.Update;

import java.util.List;

public class Release {
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
