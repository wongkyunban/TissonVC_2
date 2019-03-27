package com.wong.tissonvc_2.service.call.data;

/**
 * Created by w00255457 on 2017/9/1.
 */

public class OneKeyJoinConfParam {
    private boolean isVideoJoinConf;
    private String confID;
    private String accessCode;
    private String confPaswd;

    public OneKeyJoinConfParam() {
    }

    public boolean isVideoJoinConf() {
        return isVideoJoinConf;
    }

    public void setVideoJoinConf(boolean videoJoinConf) {
        isVideoJoinConf = videoJoinConf;
    }

    public String getConfID() {
        return confID;
    }

    public void setConfID(String confID) {
        this.confID = confID;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getConfPaswd() {
        return confPaswd;
    }

    public void setConfPaswd(String confPaswd) {
        this.confPaswd = confPaswd;
    }


}
