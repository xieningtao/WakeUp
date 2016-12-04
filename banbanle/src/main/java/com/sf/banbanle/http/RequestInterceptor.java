package com.sf.banbanle.http;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Created by mac on 16/12/4.
 */

public class RequestInterceptor implements HttpRequestInterceptor {
    @Override
    public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
        httpRequest.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        httpRequest.addHeader("User-Agent", "BCCS_SDK/3.0 (Darwin; Darwin Kernel Version 14.0.0: Fri Sep 19 00:26:44 PDT 2014; root:xnu-2782.1.97~2/RELEASE_X86_64; x86_64) PHP/5.6.3 (Baidu Push Server SDK V3.0.0 and so on..) cli/Unknown ZEND/2.6.0");
    }
}
