package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.OrderProductRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.dto.OrderDTO;
import com.example.demo.utils.CommonUtils;
import com.example.demo.utils.OrderUtils;
import com.example.demo.utils.enumeration.OrderStatus;
import com.example.demo.utils.enumeration.PaymentType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

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
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, OrderProductRepository orderProductRepository, ProductServiceImpl productServiceImpl, NamedParameterJdbcTemplate namedParameterJdbcTemplate, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderProductRepository = orderProductRepository;
        this.productServiceImpl = productServiceImpl;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.productRepository = productRepository;
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
               orders.add(this.createOrderWithProductsFromOneShop(value, userId));
            });
        if(!orders.isEmpty()){
            return orders;
        }
        else
            throw new RuntimeException("User not found");
    }



    @Override
    public Page<OrderDTO> findOrdersByUser(Long userId, int page, int size, String status) {
        Pageable pageable = PageRequest.of(page-1, size);
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        Page<Orders> ordersPage = orderRepository.findByUserId(userId, pageable);
        List<OrderDTO> orderDTOs = ordersPage.getContent().stream()
                .filter(order -> order.getStatus() == orderStatus)
                .map(order -> this.findOrderById(order.getId()))
                .collect(Collectors.toList());
        return new PageImpl<>(orderDTOs, pageable, ordersPage.getTotalElements());
    }

    @Override
    public List<Orders> findOrdersByMerchant(Long merchantId) {
        return orderRepository.findByMerchantId(merchantId);
    }

    @Override
    public Orders getOrderById(Long orderId) {
        Optional<Orders> orders = orderRepository.findById(orderId);
        if(orders.isPresent()){
            orders.get().setProducts(orderProductRepository.findByProductOrderPK_Order_Id(orderId).stream().collect(Collectors.toSet()));;
        }
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
        orders.setOrderCode(OrderUtils.generateOrderCode(namedParameterJdbcTemplate));
        orders.setStatus(OrderStatus.PENDING);
        orders.setTotal((double) 0);
        Orders finalOrder = orderRepository.save(orders);
        products.forEach(variant -> {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProductOrderPK(new ProductOrderPK());
            orderProduct.getProductOrderPK().setOrder(finalOrder);
            orderProduct.getProductOrderPK().setVariant(variant);
            orderProduct.setQuantity(variant.getQuantity());
            orderProduct.setPrice(variant.getPrice());
            Double variantPrice = orderProduct.getProductOrderPK().getVariant().getPrice()*orderProduct.getQuantity().doubleValue();
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
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(getNextStatus(order.getStatus()));
        return orderRepository.save(order);
    }


    @Override
    public List<Orders> getOrdersByMerchantAndDateRange(Long merchantId, Date startDate, Date endDate) {
        return findOrdersByMerchant(merchantId).stream()
                .filter(order -> order.getDate().after(startDate) && order.getDate().before(endDate))
                .collect(Collectors.toList());
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

        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        return getOrdersByMerchantAndDateRange(merchantId, start, end).stream()
                .collect(Collectors.groupingBy(
                        order -> order.getDate().toInstant().atZone(ZoneId.systemDefault()).getMonthValue(),
                        Collectors.summingDouble(Orders::getTotal)
                ));
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

    @Override
    public Map<Integer, Double> getRevenueByShopId(Long shopId, String time) {
        Map<Integer, Double> revenueMap = new HashMap<>();
        switch (time){
            case "thisMonth":
                 revenueMap = this.getDailyRevenueForMonth(shopId, LocalDate.now().getYear(), LocalDate.now().getMonthValue());
                 break;
            case "lastMonth":
                revenueMap = this.getDailyRevenueForMonth(shopId, LocalDate.now().getYear(), LocalDate.now().getMonthValue()-1);
                break;
            case "lastQuarter":
                revenueMap = this.getDailyRevenueForQuarter(shopId, LocalDate.now().getYear(), LocalDate.now().getMonthValue()/4+1);
                break;
            case "lastYear":
                revenueMap = this.getDailyRevenueForYear(shopId, LocalDate.now().getYear()-1);
                break;
            case "thisYear":
                revenueMap = this.getDailyRevenueForYear(shopId, LocalDate.now().getYear());
                break;
            default:
        }
        return revenueMap;
    }

    @Transactional
    @Override
    public OrderDTO toDto(Orders order) {
          OrderDTO orderDTO = new OrderDTO();
          orderDTO.setId(String.valueOf(order.getId()));
          orderDTO.setTotal(String.valueOf(order.getTotal()));
          orderDTO.setStatus(order.getStatus().name());
          orderDTO.setOrderDate(order.getDate());
          orderDTO.setOrderCode(order.getOrderCode());
          orderDTO.setMerchantNumber(order.getMerchant().getId());
          orderDTO.setMerchantName(order.getMerchant().getName());
          if(!CommonUtils.isEmpty(order.getProducts())){
              orderDTO.setVariants(order.getProducts().stream().map(product -> {
                  Variant variant = product.getProductOrderPK().getVariant();
                  variant.setQuantity(product.getQuantity());
                  variant.setPrice(product.getPrice() == null ? variant.getPrice() : product.getPrice());
                  return variant;
              }).toList());
          }
          if(!CommonUtils.isEmpty(order.getUser())){
              orderDTO.setCustomerName(order.getUser().getName());
              orderDTO.setName(order.getUser().getName());
              orderDTO.setEmail(order.getUser().getEmail());
              orderDTO.setPhone(order.getUser().getPhoneNumber());
          }
          return orderDTO;
    }

    @Override
    public List<OrderDTO> findOrdersByMerchantPaged(Long merchantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByMerchantId(merchantId, pageable)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public OrderDTO findOrderById(Long id) {
        Orders order = this.getOrderById(id);
        OrderDTO orderDTO = toDto(order);
            orderDTO.setVariants(order.getProducts().stream().map(product -> {
                Variant variant = product.getProductOrderPK().getVariant();
                variant.setQuantity(product.getQuantity());
                return variant;
            }).collect(Collectors.toList()));
            orderDTO.setAddress(order.getAddress());
            return orderDTO;
    }

    @Override
    public Map<String, Object> compareRevenueWithPreviousMonth(Long shopId) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        int previousMonth = currentMonth - 1;
        int previousYear = currentYear;

        if (previousMonth == 0) {
            previousMonth = 12;
            previousYear--;
        }

        Map<Integer, Double> currentMonthRevenue = getDailyRevenueForMonth(shopId, currentYear, currentMonth);
        Map<Integer, Double> previousMonthRevenue = getDailyRevenueForMonth(shopId, previousYear, previousMonth);

        double currentTotal = currentMonthRevenue.values().stream().mapToDouble(Double::doubleValue).sum();
        double previousTotal = previousMonthRevenue.values().stream().mapToDouble(Double::doubleValue).sum();

        double percentageChange = ((currentTotal - previousTotal) / previousTotal) * 100;

        Map<String, Object> result = new HashMap<>();
        result.put("currentMonthRevenue", currentTotal);
        result.put("previousMonthRevenue", previousTotal);
        result.put("percentageChange", percentageChange);
        result.put("currentMonthData", currentMonthRevenue);
        result.put("previousMonthData", previousMonthRevenue);

        return result;
    }

    @Override
    public Map<String, Object> compareOrderCountWithPreviousMonth(Long shopId) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        int previousMonth = currentMonth - 1;
        int previousYear = currentYear;

        if (previousMonth == 0) {
            previousMonth = 12;
            previousYear--;
        }

        Map<Integer, Long> currentMonthOrderCount = getMonthlyOrderCount(shopId, currentYear, currentMonth);
        Map<Integer, Long> previousMonthOrderCount = getMonthlyOrderCount(shopId, previousYear, previousMonth);

        long currentTotal = currentMonthOrderCount.values().stream().mapToLong(Long::longValue).sum();
        long previousTotal = previousMonthOrderCount.values().stream().mapToLong(Long::longValue).sum();

        double percentageChange = previousTotal != 0 ? ((currentTotal - previousTotal) / (double) (previousTotal == 0 ? 1 : previousTotal)) * 100 : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("currentMonthOrderCount", currentTotal);
        result.put("previousMonthOrderCount", previousTotal);
        result.put("percentageChange", percentageChange);
        result.put("currentMonthData", currentMonthOrderCount);
        result.put("previousMonthData", previousMonthOrderCount);

        return result;
    }

    private Map<Integer, Long> getMonthlyOrderCount(Long shopId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        return getOrdersByMerchantAndDateRange(shopId, start, end).stream()
                .collect(Collectors.groupingBy(
                        order -> order.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth(),
                        Collectors.counting()
                ));
    }
}
