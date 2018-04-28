package com.dishatech.vertxspringdata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.dishatech.vertxspringdata.entity.User;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

@Component
public class VertxSpringDynamoVerticle extends AbstractVerticle {

	@Autowired
	DynamoDBMapper dynamoDBMapper;

	private Integer httpPort = 8089;

	@Override
	public void start(Future<Void> fut) {
		Router router = Router.router(vertx);

		router.route("/").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response.putHeader("content-type", "text/html").end("<h1>Hello: This is my Vert.x 3 demo project</h1>");
		});
		router.get("/api/users/:id").handler(this::findOne);
		router.get("/api/users").handler(this::findAll);
		router.post("/api/users").handler(BodyHandler.create().setMergeFormAttributes(true));
		router.post("/api/users").handler(this::addOne);
		router.delete("/api/users/:id").handler(this::deleteOne);

		vertx.createHttpServer().requestHandler(router::accept).listen(httpPort);
	}

	public void findOne(RoutingContext rc) {
		String id = rc.request().getParam("id");
		User user = dynamoDBMapper.load(User.class, id);
		rc.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(user));
	}

	public void findAll(RoutingContext rc) {
		rc.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(dynamoDBMapper.scan(User.class, new DynamoDBScanExpression())));
	}

	public void addOne(RoutingContext rc) {

		final User user = Json.decodeValue(rc.getBodyAsString(), User.class);
		System.out.println("Saving user: " + user);
		dynamoDBMapper.save(user);
		rc.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(user));
	}

	private void deleteOne(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id");
		User user = dynamoDBMapper.load(User.class, id);
		dynamoDBMapper.delete(user);
		routingContext.response().setStatusCode(204).end();
	}
}
