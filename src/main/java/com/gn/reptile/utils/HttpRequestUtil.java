package com.gn.reptile.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * 接口调用工具类
 */
public class HttpRequestUtil {
    private static final String TRIP_TYPE_OW = "1";
    private static CloseableHttpClient httpClient;
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(1000);
        cm.setDefaultMaxPerRoute(200);
        cm.setDefaultMaxPerRoute(500);
        httpClient = HttpClients.custom().setConnectionManager(cm).build();
    }

    public static String get(String url) {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = "";
        try {
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(20000).setConnectionRequestTimeout(20000).setSocketTimeout(20000).build();
            httpGet.setConfig(requestConfig);
            httpGet.setConfig(requestConfig);
            httpGet.addHeader("Content-type", "application/json; charset=utf-8");
            httpGet.setHeader("Accept", "application/json");
            response = httpClient.execute(httpGet);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String post(String url, String jsonString) {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(20000).setConnectionRequestTimeout(200000).setSocketTimeout(200000).
                    setRedirectsEnabled(false).build();
            httpPost.setConfig(requestConfig);
//            httpPost.addHeader("Content-type", "application/x-www-form-urlencoded; charset=utf-8");

            httpPost.addHeader("content-type" ,"application/json;charset=UTF-8");
            httpPost.setHeader("accept", "application/json");
            httpPost.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");

            httpPost.setHeader("accept-language","zh-CN,zh;q=0.9");
            httpPost.addHeader("cookie", "_abtest_userid=0eccc82f-5393-49a0-aac4-bc5c825551d7; _RF1=14.17.102.252; _RSG=bc4oBhav..5m3Ngi94br4B; _RDG=2830fe539f3f0d2380214974f5754ebed7; _RGUID=5fbee64a-cbc8-49a5-b135-94426ca970f0; _ga=GA1.2.226537882.1619154117; _gid=GA1.2.817535352.1619154117; MKT_CKID=1619154117356.zx60s.rtem; MKT_CKID_LMT=1619154117356; MKT_Pagesource=PC; ibulanguage=CN; ibulocale=zh_cn; cookiePricesDisplayed=CNY; Union=OUID=index&AllianceID=4897&SID=155952&SourceID=&createtime=1619161568&Expires=1619766367584; MKT_OrderClick=ASID=4897155952&AID=4897&CSID=155952&OUID=index&CT=1619161567586&CURL=https%3A%2F%2Fwww.ctrip.com%2F%3Fsid%3D155952%26allianceid%3D4897%26ouid%3Dindex&VAL={\"pc_vid\":\"1619154113999.fn2c5\"}; FlightIntl=Search=[%22HKG|%E9%A6%99%E6%B8%AF(HKG)|58|HKG|480%22%2C%22TPE|%E5%8F%B0%E5%8C%97(TPE)|617|TPE|480%22%2C%222021-05-01%22]; _gat=1; _jzqco=%7C%7C%7C%7C1619161567779%7C1.1384027102.1619154117349.1619163970104.1619172333064.1619163970104.1619172333064.undefined.0.0.6.6; __zpspc=9.3.1619172333.1619172333.1%232%7Cwww.baidu.com%7C%7C%7C%25E6%2590%25BA%25E7%25A8%258B%7C%23; appFloatCnt=3; _bfa=1.1619154113999.fn2c5.1.1619161564543.1619172330087.3.9; _bfs=1.2; _bfi=p1%3D10320672927%26p2%3D101023%26v1%3D9%26v2%3D8");

            httpPost.setEntity(new StringEntity(jsonString, Charset.forName("UTF-8")));
            logger.info("-------->{}",jsonString);
            response = httpClient.execute(httpPost);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            result = sb.toString();
            logger.info("请求的结果:{}",result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


}