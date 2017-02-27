package com.newt.service;

import java.util.List;

import org.elasticsearch.common.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Service;

import com.newt.model.Product;
import com.newt.model.ProductElasticSearch;
import com.newt.repository.ProductElasticSearchRep;
import com.newt.repository.ProductRepository;

@Service

public class ProductServiceimpl implements ProductService {

	@Autowired
	ProductRepository productRepo;
	
 	@Autowired
    ProductElasticSearchRep ProductElasticSearchRep;
 	
	public Product findByproductId(int productId) {

		return productRepo.findByProductId(productId);
	}

	public Iterable<ProductElasticSearch> findAll() {

		return ProductElasticSearchRep.findAll();
	}

	public ProductElasticSearch save(ProductElasticSearch products) {
		return ProductElasticSearchRep.save(products);

	}

 
	/*public Product findByProductName(String productName) {
		 
		return productRepo.findByProductName(productName);
	}*/
	   @Inject
       public void setRepository(ProductElasticSearchRep repository) {
       this.ProductElasticSearchRep = repository;
       }
	   
	   @Override
		public List<ProductElasticSearch> findByProductName(String productName) {
			List<ProductElasticSearch> product =ProductElasticSearchRep.findProductElasticSearchByProductName(productName);
			return product;
		}
}
