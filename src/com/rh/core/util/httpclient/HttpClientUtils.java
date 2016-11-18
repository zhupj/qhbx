package com.rh.core.util.httpclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.rh.core.serv.OutBean;
import com.rh.core.util.JsonUtils;

/**
 * 
 * @author yangjy
 * 
 */
public class HttpClientUtils {
    
    /**
     * 
     * @param url 访问web页面的url
     * @return HttpGet 对象
     */
    public static HttpGet createHttpGet(String url) {
        HttpGet post = new HttpGet(url);

        post.setHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:10.0) Gecko/20100101 Firefox/10.0");

        return post;
    }
    
    /**
     * 
     * @param url 请求web地址
     * @param postParams post参数
     * @param encoding post数据编码
     * @return url指向页面的HTML内容
     * @throws IOException 异常
     */
    @SuppressWarnings("rawtypes")
    public static HttpPost createHttpPost(String url, Map<String, String> postParams, 
            String encoding) throws IOException {
        HttpPost post = new HttpPost(url);

        post.setHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:10.0) Gecko/20100101 Firefox/10.0");

        if (postParams != null) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator it = postParams.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                NameValuePair pair = new BasicNameValuePair(key, postParams.get(key));
                list.add(pair);
            }

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, encoding);

            post.setEntity(entity);
        }
        
        return post;
    }

    /**
     * 
     * @return 默认的HttpParams参数
     */
    public static HttpParams createDefaultHttpParams() {
        HttpParams httpParameters = new BasicHttpParams();
        //连接服务器超时时间：3秒钟
        HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
        //5分钟没返回则超时
        HttpConnectionParams.setSoTimeout(httpParameters, 5 * 60 * 1000);  

        return httpParameters;
    }

    /**
     * 
     * @return HttpClient 对象
     */
    public static HttpClient createHttpClient() {
        HttpClient client = new DefaultHttpClient(HttpClientUtils.createDefaultHttpParams());

        return client;
    }

    /**
     * 
     * @param url 访问web页面的url
     * @return url指向页面的HTML内容
     * @throws IOException 异常
     */
    public static String getWebContent(String url) throws IOException {
        HttpClient client = createHttpClient();
        HttpResponse response = client.execute(createHttpGet(url));
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException(response.getStatusLine().toString() + "  url: " + url);
        }

        return HttpResponseUtils.getResponseContent(response);
    }
    
    /**
     * 
     * @param url 请求web地址
     * @param postParam post参数
     * @param endcoding post数据编码
     * @return url指向页面的HTML内容
     * @throws IOException 异常
     */
    public static String getWebContent(String url, Map<String, String> postParam, String endcoding)
            throws IOException {
        HttpClient client = createHttpClient();
        HttpResponse response = client.execute(createHttpPost(url, postParam, endcoding));
//        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//            throw new IOException(response.getStatusLine().toString() + "  url: " + url);
//        }

        return HttpResponseUtils.getResponseContent(response);        
    }
    
    /**
     * 执行Post请求
     * @param post post请求对象
     * @return 返回请求结果
     * @throws IOException IO异常
     */
    public static OutBean execute(HttpPost post) throws IOException {
        HttpClient httpclient = HttpClientUtils.createHttpClient();
        HttpResponse response = httpclient.execute(post);

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//            OutBean outBean = new OutBean();
//            outBean.setError();
//            outBean.set("_RES_MSG", HttpResponseUtils.getResponseContent(response));
//            return outBean;
            throw new IOException("操作失败。" + HttpResponseUtils.getResponseContent(response));
        }

        String json = HttpResponseUtils.getResponseContent(response);

        OutBean outBean = new OutBean(JsonUtils.toBean(json));

        if (!outBean.isOk()) {
            throw new IOException("操作失败。" + outBean.getMsg());
        }

        return outBean;
    }
}