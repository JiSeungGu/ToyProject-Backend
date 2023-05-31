package com.example.common.fabric.sdk.event;

import com.example.common.entity.FabTransation;
import com.example.common.fabric.sdk.client.ChannelClient;
import com.example.common.fabric.sdk.client.FabricClient;
import com.example.common.fabric.sdk.user.MakeFabricObject;
import com.example.common.fabric.sdk.user.UserContext;
import com.example.common.fabric.sdk.util.FabConfBean;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.BlockListener;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.Iterator;

@Slf4j
public class EventCapture extends Thread {

  private FabConfBean fabConfBean = FabConfBean.getInstance();

  @Override
  public void run() {
    try {
      listenEvent();
    } catch (Exception e) {
    }
  }

  public void listenEvent() throws ProposalException, InvalidArgumentException {

    String handler ="";
    MakeFabricObject makeObject = new MakeFabricObject();
    Channel channel = null;
    try {
      UserContext org1Admin = makeObject.getUserContext("org1", "Org1MSP");
      FabricClient fabClient = new FabricClient(org1Admin);
      Peer peer0_org1 = makeObject.getPeer(fabClient, "peer0org1");
      ChannelClient channelClient = fabClient.createChannelClient("mychannel");
      channel = channelClient.getChannel();
      channel.addPeer(peer0_org1);
      channel.initialize();

      boolean first = true;
      while (true) {
        if (first) {
          handler = listenBlockEvent(channel, 1);
        }
        first = false;
        Thread.sleep(1000 * 3600);
      }
    } catch (Exception e) {
      log.error("ERR:" + e);
    } finally {
      channel.unregisterBlockListener(handler);
      channel.shutdown(true);
    }
  }

  private String listenBlockEvent(Channel channel, int index) throws Exception {

    //ObjectMapper objectMapper = new ObjectMapper();

    BlockListener listener =
        new BlockListener() {
          @Override
          public void received(BlockEvent blockEvent) {
            Iterator<TransactionEvent> transactionEvents =
                blockEvent.getTransactionEvents().iterator();
            while (transactionEvents.hasNext()) {
              TransactionEvent transactionEvent = transactionEvents.next();
              String txid = transactionEvent.getTransactionID();
              //log.info("EVENT:" + index +":"+ txid);
              int validcd = transactionEvent.getValidationCode();
              FabTransation fabTransation = (FabTransation) fabConfBean.getMapObject(txid);
              if (fabTransation != null) {
                fabTransation.updVaildCd(validcd, blockEvent.getBlockNumber());
                fabConfBean.putMapObject(txid, fabTransation);
              }
            }
          }
        };
    String hanndler = channel.registerBlockListener(listener);

    return hanndler;
  }
}
