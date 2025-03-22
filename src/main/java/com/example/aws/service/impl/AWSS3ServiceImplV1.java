package com.example.aws.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.aws.config.AbstractAwsConfig;
import com.example.aws.service.DocumentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@ConditionalOnProperty( name = "cloud.aws.sdk.version", havingValue = "V1" )
@Service
public class AWSS3ServiceImplV1 implements DocumentService {

    private final AbstractAwsConfig awsS3Config;
    private final AmazonS3 s3Client;

    public AWSS3ServiceImplV1( @Qualifier("AWSS3ConfigV1") AbstractAwsConfig awsS3Config, AmazonS3 s3Client ) {
        this.awsS3Config = awsS3Config;
        this.s3Client = s3Client;
    }

    @Override
    public void uploadDocument( String fileName, MultipartFile file ) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType( file.getContentType() );
        metadata.setContentLength( file.getSize() );
        PutObjectResult result = s3Client.putObject( awsS3Config.getBucketName(), fileName, file.getInputStream(), metadata );
    }

    @Override
    public ByteArrayOutputStream downloadDocument( String documentId ) throws IOException {
        S3Object s3Object = s3Client.getObject( awsS3Config.getBucketName(), documentId );
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy( inputStream, outputStream );
        return outputStream;
    }
}
