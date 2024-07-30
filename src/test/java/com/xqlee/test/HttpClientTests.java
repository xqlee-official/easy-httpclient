/*
 *  Copyright 2017-2023 xqlee.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xqlee.test;

import com.xqlee.utils.http.EasyHttpUtil;
import com.xqlee.utils.http.HttpResponse;
import org.apache.http.Header;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Unit test for simple App.
 */
public class HttpClientTests {


    public static void main(String[] args) throws IOException {
//        getRedirect();
        t();
    }


    public static void t(){
        String request="{\"shipping_list\":[{\"tracking_no\":\"\",\"item_desc\":\"木子口语-颜色:黑色*1\",\"contact\":{\"receiver_contact\":\"15696058569\"},\"express_company\":\"\"}],\"upload_time\":\"2024-01-03T12:54:37+0800\",\"order_key\":{\"out_trade_no\":\"20240103125339190626500\",\"mchid\":\"1649186600\",\"order_number_type\":1},\"logistics_type\":3,\"payer\":{\"openid\":\"o2gqx61nGW9wI5wzgJ7Y7cCA8WY8\"},\"delivery_mode\":1}";
        String body = null;
        try {
            body = EasyHttpUtil.post("http://localhost/t")
                    .header("Content-Type","application/json;charset=UTF-8")
                    .body(request)
                    .execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(body);
    }

    public static void getRedirect() throws IOException {
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("Cookie","123");
        headerMap.put("referer","https://www.douyin.com/");
        headerMap.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36 Edg/117.0.2045.60");

        String url="https://www.douyin.com/aweme/v1/play/?video_id=v0300fg10000cjo168bc77u5m8uvemeg";
        String s = EasyHttpUtil.get(url)
                .headerMap(headerMap)
                .executeRedirect();
        System.out.println(s);
    }

    public static void get() throws IOException {
        HttpResponse response = EasyHttpUtil.get("http://www.baidu.com")
                .execute();
        String body = response.body();
        System.out.println("body:\n"+body);
        int code = response.code();
        System.out.println("http code:"+code);
        Header firstHeader = response.getFirstHeader("Content-Type");
        if (Objects.nonNull(firstHeader)){
            System.out.println("header :"+firstHeader.getValue());
        }
    }


    public static void postForm() throws IOException {
        HttpResponse response = EasyHttpUtil.post("https://www.baidu.com")
                .form("keywords", "测试")
                .header("token", "123456")
                .cookie("BAIDU", "11234")
                .execute();
        String body = response.body();
    }

    public static void postJson() throws IOException {
        HttpResponse response = EasyHttpUtil.post("https://www.baidu.com")
                .body("{\"keywords\":\"测试\"}")
                .header("token", "123456")
                .cookie("BAIDU", "11234")
                .execute();
        String body = response.body();
    }

    public static void getFile()throws IOException {
        HttpResponse response = EasyHttpUtil.get("https://www.baidu.com")
                .execute();
        byte[] bytes = response.bodyBytes();
        try (FileOutputStream fos=new FileOutputStream("d:/tmp/baidu.html")){
            fos.write(bytes);
            fos.flush();
        }
    }

}
