package io.springboot.twitter.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.util.List;

public class WebUtils {
    private WebUtils() {}

    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Requested-For");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String throwableStackTrace(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        throwable.printStackTrace(new PrintWriter(stringBuilderWriter));
        return stringBuilderWriter.toString();
    }


    public static MediaType acceptMediaType(HttpServletRequest request) {

        MediaType mediaType = null;

        List<MediaType> mediaTypes = MediaType.parseMediaTypes(request.getHeader(HttpHeaders.ACCEPT));

        if (mediaTypes.isEmpty()) {
            mediaType = MediaType.ALL;
        } else {
            mediaType = mediaTypes.get(0); // 默认第一个，不计算期望度
        }
        return mediaType;
    }
}
