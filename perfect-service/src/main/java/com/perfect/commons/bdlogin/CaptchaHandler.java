package com.perfect.commons.bdlogin;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by baizz on 2014-11-8.
 * refactor 2015-1-7
 */
@Component("captchaHandler")
public class CaptchaHandler extends AbstractBaiduHttpClient {

    private static final ObjectMapper mapper = new ObjectMapper();

    private byte[] captchaBytes;
    private StringJoiner _cookies;
    private String exceptionMsg;


    public void handle(String phantomJSPath) throws IOException {
        clearCookie();
        exceptionMsg = null;
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("phantomjs " + phantomJSPath);
        JsonNode jsonNode;
        try (InputStream is = process.getInputStream()) {
            jsonNode = mapper.readTree(is);
        } catch (JsonParseException e) {
            // com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'HTTP': was expecting ('true', 'false' or 'null')
            // at [Source: java.lang.UNIXProcess$ProcessPipeInputStream@1e471884; line: 1, column: 6]
            e.printStackTrace();
            exceptionMsg = "com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'HTTP': was expecting ('true', 'false' or 'null')\n at [Source: java.lang.UNIXProcess$ProcessPipeInputStream@1e471884; line: 1, column: 6]";
            return;
        }
        try {
            Objects.requireNonNull(jsonNode);
        } catch (NullPointerException e) {
            e.printStackTrace();
            exceptionMsg = "java.lang.NullPointerException: no usable captcha cookies.";
            return;
        }

        List<String> cookieList = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            cookieList.add(node.get("name").asText() + "=" + node.get("value").asText() + "; " + "path=" + node.get("path").asText() + "; domain=" + jsonNode.get("domain").asText() + "; " + "httponly");
        }
        for (String strCookie : cookieList) {
            _cookies.add(strCookie.substring(0, strCookie.indexOf(";")));
        }

        HttpGet captchaRequest = new HttpGet(CAPTCHA_URL + System.currentTimeMillis());
        BaiduHttpClient.headerWrap(captchaRequest);
        captchaRequest.addHeader("Cookie", _cookies.toString());

        try (CloseableHttpResponse response = httpClient.execute(captchaRequest)) {
            if (response.getHeaders("Set-Cookie") != null) {
                String tmpStr = response.getHeaders("Set-Cookie")[0].getValue();
                tmpStr = tmpStr.substring(0, tmpStr.indexOf(";"));
                _cookies.add(tmpStr);

                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                captchaBytes = IOUtils.toByteArray(content);
                FileUtils.writeByteArrayToFile(new File("/home/baizz/captcha.jpeg"), captchaBytes);
                EntityUtils.consume(entity);
            }
            exceptionMsg = "java.lang.NullPointerException: no usable captcha cookies.";
        }
    }

    public byte[] getCaptchaBytes() {
        return captchaBytes;
    }

    public String getCaptchaCookies() {
        return _cookies.toString();
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    private void clearCookie() {
        captchaBytes = new byte[0];
        _cookies = new StringJoiner(";");
    }
}
