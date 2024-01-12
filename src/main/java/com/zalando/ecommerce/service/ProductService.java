package com.zalando.ecommerce.service;

import com.zalando.ecommerce.exception.ProductNotFoundException;
import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Page<Product> getAllProducts(int pageNum, int size){
        PageRequest request = PageRequest.of(pageNum, size);
        return productRepository.findAll(request);
    }

    public Page<Product> getProductsWithName(String productName, int pageNum, int size){
        PageRequest request = PageRequest.of(pageNum, size);
        return productRepository.getProductsByProductNameContainingIgnoreCase(productName, request);
    }

    public Product getProductById(int productId) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()){
            return product.get();
        }else throw new ProductNotFoundException("No product matched the given ID.");
    }
}
