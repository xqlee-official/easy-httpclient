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

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HttpRequest {
    protected static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    private static CloseableHttpClient httpClient;

    static {
        poolingHttpClientConnectionManager=new PoolingHttpClientConnectionManager();
        //每个路由的最大并发数连接
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(10);
        //总连接数
        poolingHttpClientConnectionManager.setMaxTotal(30);

        httpClient=HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .build();
    }




    private  int MAX_SOCKET_TIMEOUT = 60000;
    private  int MAX_CONNECTION_TIMEOUT = 60000;

    /**
     * http 请求地址
     */
    protected String url;
    /**
     * http请求头部参数
     */
    protected Map<String,String> headers=new ConcurrentHashMap<>(16);
    /**
     * form 表单参数 （适用于POST等请求）
     */
    protected Map<String,Object> form=null;

    /**
     * body 参数（常见json字符）
     */
    protected String body=null;

    /**
     * 方法
     */
    private HttpMethod httpMethod;

    /**
     * 自定义配置
     */
    protected RequestConfig customConfig=null;

    private final BasicCookieStore cookieStore=new BasicCookieStore();

    private String charsetName="UTF-8";

    public HttpRequest(String url) {
        if (null==url){
            throw new RuntimeException("Url Can't Null.");
        }else{
            String urlLowerCase = url.toLowerCase();
            if (!urlLowerCase.startsWith("http://")&&!urlLowerCase.startsWith("https://")){
                throw new RuntimeException("Url Must Be Start With Http:// or https://");
            }
        }
        this.httpMethod = HttpMethod.GET;
        this.url = url;
        //默认头设置（后面通过header覆盖）
        this.headers.put("Accept","text/html,application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        this.headers.put("Accept-Encoding","gzip, deflate");
        this.headers.put("Accept-Language","zh-CN,zh;q=0.8");
        this.headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");


    }


    protected static HttpRequest create(String url){
        return new HttpRequest(url);
    }



    public HttpRequest header(String name,String value){
        if (null!=name&&null!=value){
            this.headers.put(name,value);
        }
        return this;
    }

    public HttpRequest charset(String charsetName){
        if (Objects.nonNull(charsetName)&& !charsetName.isEmpty()){
            this.charsetName=charsetName;
        }
        return this;
    }

    public HttpRequest headerMap(Map<String,String> headers){
        if (Objects.nonNull(headers)){
            this.headers.putAll(headers);
        }
        return this;
    }


    public HttpRequest cookie(String name,String value){
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(cookieDomain());
        cookie.setPath("/");
        this.cookieStore.addCookie(cookie);
        return this;
    }


    public HttpRequest cookie(BasicClientCookie cookie){
        this.cookieStore.addCookie(cookie);
        return this;
    }

    private String cookieDomain(){
        if (Objects.isNull(this.url)){
           return "localhost";
       }else if (url.isEmpty()){
           return "localhost";
       }else{
            String urlLowerCase = this.url.toLowerCase();
            if (urlLowerCase.startsWith("http")||urlLowerCase.startsWith("https")){
                Matcher matcher = Pattern.compile("^http(s)?://([^:/]*)[:/].*$").matcher(urlLowerCase);
                if (matcher.find()){
                    return matcher.group(2);
                }
            }
        }
        return "localhost";
    }


    public HttpRequest timeout(int seconds){
        this.MAX_SOCKET_TIMEOUT=seconds*1000;
        this.MAX_CONNECTION_TIMEOUT=seconds*1000;
        return this;
    }


    protected static void connect(int maxPerRoute,int maxTotal){
         poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxPerRoute);
         poolingHttpClientConnectionManager.setMaxTotal(maxTotal);
         httpClient=HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .build();
    }


    public HttpRequest config(RequestConfig config){
        if (Objects.nonNull(config)){
            this.customConfig=config;
        }
        return this;
    }

    public HttpRequest form(String name,Object value){
        this.body=null;
        if (value instanceof Serializable){
           return this.putToForm(name,value);
        }else{
            throw new RuntimeException("Form Value Must Instanceof Serializable");
        }
    }


    public HttpRequest formMap(Map<String,Object> map){
        if (Objects.nonNull(map)){
            for (String key : map.keySet()) {
               this.putToForm(key,map.get(key));
            }
        }
        return this;
    }


    public HttpRequest formMapStr(Map<String,String> map){
        if (Objects.nonNull(map)){
            for (String key : map.keySet()) {
                this.putToForm(key,map.get(key));
            }
        }
        return this;
    }


    public HttpRequest body(String body){
        if (null!=body){
            this.form=null;
            this.body=body;
        }
        return this;
    }



    public HttpRequest method(HttpMethod httpMethod){
        this.httpMethod = httpMethod;
        return this;
    }


    private HttpRequest putToForm(String name,Object value){
        if (null != name && null != value){
            if (null == this.form){
                this.form=new ConcurrentHashMap<>(16);
            }
            this.form.put(name,value);
        }
        return this;
    }


    public enum HttpMethod {
        GET("GET"),
        POST("POST"),
        DELETE("DELETE"),
        PATCH("PATCH"),
        PUT("PUT"),
        HEAD("HEAD"),
        OPTIONS("OPTIONS"),
        TRACE("TRACE"),
        ;
        /**
         * 方法名称
         */
        final String name;

        public String getName() {
            return name;
        }

        HttpMethod(String name){
            this.name=name;
        }
    }

    public HttpResponse execute() throws IOException {

        String method=this.httpMethod.getName();
        RequestConfig config = RequestConfig.custom().setSocketTimeout(MAX_SOCKET_TIMEOUT).setConnectTimeout(MAX_CONNECTION_TIMEOUT).build();
        if (Objects.nonNull(customConfig)){
            config=customConfig;
        }
        HttpEntityEnclosingRequestBase request = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return method;
            }
        };
        //头部处理
        if (Objects.nonNull(this.headers)){
            for (String key : this.headers.keySet()) {
                request.addHeader(key,this.headers.get(key));
            }
        }
        //方法处理
        if (Objects.equals(this.httpMethod, HttpMethod.GET)){
            //处理参数
            if (Objects.nonNull(this.form)){
                if (!this.url.contains("?")){
                    this.url+="?";
                }
                List<String> params=new ArrayList<>();
                for (String key : this.form.keySet()) {
                    params.add(key+"="+this.form.get(key));
                }
                this.url+=String.join("&",params);
            }
        } else if (Objects.equals(this.httpMethod,HttpMethod.POST)) {
            //目前仅判断form参数和json参数
            if (Objects.nonNull(this.form)){
                //form 参数（不含file）
                List<NameValuePair> list = new ArrayList<>();
                for (String key : this.form.keySet()) {
                    list.add(new BasicNameValuePair(key, String.valueOf(form.get(key))));
                }
                request.setEntity(new UrlEncodedFormEntity(list));
            }else{
                //json body参数
                assert this.headers != null;
                String contentType = this.headers.get("Content-Type");
                if (Objects.isNull(contentType)||contentType.isEmpty()){
                    this.headers.put("Content-Type", "application/json;charset="+charsetName);
                }
                if (null == body){
                    body="";
                }
                request.setEntity(new StringEntity(body, Charset.forName(charsetName)));
            }
        }

        request.setURI(URI.create(this.url));
        HttpClientContext context=HttpClientContext.create();
        context.setRequestConfig(config);
        context.setCookieStore(this.cookieStore);
        try(CloseableHttpResponse response = httpClient.execute(request,context)){
            return new HttpResponse(response, EntityUtils.toByteArray(response.getEntity()), cookieStore.getCookies());
        }
    }

    public String executeRedirect() throws IOException {

        String method=this.httpMethod.getName();

        HttpEntityEnclosingRequestBase request = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return method;
            }
        };
        //头部处理
        if (Objects.nonNull(this.headers)){
            for (String key : this.headers.keySet()) {
                request.addHeader(key,this.headers.get(key));
            }
        }
        //方法处理
        if (Objects.equals(this.httpMethod, HttpMethod.GET)){
            //处理参数
            if (Objects.nonNull(this.form)){
                if (!this.url.contains("?")){
                    this.url+="?";
                }
                List<String> params=new ArrayList<>();
                for (String key : this.form.keySet()) {
                    params.add(key+"="+this.form.get(key));
                }
                this.url+=String.join("&",params);
            }
        } else if (Objects.equals(this.httpMethod,HttpMethod.POST)) {
            //目前仅判断form参数和json参数
            if (Objects.nonNull(this.form)){
                //form 参数（不含file）
                List<NameValuePair> list = new ArrayList<>();
                for (String key : this.form.keySet()) {
                    list.add(new BasicNameValuePair(key, String.valueOf(form.get(key))));
                }
                request.setEntity(new UrlEncodedFormEntity(list));
            }else{
                //json body参数
                assert this.headers != null;
                String contentType = this.headers.get("Content-Type");
                if (Objects.isNull(contentType)||contentType.isEmpty()){
                    this.headers.put("Content-Type", "application/json;charset="+charsetName);
                }
                if (null == body){
                    body="";
                }
                request.setEntity(new StringEntity(body, Charset.forName(charsetName)));
            }
        }

        request.setURI(URI.create(this.url));

        //设置不允许重定向
        RequestConfig config = RequestConfig.custom().setRedirectsEnabled(false).build();

        HttpClientContext context=HttpClientContext.create();
        context.setRequestConfig(config);
        try(CloseableHttpResponse response = httpClient.execute(request,context)){
            int code = response.getStatusLine().getStatusCode();
            String newuri="";
            if (code == 302||code==301) {
                // 跳转的目标地址是在response的 HTTP-HEAD 中的，location的值
                Header header = response.getFirstHeader("location");
                // 这就是跳转后的地址，再向这个地址发出新申请，以便得到跳转后的信息是啥。
                newuri = header.getValue();
                return newuri;
            }else{
                return "";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
