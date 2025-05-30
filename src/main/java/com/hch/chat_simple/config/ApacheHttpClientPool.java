// package com.hch.chat_simple.config;

// import java.security.KeyManagementException;
// import java.security.NoSuchAlgorithmException;
// import java.security.cert.CertificateException;
// import java.security.cert.X509Certificate;

// import javax.net.ssl.SSLContext;
// import javax.net.ssl.TrustManager;
// import javax.net.ssl.X509TrustManager;

// import org.apache.hc.client5.http.config.ConnectionConfig;
// import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
// import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
// import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
// import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
// import org.apache.hc.core5.http.io.SocketConfig;
// import org.apache.hc.core5.http.ssl.TLS;
// import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
// import org.apache.hc.core5.pool.PoolReusePolicy;
// import org.apache.hc.core5.util.Timeout;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class ApacheHttpClientPool {
    
//     /**
//      * TODO 可以将参数改为可配置的
//      * @return
//      * @throws NoSuchAlgorithmException
//      * @throws KeyManagementException
//      */
//     @Bean
//     public PoolingHttpClientConnectionManager createHttpPoolManager() throws NoSuchAlgorithmException, KeyManagementException {
//         TrustManager[] trustAllCerts = new TrustManager[] {
//             new X509TrustManager() {

//                 @Override
//                 public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

//                 }

//                 @Override
//                 public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

//                 }

//                 @Override
//                 public X509Certificate[] getAcceptedIssuers() {
//                     return null;
//                 }
                
//             }
//         };
//         SSLContext sslContext = SSLContext.getInstance("TLS");
//         sslContext.init(null, trustAllCerts, null);
//         return PoolingHttpClientConnectionManagerBuilder.create()
//             .setTlsSocketStrategy((TlsSocketStrategy) ClientTlsStrategyBuilder.create()
//                 .setSslContext(sslContext)
//                 .setTlsVersions(TLS.V_1_3)
//                 .buildClassic()
//             )
//             .setDefaultSocketConfig(SocketConfig.custom()
//                 .setSoTimeout(Timeout.ofSeconds(30))
//                 .build()
//             )
//             .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
//             .setConnPoolPolicy(PoolReusePolicy.LIFO)
//             .setDefaultConnectionConfig(ConnectionConfig.custom()
//                 .setSocketTimeout(Timeout.ofSeconds(30))
//                 .setConnectTimeout(Timeout.ofSeconds(30))
//                 .setTimeToLive(Timeout.ofSeconds(300))
//                 .build()
//             ).build();
//     }
// }
