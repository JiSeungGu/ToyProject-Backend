/*
 * SejongTelecom Core DEV Team
 */
package com.example.common.fabric.sdk.client;

import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Wrapper class for HFClient.
 *
 * @author
 */
@Slf4j
public class FabricClient {

  private HFClient instance;

  /**
   * Return an instance of HFClient.
   *
   * @return
   */
  public HFClient getInstance() {
    return instance;
  }

  /**
   * Constructor
   *
   * @throws CryptoException
   * @throws InvalidArgumentException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public FabricClient(User context)
      throws CryptoException, InvalidArgumentException, IllegalAccessException,
          InstantiationException, ClassNotFoundException, NoSuchMethodException,
          InvocationTargetException {
    CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
    // setup the client
    instance = HFClient.createNewInstance();
    instance.setCryptoSuite(cryptoSuite);
    instance.setUserContext(context);
  }

  /**
   * Create a channel client.
   *
   * @param name
   * @return
   * @throws InvalidArgumentException
   */
  public ChannelClient createChannelClient(String name) throws InvalidArgumentException {
    Channel channel = instance.newChannel(name);
    ChannelClient client = new ChannelClient(name, channel, this);
    return client;
  }

  /**
   * Deploy chain code.
   *
   * @param chainCodeName
   * @param chaincodePath
   * @param codepath
   * @param language
   * @param version
   * @param peers
   * @return
   * @throws InvalidArgumentException
   * @throws IOException
   * @throws ProposalException
   */
  public Collection<ProposalResponse> deployChainCode(
      String chainCodeName,
      String chaincodePath,
      String codepath,
      String language,
      String version,
      Collection<Peer> peers,
      String indexpath)
      throws InvalidArgumentException, IOException, ProposalException {
    InstallProposalRequest request = instance.newInstallProposalRequest();
    ChaincodeID.Builder chaincodeIDBuilder =
        ChaincodeID.newBuilder().setName(chainCodeName).setVersion(version).setPath(chaincodePath);
    ChaincodeID chaincodeID = chaincodeIDBuilder.build();
    log.info(
        "Deploying chaincode "
            + chainCodeName
            + " using Fabric client "
            + instance.getUserContext().getMspId()
            + " "
            + instance.getUserContext().getName());
    request.setChaincodeID(chaincodeID);
    request.setUserContext(instance.getUserContext());
    request.setProposalWaitTime(100000);
    request.setChaincodeSourceLocation(new File(codepath));
    if (!indexpath.equals("") && indexpath != null) {
      log.info("INDEXPATH:" + "index/" + indexpath);
      request.setChaincodeMetaInfLocation(new File("index/" + indexpath));
    }
    request.setChaincodeVersion(version);
    Collection<ProposalResponse> responses = instance.sendInstallProposal(request, peers);
    return responses;
  }
}
