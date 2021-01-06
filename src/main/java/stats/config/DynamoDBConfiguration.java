package stats.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class DynamoDBConfiguration {
    private DynamoDB db;
    private DynamoDBMapper mapper;
    private GetPropertyValues properties;
    private Properties prop;

    public DynamoDBConfiguration() {
        try {
            properties = new GetPropertyValues();
            prop = properties.getPropValues();
            AmazonDynamoDB client = configDBClientBuilder(prop.getProperty("env"), prop.getProperty("accessKey")
                    , prop.getProperty("secretKey"), prop.getProperty("dynamoDBEndpoint"), prop.getProperty("awsRegion"));
            this.mapper = new DynamoDBMapper(client);
            this.db = new DynamoDB(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DynamoDB getDb() {
        return db;
    }

    public DynamoDBMapper getMapper() {
        return mapper;
    }

    private AmazonDynamoDB configDBClientBuilder(String env, String accessKey, String secretKey, String db, String region) throws IOException {
        //Development
        if (env.equals("development")) {
            return AmazonDynamoDBClientBuilder
                    .standard()
                    .withEndpointConfiguration(amazonEndpointConfiguration(db, region))
                    .build();
        }
        //Production
        return AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(amazonAWSCredentials(accessKey, secretKey))
                .build();
    }

    private AwsClientBuilder.EndpointConfiguration amazonEndpointConfiguration(String db, String region) throws IOException {
        return new AwsClientBuilder.EndpointConfiguration(db, region);
    }

    private AWSCredentialsProvider amazonAWSCredentials(String accessKey, String secretKey) throws IOException {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
    }


}
