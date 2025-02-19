package io.johnathanluong.ecommerce.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.johnathanluong.ecommerce.api.entity.Product;
import io.johnathanluong.ecommerce.api.repository.ProductRepository;
@Service
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    
    public ProductServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product){
        product.setCreatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Override
    public Product getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }
    
    @Override
    public Product updateProduct(Long id, Product updatedProduct) {
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            Product existingProduct = product.get();
            if(updatedProduct.getName() != null)
                existingProduct.setName(updatedProduct.getName());
            if(updatedProduct.getDescription() != null)
                existingProduct.setDescription(updatedProduct.getDescription());
            if(updatedProduct.getCategory() != null)
                existingProduct.setCategory(updatedProduct.getCategory());
            if(updatedProduct.getPrice() != null)
                existingProduct.setPrice(updatedProduct.getPrice());
            if(updatedProduct.getStock() != null)
                existingProduct.setStock(updatedProduct.getStock());
            if(updatedProduct.getSku() != null)
                existingProduct.setSku(updatedProduct.getSku());
            if(updatedProduct.getBrand() != null)
                existingProduct.setBrand(updatedProduct.getBrand());
            return productRepository.save(existingProduct);
        }
        
        return null;
    }
    
    @Override
    public boolean deleteProduct(Long id) {
        if(productRepository.existsById(id)){
            productRepository.deleteById(id);
            return true;
        }
        else{
            return false;
        }
    }
}