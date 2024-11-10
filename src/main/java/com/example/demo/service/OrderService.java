package com.example.demo.service;

import com.example.demo.model.Orders;
import com.example.demo.model.Product;
import com.example.demo.model.Variant;
import com.example.demo.utils.enumeration.OrderStatus;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderService extends GenerateService<Orders>{
    List<Orders> findOrdersByUser(Long userId);
    List<Orders> findOrdersByMerchant(Long merchantId);
    List<Orders> createOrder(List<Variant> variants, Long userId,Long merchantNumber);
    Orders updateOrderStatus(Long orderId);
    List<Orders> findOrdersByDateRange(Date startDate, Date endDate);
    List<Orders> createOrderWithProductsFromManyShop(List<Variant> products, Long userId);
    Double calculateTotalRevenueForMerchant(Long merchantId);
    List<Product> findMostOrderedProducts(int limit);
    List<Object[]> getDailyRevenueBetweenDates(Date startDate, Date endDate);
    List<Object[]> getMonthlyRevenueBetweenDates(Date startDate, Date endDate);
    List<Object[]> getTopMerchantsByRevenue(Date startDate, Date endDate, int limit);
    List<Object[]> getTopProductsByRevenue(Date startDate, Date endDate, int limit);
    List<Object[]> getRevenueByPriceRange(Date startDate, Date endDate);
    List<Object[]> getProductRevenueAnalysis(Date startDate, Date endDate);
    List<Object[]> getProductCategoryRevenueAnalysis(Date startDate, Date endDate);
    List<Object[]> getMonthlyProductRevenueAnalysis(Date startDate, Date endDate);
    Orders getOrderById(Long orderId);
    Orders createOrderWithProductsFromOneShop(List<Variant> products, Long userId);
    Page<Product> findMostOrderedProducts(Date startDate, Date endDate, int page, int size);
    double getTotalRevenueForMerchant(Long merchantId);
    List<Orders> getOrdersByMerchantAndDateRange(Long merchantId, Date startDate, Date endDate);
    Map<String, Double> getRevenueByProductForMerchant(Long merchantId);
    List<Product> getTopSellingProductsForMerchant(Long merchantId, int limit);
    Map<String, Long> getOrderCountByStatusForMerchant(Long merchantId);
    double getAverageOrderValueForMerchant(Long merchantId);
    Map<String, Double> getMonthlyRevenueForMerchant(Long merchantId, int year);
    Map<Integer, Double> getDailyRevenueForMonth(Long merchantId, int year, int month);
    Map<Integer, Double> getDailyRevenueForQuarter(Long merchantId, int year, int quarter);
    Map<Integer, Double> getDailyRevenueForYear(Long merchantId, int year);
}
