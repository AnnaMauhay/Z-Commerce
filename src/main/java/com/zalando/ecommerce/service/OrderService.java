package com.zalando.ecommerce.service;

import com.zalando.ecommerce.exception.EmptyCartException;
import com.zalando.ecommerce.exception.InsufficientStockException;
import com.zalando.ecommerce.model.*;
import com.zalando.ecommerce.repository.OrderItemRepository;
import com.zalando.ecommerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ProductService productService;

    @Transactional
    public Order createOrder(User user) throws EmptyCartException, InsufficientStockException {
        Set<CartItem> cartItemSet = cartService.getCartByCustomer(user);
        if(cartItemSet.isEmpty()) {
            throw new EmptyCartException("Order cannot be made on an empty cart.");
        }

        reduceProductsStockQuantities(cartItemSet);

        Order createdOrder = orderRepository.save(new Order(user, cartItemSet, OrderStatus.PROCESSING));
        createOrderItemsFromCart(cartItemSet, createdOrder);
        cartService.deletePurchasedCartItems(user);
        return orderRepository.getReferenceById(createdOrder.getOrderId());
    }

    private void reduceProductsStockQuantities(Set<CartItem> cartItemSet) throws InsufficientStockException {
        Set<CartItem> unavailableCartItemItems = cartItemSet.stream()
                .filter(cart -> cart.getQuantity() > cart.getProduct().getStockQuantity())
                .collect(Collectors.toSet());
        if (!unavailableCartItemItems.isEmpty()){
            throw new InsufficientStockException("There is not enough stock available for the following items: ", unavailableCartItemItems);
        }
        for (CartItem cartItem : cartItemSet) {
            productService.reduceQuantity(cartItem.getQuantity(), cartItem.getProduct());
        }
    }

    private void createOrderItemsFromCart(Set<CartItem> cartItemSet, Order order) {
        Set<OrderItem> orderItemSet = cartItemSet.stream().map(cart -> {
            return new OrderItem(cart.getQuantity(), cart.getProduct(), order);
        }).collect(Collectors.toSet());
        orderItemRepository.saveAll(orderItemSet);
    }

}
