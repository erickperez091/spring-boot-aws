package com.example.aws.service.impl;

import com.example.aws.config.AWSSNSConfig;
import com.example.aws.config.AWSSQSConfig;
import com.example.aws.config.AbstractAwsConfig;
import com.example.aws.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConditionalOnProperty( name = "cloud.aws.sdk.version", havingValue = "V2" )
@Service
public class AWSS3ServiceImplV2 implements DocumentService {

    private final AbstractAwsConfig awsS3Config;
    private final AWSSNSConfig awsSNSConfig;
    private final AWSSQSConfig awsSQSConfig;
    private final S3Client s3Client;
    private final SnsClient snsClient;
    private final SqsClient sqsClient;

    @Value( "${cloud.aws.sqs.messageKey}" )
    private String messageKey;

    public AWSS3ServiceImplV2( @Qualifier( value = "AWSS3ConfigV2" ) AbstractAwsConfig awsS3Config, @Qualifier( value = "AWSSNSConfig" ) AWSSNSConfig awsSNSConfig, @Qualifier( value = "AWSSQSConfig" ) AWSSQSConfig awsSQSConfig, S3Client s3Client, SnsClient snsClient, SqsClient sqsClient ) {
        this.awsS3Config = awsS3Config;
        this.awsSNSConfig = awsSNSConfig;
        this.awsSQSConfig = awsSQSConfig;
        this.s3Client = s3Client;
        this.snsClient = snsClient;
        this.sqsClient = sqsClient;
    }

    @Override
    public void uploadDocument( String fileName, MultipartFile file ) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        Map< String, String > metadataMap = new HashMap<>();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket( awsS3Config.getBucketName() )
                .key( fileName )
                .metadata( metadataMap )
                .build();

        s3Client.putObject( putObjectRequest, RequestBody.fromInputStream( file.getInputStream(), file.getSize() ) );
        this.awsSNSConfig.sendMessage( fileName, snsClient );
        List< Message > messages = this.awsSQSConfig.getMessages( sqsClient );
        messages.forEach( message -> {
            JSONObject object = new JSONObject( message.body() );
            System.out.println( object.has( messageKey ) ? object.getString( messageKey ) : "Not Present" );
        } );
    }

    @Override
    public ByteArrayOutputStream downloadDocument( String documentId ) throws IOException {
        return null;
    }
}
