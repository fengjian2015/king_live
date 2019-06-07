package com.wewin.live.listanim;

/**
 * @author  : zhouyx
 * Date   : 2017/8/2
 * Description : 礼物数据抽象类
 */
public abstract class AbstractGiftModel {
    /**
     * 发送礼物的数量
     */
    private int count;
    /**
     * 发送礼物的时间
     */
    private long sendTime;
    /**
     * 如果startComboCount不为0，则从startComboCount开始计数连击动画直至到startComboCount + count
     */
    private int startComboCount;
    private int gif_time;

    public int getGif_time() {
        return gif_time;
    }

    public void setGif_time(int gif_time) {
        this.gif_time = gif_time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public int getStartComboCount() {
        return startComboCount;
    }

    public void setStartComboCount(int startComboCount) {
        this.startComboCount = startComboCount;
    }

    /**
     * 返回一个唯一标志位，用于判断是否同一个人发的同一件礼物，用于连击判断
     * 一般可以使用发送人id + 礼物id的组合
     *
     * @return
     */
    public abstract String getPrimaryKey();

}
