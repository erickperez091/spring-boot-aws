package com.example.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListTopicsRequest;
import software.amazon.awssdk.services.sns.model.ListTopicsResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.net.URI;

@ConditionalOnProperty(name = "cloud.aws.sdk.version", havingValue = "V2")
@Configuration(value = "AWSSNSConfig")
public class AWSSNSConfig extends AbstractAwsConfig{

    @Value( "${cloud.aws.sns.topicARN}" )
    private String topicArn;

    @Bean
    public SnsClient snsClient() {
        String endpoint = String.format("%s://%s", "http", this.getAwsUrl());

        SnsClient client = SnsClient.builder()
                .endpointOverride( URI.create(endpoint))
                .credentialsProvider( StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(this.getAccessKey(), this.getSecretKey())))
                .region( Region.of(this.getRegion()))
                .build();
        /*
        ListTopicsRequest request = ListTopicsRequest.builder()
                .build();

        ListTopicsResponse result = client.listTopics(request);
        System.out.println("Status was " + result.sdkHttpResponse().statusCode() + "\n\nTopics\n\n" + result.topics());

         */
        return client;
    }

    public void sendMessage( String body, SnsClient snsClient ) {
        PublishRequest publishRequest = PublishRequest.builder().message( body ).topicArn( topicArn ).build();
        PublishResponse publishResponse = snsClient.publish( publishRequest );
        System.out.println(publishResponse.messageId() + " Message sent. Status is " + publishResponse.sdkHttpResponse().statusCode());
    }
}
