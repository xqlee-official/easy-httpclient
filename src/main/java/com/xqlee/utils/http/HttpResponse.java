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
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.cookie.Cookie;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class HttpResponse {


    private final StatusLine statusline;

    /**
     * http 响应状态码
     */
    int code;

    /**
     * 协议信息
     */
    ProtocolVersion ver;

    private final Locale locale;

    private final org.apache.http.HttpResponse original;

    /**
     * 内容
     */
    private final byte[] contentBytes;

    private final List<Cookie> cookies;

    public HttpResponse(org.apache.http.HttpResponse response, byte[] contentBytes, List<Cookie> cookies) throws IOException {
        this.original=response;
        this.contentBytes=contentBytes;
        this.statusline=response.getStatusLine();
        this.code=response.getStatusLine().getStatusCode();
        this.ver=response.getProtocolVersion();
        this.locale=response.getLocale();
        this.cookies = cookies;
    }

    public String body() throws IOException {
        String charset="utf-8";
        Header contentType = this.original.getEntity().getContentType();
        if (Objects.nonNull(contentType)){
            String charSet = getCharSet(contentType.getValue());
            if (Objects.nonNull(charSet)){
                charset=charSet;
            }
        }
        return new String(contentBytes,charset);
    }

    public String body(String charset) throws IOException {
        return new String(contentBytes,charset);
    }

    public byte[] bodyBytes()throws IOException{
        return contentBytes;
    }

    /**
     * 根据HTTP 响应头部的content type抓取响应的字符集编码
     *
     * @param content
     * @return
     */
    private static String getCharSet(String content) {
        String regex = ".*charset=([^;]*).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()){
            return matcher.group(1);
        }else{
            return null;
        }

    }

    public List<Cookie> getCookie(String name){
        if (Objects.isNull(this.cookies)){
            return null;
        }
        return this.cookies.stream().filter(o->Objects.equals(o.getName(),name)).collect(Collectors.toList());
    }

    public Cookie getFirstCookie(String name){
        if (Objects.isNull(this.cookies)){
            return null;
        }
        return this.cookies.stream().filter(o->Objects.equals(o.getName(),name)).findFirst().orElse(null);
    }

    public Header[] getHeaders(String var1){
        return this.original.getHeaders(var1);
    }

    public Header getFirstHeader(String var1){
        return this.original.getFirstHeader(var1);
    }

    public Header getLastHeader(String var1){
        return this.original.getLastHeader(var1);
    }

   public Header[] getAllHeaders(){
        return this.original.getAllHeaders();
    }

    public  boolean containsHeader(String var1){
        return this.original.containsHeader(var1);
    }

    public StatusLine getStatusline() {
        return statusline;
    }

    public int code() {
        return code;
    }

    public ProtocolVersion getVer() {
        return ver;
    }

    public Locale getLocale() {
        return locale;
    }
}
