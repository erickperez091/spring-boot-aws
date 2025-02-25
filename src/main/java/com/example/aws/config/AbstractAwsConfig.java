package com.example.aws.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class AbstractAwsConfig {
    @Value( "${cloud.aws.credentials.accessKey}" )
    private String accessKey;

    @Value( "${cloud.aws.credentials.secretKey}" )
    private String secretKey;

    @Value( "${cloud.aws.region}" )
    private String region;

    @Value( "${cloud.aws.bucketName}")
    private String bucketName;

    @Value( "${cloud.aws.s3Endpoint}" )
    private String s3Endpoint;
}
