package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.OrderProductRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.utils.CommonUtils;
import com.example.demo.utils.OrderUtils;
import com.example.demo.utils.enumeration.OrderStatus;
import com.example.demo.utils.enumeration.PaymentType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.Instant;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductServiceImpl productServiceImpl;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, OrderProductRepository orderProductRepository, ProductServiceImpl productServiceImpl, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderProductRepository = orderProductRepository;
        this.productServiceImpl = productServiceImpl;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Orders> findAll() {
        return List.of();
    }

    @Override
    public Orders findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public Orders save(Orders entity) {

        return null;
    }

    /**
     * Creates orders with products from multiple shops for a specific user.
     * This method groups variants by their associated merchants, creates separate orders for each merchant,
     * and updates the product stock accordingly.
     *
     * @param variants A list of Variant objects representing the products to be ordered.
     *                 Each variant should contain information about the product, quantity, and associated merchant.
     * @param userId   The ID of the user placing the order.
     * @throws RuntimeException if the user with the given ID is not found.
     */
    @Transactional
    @Override
    public List<Orders> createOrderWithProductsFromManyShop(List<Variant> variants, Long userId) {
        HashMap<String, List<Variant>> productMap = new HashMap<>();
        List<Orders> orders = new ArrayList<>();
            for (int i = 0; i < variants.size(); i++) {
                List<Variant> firstShopProducts = variants.stream()
                        .filter(variant -> variants.stream().findFirst()
                                .map(v -> v.getProduct().getMerchant().getId())
                                .map(id -> id.equals(variant.getProduct().getMerchant().getId()))
                                .orElse(false))
                        .toList();
                variants.stream().findFirst().ifPresent(v -> 
                    productMap.put(String.valueOf(v.getProduct().getMerchant().getId()), firstShopProducts)
                );
                variants.removeAll(firstShopProducts);
            }
            productMap.forEach((key, value) -> {
                value.stream().findFirst().ifPresent(firstVariant -> {
                    Orders order = new Orders();
                    order.setMerchant(firstVariant.getProduct().getMerchant());
                    order.setStatus(OrderStatus.PENDING);
                    Orders finalOrder = orderRepository.save(order);
                    value.forEach(variant -> {
                        Double orderPrice = 0.0;
                        OrderProduct orderProduct = new OrderProduct();
                        orderProduct.setProductOrderPK(new ProductOrderPK());
                        orderProduct.getProductOrderPK().setOrder(finalOrder);
                        orderProduct.getProductOrderPK().setVariant(variant);
                        orderProduct.setQuantity(variant.getQuantity());
                        orderPrice += orderProduct.getQuantity().doubleValue();
                        orderProductRepository.save(orderProduct);
                        finalOrder.setTotal(finalOrder.getTotal() + orderPrice);
                        productServiceImpl.updateProductStock(variant.getProduct().getId(), variant.getQuantity(), variant.getId());
                    });
                    orderRepository.save(finalOrder);
                    orders.add(finalOrder);
                });
            });
        if(!orders.isEmpty()){
            return orders;
        }
        else
            throw new RuntimeException("User not found");
    }



    @Override
    public List<Orders> findOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Orders> findOrdersByMerchant(Long merchantId) {
        return orderRepository.findByMerchantId(merchantId);
    }

    @Override
    public Orders getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    /**
     * Creates an order with products from a single shop for a specific user.
     * This method creates a new order, associates it with the user and merchant,
     * adds the specified products to the order, updates the product stock,
     * and calculates the total price of the order.
     *
     * @param products A list of Variant objects representing the products to be ordered.
     *                 Each variant should contain information about the product, quantity, and associated merchant.
     * @param userId   The ID of the user placing the order.
     * @return The created and saved Orders object representing the new order.
     * @throws RuntimeException if the user with the given ID is not found.
     */
    @Override
    @Transactional
    public Orders createOrderWithProductsFromOneShop(List<Variant> products, Long userId) {
        Orders orders = new Orders();
        orders.setMerchant(products.stream().findFirst().get().getProduct().getMerchant());
        orders.setStatus(OrderStatus.PENDING);
        orders.setTotal((double) 0);
        Orders finalOrder = orderRepository.save(orders);
        products.forEach(variant -> {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProductOrderPK(new ProductOrderPK());
            orderProduct.getProductOrderPK().setOrder(finalOrder);
            orderProduct.getProductOrderPK().setVariant(variant);
            orderProduct.setQuantity(variant.getQuantity());
            Double variantPrice = orderProduct.getQuantity().doubleValue();
            orderProductRepository.save(orderProduct);
            finalOrder.setTotal(finalOrder.getTotal() + variantPrice);
            productServiceImpl.updateProductStock(variant.getProduct().getId(), variant.getQuantity(), variant.getId());
        });
        return orderRepository.save(finalOrder);
    }

    /**
     * Creates one or more orders based on the provided variants, user, and order details.
     * This method handles creating orders from a single shop or multiple shops,
     * sets order details, and updates the order status.
     *
     * @param variants        A list of Variant objects representing the products to be ordered.
     * @param userId          The ID of the user placing the order. Can be null for guest orders.
     * @param merchantNumber  The number of merchants involved in the order. 1 for single shop, >1 for multiple shops.
     * @param details         A HashMap containing order details such as address, payment type, name, phone, and email.
     * @return                A List of Orders objects representing the created orders.
     * @throws RuntimeException if there's an error during order creation, including user not found.
     */
    @Transactional
    public List<Orders> createOrder(List<Variant> variants, Long userId, Long merchantNumber, HashMap<String,String> details) {
        try {
            List<Orders> orders = new LinkedList<>();
            if(merchantNumber == 1){
                orders = Collections.singletonList(createOrderWithProductsFromOneShop(variants, userId));
            } else {
                orders = createOrderWithProductsFromManyShop(variants, userId);
            }
            
            User user = null;
            if(userId != null){
                user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            }
            
            for (Orders order : orders) {
                order.setAddress(details.get("address"));
                order.setDate(new Date());
                order.setPaymentType(PaymentType.valueOf(details.get("paymentType")));
                order.setName(details.get("name"));
                order.setPhone(details.get("phone"));
                order.setEmail(details.get("email"));
                order.setStatus(OrderStatus.PENDING);
                if (user != null) {
                    order.setUser(user);
                }
            }
            orders = orderRepository.saveAll(orders);
            orders.stream().forEach(order -> OrderUtils.updateOrderStatus(namedParameterJdbcTemplate, String.valueOf(order.getId()), order.getStatus().toString()));
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating order", e);
        }
    }

    private static final OrderStatus[] ORDER_STATUSES = {
            OrderStatus.PENDING,
            OrderStatus.DOING,
            OrderStatus.SHIPPING,
            OrderStatus.DONE,
            OrderStatus.CANCEL
    };

    private OrderStatus getNextStatus(OrderStatus currentStatus) {
        for (int i = 0; i < ORDER_STATUSES.length - 1; i++) {
            if (ORDER_STATUSES[i] == currentStatus) {
                return ORDER_STATUSES[i + 1];
            }
        }
        return currentStatus;
    }

    /**
     * Updates the status of an order to the next sequential status.
     * The order of statuses is defined in the ORDER_STATUSES array.
     *
     * @param orderId The unique identifier of the order to be updated.
     * @return The updated Orders object with the new status.
     * @throws RuntimeException if the order with the given ID is not found.
     */
    @Override
    public Orders updateOrderStatus(Long orderId) {
        // Implementation to update the status of an order
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(getNextStatus(order.getStatus()));
        return orderRepository.save(order);
    }

    @Override
    public List<Orders> findOrdersByDateRange(Date startDate, Date endDate) {
        // Implementation to find orders within a specific date range
        return orderRepository.findByDateBetween(startDate, endDate);
    }

    @Override
    public Double calculateTotalRevenueForMerchant(Long merchantId) {
        // Implementation to calculate total revenue for a merchant
        List<Orders> merchantOrders = orderRepository.findByMerchantId(merchantId);
        return merchantOrders.stream()
                .mapToDouble(Orders::getTotal)
                .sum();
    }

    /**
     * Finds the most ordered products within a specific date range, with pagination.
     *
     * @param startDate The start date of the range to search for orders.
     * @param endDate The end date of the range to search for orders.
     * @param page The page number (0-based) for pagination.
     * @param size The number of items per page.
     * @return A Page of Product objects representing the most ordered products.
     */
    @Override
    public Page<Product> findMostOrderedProducts(Date startDate, Date endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findMostOrderedProducts(startDate, endDate, pageable);
    }

    @Override
    public List<Object[]> getDailyRevenueBetweenDates(Date startDate, Date endDate) {
        return orderRepository.getDailyRevenueBetweenDates(startDate, endDate);
    }

    @Override
    public List<Object[]> getMonthlyRevenueBetweenDates(Date startDate, Date endDate) {
        return orderRepository.getMonthlyRevenueBetweenDates(startDate, endDate);
    }

    @Override
    public List<Object[]> getTopMerchantsByRevenue(Date startDate, Date endDate, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.getTopMerchantsByRevenue(startDate, endDate, pageable);
    }

    @Override
    public List<Object[]> getTopProductsByRevenue(Date startDate, Date endDate, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.getTopProductsByRevenue(startDate, endDate, pageable);
    }

    @Override
    public List<Object[]> getRevenueByPriceRange(Date startDate, Date endDate) {
        return orderRepository.getRevenueByPriceRange(startDate, endDate);
    }


    @Override
    public List<Object[]> getProductRevenueAnalysis(Date startDate, Date endDate) {
        return orderRepository.getProductRevenueAnalysis(startDate, endDate);
    }

    @Override
    public List<Object[]> getProductCategoryRevenueAnalysis(Date startDate, Date endDate) {
        return orderRepository.getProductCategoryRevenueAnalysis(startDate, endDate);
    }

    @Override
    public List<Object[]> getMonthlyProductRevenueAnalysis(Date startDate, Date endDate) {
        return orderRepository.getMonthlyProductRevenueAnalysis(startDate, endDate);
    }

    @Override
    public List<Product> findMostOrderedProducts(int limit) {
        return List.of();
    }


    @Override
    public double getTotalRevenueForMerchant(Long merchantId) {
        return findOrdersByMerchant(merchantId).stream()
                .mapToDouble(Orders::getTotal)
                .sum();
    }

    @Override
    public List<Orders> getOrdersByMerchantAndDateRange(Long merchantId, Date startDate, Date endDate) {
        return findOrdersByMerchant(merchantId).stream()
                .filter(order -> order.getDate().after(startDate) && order.getDate().before(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Double> getRevenueByProductForMerchant(Long merchantId) {
        return findOrdersByMerchant(merchantId).stream()
                .flatMap(order -> order.getProducts().stream())
                .collect(Collectors.groupingBy(
                        op -> op.getProductOrderPK().getVariant().getProduct().getName(),
                        Collectors.summingDouble(op -> op.getQuantity() * op.getProductOrderPK().getVariant().getPrice())
                ));
    }

    @Override
    public List<Product> getTopSellingProductsForMerchant(Long merchantId, int limit) {
        Map<Product, Long> productSales = findOrdersByMerchant(merchantId).stream()
                .flatMap(order -> order.getProducts().stream())
                .collect(Collectors.groupingBy(
                        op -> op.getProductOrderPK().getVariant().getProduct(),
                        Collectors.summingLong(op -> op.getQuantity())
                ));

        return productSales.entrySet().stream()
                .sorted(Map.Entry.<Product, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getOrderCountByStatusForMerchant(Long merchantId) {
        return findOrdersByMerchant(merchantId).stream()
                .collect(Collectors.groupingBy(
                        order -> order.getStatus().name(),
                        Collectors.counting()
                ));
    }

    @Override
    public double getAverageOrderValueForMerchant(Long merchantId) {
        List<Orders> orders = findOrdersByMerchant(merchantId);
        return orders.stream()
                .mapToDouble(Orders::getTotal)
                .average()
                .orElse(0.0);
    }

    @Override
    public Map<String, Double> getMonthlyRevenueForMerchant(Long merchantId, int year) {
        return findOrdersByMerchant(merchantId).stream()
                .filter(order -> order.getDate().getYear() + 1900 == year)
                .collect(Collectors.groupingBy(
                        order -> String.format("%02d", order.getDate().getMonth() + 1),
                        Collectors.summingDouble(Orders::getTotal)
                ));
    }
    @Override
    public Map<Integer, Double> getDailyRevenueForMonth(Long merchantId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return getDailyRevenueForDateRange(merchantId, startDate, endDate);
    }

    @Override
    public Map<Integer, Double> getDailyRevenueForQuarter(Long merchantId, int year, int quarter) {
        LocalDate startDate = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        return getDailyRevenueForDateRange(merchantId, startDate, endDate);
    }

    @Override
    public Map<Integer, Double> getDailyRevenueForYear(Long merchantId, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        return getDailyRevenueForDateRange(merchantId, startDate, endDate);
    }

    private Map<Integer, Double> getDailyRevenueForDateRange(Long merchantId, LocalDate startDate, LocalDate endDate) {
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        return getOrdersByMerchantAndDateRange(merchantId, start, end).stream()
                .collect(Collectors.groupingBy(
                        order -> order.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth(),
                        Collectors.summingDouble(Orders::getTotal)
                ));
    }
}
