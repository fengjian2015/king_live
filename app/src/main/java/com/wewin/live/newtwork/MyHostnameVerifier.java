package com.wewin.live.newtwork;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by GA on 2018/6/21.
 */

public class MyHostnameVerifier {
    public MyHostnameVerifier() {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession sslSession) {
//                if (hostname.equalsIgnoreCase("api.github.com") ||
//                        hostname.equalsIgnoreCase("www.bclould.com") ||
//                        hostname.equalsIgnoreCase("api.cnblocklink.com") ||
//                        hostname.equalsIgnoreCase("socket.bclould.com") ||
//                        hostname.equalsIgnoreCase("socket.cnblocklink.com") ||
//                        hostname.equalsIgnoreCase("offline.cnblocklink.com") ||
//                         hostname.equalsIgnoreCase("msg.umengcloud.com")||
//                         hostname.equalsIgnoreCase("topper-bucket.oss-cn-shenzhen.aliyuncs.com")||
//                        hostname.equalsIgnoreCase("image.baidu.com")){
                    return true;
//                } else {
//                    return false;
//                }
            }
        });
    }
}
