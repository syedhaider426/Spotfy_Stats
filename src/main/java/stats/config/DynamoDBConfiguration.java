package stats.config;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;

/**
 * Setup the connection to DynamoDB
 */
@Configuration
public class DynamoDBConfiguration {
    private DynamoDB db;
    private DynamoDBMapper mapper;
    private GetPropertyValues properties;
    private Properties prop;
    private AmazonDynamoDB client;

    public DynamoDBConfiguration() {
        try {
            //Load in properties file
            properties = new GetPropertyValues();

            //Get the properties object
            prop = properties.getPropValues();

            //Configure DynamoDB Client
            client = configDBClientBuilder(prop.getProperty("env"), prop.getProperty("dynamoDBEndpoint"), prop.getProperty("awsRegion"));
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

    /**
     * This will create the connection the DynamoDB endpoint
     *
     * @param env    development/production environment
     * @param db     endpoint for DynamoDB
     * @param region region where DynamoDB is hosted
     * @return AmazonDynamoDB Client
     */
    private AmazonDynamoDB configDBClientBuilder(String env, String db, String region) {
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
                .withCredentials(new ProfileCredentialsProvider())
                .build();
    }

    private AwsClientBuilder.EndpointConfiguration amazonEndpointConfiguration(String db, String region) {
        return new AwsClientBuilder.EndpointConfiguration(db, region);
    }


}
