package com.example.common.fabric.sdk.user;

import com.example.common.fabric.sdk.client.FabricClient;
import com.example.common.fabric.sdk.util.FabConfBean;
import com.example.common.fabric.sdk.util.FabricUtil;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class MakeFabricObject {

  private FabConfBean FCB = null;

  public MakeFabricObject() {
    FCB = FabConfBean.getInstance();
  }

  public UserContext getUserContext(String orgName, String MspID) {
    UserContext orgUser = null;
    try {
      String ADMINPK = FCB.getMap(orgName + "msp");
      String ADMINCERT = FCB.getMap(orgName + "msp");

      Enrollment enrollAdmin =
          FabricUtil.getEnrollment(
              ADMINPK, "keystore/priv_sk", ADMINCERT, "signcerts/cert.pem"); // 인증서
      orgUser = new UserContext(orgName, MspID, enrollAdmin);
    } catch (Exception e) {
      System.out.println("UserContext Make Err:" + e.toString());
    }

    return orgUser;
  }

  public Orderer getOrderer(FabricClient fabClient, int ordererNo) {

    Orderer orderer = null;

    Properties ordererProperties = new Properties();
    ordererProperties.setProperty("pemFile", FCB.getMap("orderer" + ordererNo + "crt"));
    ordererProperties.setProperty("trustServerCertificate", "true");
    ordererProperties.setProperty("hostnameOverride", FCB.getMap("orderer" + ordererNo + "name"));
    ordererProperties.setProperty("sslProvider", "openSSL");
    ordererProperties.setProperty("negotiationType", "TLS");
    ordererProperties.put("allowAllHostNames", "true");

    try {
      orderer =
          fabClient
              .getInstance()
              .newOrderer(
                  FCB.getMap("orderer" + ordererNo + "name"),
                  FCB.getMap("orderer" + ordererNo + "grpc"),
                  ordererProperties);

    } catch (Exception e) {
      log.error("getOrderer ERR:" + e.toString());
    }

    return orderer;
  }

  public Peer getPeer(FabricClient fabClient, String peerName) {

    Peer peer = null;

    Properties peerProperties = new Properties();
    peerProperties.setProperty("pemFile", FCB.getMap(peerName + "crt"));
    peerProperties.setProperty("trustServerCertificate", "true");
    peerProperties.setProperty("hostnameOverride", FCB.getMap(peerName + "name"));
    peerProperties.setProperty("sslProvider", "openSSL");
    peerProperties.setProperty("negotiationType", "TLS");
    peerProperties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);
    peerProperties.put("allowAllHostNames", "true");
    try {
      peer =
          fabClient
              .getInstance()
              .newPeer(
                  FCB.getMap(peerName + "name"), FCB.getMap(peerName + "grpc"), peerProperties);
    } catch (Exception e) {
      log.error("getPeer ERR:" + peerName + ":" + e.toString());
    }

    return peer;
  }

  public TransactionProposalRequest getChainCodeRequest(
      FabricClient fabClient, String codenm, String funcName, String[] arguments) {

    TransactionProposalRequest request = null;
    try {

      request = fabClient.getInstance().newTransactionProposalRequest();
      ChaincodeID ccid = ChaincodeID.newBuilder().setName(codenm).build();
      request.setChaincodeID(ccid);
      request.setFcn(funcName);
      request.setArgs(arguments);
      request.setProposalWaitTime(10000);

      Map<String, byte[]> reqMap = new HashMap<>();
      reqMap.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
      reqMap.put("method", "TransactionProposalRequest".getBytes(UTF_8));
      reqMap.put("result", ":)".getBytes(UTF_8));

      // byte[] EXPECTED_EVENT_DATA = "!oxoxoxo...".getBytes(UTF_8);
      // String EXPECTED_EVENT_NAME = "eventX";
      // reqMap.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA);

      request.setTransientMap(reqMap);

    } catch (Exception e) {
      log.error("getChainCodeRequest ERR:" + e.toString());
    }
    return request;
  }
}
