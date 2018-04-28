This is demo to show how to use dynamo db with Vert.X, Spring boot reactive framework to build server less microservice.

Download DynamoDB tar or zip file (https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)

Start DB using default port 8000 (http://localhost:8000/shell)
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar –sharedDb

Download Sample Application from GitHub(https://github.com/todinesh/VertxSpringDynamoDB). Application will automatically create the table. Environment prefix is added to the table name during runtime so you can run application using same AWS account for different env.

Compile and execute the code: mvn spring-boot:run

Test application using postman or any rest client.
  Insert user: http://localhost:8089/api/users
  Test Data: {"id":"1","firstName":"John","lastName":"Doe"}
  Find all users: http://localhost:8089/api/users
  Find one user: http://localhost:8089/api/users/1
  Delete user: http://localhost:8089/api/users/1

Create table
Using AWS CLI: aws dynamodb create-table --table-name devuser --attribute-definitions AttributeName=id,AttributeType=S --key-schema AttributeName=id,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 --endpoint-url http://localhost:8000

Or through local console (http://localhost:8000/shell):
Use below JS file to create table

var params = {
TableName: 'devuser',
KeySchema: [
    {
        AttributeName: 'id',
        KeyType: 'HASH',
    },
],
AttributeDefinitions: [
    {
        AttributeName: 'id',
        AttributeType: 'S',
    },
],
ProvisionedThroughput: {
    ReadCapacityUnits: 1,
    WriteCapacityUnits: 1,
},
};
dynamodb.createTable(params, function(err, data) {
    if (err) ppJson(err); // an error occurred
    else ppJson(data); // successful response
});

Below bean add environment prefix to the table name.
	@Bean
	public DynamoDBMapperConfig dynamoDBMapperConfig() {
		DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
		builder.withTableNameOverride(TableNameOverride.withTableNamePrefix(environment));
		return new DynamoDBMapperConfig(DynamoDBMapperConfig.DEFAULT, builder.build());
	}
