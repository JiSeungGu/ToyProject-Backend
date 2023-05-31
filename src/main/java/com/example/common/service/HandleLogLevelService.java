package com.example.common.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.example.common.response.CommonResult;
import lombok.RequiredArgsConstructor;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HandleLogLevelService {

    private final ResponseService responseService;

    public CommonResult setLogLevel(String loggerLevel) {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        rootLogger.setLevel(Level.toLevel(loggerLevel));

        (rootLogger.getAppender("CONSOLE")).start();
        (rootLogger.getAppender("FILE")).start();

        String returnMsg = "changed logger level : " + loggerLevel;

        return responseService.getSingleResult(returnMsg);
    }

    public CommonResult getLogLevel() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        String loggerLevel = rootLogger.getLevel().toString();

        String returnMsg = "current logger level : " + loggerLevel;

        return responseService.getSingleResult(returnMsg);
    }
}
