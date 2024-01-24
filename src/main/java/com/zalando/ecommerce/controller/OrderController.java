package com.zalando.ecommerce.controller;

import com.zalando.ecommerce.dto.OrderUpdateRequest;
import com.zalando.ecommerce.exception.EmptyCartException;
import com.zalando.ecommerce.exception.InsufficientStockException;
import com.zalando.ecommerce.exception.OrderNotFoundException;
import com.zalando.ecommerce.exception.UserNotFoundException;
import com.zalando.ecommerce.model.*;
import com.zalando.ecommerce.service.OrderService;
import com.zalando.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal UserDetails principal){
        Order order = null;
        try {
            order = orderService.createOrder(userService.getUserByEmail(principal.getUsername()));
        } catch (EmptyCartException | InsufficientStockException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
        }
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{order-id}")
                .buildAndExpand(order.getOrderId())
                .toUri();
        return ResponseEntity.created(uri).body(order);
    }

    @GetMapping("/{order-id}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<?> getOrder(@AuthenticationPrincipal UserDetails principal,
                                      @PathVariable("order-id") int orderId) {
        try {
            return ResponseEntity.ok(orderService.getOrderByUserAndId(userService.getUserByEmail(principal.getUsername()), orderId));
        } catch (OrderNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<?> getOrders(@RequestParam("status") OrderStatus status,
                                                 @RequestParam("page") int pageCtr,
                                                 @RequestParam("size") int size,
                                                 @AuthenticationPrincipal UserDetails principal){
        User user;
        try {
            user = userService.getUserByEmail(principal.getUsername());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
        }
        return ResponseEntity.ok(orderService.getOrdersByStatus(user, status, pageCtr, size));
    }

    @PutMapping("/{order-id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> updateOrder(@AuthenticationPrincipal UserDetails principal,
                                        @PathVariable("order-id") int orderId,
                                        @Validated @RequestBody OrderUpdateRequest updateRequest){
        User user;
        try {
            user = userService.getUserByEmail(principal.getUsername());
            Order order = orderService.updateOrderStatus(user,orderId, updateRequest);
            return ResponseEntity.ok(order);
        } catch (UserNotFoundException | OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
        }
    }
}
