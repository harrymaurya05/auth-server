package com.auth.server.utils.activemq;

import com.auth.server.models.User;
import java.io.Serializable;

/**
 * Created by admin on 01/03/22.
 */
public class VideoEncodingEvent implements Serializable {
    private static final long serialVersionUID = 3224676866343498249L;
    private String uesrname ;
    private String videoName;
    private double videoSize;
    private double videoDuration;
    private String videoPath;
    private User user;
    private String requestId;

    public String getUesrname() {
        return uesrname;
    }

    public void setUesrname(String uesrname) {
        this.uesrname = uesrname;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public double getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(double videoSize) {
        this.videoSize = videoSize;
    }

    public double getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(double videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRequestId() {

        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override public String toString() {
        return "VideoEncodingEvent{" + "uesrname='" + uesrname + '\'' + ", videoName='" + videoName + '\''
                + ", videoSize=" + videoSize + ", videoDuration=" + videoDuration + ", videoPath='" + videoPath + '\''
                + ", user=" + user + '}';
    }
}
