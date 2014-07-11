package com.foolchi.safeguard.domain;

/**
 * Created by foolchi on 6/20/14.
 */
public class UpdateInfo {
    private String version, description, url;

    public String getVersion(){
        return version;
    }
    public void setVersion(String version){
        this.version = version;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getUrl(){
        return url;
    }
    public void setUrl(String url){
        this.url = url;
    }
}
