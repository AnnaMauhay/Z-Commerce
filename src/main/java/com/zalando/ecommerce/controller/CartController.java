package com.zalando.ecommerce.controller;

import com.zalando.ecommerce.dto.CartRequest;
import com.zalando.ecommerce.exception.*;
import com.zalando.ecommerce.model.ErrorResponse;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.service.CartService;
import com.zalando.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Cart management APIs")
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get cart for the authenticated customer.", description = "Returns a list of products in the cart and the cart's total price.", security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetails principal){
        try {
            return ResponseEntity.ok(cartService.getCartAndTotal(userService.getUserByEmail(principal.getUsername())));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Add product on the authenticated customer's cart.", description = "Returns a list of products in the cart and the cart's total price.", security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<?> addToCart(@AuthenticationPrincipal UserDetails principal,
                                       @RequestBody CartRequest cartRequest){
        try {
            User user = userService.getUserByEmail(principal.getUsername());
            return ResponseEntity.ok(cartService.addProductToCart(cartRequest, user));
        } catch (UserNotFoundException | InsufficientStockException | ProductNotFoundException |
                 DuplicateProductException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Update the product quantity on the authenticated customer's cart.", description = "Returns a list of products in the cart and the cart's total price.", security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<?> updateProductInCart(@AuthenticationPrincipal UserDetails principal,
                                       @RequestBody CartRequest cartRequest){
        try {
            User user = userService.getUserByEmail(principal.getUsername());
            return ResponseEntity.ok(cartService.updateProductInCart(cartRequest, user));
        } catch (UserNotFoundException | StockLimitExceededException | ProductNotFoundException |
                 InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
        }
    }

    @DeleteMapping("/{product-id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Remove the product on the authenticated customer's cart.", description = "Returns a list of products in the cart and the cart's total price.", security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<?> removeFromCart(@AuthenticationPrincipal UserDetails principal,
                                            @PathVariable("product-id") int productId){
        try {
            User user = userService.getUserByEmail(principal.getUsername());
            return ResponseEntity.ok(cartService.releaseProductFromCart(productId, user));
        } catch (UserNotFoundException | ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
        }
    }
}
