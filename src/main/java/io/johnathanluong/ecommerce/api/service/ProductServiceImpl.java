package io.johnathanluong.ecommerce.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.johnathanluong.ecommerce.api.entity.Product;

@Service
public class ProductServiceImpl implements ProductService{

    // In memory for now until I setup Postgres
    private final Map<Long, Product> productMap = new HashMap<>();
    private Long nextProductId = 1L;

    @Override
    public Product createProduct(Product product){
        product.setId(nextProductId);
        productMap.put(product.getId(), product);
        nextProductId++;

        return product;
    }

    @Override
    public void deleteProduct(Long id) {
        if(productMap.containsKey(id)){
            productMap.remove(id);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }

    @Override
    public Product getProductById(Long id) {
        if(productMap.containsKey(id)){
            return productMap.get(id);
        }

        return null;
    }

    @Override
    public Product updateProduct(Long id, Product updatedProduct) {
        if(productMap.containsKey(id)){
            updatedProduct.setId(id);
            productMap.put(id, updatedProduct);
            return productMap.get(id);
        }

        return null;
    }
}