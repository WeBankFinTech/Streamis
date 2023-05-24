package com.webank.wedatasphere.streamis.jobmanager.utils;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.webank.wedatasphere.streamis.jobmanager.model.ResponseWithHeaders;
import com.webank.wedatasphere.streamis.jobmanager.vo.LinkisResponse;
import com.webank.wedatasphere.streamis.jobmanager.vo.LinkisResponseData;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author jefftlin
 * @create 2022-12-23
 **/
public class HttpClientUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static PoolingHttpClientConnectionManager cm = null;
    private static RequestConfig requestConfig = null;

    public static final CommonVars<String> LINKIS_GATEWAY_URL = CommonVars.apply("wds.dss.gateway.url", "");


    static {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .build();
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(300);
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(1000 * 60 * 30)
                .setConnectTimeout(1000 * 10)
                .setExpectContinueEnabled(true)
                .setConnectionRequestTimeout(1000 * 60 * 30)
                .build();
    }


    public static CloseableHttpClient getHttpClient(String token) {
        CloseableHttpClient httpClient = null;
        if (token == null) {
            httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .build();
        } else {
            CookieStore cookieStore = new BasicCookieStore();
            BasicClientCookie cookie = new BasicClientCookie("", token);
            cookieStore.addCookie(cookie);
            httpClient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .setConnectionManager(cm)
                    .build();

        }
        return httpClient;
    }

    public static void closeResponse(CloseableHttpResponse closeableHttpResponse) throws IOException {
        EntityUtils.consume(closeableHttpResponse.getEntity());
        closeableHttpResponse.close();
    }


    public static ResponseWithHeaders get(Map<String, String> headers, String url, String params, String token) throws IOException {

        CloseableHttpClient httpClient = getHttpClient(null);
        CloseableHttpResponse closeableHttpResponse = null;
        // 创建get请求
        HttpGet httpGet = null;
        if (params != null) {
            httpGet = new HttpGet(url + "?" + params);
        } else {
            httpGet = new HttpGet(url);
        }
        if (headers != null) {
            Iterator iterator = headers.keySet().iterator();
            while (iterator.hasNext()) {
                String headerName = iterator.next().toString();
                httpGet.addHeader(headerName, headers.get(headerName));
            }
        }
        httpGet.setConfig(requestConfig);
        httpGet.addHeader("Content-Type", "application/json");
        httpGet.addHeader("Cookie", token);
        closeableHttpResponse = httpClient.execute(httpGet);
        HttpEntity entity = closeableHttpResponse.getEntity();
        Header[] allHeaders = closeableHttpResponse.getAllHeaders();
        String response = EntityUtils.toString(entity, "UTF-8");
        closeResponse(closeableHttpResponse);
        // admin_check接口限制
        if (closeableHttpResponse.getStatusLine().getStatusCode() == 403 &&
                closeableHttpResponse.getStatusLine().getReasonPhrase().equalsIgnoreCase("forbidden")) {
            return new ResponseWithHeaders(response, allHeaders);
        }
        if (closeableHttpResponse.getStatusLine().getStatusCode() != 200 &&
                closeableHttpResponse.getStatusLine().getStatusCode() != 202) {
            logger.error("Execute Streamis GET,headers is : {},url is : {},params is : {},response is : {}",
                    closeableHttpResponse.getAllHeaders(), httpGet.getURI(), params, response);
            throw new IOException(response);
        }
        return new ResponseWithHeaders(response, allHeaders);
    }

    public static String getLinkisTicketId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = null;
        for (Cookie cookie : cookies) {
            //优先使用proxyCookie，自测是发现用不了。无法通过Linkis登录验证
//            if (cookie.getName().equalsIgnoreCase("linkis_user_session_proxy_ticket_id_v1")) {
//                token="linkis_user_session_proxy_ticket_id_v1" + "=" + cookie.getValue();
//                continue;
//            }
            if (cookie.getName().equalsIgnoreCase("linkis_user_session_ticket_id_v1")) {
                token ="linkis_user_session_ticket_id_v1" + "=" + cookie.getValue();
            }
        }
        return token;
    }

    public static boolean checkSystemAdmin(String token){
        String linkisUrl= LINKIS_GATEWAY_URL.getValue()+ "/api/rest_j/v1/jobhistory/governanceStationAdmin";
        Gson gson = BDPJettyServerHelper.gson();
        boolean flag =false;
        try {
            String responseStr = get(null, linkisUrl, null, token).getResponseStr();
            LinkisResponseData linkisResponseData = gson.fromJson(responseStr, LinkisResponse.class).getData();
            flag =linkisResponseData.getAdmin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return flag;
    }
}
