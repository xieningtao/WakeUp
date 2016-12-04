package com.sf.banbanle.sign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by mac on 16/12/3.
 */

public class PushSignature {

    public static final String URL_KEY = "url";
    public static final String HTTP_METHOD_KEY = "http_method";
    public static final String CLIENT_SECRET_KEY = "client_secret";

    public String digest(String accessKey, String secretKey, Map<String, String> params) {
        return null;
    }

    private   Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, String> sortMap = new TreeMap<String, String>(
                new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        return lhs.compareTo(rhs);
                    }
                });

        sortMap.putAll(map);

        return sortMap;
    }

    public String digest(String method, String url, String clientSecret, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(url);
        List<String> paramsContent=new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramsContent.add(entry.getKey()+"="+entry.getValue());
        }
        Collections.sort(paramsContent, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        for(String content:paramsContent){
            sb.append(content);
        }
        sb.append(clientSecret);
        String encodeString = MessageDigestUtility.urlEncode(sb.toString());
        if ( encodeString != null ) {
            encodeString = encodeString.replaceAll("\\*", "%2A");
        }
        return MessageDigestUtility.toMD5Hex(encodeString);
    }


    public boolean checkParams(Map<String, String> params) {
        return false;
    }

}
