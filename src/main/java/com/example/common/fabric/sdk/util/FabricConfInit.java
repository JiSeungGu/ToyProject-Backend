//package com.example.common.fabric.sdk.util;
//
//import com.example.common.fabric.sdk.event.EventCapture;
//import com.example.common.fabric.sdk.pool.RevObjectPool;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.MessageSource;
//import org.springframework.context.i18n.LocaleContextHolder;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
//@Slf4j
//@RequiredArgsConstructor
//public @Component class FabricConfInit {
//
//  private final MessageSource messageSource;
//
//  @PostConstruct
//  public void init() {
//
//    try {
//
//      log.info("=====FAB CONF INIT START=====");
//      log.info("FABRICTYPE:" + getMessage("fabric.type"));
//
//      FabConfBean FCB = FabConfBean.getInstance();
//      String cryptodir = getMessage("fabric.cryptodir");
//      FCB.putMap("cryptodir", cryptodir);
//      FCB.putMap("fabrictype", getMessage("fabric.type"));
//
//      // ORDERER
//      FCB.putMap("orderer0crt", cryptodir + getMessage("fabric.orderer.orderer0.crt"));
//      FCB.putMap("orderer0name", getMessage("fabric.orderer.orderer0.name"));
//      FCB.putMap("orderer0grpc", getMessage("fabric.orderer.orderer0.grpc"));
//      FCB.putMap("orderer1crt", cryptodir + getMessage("fabric.orderer.orderer1.crt"));
//      FCB.putMap("orderer1name", getMessage("fabric.orderer.orderer1.name"));
//      FCB.putMap("orderer1grpc", getMessage("fabric.orderer.orderer1.grpc"));
//      FCB.putMap("orderer2crt", cryptodir + getMessage("fabric.orderer.orderer2.crt"));
//      FCB.putMap("orderer2name", getMessage("fabric.orderer.orderer2.name"));
//      FCB.putMap("orderer2grpc", getMessage("fabric.orderer.orderer2.grpc"));
//
//      // ORG
//      FCB.putMap("org1", getMessage("fabric.org.org1.name"));
//      FCB.putMap("org1Msp", getMessage("fabric.org.org1.mspname"));
//      FCB.putMap("org1name", cryptodir + getMessage("fabric.org.org1.name"));
//      FCB.putMap("org1mspname", cryptodir + getMessage("fabric.org.org1.mspname"));
//      FCB.putMap("org1msp", cryptodir + getMessage("fabric.org.org1.msp"));
//
//      FCB.putMap("org2", getMessage("fabric.org.org2.name"));
//      FCB.putMap("org2Msp", getMessage("fabric.org.org2.mspname"));
//      FCB.putMap("org2name", cryptodir + getMessage("fabric.org.org2.name"));
//      FCB.putMap("org2mspname", cryptodir + getMessage("fabric.org.org2.mspname"));
//      FCB.putMap("org2msp", cryptodir + getMessage("fabric.org.org2.msp"));
//
//      FCB.putMap("org3", getMessage("fabric.org.org3.name"));
//      FCB.putMap("org3Msp", getMessage("fabric.org.org3.mspname"));
//      FCB.putMap("org3name", cryptodir + getMessage("fabric.org.org3.name"));
//      FCB.putMap("org3mspname", cryptodir + getMessage("fabric.org.org3.mspname"));
//      FCB.putMap("org3msp", cryptodir + getMessage("fabric.org.org3.msp"));
//
//      FCB.putMap("org4", getMessage("fabric.org.org4.name"));
//      FCB.putMap("org4Msp", getMessage("fabric.org.org4.mspname"));
//      FCB.putMap("org4name", cryptodir + getMessage("fabric.org.org4.name"));
//      FCB.putMap("org4mspname", cryptodir + getMessage("fabric.org.org4.mspname"));
//      FCB.putMap("org4msp", cryptodir + getMessage("fabric.org.org4.msp"));
//
//      FCB.putMap("org5", getMessage("fabric.org.org5.name"));
//      FCB.putMap("org5Msp", getMessage("fabric.org.org5.mspname"));
//      FCB.putMap("org5name", cryptodir + getMessage("fabric.org.org5.name"));
//      FCB.putMap("org5mspname", cryptodir + getMessage("fabric.org.org5.mspname"));
//      FCB.putMap("org5msp", cryptodir + getMessage("fabric.org.org5.msp"));
//
//      // PEER
//      FCB.putMap("peer0org1crt", cryptodir + getMessage("fabric.peer.peer0org1.crt"));
//      FCB.putMap("peer0org1name", getMessage("fabric.peer.peer0org1.name"));
//      FCB.putMap("peer0org1grpc", getMessage("fabric.peer.peer0org1.grpc"));
//      FCB.putMap("peer0org2crt", cryptodir + getMessage("fabric.peer.peer0org2.crt"));
//      FCB.putMap("peer0org2name", getMessage("fabric.peer.peer0org2.name"));
//      FCB.putMap("peer0org2grpc", getMessage("fabric.peer.peer0org2.grpc"));
//      FCB.putMap("peer0org3crt", cryptodir + getMessage("fabric.peer.peer0org3.crt"));
//      FCB.putMap("peer0org3name", getMessage("fabric.peer.peer0org3.name"));
//      FCB.putMap("peer0org3grpc", getMessage("fabric.peer.peer0org3.grpc"));
//      FCB.putMap("peer0org4crt", cryptodir + getMessage("fabric.peer.peer0org4.crt"));
//      FCB.putMap("peer0org4name", getMessage("fabric.peer.peer0org4.name"));
//      FCB.putMap("peer0org4grpc", getMessage("fabric.peer.peer0org4.grpc"));
//      FCB.putMap("peer0org5crt", cryptodir + getMessage("fabric.peer.peer0org5.crt"));
//      FCB.putMap("peer0org5name", getMessage("fabric.peer.peer0org5.name"));
//      FCB.putMap("peer0org5grpc", getMessage("fabric.peer.peer0org5.grpc"));
//
//      RevObjectPool ROP = RevObjectPool.getInstance();
//      ROP.initObject();
//
//      EventCapture eventCapture = new EventCapture();
//      eventCapture.start();
//
//      log.info("=====FAB CONF INIT END=====");
//
//    } catch (Exception e) {
//      System.out.println("Fabric Config Init Error:" + e);
//    }
//  }
//
//  private String getMessage(String code) {
//    return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
//  }
//}
