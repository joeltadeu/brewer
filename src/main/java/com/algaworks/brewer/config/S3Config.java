package com.algaworks.brewer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
@PropertySource(value = "file:\\${USERPROFILE}\\.brewer-s3.properties", ignoreResourceNotFound = true)
public class S3Config {

	@Autowired
	private Environment env;

	@Bean
	public AmazonS3 amazonS3() {
		BasicAWSCredentials creds = new BasicAWSCredentials(env.getProperty("aws.accessKeyId"),
				env.getProperty("aws.secretKey"));
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.SA_EAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(creds)).build();
		return s3Client;
	}
}
