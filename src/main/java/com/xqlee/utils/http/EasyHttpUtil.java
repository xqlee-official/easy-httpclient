/*
 *  Copyright 2017-2024 xqlee.com.
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

package com.xqlee.utils.http;

public class EasyHttpUtil {

    /**
     * POST请求
     * @param url api地址
     * @return 执行结果
     */
    public static HttpRequest post(String url){
        return HttpRequest.create(url).method(HttpRequest.HttpMethod.POST);
    }

    /**
     * get请求
     * @param url api地址
     * @return 执行结果
     */
    public static HttpRequest get(String url){
        return HttpRequest.create(url).method(HttpRequest.HttpMethod.GET);
    }

    /**
     * put 请求
     * @param url api地址
     * @return 执行结果
     */
    public static HttpRequest put(String url){
        return HttpRequest.create(url).method(HttpRequest.HttpMethod.PUT);
    }

    /**
     * patch 请求
     * @param url api地址
     * @return 执行结果
     */
    public static HttpRequest patch(String url){
        return HttpRequest.create(url).method(HttpRequest.HttpMethod.PATCH);
    }

    /**
     * head 请求
     * @param url api地址
     * @return 执行结果
     */
    public static HttpRequest head(String url){
        return HttpRequest.create(url).method(HttpRequest.HttpMethod.HEAD);
    }

    /**
     * delete 请求
     * @param url api地址
     * @return 执行结果
     */
    public static HttpRequest delete(String url){
        return HttpRequest.create(url).method(HttpRequest.HttpMethod.DELETE);
    }
    /**
     * options 请求
     * @param url api地址
     * @return 执行结果
     */
    public static HttpRequest options(String url){
        return HttpRequest.create(url).method(HttpRequest.HttpMethod.OPTIONS);
    }

    /**
     * trace 请求
     * @param url api地址
     * @return 执行结果
     */
    public static HttpRequest trace(String url){
        return HttpRequest.create(url).method(HttpRequest.HttpMethod.TRACE);
    }


    /**
     * 连接池管理 数量
     * @param maxPerRoute 每个路由的最大并发数连接
     * @param maxTotal 总连接数
     */
    public static void poolConnect(int maxPerRoute,int maxTotal){
        HttpRequest.connect(maxPerRoute,maxTotal);
    }

}
