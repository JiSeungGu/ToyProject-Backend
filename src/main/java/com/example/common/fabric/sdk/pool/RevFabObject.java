package com.example.common.fabric.sdk.pool;

import com.example.common.fabric.sdk.client.ChannelClient;
import com.example.common.fabric.sdk.client.FabricClient;
import com.example.common.fabric.sdk.user.MakeFabricObject;
import com.example.common.fabric.sdk.user.UserContext;
import com.example.common.fabric.sdk.util.FabConfBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;

@Slf4j
@Setter
@Getter
public class RevFabObject {

  private boolean isInit;
  private Channel channel;
  private FabricClient fabClient;
  private UserContext org1Admin;
  private ChannelClient channelClient;
  private Orderer orderer0;
  private Orderer orderer1;
  private Orderer orderer2;
  private Peer peer0_org1;
  private Peer peer0_org2;
  private Peer peer0_org3;
  //private Peer peer0_org4;
  private double TStartTime;
  private FabConfBean FCB = null;


  public void setTStartTime(double tStartTime) {
    TStartTime = tStartTime;
  }

  public RevFabObject(boolean isInit, int objectnum) {
    FCB = FabConfBean.getInstance();
    this.isInit = isInit;

    try {
      MakeFabricObject makeObject = new MakeFabricObject();

      org1Admin = makeObject.getUserContext("org1", "Org1" + "MSP");

      fabClient = new FabricClient(org1Admin);

      orderer0 = makeObject.getOrderer(fabClient, 0);
      //orderer1 = makeObject.getOrderer(fabClient, 1);
      //orderer2 = makeObject.getOrderer(fabClient, 2);

      String fabrictype = FCB.getMap("fabrictype").toString();
      if(fabrictype.equals("real")) {
        peer0_org1 = makeObject.getPeer(fabClient, "peer0org1");
        peer0_org2 = makeObject.getPeer(fabClient, "peer0org2");
      } else {
        peer0_org1 = makeObject.getPeer(fabClient, "peer0org1");
      }

      channelClient = fabClient.createChannelClient("mychannel");
      channel = channelClient.getChannel();
      channel.addOrderer(orderer0);
      //channel.addOrderer(orderer1);
      //channel.addOrderer(orderer2);

      if(fabrictype.equals("real")) {
        channel.addPeer(peer0_org1);
        channel.addPeer(peer0_org2);
      } else {
        channel.addPeer(peer0_org1);
      }
      channel.initialize();
    } catch (Exception e) {
      log.error("INIT ERR:" + e);
    }
  }

  public void shutdown() {
    this.channel.shutdown(true);
  }

  public boolean isShutdown() {
    return this.channel.isShutdown();
  }
}
