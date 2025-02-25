package com.example.aws.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.net.URISyntaxException;

@ConditionalOnProperty(name = "cloud.aws.sdk.version", havingValue = "V2")
@Configuration
public class AWSS3ConfigV2 extends AbstractAwsConfig {

    @Bean
    public S3Client s3Client() throws URISyntaxException {

        String endpoint = String.format("%s://%s", "http", this.getS3Endpoint());
        // S3 Client with configured credentials, endpoint directing to LocalStack and desired region.
        return S3Client.builder()
                .forcePathStyle( true )
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider( StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(this.getAccessKey(), this.getSecretKey())))
                .region(Region.of(this.getRegion()))
                .build();
    }

}
