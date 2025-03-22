package com.example.aws.service.impl;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.aws.config.AWSSNSConfig;
import com.example.aws.config.AbstractAwsConfig;
import com.example.aws.service.DocumentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sns.SnsClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ConditionalOnProperty( name = "cloud.aws.sdk.version", havingValue = "V2" )
@Service
public class AWSS3ServiceImplV2 implements DocumentService {

    private final AbstractAwsConfig awsS3Config;
    private final AWSSNSConfig awsSNSConfig;
    private final S3Client s3Client;
    private final SnsClient snsClient;


    public AWSS3ServiceImplV2( @Qualifier( value = "AWSS3ConfigV2" ) AbstractAwsConfig awsS3Config, @Qualifier( value = "AWSSNSConfig" ) AWSSNSConfig awsSNSConfig, S3Client s3Client, SnsClient snsClient ) {
        this.awsS3Config = awsS3Config;
        this.awsSNSConfig = awsSNSConfig;
        this.s3Client = s3Client;
        this.snsClient = snsClient;
    }

    @Override
    public void uploadDocument( String fileName, MultipartFile file ) throws IOException {
        Map< String, String > metadataMap = new HashMap<>();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket( awsS3Config.getBucketName() )
                .key( fileName )
                .metadata( metadataMap )
                .build();
        s3Client.putObject( putObjectRequest, RequestBody.fromInputStream( file.getInputStream(), file.getSize() ) );
        this.awsSNSConfig.sendMessage( fileName, snsClient );
    }

    @Override
    public ByteArrayOutputStream downloadDocument( String documentId ) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest
                .builder()
                .bucket( awsS3Config.getBucketName() )
                .key( documentId )
                .build();
        ResponseInputStream< GetObjectResponse > s3ObjectInputStream = s3Client.getObject(getObjectRequest);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy( s3ObjectInputStream, outputStream );
        return outputStream;
    }
}
