package net.temp.elastic.clients;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import nl.altindag.ssl.SSLFactory;

public class ElasticsearchClientConfig {
    private RestClient restClient;
    private static ElasticsearchTransport elasticsearchTransport;
    private ElasticsearchClient elasticsearchClient;
    private JacksonJsonpMapper jsonMapper;

    public ElasticsearchClientConfig(){
        init();
    }
    /**
     * Initialization of the client
     */
    public void init(){
        jsonMapper = new JacksonJsonpMapper();

        final String user="elastic";
        
        String filePath = "C://Alex//file.properties";

        Properties properties = new Properties();
        try {
			properties.load(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String keyValue = properties.getProperty("pass");

        System.out.println("The value of the 'key' tag is: " + keyValue);
        
        
        
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, keyValue));
        SSLFactory sslFactory = SSLFactory.builder()
                .withUnsafeTrustMaterial()
                .withUnsafeHostnameVerifier()
                .build();

        restClient = RestClient.builder(
                new HttpHost("localhost",9200,"https")).setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                		.setSSLContext(sslFactory.getSslContext()).setSSLHostnameVerifier(sslFactory.getHostnameVerifier()))
        		.build();


        elasticsearchTransport =
            new RestClientTransport(restClient, jsonMapper);

        elasticsearchClient = new ElasticsearchClient(elasticsearchTransport);
    }

    /**
     * Shutdown the transport
     * @throws IOException
     */
    public void close() throws IOException {
        System.out.println("shutting down the client..");
        elasticsearchTransport.close();
    }

    /**
     * Get the client
     * @return
     */
    public ElasticsearchClient getElasticsearchClient(){
        return this.elasticsearchClient;
    }
}