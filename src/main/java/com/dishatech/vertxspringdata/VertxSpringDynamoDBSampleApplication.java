package com.dishatech.vertxspringdata;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.dishatech.vertxspringdata.service.VertxSpringDynamoVerticle;

import io.vertx.core.Vertx;

@SpringBootApplication
public class VertxSpringDynamoDBSampleApplication {

	@Autowired
	VertxSpringDynamoVerticle vertxSpringDynamoVerticle;

	@Autowired
	AmazonDynamoDB amazonDynamoDB;

	@Value("${amazon.aws.dynamodb.env}")
	private String env;

	public static void main(String[] args) {
		SpringApplication.run(VertxSpringDynamoDBSampleApplication.class, args);
	}

	@EventListener
	public void deployVerticles(ApplicationReadyEvent event) {
		Vertx.vertx().deployVerticle(vertxSpringDynamoVerticle);
	}

	@PostConstruct
	public void createTable() {
		try {
			String tName = env + "user";
			CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tName)
					.withKeySchema(new KeySchemaElement().withAttributeName("id").withKeyType(KeyType.HASH))
					.withAttributeDefinitions(
							new AttributeDefinition().withAttributeName("id").withAttributeType(ScalarAttributeType.S))
					.withProvisionedThroughput(
							new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
			TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest);
		} catch (Exception e) {
			System.out.println("failed:" + e.getMessage());
		}
	}
}
