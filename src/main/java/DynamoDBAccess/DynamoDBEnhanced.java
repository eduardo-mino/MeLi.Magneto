package DynamoDBAccess;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

import MutantAnalysis.DNA;
import MutantAnalysis.DnaStats;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        Region region = Region.US_EAST_2;
         this.ddb = DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        this.enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

    }

    private long GetRecordCount(String table){
        DynamoDbTable<DnaItems> mappedTable = enhancedClient.table(table, TABLE_SCHEMA);

        ScanEnhancedRequest scanReq = ScanEnhancedRequest.builder()
                .build();

        long result = mappedTable.scan(scanReq).items().stream().count();
        return result;
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

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public DnaStats GetStats(String humans, String mutants){
        DnaStats stats = new DnaStats();
        stats.count_mutant_dna = this.GetRecordCount(mutants);
        stats.count_human_dna = this.GetRecordCount(humans);
        stats.ratio = (float)stats.count_mutant_dna / stats.count_human_dna;

        return stats;
    }
}