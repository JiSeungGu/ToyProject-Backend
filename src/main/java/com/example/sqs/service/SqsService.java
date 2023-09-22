package com.example.sqs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SqsService {

  private static final String QUEUE_URL = "https://sqs.ap-northeast-2.amazonaws.com/905126260776/TestQueue.fifo";
  private static final SqsClient sqsClient = SqsClient.builder()
    .region(Region.AP_NORTHEAST_2)
//      .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
    .credentialsProvider(ProfileCredentialsProvider.create())
    .build();

  public static void main(String[] args) {

    System.out.println(recevie());
  }

  public static List<Message> recevie()  {
  try{
    ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
      .queueUrl(QUEUE_URL)
      .maxNumberOfMessages(5)
      .build();
    List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
    deleteSqs(messages);
    return messages;

  } catch (SqsException e) {
    System.err.println(e.awsErrorDetails().errorMessage());
    System.exit(1);
  }
      return null;
  }

  public static void deleteSqs(List<Message> messages) {
    try {
      for (Message message : messages) {
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
          .queueUrl(QUEUE_URL)
          .receiptHandle(message.receiptHandle())
          .build();
        sqsClient.deleteMessage(deleteMessageRequest);
      }
    } catch (SqsException e) {
      System.err.println(e.awsErrorDetails().errorMessage());
      System.exit(1);
    }
  }

  public void sqsSend() {
    SqsClient sqsClient = SqsClient.builder()
      .region(Region.AP_NORTHEAST_2)
//      .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
      .credentialsProvider(ProfileCredentialsProvider.create())
      .build();

    sqsClient.sendMessage(SendMessageRequest.builder()
      .queueUrl(QUEUE_URL)
      .messageBody("Hello world! messageGroup1 Test4")
      .delaySeconds(0)
      .messageGroupId("3")
      .messageDeduplicationId("3")
      .build());
  }


  public void sqsSendMulti() {
    SendMessageBatchRequest sendMessageBatchRequest = SendMessageBatchRequest.builder()
      .queueUrl(QUEUE_URL)
      .entries(SendMessageBatchRequestEntry.builder().id("id1").messageBody("Hello from msg 1").build(),
        SendMessageBatchRequestEntry.builder().id("id2").messageBody("msg 2").delaySeconds(10).build())
      .build();
    sqsClient.sendMessageBatch(sendMessageBatchRequest);
  }

  public static void sendBatchMessages(SqsClient sqsClient, String queueUrl) {

    System.out.println("\nSend multiple messages");
    try {
      // snippet-start:[sqs.java2.sqs_example.send__multiple_messages]
      SendMessageBatchRequest sendMessageBatchRequest = SendMessageBatchRequest.builder()
        .queueUrl(queueUrl)
        .entries(SendMessageBatchRequestEntry.builder().id("id1").messageBody("Hello from msg 1").build(),
          SendMessageBatchRequestEntry.builder().id("id2").messageBody("msg 2").delaySeconds(10).build())
        .build();
      sqsClient.sendMessageBatch(sendMessageBatchRequest);
      // snippet-end:[sqs.java2.sqs_example.send__multiple_messages]

    } catch (SqsException e) {
      System.err.println(e.awsErrorDetails().errorMessage());
      System.exit(1);
    }
  }

}
