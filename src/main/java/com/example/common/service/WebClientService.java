package com.example.common.service;

import com.example.common.define.TypeDefine;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebClientService {
    public WebClient getWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create().secure(builder -> builder.sslContext(sslContext))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TypeDefine.TimeOut.CONNECTION_TIMEOUT.getCode())
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(TypeDefine.TimeOut.READ_TIMEOUT.getCode(), TimeUnit.MILLISECONDS)) // Read Timeout
                                .addHandlerLast(new WriteTimeoutHandler(TypeDefine.TimeOut.WRITE_TIMEOUT.getCode(), TimeUnit.MILLISECONDS))) // Write Timeout
                .doOnError((request, e) -> {
                            log.error("Error on request: " + e.getMessage());
                        },
                        (response, e) -> {
                            log.error("Error on response: " + e.getMessage());
                        });

        ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(clientHttpConnector)
                .build();
    }

    public WebClient getWebClient(String baseUrl) throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create().secure(builder -> builder.sslContext(sslContext))
                .baseUrl(baseUrl)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TypeDefine.TimeOut.CONNECTION_TIMEOUT.getCode())
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(TypeDefine.TimeOut.READ_TIMEOUT.getCode(), TimeUnit.MILLISECONDS)) // Read Timeout
                                .addHandlerLast(new WriteTimeoutHandler(TypeDefine.TimeOut.WRITE_TIMEOUT.getCode(), TimeUnit.MILLISECONDS))) // Write Timeout
                .doOnError((request, e) -> {
                            log.error("Error on request: " + e.getMessage());
                        },
                        (response, e) -> {
                            log.error("Error on response: " + e.getMessage());
                        });

        ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(clientHttpConnector)
                .build();
    }
}
