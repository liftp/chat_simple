// package com.hch.chat_simple.controller;

// import java.io.IOException;
// import java.net.URI;
// import java.net.URISyntaxException;
// import java.security.KeyManagementException;
// import java.security.KeyStoreException;
// import java.security.NoSuchAlgorithmException;
// import java.security.cert.CertificateException;
// import java.security.cert.X509Certificate;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.Enumeration;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Map.Entry;
// import java.util.stream.Collectors;

// import javax.net.ssl.SSLContext;
// import javax.net.ssl.TrustManager;
// import javax.net.ssl.X509TrustManager;

// import org.apache.commons.lang3.StringUtils;
// import org.apache.hc.client5.http.config.ConnectionConfig;
// import org.apache.hc.client5.http.config.RequestConfig;
// import org.apache.hc.client5.http.cookie.StandardCookieSpec;
// import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
// import org.apache.hc.client5.http.impl.classic.HttpClients;
// import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
// import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
// import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
// import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
// import org.apache.hc.core5.http.io.SocketConfig;
// import org.apache.hc.core5.http.ssl.TLS;
// import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
// import org.apache.hc.core5.pool.PoolReusePolicy;
// import org.apache.hc.core5.util.Timeout;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.client.RestTemplate;

// import com.hch.chat_simple.util.Constant;
// import com.hch.chat_simple.util.RedisUtil;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @RestController
// @Tag(name = "代理转发控制类")
// @RequestMapping("/proxy")
// public class ProxyController {

//     @Autowired
//     private PoolingHttpClientConnectionManager connectionManager;

//     private Map<String, String>  accordInstanceChoiceUri() {
//         return RedisUtil.mapGetAll(Constant.INST_WITH_MAP_KEY);
//         // TODO 可以继续优化，检查当前server的ip，不进行转发，直接路由，这里暂不处理
//     }

//     private List<String> toChoiceTargetInstance(String chatType, String groupOrUserId, Map<String, String> instanceWithPos, HttpHeaders headers) {
//         boolean isGroup = "2".equals(chatType);
//         Long idLong = -1L;
//         if (StringUtils.isNotBlank(groupOrUserId)) {
//             idLong = Long.valueOf(groupOrUserId);
//         }
//         if (isGroup) {
//             // 获取群聊的所有人员id
//             List<Long> groupUserIds = new ArrayList<>();
//             Map<String, List<Long>> instanceWithUser = new HashMap<>();
//             int instanceSize = instanceWithPos.size();
//             for (Entry<String,String> entry : instanceWithPos.entrySet()) {
//                 Integer entryPos = Integer.valueOf(entry.getValue());
//                 List<Long> thisInstanceUsers = groupUserIds.stream().filter(e -> entryPos.equals(e.intValue() % instanceSize)).collect(Collectors.toList());
//                 instanceWithUser.put(entry.getKey(), thisInstanceUsers);
//             }
//             // 多个实例的请求转发

            
            
//         } else {
//             // 根据实例映射值，取模，找到对应实例上, 
//             //（这里算法容易有错，实际上，得保证map的values是保证1,2,3,4...依次递增，否则，可能取模后找不到对应的实例）
//             // 对应实例位置保存的时候，实际上得保证，redis的缓存是从1开始的，而且必须所有实例都运行好之后，才能接收请求，否则实例数量不同，模出的值也不一样
//             int pos = (idLong.intValue() % instanceWithPos.size()) + 1;
//             for (Entry<String,String> entry : instanceWithPos.entrySet()) {
//                 if (Integer.valueOf(entry.getValue()).equals(pos)) {
//                     return Arrays.asList(entry.getKey());
//                 }
//             }
//         }
//         return new ArrayList<>();
//     }

//     @Operation(summary = "接口转发")
//     public ResponseEntity<String> handleRequest(HttpServletRequest request) throws IOException, URISyntaxException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

//         String method = request.getMethod();
//         String path = getRequestPath(request);
//         path = path.replace("/proxy", "");
//         log.info("after replace url : {}", path);
//         HttpHeaders headers = getRequestHeaders(request);
//         HttpEntity<?> entity = new HttpEntity<>(headers);

//         // URI根据实例的存储，特殊处理
//         URI trgURL = new URI("" + path);

//         RestTemplate restTemplate = new RestTemplate(getSecureHttpRequestFactory());
//         if (method.equalsIgnoreCase(HttpMethod.GET.name())) {
//             return restTemplate.exchange(trgURL, HttpMethod.GET, entity, String.class);
//         } else if (method.equalsIgnoreCase(HttpMethod.POST.name())) {
//             String requeBody = getRequestBody(request);
//             headers.setContentType(MediaType.APPLICATION_JSON);
//             HttpEntity<String> posEntity = new HttpEntity<>(requeBody, headers);
//             return restTemplate.exchange(trgURL, HttpMethod.POST, posEntity, String.class);
//         } else {
//             return ResponseEntity.badRequest().body("Unsupported request method: " + method);
//         }

//     }

//     private String getRequestPath(HttpServletRequest request) {
//         String ctxPath = request.getContextPath();
//         String servletPath = request.getServletPath();
//         String pathInfo = request.getPathInfo() != null ? request.getPathInfo() : "";
//         return ctxPath + servletPath + pathInfo;
//     }

//     private HttpHeaders getRequestHeaders(HttpServletRequest request) {
//         HttpHeaders headers = new HttpHeaders();
//         Enumeration<String> headerNames = request.getHeaderNames();
//         while (headerNames.hasMoreElements()) {
//             String headerName = headerNames.nextElement();
//             List<String> headerValues = Collections.list(request.getHeaders(headerName));
//             headers.put(headerName, headerValues);
//         }
//         return headers;
//     }

//     private String getRequestBody(HttpServletRequest request) throws IOException {
//         return request.getReader().lines().reduce("", (acc, actual) -> acc + actual);
//     }

//     private HttpComponentsClientHttpRequestFactory getSecureHttpRequestFactory() {

//         CloseableHttpClient closeableHttpClient = HttpClients.custom()
//                     .setConnectionManager(connectionManager)
//                     .setDefaultRequestConfig(RequestConfig.custom()
//                         .setCookieSpec(StandardCookieSpec.STRICT)
//                         .build()
//                     )
//                     .build();

//         HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//         requestFactory.setHttpClient(closeableHttpClient);
            
//         return requestFactory;
//     }


// }
