package com.example.aws.config;

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
@Configuration(value = "AWSS3ConfigV2")
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
