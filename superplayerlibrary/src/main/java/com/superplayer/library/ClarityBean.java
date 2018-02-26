package com.superplayer.library;

import java.io.Serializable;

/**
 * Created by NJ on 2018/1/30.
 */

public class ClarityBean implements Serializable{
    private String videoPath ;
    private String clarityKinds ;
    private int defaultClarity ;
    private int isLive ;

    @Override
    public String toString() {
        return "ClarityBean{" +
                "videoPath='" + videoPath + '\'' +
                ", clarityKinds='" + clarityKinds + '\'' +
                ", isLive=" + isLive +
                '}';
    }

    public int getIsLive() {
        return isLive;
    }

    public void setIsLive(int isLive) {
        this.isLive = isLive;
    }


    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getClarityKinds() {
        return clarityKinds;
    }

    public void setClarityKinds(String clarityKinds) {
        this.clarityKinds = clarityKinds;
    }
    private int getDefaultClarity() {
        return defaultClarity;
    }

    public void setDefaultClarity(int defaultClarity) {
        this.defaultClarity = defaultClarity;
    }
}
