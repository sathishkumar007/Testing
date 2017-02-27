package com.newt.controller;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.elasticsearch.client.node.NodeClient;
 
import com.newt.model.Product;
import com.newt.model.ProductElasticSearch;
import com.newt.service.ProductService;
import com.wordnik.swagger.annotations.ApiOperation;
@CrossOrigin
@RestController
@RequestMapping("/products")
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.newt.repository")
public class ProductController {

	@Autowired
	ProductService productService;
	
	@Autowired
	private ElasticsearchTemplate template;
	@Bean
	public ElasticsearchTemplate elasticsearchTemplate() {
		return new ElasticsearchTemplate(getNodeClient());
	}
	
	
	 
 
	@RequestMapping(value = "search/id/{productId}", method = RequestMethod.GET,  produces = "application/json")
	@ApiOperation(value = "Find By ProductID")
	public Product findbyproductId(@PathVariable int productId) {
		
		return productService.findByproductId(productId);
	}

	@RequestMapping(value = "search/name/{productName}", method = RequestMethod.GET)
	@ApiOperation(value = "Find By ProductName")
	public List<ProductElasticSearch> findbyproductName(@PathVariable String productName) {
		return productService.findByProductName(productName);
	}
		
	@RequestMapping(method = RequestMethod.GET)
	public Iterable<ProductElasticSearch> findAll() {
		return productService.findAll();
	}

	@ApiOperation(value = "post a product")
	@RequestMapping(method = RequestMethod.POST)
	public ProductElasticSearch create(@RequestBody ProductElasticSearch car) {
		
		IndexQuery indexQuery = new IndexQuery();
		//indexQuery.setId(car.getProductId());
		indexQuery.setIndexName("Prodcut List");
		 
		indexQuery.setObject(car);
		template.putMapping(ProductElasticSearch.class);
		template.index(indexQuery);
		template.refresh(ProductElasticSearch.class, true);
		return productService.save(car);
	}
	
	private static NodeClient getNodeClient() {
		return (NodeClient) nodeBuilder().clusterName(UUID.randomUUID().toString()).local(true).node()
				.client();
	}
}
