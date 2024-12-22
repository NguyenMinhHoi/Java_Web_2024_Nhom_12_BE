package com.example.demo.service;

import com.example.demo.model.Orders;
import com.example.demo.model.Product;
import com.example.demo.model.Variant;
import com.example.demo.service.dto.OrderDTO;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface OrderService extends GenerateService<Orders>{

    Page<OrderDTO> findOrdersByUser(Long userId, int page, int size, String status);

    List<Orders> findOrdersByMerchant(Long merchantId);
    OrderDTO findOrderById(Long id);
    List<Orders> createOrder(List<Variant> variants, Long userId,Long merchantNumber, HashMap<String,String> details);
    Orders updateOrderStatus(Long orderId);
    List<Orders> createOrderWithProductsFromManyShop(List<Variant> products, Long userId);
    Orders getOrderById(Long orderId);
    Orders createOrderWithProductsFromOneShop(List<Variant> products, Long userId);
    List<Orders> getOrdersByMerchantAndDateRange(Long merchantId, Date startDate, Date endDate);
    Map<Integer, Double> getDailyRevenueForMonth(Long merchantId, int year, int month);
    Map<Integer, Double> getDailyRevenueForQuarter(Long merchantId, int year, int quarter);
    Map<Integer, Double> getDailyRevenueForYear(Long merchantId, int year);

    Map<Integer, Double> getRevenueByShopId(Long shopId, String time);

    OrderDTO toDto(Orders order);

    List<OrderDTO> findOrdersByMerchantPaged(Long merchantId, int page, int size);

    Map<String, Object> compareRevenueWithPreviousMonth(Long shopId);

    Map<String, Object> compareOrderCountWithPreviousMonth(Long shopId);
}
