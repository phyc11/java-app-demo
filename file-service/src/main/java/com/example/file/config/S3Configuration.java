package com.example.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Configuration {
    @Bean
    S3Client s3Client(@Value("${file.storage.endpoint}") String endpoint,
                      @Value("${file.storage.region:us-east-1}") String region,
                      @Value("${file.storage.access-key}") String accessKey,
                      @Value("${file.storage.secret-key}") String secretKey) {
        return S3Client.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(software.amazon.awssdk.services.s3.S3Configuration.builder().pathStyleAccessEnabled(true).build()).build();
    }

    @Bean
    S3Presigner s3Presigner(@Value("${file.storage.endpoint}") String endpoint,
                            @Value("${file.storage.region:us-east-1}") String region,
                            @Value("${file.storage.access-key}") String accessKey,
                            @Value("${file.storage.secret-key}") String secretKey) {
        return S3Presigner.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(software.amazon.awssdk.services.s3.S3Configuration.builder().pathStyleAccessEnabled(true).build()).build();
    }
}
