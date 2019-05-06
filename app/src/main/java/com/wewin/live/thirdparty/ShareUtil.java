package com.wewin.live.thirdparty;

import com.wewin.live.R;
import com.wewin.live.modle.BaseInfoConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author jsaon
 * @date 2019/3/18
 */
public class ShareUtil {
    public static int[] content=new int[]{R.string.wechat,R.string.wechat_circle,R.string.qq,R.string.qq_circle,R.string.weibo,R.string.copy_link};
    public static int[] imageResources=new int[]{R.mipmap.icon_share_wx,R.mipmap.icon_share_wx_circle,R.mipmap.icon_share_qq,R.mipmap.icon_share_qq_circle
            ,R.mipmap.icon_share_wb,R.mipmap.icon_share_link};

    public static List<HashMap> getShareList(){
        List<HashMap> list=new ArrayList<>();
        for(int i = 0; i<ShareUtil.content.length; i++){
            HashMap hashMap=new HashMap();
            hashMap.put(BaseInfoConstants.AVATAR,ShareUtil.imageResources[i]);
            hashMap.put(BaseInfoConstants.NAME,ShareUtil.content[i]);
            list.add(hashMap);
        }
        return list;
    }
}
