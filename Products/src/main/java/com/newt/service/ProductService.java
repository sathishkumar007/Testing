package com.newt.service;

import java.util.List;

import com.newt.model.Product;
import com.newt.model.ProductElasticSearch;

public interface ProductService {
	public Product findByproductId(int productId);

	public Iterable<ProductElasticSearch> findAll();

	public ProductElasticSearch save(ProductElasticSearch car);

	//public Product findByProductName(String productName);
	public List<ProductElasticSearch> findByProductName(String productName);
	
	
}
