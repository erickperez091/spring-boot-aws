package com.example.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchResponse;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@ConditionalOnProperty( name = "cloud.aws.sdk.version", havingValue = "V2" )
@Configuration( value = "AWSSQSConfig" )
public class AWSSQSConfig extends AbstractAwsConfig {

    @Value( "${cloud.aws.sqs.queueURL}" )
    private String queueName;


    @Bean
    public SqsClient sqsClient() {
        String endpoint = String.format( "%s://%s", "http", this.getAwsUrl() );

        return SqsClient.builder()
                .endpointOverride( URI.create( endpoint ) )
                .credentialsProvider( StaticCredentialsProvider.create(
                        AwsBasicCredentials.create( this.getAccessKey(), this.getSecretKey() ) ) )
                .region( Region.of( this.getRegion() ) )
                .build();
    }

    public List< Message > getMessages( SqsClient sqsClient ) {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest
                .builder()
                .queueUrl( queueName )
                .waitTimeSeconds( 10 )
                .maxNumberOfMessages( 10 )
                .build();

        return sqsClient.receiveMessage( receiveMessageRequest ).messages();
    }

    public void deleteMessageBatch( SqsClient sqsClient, List< Message > messages ) {
        List< DeleteMessageBatchRequestEntry > entries = new ArrayList<>();
        messages.forEach( message -> {
                    DeleteMessageBatchRequestEntry entry = DeleteMessageBatchRequestEntry
                            .builder()
                            .id( message.messageId() )
                            .receiptHandle( message.receiptHandle() )
                            .build();
                    entries.add( entry );
                }
        );
        DeleteMessageBatchRequest deleteMessageBatchRequest = DeleteMessageBatchRequest
                .builder()
                .queueUrl( queueName )
                .entries( entries )
                .build();

        DeleteMessageBatchResponse response = sqsClient.deleteMessageBatch( deleteMessageBatchRequest );
        System.out.println("Status: " + response.sdkHttpResponse().statusCode() + ", Text: " + response.sdkHttpResponse().statusText()) ;
    }

    public void deleteMessage( SqsClient sqsClient, Message message ) {
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest
                .builder()
                .queueUrl( queueName )
                .receiptHandle( message.receiptHandle() )
                .build();

        DeleteMessageResponse response = sqsClient.deleteMessage( deleteMessageRequest );
        System.out.println("Status: " + response.sdkHttpResponse().statusCode() + ", Text: " + response.sdkHttpResponse().statusText()) ;
    }
}
