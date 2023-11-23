package net.temp.elastic.clients;



import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.fasterxml.jackson.databind.node.ObjectNode;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;

/**
 * A indexing operations helper client
 *
 */
public class IndexingOperationsManager {

    private ElasticsearchClientConfig elasticsearchClientConfig;
    private ElasticsearchClient elasticsearchClient;

    public IndexingOperationsManager(){
        elasticsearchClientConfig = new ElasticsearchClientConfig();
        this.elasticsearchClient = elasticsearchClientConfig.getElasticsearchClient();
    }


    public void createIndexUsingClient(String indexName) throws IOException {
        ElasticsearchIndicesClient elasticsearchIndicesClient =
                this.elasticsearchClient.indices();

        ExistsRequest request= ExistsRequest.of(e -> e.index(indexName));

        BooleanResponse result = this.elasticsearchClient.indices().exists(request);

        if (!result.value()) {
        	
        	CreateIndexRequest createIndexRequest =
                    new CreateIndexRequest.Builder().index(indexName).build();

            CreateIndexResponse createIndexResponse =
                    elasticsearchIndicesClient.create(createIndexRequest);

            System.out.println("Index created successfully using Client"+createIndexResponse);
		}
        else
        {
        	System.out.println("Index already exists");
        }
        
    }



    
    
    public String indexDocumentWithJSON(String indexName, String jsonString) throws IOException {
    	
    	Reader stream = new StringReader(jsonString);

    	IndexRequest<JsonData> request = IndexRequest.of(i -> i
    		    .index(indexName)
    		    .withJson(stream)
    		);
    	
    	IndexResponse response =this.elasticsearchClient.index(request);
    	System.out.println("Indexed with id " + response.version());
    	return response.id();
    }
    
    
    
    public String getDocumentById (String indexName, String inputId) throws IOException {
    	String result="";
    	
    	GetResponse<ObjectNode> response = this.elasticsearchClient.get(g -> g
    		    .index(indexName)
    		    .id(inputId),
    		    ObjectNode.class     
    		);

    		if (response.found()) {
    		    ObjectNode json = response.source();
    		    result = json.toString();
    		    System.out.println("document found: " + result);
    		    
    		} else {
    			System.out.println("document not found");
    		}   	
    	return result;
    }
    
    


    public void close() throws IOException {
        elasticsearchClientConfig.close();
    }

    

}