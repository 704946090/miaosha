package com.ceshi.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public class Sign {

    public static String sign(SortedMap<Object,Object> parameters,String Key){

//        String appid="wxd930ea5d5a258f4f";
//        String mch_id="10000100";
//        String device_info="1000";
//        String body="test";
//        String nonce_str="ibuaiVcKdpRxkhJA";
//
//        SortedMap<Object,Object> parameters=new TreeMap<Object, Object>();
//        parameters.put("appid",appid);
//        parameters.put("mch_id",mch_id);
//        parameters.put("device_info",device_info);
//        parameters.put("body",body);
//        parameters.put("nonce_str",nonce_str);
//
//        String key="192006250b4c09247ec02edce69f6a2d";

        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator<?> it = es.iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k)
                    && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        System.out.println("sb:"+sb.toString());

        sb.append("key="+Key);

        String sign= MD5.MD5Encode(sb.toString(),"UTF-8").toUpperCase();

        System.out.println("sign:"+sign);
        return sign;
    }

    public static void main(String[] args) {

    }
}
