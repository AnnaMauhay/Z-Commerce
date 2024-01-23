package com.zalando.ecommerce.service;

import com.zalando.ecommerce.exception.EmptyCartException;
import com.zalando.ecommerce.exception.InsufficientStockException;
import com.zalando.ecommerce.exception.OrderNotFoundException;
import com.zalando.ecommerce.model.*;
import com.zalando.ecommerce.repository.OrderItemRepository;
import com.zalando.ecommerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
        createdOrder.setOrderItems(createOrderItemsFromCart(cartItemSet, createdOrder));
        cartService.deletePurchasedCartItems(user);
        return orderRepository.save(createdOrder);
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

    private List<OrderItem> createOrderItemsFromCart(Set<CartItem> cartItemSet, Order order) {
        Set<OrderItem> orderItemSet = cartItemSet.stream().map(cart -> {
            return new OrderItem(cart.getQuantity(), cart.getProduct(), order);
        }).collect(Collectors.toSet());
        return orderItemRepository.saveAll(orderItemSet);
    }

    public Order getOrderByUserAndId(User user, int orderId) throws OrderNotFoundException {
        Optional<Order> order = orderRepository.getOrderByCustomerAndOrderId(user, orderId);
        if (order.isPresent()){
            return order.get();
        }else {
            throw new OrderNotFoundException("No order matched the given order ID for this customer.");
        }
    }

}
