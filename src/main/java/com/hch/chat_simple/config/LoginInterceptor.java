package com.hch.chat_simple.config;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hch.chat_simple.auth.NoAuth;
import com.hch.chat_simple.pojo.dto.TokenInfoDTO;
import com.hch.chat_simple.util.ContextUtil;
import com.hch.chat_simple.util.Payload;
import com.hch.chat_simple.util.StatusCodeEnum;
import com.hch.chat_simple.util.TokenUtil;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 双Token认证拦截器
 * AccessToken: 短期令牌(30分钟)，存储在Redis中，用于接口访问鉴权
 * RefreshToken: 长期令牌(7天)，JWT存放在浏览器端，用于刷新AccessToken
 */
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
            }

            // ========== 双Token认证机制 ==========
            String accessToken = request.getHeader("accessToken");
            String refreshToken = request.getHeader("refreshToken");

            // 1. 优先验证AccessToken（Redis中查询）
            if (StringUtils.isNotBlank(accessToken)) {
                TokenInfoDTO tokenInfo = TokenUtil.getAccessTokenInfo(accessToken);
                if (tokenInfo != null) {
                    // AccessToken有效，设置上下文用户信息
                    ContextUtil.setUserId(tokenInfo.getUserId());
                    ContextUtil.setUsername(tokenInfo.getUsername());
                    ContextUtil.setRealName(tokenInfo.getRealName());
                    return true;
                }

                // AccessToken已过期(不在Redis中)，尝试使用RefreshToken刷新
                log.info("AccessToken已过期，尝试使用RefreshToken刷新");
                if (StringUtils.isNotBlank(refreshToken)) {
                    return handleRefreshToken(refreshToken, response);
                } else {
                    // 没有提供RefreshToken，返回AccessToken过期提示
                    Payload<String> result = Payload.of("accessToken已过期，请提供refreshToken进行刷新", 
                        StatusCodeEnum.TOKEN_EXPIRE.getCode(), StatusCodeEnum.TOKEN_EXPIRE.getDesc());
                    responseData(response, JSON.toJSONString(result));
                    return false;
                }
            }

            // 2. 没有AccessToken，但有RefreshToken（可能是AccessToken丢失）
            if (StringUtils.isNotBlank(refreshToken)) {
                return handleRefreshToken(refreshToken, response);
            }

            // 3. 两个Token都没有
            Payload<String> result = Payload.of("token缺失", StatusCodeEnum.TOKEN_LACK);
            responseData(response, JSON.toJSONString(result));
            return false;

        } else {
            // 非HandlerMethod类型放行（如静态资源等）
            return true;
        }
    }

    /**
     * 处理RefreshToken刷新AccessToken的逻辑
     * @param refreshToken RefreshToken字符串
     * @param response HttpServletResponse
     * @return 是否放行
     * @throws Exception 异常
     */
    private boolean handleRefreshToken(String refreshToken, HttpServletResponse response) throws Exception {
        TokenInfoDTO tokenInfo = TokenUtil.parseRefreshTokenInfo(refreshToken);
        if (tokenInfo != null) {
            // RefreshToken有效，创建新的AccessToken
            String newAccessToken = TokenUtil.createAccessToken();
            TokenInfoDTO newTokenInfo = TokenInfoDTO.builder()
                    .username(tokenInfo.getUsername())
                    .userId(tokenInfo.getUserId())
                    .realName(tokenInfo.getRealName())
                    .build();

            // 将新的AccessToken存入Redis
            TokenUtil.storeAccessToken(newAccessToken, newTokenInfo);

            // 设置上下文用户信息
            ContextUtil.setUserId(tokenInfo.getUserId());
            ContextUtil.setUsername(tokenInfo.getUsername());
            ContextUtil.setRealName(tokenInfo.getRealName());
            // 将新的AccessToken放入上下文，由ExceptionAspectHandler返回给客户端
            ContextUtil.setNewAccessToken(newAccessToken);

            log.info("AccessToken已刷新，userId={}", tokenInfo.getUserId());
            // 抛出TokenExpiredException，由全局异常处理器返回新的AccessToken
            throw new TokenExpiredException("accessToken已过期，已通过refreshToken刷新", null);
        } else {
            // RefreshToken也无效或已过期，需要重新登录
            Payload<String> result = Payload.of("refreshToken已过期，请重新登录", 
                StatusCodeEnum.REFRESH_TOKEN_EXPIRE.getCode(), StatusCodeEnum.REFRESH_TOKEN_EXPIRE.getDesc());
            responseData(response, JSON.toJSONString(result));
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 上下文清除
        ContextUtil.clear();
    }

    private void responseData(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println(msg);
        out.flush();
    }
}
