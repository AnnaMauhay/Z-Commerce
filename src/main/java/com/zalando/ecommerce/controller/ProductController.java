package com.zalando.ecommerce.controller;

import com.zalando.ecommerce.dto.ProductRequest;
import com.zalando.ecommerce.exception.DuplicateProductException;
import com.zalando.ecommerce.exception.ProductNotFoundException;
import com.zalando.ecommerce.exception.UserNotFoundException;
import com.zalando.ecommerce.model.ErrorResponse;
import com.zalando.ecommerce.model.Product;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.service.ProductService;
import com.zalando.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {
    private final ProductService productService;
    private final UserService userService;

    @Operation(summary = "Get all active products.", description = "This returns all active products in paginated view.")
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(@RequestParam("page") int pageCtr,
                                                        @RequestParam("size") int size){
        return ResponseEntity.ok(productService.getAllProducts(pageCtr, size));
    }

    @Operation(summary = "Search products using a keyword.", description = "This returns all active products matching the keyword in paginated view. E.g. keywords 'heavy duty' should be written as 'heavy+duty' in the request parameter")
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
        try{
            return ResponseEntity.ok(productService.getProductById(productId));
        }catch (ProductNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER')")
    @Operation(summary = "Add a new product.", description = "This allows users with SELLER role to create a new product. The product name should be unique for every user.", security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> addNewProduct(@Validated @RequestBody ProductRequest productRequest,
                                           @AuthenticationPrincipal(expression = "username") String email){
        User user;
        Product product;
        try {
            user = userService.getUserByEmail(email);
            product = productService.addProduct(productRequest, user);
        } catch (UserNotFoundException | DuplicateProductException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
        }
        System.out.println(product.getProductId());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{product-id}")
                .buildAndExpand(product.getProductId())
                .toUri();
        return ResponseEntity.created(uri).body(product);
    }

    @PutMapping("/{product-id}")
    @PreAuthorize("hasAnyRole('SELLER')")
    public ResponseEntity<?> updateProduct(@Validated @RequestBody ProductRequest productRequest,
                                           @AuthenticationPrincipal(expression = "username") String email,
                                           @PathVariable("product-id") int productId){
        User user;
        Product product;
        try {
            user = userService.getUserByEmail(email);
            product = productService.updateProductDetails(productId, user, productRequest);
        } catch (UserNotFoundException | ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
        }
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{product-id}")
    @PreAuthorize("hasAnyRole('SELLER')")
    public ResponseEntity<?> deleteProduct(@AuthenticationPrincipal (expression = "username") String email,
                                           @PathVariable("product-id") int productId){
        User user;
        Product product;
        try {
            user = userService.getUserByEmail(email);
            product = productService.archiveProduct(productId, user);
        } catch (UserNotFoundException | ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
        }
        return ResponseEntity.ok(product);
    }
}