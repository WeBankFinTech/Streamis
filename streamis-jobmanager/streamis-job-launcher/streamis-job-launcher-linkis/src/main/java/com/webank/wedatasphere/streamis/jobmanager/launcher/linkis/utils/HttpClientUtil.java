package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.utils;

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.exception.JobErrorException;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jefftlin
 * @create 2022-12-23
 **/
public class HttpClientUtil {

    private HttpClientUtil() {}

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);


    private static String SPARK_SECRET_PATH = "";

    private static int HTTP_TIMEOUT_IN_MILLISECONDS = 5000;

    private static final int POOL_SIZE = 20;

    private static ThreadPoolExecutor asyncExecutor = RetryUtil.createThreadPoolExecutor();

    private static Properties properties;

    public static void setHttpTimeoutInMillionSeconds(int httpTimeoutInMillionSeconds) {
        HTTP_TIMEOUT_IN_MILLISECONDS = httpTimeoutInMillionSeconds;
    }

    /**
     * Register httpclient by engineType
     *
     * @param prop
     * @return
     */
    public static synchronized CloseableHttpClient createHttpClientUtil(Properties prop) {
        CredentialsProvider provider = new BasicCredentialsProvider();
        if (Objects.nonNull(prop)) {
            provider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(prop.getProperty("auth.key"), prop.getProperty("auth.pass")));
        }

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(HTTP_TIMEOUT_IN_MILLISECONDS)
                .setConnectTimeout(HTTP_TIMEOUT_IN_MILLISECONDS).setConnectionRequestTimeout(HTTP_TIMEOUT_IN_MILLISECONDS)
                .setStaleConnectionCheckEnabled(true).build();

        return HttpClientBuilder.create().setMaxConnTotal(POOL_SIZE).setMaxConnPerRoute(POOL_SIZE)
                .setDefaultRequestConfig(requestConfig).setDefaultCredentialsProvider(provider).build();
    }

    public void destroy(CloseableHttpClient httpClient) {
        destroyApacheHttpClient(httpClient);
    }

    /**
     * Destroy
     *
     */
    public static void destroyApacheHttpClient(CloseableHttpClient httpClient) {
        try {
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static HttpGet getGetRequest() {
        return new HttpGet();
    }

    public static HttpPost getPostRequest(String uri, HttpEntity entity, String... headers) {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        if(headers.length % 2 == 0){
            for(int i = 0; i < headers.length; i += 2) {
                httpPost.addHeader(headers[i], headers[i + 1]);
            }
        }
        return httpPost;
    }

    public static HttpPut getPutRequest() {
        return new HttpPut();
    }

    public static HttpDelete getDeleteRequest() {
        return new HttpDelete();
    }

    /**
     * Synchronous execution
     *
     * @param httpClient
     * @param httpRequestBase
     * @param type
     * @return
     * @param <T>
     * @throws Exception
     */
    public <T>T executeAndGet(CloseableHttpClient httpClient, HttpRequestBase httpRequestBase, Class<T> type) throws IOException {
        return httpClient.execute(httpRequestBase, httpResponse -> {
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                logger.info("Request path: " + httpRequestBase.getURI() + ", method：" + httpRequestBase.getMethod()
                        + ",STATUS CODE = " + httpResponse.getStatusLine().getStatusCode());
                httpRequestBase.abort();
                throw new RuntimeException("Response Status Code : " + httpResponse.getStatusLine().getStatusCode());
            } else {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    String entityString =  EntityUtils.toString(entity, Consts.UTF_8);
                    if(type.equals(String.class)){
                        return (T)entityString;
                    }
                    return GsonUtil.fromJson(entityString, type);
                } else {
                    throw new RuntimeException("Response Entity Is Null");
                }
            }
        });
    }

    /**
     * Asynchronous execution
     *
     * @param httpClient
     * @param httpRequestBase
     * @param type
     * @param retryTimes
     * @param retryInterval
     * @return
     * @param <T>
     */
    public <T>T executeAndGetWithRetry(CloseableHttpClient httpClient, final HttpRequestBase httpRequestBase,
                                       Class<T> type, final int retryTimes, final long retryInterval) throws JobErrorException {
        try {
            return RetryUtil.asyncExecuteWithRetry(() -> executeAndGet(httpClient, httpRequestBase, type),
                    retryTimes, retryInterval, true, HTTP_TIMEOUT_IN_MILLISECONDS + 1000L, asyncExecutor);
        } catch (Exception e) {
            throw new JobErrorException(-1,e.getMessage());
        }
    }

    public static synchronized Properties getSecurityProperties() throws JobErrorException {
        if (properties == null) {
            InputStream secretStream = null;
            try {
                secretStream = new FileInputStream(SPARK_SECRET_PATH);
            } catch (FileNotFoundException e) {
                throw new JobErrorException(-1,"Spark配置要求加解密，但无法找到密钥的配置文件");
            }

            properties = new Properties();
            try {
                properties.load(secretStream);
                secretStream.close();
            } catch (IOException e) {
                throw new JobErrorException(-1,"读取加解密配置文件出错");
            }
        }

        return properties;
    }
}
