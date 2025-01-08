package com.hch.chat_simple.config;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hch.chat_simple.auth.NoAuth;
import com.hch.chat_simple.pojo.dto.TokenInfoDTO;
import com.hch.chat_simple.util.ContextUtil;
import com.hch.chat_simple.util.Payload;
import com.hch.chat_simple.util.TokenUtil;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.getBean() instanceof BasicErrorController) {
                return true;
            }

            NoAuth noAuth = handlerMethod.getMethod().getAnnotation(NoAuth.class);
            if (noAuth != null) {
                log.info("未拦截请求：{},进行访问", noAuth.description());
                return true;
            } else {
                String token = request.getHeader("token");

                if (StringUtils.isNotBlank(token)) {
                    DecodedJWT decodeJwt = null;
                    if ((decodeJwt = TokenUtil.verigyToken(token)) != null) {
                        // 根据token 设置上线文用户信息, 其他信息可以从request.getCookies()设置
                        String subject = decodeJwt.getSubject();
                        TokenInfoDTO tokenInfo = JSON.parseObject(subject, TokenInfoDTO.class);
                        ContextUtil.setUserId(tokenInfo.getUserId());
                        ContextUtil.setUsername(tokenInfo.getUsername());
                        ContextUtil.setRealName(tokenInfo.getRealName());
                        return true;
                    } else {

                        // todo response set error info
                        Payload<String> result = Payload.of("token验证失败", 505, "token验证失败");
                        repsonseData(response, JSON.toJSONString(result));
                    }

                } else {
                    Payload<String> result = Payload.of("token不存在", 506, "token不存在");
                    repsonseData(response, JSON.toJSONString(result));
                }
            }
        } else {
            System.out.println(handler);
            // 非 HandlerMethod 类型放行
            return true;
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 上下文清除
        ContextUtil.clear();
    }

    private void repsonseData(HttpServletResponse response, String msg) throws IOException {
        // response.setStatus(statusCode);
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        out.println(msg);
        out.flush();
    }
    
}
