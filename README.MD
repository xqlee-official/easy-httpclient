# Easy-Httpclient
## Description
Easy way to use apache httpclient

## Usage

maven pom
````xml
<dependency>
    <groupId>com.xqlee.utils</groupId>
    <artifactId>easy-httpclient</artifactId>
    <version>1.0.1</version>
</dependency>
````

Java  Codes
````java
    public static void get() throws IOException {
        HttpResponse response = EasyHttpUtil.get("http://www.bing.com")
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
        HttpResponse response = EasyHttpUtil.post("https://www.bing.com")
                .form("keywords", "test")
                .header("token", "123456")
                .cookie("BING", "11234")
                .execute();
        String body = response.body();
    }

    public static void postJson() throws IOException {
        HttpResponse response = EasyHttpUtil.post("https://www.bing.com")
                .body("{\"keywords\":\"test\"}")
                .header("token", "123456")
                .cookie("BING", "11234")
                .execute();
        String body = response.body();
    }

    public static void getFile()throws IOException {
        HttpResponse response = EasyHttpUtil.get("https://www.bing.com")
                .execute();
        byte[] bytes = response.bodyBytes();
        try (FileOutputStream fos=new FileOutputStream("d:/tmp/bing.html")){
            fos.write(bytes);
            fos.flush();
        }
    }
````

## Depends

- apache httpclient 4.15.3

## Change logs
#### 1.0.1
- First edition
## LICENSE
Apache License 2.0
