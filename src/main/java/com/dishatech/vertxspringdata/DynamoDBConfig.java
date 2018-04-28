package com.dishatech.vertxspringdata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;

@Configuration
public class DynamoDBConfig {

	@Value("${amazon.aws.dynamodb.env}")
	private String environment;
	@Value("${amazon.aws.dynamodb.endpoint}")
	private String amazonDynamoDBEndpoint;
	@Value("${amazon.aws.accesskey}")
	private String accessKey;
	@Value("${amazon.aws.secretkey}")
	private String secretKey;

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(amazonAWSCredentials());
		if (!amazonDynamoDBEndpoint.isEmpty()) {
			amazonDynamoDB.setEndpoint(amazonDynamoDBEndpoint);
		}
		return amazonDynamoDB;
	}

	@Bean
	public AWSCredentials amazonAWSCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}

	@Bean
	public DynamoDBMapperConfig dynamoDBMapperConfig() {
		DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
		builder.withTableNameOverride(TableNameOverride.withTableNamePrefix(environment));
		return new DynamoDBMapperConfig(DynamoDBMapperConfig.DEFAULT, builder.build());
	}

	@Bean
	public DynamoDBMapper dynamoDBMapper() {
		return new DynamoDBMapper(amazonDynamoDB(), dynamoDBMapperConfig());
	}
}
