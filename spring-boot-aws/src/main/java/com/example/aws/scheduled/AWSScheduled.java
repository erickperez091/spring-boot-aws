package com.example.aws.scheduled;

import com.example.aws.config.AWSSQSConfig;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Component
@ConditionalOnProperty(name = "cloud.aws.sdk.version", havingValue = "V2")
public class AWSScheduled {

    Logger logger = LoggerFactory.getLogger(AWSScheduled.class);

    @Value( "${cloud.aws.sqs.messageKey}" )
    private String messageKey;

    private final SqsClient sqsClient;
    private final AWSSQSConfig awsSQSConfig;

    public AWSScheduled( SqsClient sqsClient, @Qualifier("AWSSQSConfig") AWSSQSConfig awsSQSConfig ) {
        this.sqsClient = sqsClient;
        this.awsSQSConfig = awsSQSConfig;
    }

    @Scheduled( cron = "0 */1 * * * *")
    public void readMessage( ) {
        logger.info( "[START].[AWSScheduled].[readMessage]:Reading messages from sqs queue" );
        List< Message > messages = this.awsSQSConfig.getMessages( sqsClient );
        messages.forEach( message -> {
            logger.info( "[START].[AWSScheduled].[readMessage]:Processing message: " + message.messageId() );
            JSONObject object = new JSONObject( message.body() );
            System.out.println( object.has( messageKey ) ? object.getString( messageKey ) : "Not Present" );
            awsSQSConfig.deleteMessage( sqsClient, message );
            logger.info( "[END].[AWSScheduled].[readMessage]:Processing message: " + message.messageId() );
        } );
        logger.info( "[END].[AWSScheduled].[readMessage]:Reading messages from sqs queue" );
    }
}
