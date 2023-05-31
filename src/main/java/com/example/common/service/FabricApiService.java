package com.example.common.service;

import com.example.common.dto.FabQueryDto;
import com.example.common.entity.FabQuery;
import com.example.common.entity.FabTransation;
import com.example.common.fabric.sdk.client.ChannelClient;
import com.example.common.fabric.sdk.client.FabricClient;
import com.example.common.fabric.sdk.pool.RevFabObject;
import com.example.common.fabric.sdk.pool.RevObjectPool;
import com.example.common.fabric.sdk.user.MakeFabricObject;
import com.example.common.fabric.sdk.util.FabConfBean;
import com.example.common.mapper.FabQueryMapper;
import com.example.common.response.CommonResult;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class FabricApiService {

  private final ResponseService responseService;
  private FabConfBean fabConfBean = FabConfBean.getInstance();
  private final ApplicationEventPublisher publisher;

  // 트랜잭션 INVOKE
  public HashMap<Object, Object> invoke(FabQueryDto fabQueryDto) {

    //StopWatch stopWatch = new StopWatch("INVOKE 속도 체크");
    //stopWatch.start("Endorsing");

    FabQuery fabQuery = FabQueryMapper.INSTANCE.fabQueryDtoToEntity(fabQueryDto);

    HashMap<Object, Object> resultMap = new HashMap<Object, Object>();
    resultMap.put("result", true);

    String[] args = fabQuery.getParams().split(",");
    RevObjectPool ROP = RevObjectPool.getInstance();
    RevFabObject revFabObject = ROP.getObject();
    resultMap.put("validmsg", "VALID");

    if (revFabObject == null) {
      //return responseService.getFailResult(-900, "Fabric Connection Error");
      resultMap.put("result", false);
      resultMap.put("rescd", -900);
      resultMap.put("validmsg", "INVALID");
      resultMap.put("resmsg", "Fabric Connection Error");
      return resultMap;
    }
    try {
      MakeFabricObject makeObject = new MakeFabricObject();
      FabricClient fabClient = revFabObject.getFabClient();
      ChannelClient channelClient = revFabObject.getChannelClient();
      TransactionProposalRequest request =
              makeObject.getChainCodeRequest(
                      fabClient, fabQuery.getCodenm(), fabQuery.getFuncnm(), args);
      Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
      for (ProposalResponse res : responses) {
        if (res.isVerified() && res.getStatus() == ChaincodeResponse.Status.SUCCESS) {
          ByteString payload = res.getProposalResponse().getResponse().getPayload();
          String resmsg = res.getStatus().toString();
          String txid = res.getTransactionID();
          resultMap.put("txid", txid);
          FabTransation fabTransation = new FabTransation(txid, resmsg);
          String response = new String(res.getChaincodeActionResponsePayload());
          if (response != null && response.length() > 0) {
            resultMap.put("resmsg", response);
          } else {
            resultMap.put("resmsg", resmsg);
          }
          fabConfBean.putMapObject(txid, fabTransation);
        } else {
          resultMap.put("result", false);
          resultMap.put("resmsg", res.getMessage());
          log.error("Fail: " + res.getMessage());
        }
        resultMap.put("rescd", res.getStatus().getStatus());
        break;
      }
    } catch (Exception e) {
      resultMap.put("result", false);
      resultMap.put("rescd", -902);
      resultMap.put("validmsg", "INVALID");
      resultMap.put("resmsg", e.toString());
      log.error("Normal Invoke Err:" + e.toString());
    } finally {
      ROP.retObject(revFabObject);
    }

    //stopWatch.stop();
    // --Endoring 종료

    //stopWatch.start("Commiting");

    if ((boolean) resultMap.get("result") == true) {
      resultMap = getEvent(resultMap);
    }

    // --Comminting 종료
    //stopWatch.stop();

//    log.info(" INVOKE TIME : " + stopWatch.prettyPrint());
//    log.info(" total sec : " + stopWatch.getTotalTimeSeconds());
    resultMap.remove("result");

    log.info("INVOKE:" + fabQuery.getCodenm() +":"+ fabQuery.getFuncnm()  +"=>" + resultMap.toString());
    return resultMap;
  }

  // 트랜잭션 QUERY
  public HashMap<Object, Object> query(FabQueryDto fabQueryDto) {

    FabQuery fabQuery = FabQueryMapper.INSTANCE.fabQueryDtoToEntity(fabQueryDto);

    String[] args = fabQuery.getParams().split(",");
    HashMap<Object, Object> resultMap = new HashMap<Object, Object>();

    RevObjectPool ROP = RevObjectPool.getInstance();
    RevFabObject revFabObject = ROP.getObject();

    if (revFabObject == null) {
      resultMap.put("result", false);
      resultMap.put("rescd", -900);
      resultMap.put("resmsg", "Fabric Connection Error");
      return resultMap;
    }

    try {
      ChannelClient channelClient = revFabObject.getChannelClient();

      Collection<ProposalResponse> responses =
              channelClient.queryByChainCode(fabQuery.getCodenm(), fabQuery.getFuncnm(), args);

      for (ProposalResponse pres : responses) {
        if (pres.isVerified() && pres.getStatus() == ChaincodeResponse.Status.SUCCESS) {
          String stringResponse = new String(pres.getChaincodeActionResponsePayload());
          resultMap.put("resmsg", "SUCCESS");
          resultMap.put("value", stringResponse);
        } else {
          resultMap.put("result", false);
          resultMap.put("resmsg", pres.getMessage());
          log.error("Fail: " + pres.getMessage());
        }
        resultMap.put("rescd", pres.getStatus().getStatus());
        break;
      }

    } catch (Exception e) {
      resultMap.put("rescd", 500);
      resultMap.put("resmsg", e.toString());
    } finally {
      ROP.retObject(revFabObject);
    }

    log.info("QUERY:"+ fabQuery.getCodenm() +":"+ fabQuery.getFuncnm() +":"+fabQuery.getParams() +"=>" + resultMap.toString());
    return resultMap;
  }

  // 트랜잭션 정보조회
  public CommonResult transactionInfo(String transactionId) {

    HashMap<Object, Object> resultMap = new HashMap<Object, Object>();

    RevObjectPool ROP = RevObjectPool.getInstance();
    RevFabObject revFabObject = ROP.getObject();

    if (revFabObject == null) {
      return responseService.getFailResult(-900, "Fabric Connection Error");
    }

    try {
      ChannelClient channelClient = revFabObject.getChannelClient();
      Channel channel = revFabObject.getChannel();

      TransactionInfo txInfo = channel.queryTransactionByID(transactionId);
      int validcd = txInfo.getValidationCode().getNumber();
      resultMap.put("resmsg", "SUCCESS");
      resultMap.put("validcd", validcd);
    } catch (Exception e) {
      resultMap.put("resmsg", e.toString());
      resultMap.put("validcd", 999);
    } finally {
      ROP.retObject(revFabObject);
    }
    return responseService.getSingleResult(resultMap);
  }

  private HashMap<Object, Object> getEvent(HashMap<Object, Object> resultMap) {

    resultMap.put("result", false);
    String txid = resultMap.get("txid").toString();

    FabTransation fabTransation = (FabTransation) fabConfBean.getMapObject(txid);
    if (fabTransation == null || !fabTransation.getResmsg().equals("SUCCESS")) {
      fabConfBean.removeMapObject(txid);
      return resultMap;
    }

    try {
      int loop = 0;
      while (true) {
        Thread.sleep(200);
        fabTransation = (FabTransation) fabConfBean.getMapObject(txid);
        if (fabTransation != null && fabTransation.getStatus().equals("C")) {
          resultMap.put("blockno", fabTransation.getBlockno());
          int validcd = fabTransation.getVaildcd();
          resultMap.put("validcd", validcd);
          if (validcd != 0) {
            resultMap.put("result", false);
            resultMap.put("resmsg", "FAIL");
          } else {
            resultMap.put("result", true);
          }
          break;
        }
        loop++;
        if (loop > 100) {
          break;
        }
      }
    } catch (Exception e) {
    } finally {
      fabConfBean.removeMapObject(txid);
    }
    return resultMap;
  }

/*
   private HashMap<Object, Object> listenBlockEvent(Channel channel,HashMap<Object, Object> resultMap) throws Exception {

     FabTransation fabTransation = new FabTransation();

     BlockListener listener =
         new BlockListener() {
           @Override
           public void received(BlockEvent blockEvent) {
             Iterator<TransactionEvent> transactionEvents = blockEvent.getTransactionEvents()
                 .iterator();
             while (transactionEvents.hasNext()) {
               TransactionEvent transactionEvent = transactionEvents.next();
               String txid = transactionEvent.getTransactionID();
               log.info("EVENT TXID:" + txid);
               FabTransation fabTransation = (FabTransation) fabConfBean.getMapObject(txid);
               if (transactionEvent.getTransactionID().equals(txid)) {
                 int validcd = transactionEvent.getValidationCode();

                 fabTransation.updVaildCd(validcd, blockEvent.getBlockNumber());
                   resultMap.put("blockno", fabTransation.getBlockno());
                   resultMap.put("validcd", validcd);
                   if (validcd != 0) {
                     resultMap.put("result", false);
                     resultMap.put("resmsg", "FAIL");
                   } else {
                     resultMap.put("result", true);
                   }
                   break;
               }
             }
           }
         };
     String hanndler = channel.registerBlockListener(listener);
     resultMap.put("hanndler", hanndler);
     return resultMap;
   }
*/
}
