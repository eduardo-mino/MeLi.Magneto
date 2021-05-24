package DynamoDBAccess;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

import MutantAnalysis.DNA;
import MutantAnalysis.DnaStats;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import org.springframework.stereotype.Component;

@Component("DynamoDBEnhanced")
public class DynamoDBEnhanced {
    private DynamoDbClient ddb;
    private DynamoDbEnhancedClient enhancedClient;
    final static Logger logger =
            LogManager.getLogger(DynamoDBEnhanced.class.getName());

    private final ProvisionedThroughput DEFAULT_PROVISIONED_THROUGHPUT =
            ProvisionedThroughput.builder()
                    .readCapacityUnits(50L)
                    .writeCapacityUnits(50L)
                    .build();

    private final TableSchema<DnaItems> TABLE_SCHEMA =
            StaticTableSchema.builder(DnaItems.class)
                    .newItemSupplier(DnaItems::new)
                    .addAttribute(String.class, a -> a.name("dna")
                        .getter(DnaItems::getDna)
                            .setter(DnaItems::setDna)
                            .tags(primaryPartitionKey()))
                    .build();
    public DynamoDBEnhanced(){
        try {
            Region region = Region.US_EAST_2;
            this.ddb = DynamoDbClient.builder()
                    .region(region)
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .build();
            this.enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddb)
                    .build();
        }
        catch(Exception ex) {
            logger.fatal("An exception occurred: " + ex);
            throw ex;
        }
    }

    private long GetRecordCount(String table){
        try {
            DynamoDbTable<DnaItems> mappedTable = enhancedClient.table(table, TABLE_SCHEMA);

            ScanEnhancedRequest scanReq = ScanEnhancedRequest.builder()
                    .build();

            long result = mappedTable.scan(scanReq).items().stream().count();
            return result;
        }
        catch(Exception ex){
            logger.fatal("An exception occurred: " + ex);
            throw ex;
        }
    }

    // Uses the enhanced client to inject a new post into a DynamoDB table
    public void injectDynamoItem(DNA item, String table) {

        Gson gson=new GsonBuilder().create();
        String jsonArray = gson.toJson(item.dna);


        try {
            // Create a DynamoDbTable object
            DynamoDbTable<DnaItems> mappedTable = enhancedClient.table(table, TABLE_SCHEMA);
            DnaItems dnaItems = new DnaItems();
            dnaItems.setDna(jsonArray);

            PutItemEnhancedRequest enReq = PutItemEnhancedRequest.builder(DnaItems.class)
                    .item(dnaItems)
                    .build();

            mappedTable.putItem(enReq);

        } catch (Exception ex) {
            logger.fatal("An exception occurred: " + ex);
            throw ex;
        }
    }

    public DnaStats GetStats(String humans, String mutants){
        try{
            DnaStats stats = new DnaStats();
            stats.count_mutant_dna = this.GetRecordCount(mutants);
            stats.count_human_dna = this.GetRecordCount(humans);
            if(stats.count_human_dna!=0) {
                stats.ratio = (float) stats.count_mutant_dna / stats.count_human_dna;
            }
            else{
                stats.ratio = 0;
            }

            return stats;
        }
        catch(Exception ex){
            logger.fatal("An exception occurred: " + ex);
            throw ex;
        }
    }
}