package com.zalando.ecommerce.controller;

import com.zalando.ecommerce.exception.ProductNotFoundException;
import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(@RequestParam("page") int pageCtr,
                                                        @RequestParam("size") int size){
        return ResponseEntity.ok(productService.getAllProducts(pageCtr, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Product>> getProductsWithName(@RequestParam("keyword") String productName,
                                                             @RequestParam("page") int pageCtr,
                                                        @RequestParam("size") int size){
        if(productName.contains("+")){
            productName = productName.replace('+',' ');
        }
        return ResponseEntity.ok(productService.getProductsWithName(productName, pageCtr, size));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") int productId){
        System.out.println(productId);
        try{
            return ResponseEntity.ok(productService.getProductById(productId));
        }catch (ProductNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
}