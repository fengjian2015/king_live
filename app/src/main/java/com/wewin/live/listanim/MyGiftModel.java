package com.wewin.live.listanim;

/*
 *   author:jason
 *   date:2019/5/2114:13
 */
public class MyGiftModel extends AbstractGiftModel {
    private String giftId;
    private String sendId;
    private String senderName;
    private String senderAvatar;
    private String giftName;
    private String giftPic;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getGiftPic() {
        return giftPic;
    }

    public void setGiftPic(String giftPic) {
        this.giftPic = giftPic;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    @Override
    public String getPrimaryKey() {
        return sendId + "_" + giftId;
    }
}
