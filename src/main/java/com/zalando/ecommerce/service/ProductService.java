package com.zalando.ecommerce.service;

import com.zalando.ecommerce.dto.ProductRequest;
import com.zalando.ecommerce.exception.DuplicateProductException;
import com.zalando.ecommerce.exception.InsufficientStockException;
import com.zalando.ecommerce.exception.ProductNotFoundException;
import com.zalando.ecommerce.exception.StockLimitExceededException;
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
        return productRepository.findAllByArchivedIsFalse(request);
    }

    public Page<Product> getProductsWithName(String productName, int pageNum, int size){
        PageRequest request = PageRequest.of(pageNum, size);
        return productRepository.getProductsByArchivedIsFalseAndProductNameContainingIgnoreCase(productName, request);
    }

    public Product getProductById(int productId) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findProductsByProductIdAndArchivedIsFalse(productId);
        if (product.isPresent()){
            return product.get();
        }else throw new ProductNotFoundException("No product matched the given ID.");
    }

    public Product addProduct(ProductRequest productRequest, User user) throws DuplicateProductException {
        Optional<Product> product = productRepository.getProductByProductNameContainingIgnoreCaseAndSeller(productRequest.getProductName(), user);
        if (product.isEmpty()){
            return productRepository.save(new Product(
                    productRequest.getProductName(),
                    productRequest.getDescription(),
                    productRequest.getPrice(),
                    productRequest.getStockQty(),
                    user));
        }else throw new DuplicateProductException("Product name is already registered with this seller.");
    }

    public Product updateProductDetails(int productId, User user, ProductRequest productRequest) throws ProductNotFoundException {
        Optional<Product> foundProduct = productRepository.getProductByProductIdAndSellerAndArchived(productId, user,false);
        if (foundProduct.isPresent()){
            Product product = foundProduct.get();
            product.setProductName(productRequest.getProductName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setStockQty(productRequest.getStockQty());
            return productRepository.save(product);
        }else throw new ProductNotFoundException("No active product matched the given ID for this seller.");
    }

    public Product archiveProduct(int productId, User user) throws ProductNotFoundException {
        Optional<Product> foundProduct = productRepository.getProductByProductIdAndSellerAndArchived(productId, user, false);
        if (foundProduct.isPresent()){
            Product product = foundProduct.get();
            product.setArchived(true);
            return productRepository.save(product);
        }else throw new ProductNotFoundException("No active product matched the given ID for this seller.");
    }

    public void reduceQuantity(int quantity, Product product) throws InsufficientStockException {
        if (product.reduceQuantity(quantity)){
            productRepository.save(product);
        }else throw new InsufficientStockException("There is not enough stock for the requested quantity.");
    }

    public void increaseQuantity(int quantity, Product product) throws StockLimitExceededException {
        if(product.increaseQuantity(quantity)){
            productRepository.save(product);
        }else throw new StockLimitExceededException("Stock quantity must not be more than 10,000.");
    }
}
