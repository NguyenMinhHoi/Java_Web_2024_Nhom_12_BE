package com.example.demo.repository;

import com.example.demo.model.OrderProduct;
import com.example.demo.model.Orders;
import com.example.demo.model.Product;
import com.example.demo.utils.enumeration.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository

public interface OrderRepository extends JpaRepository<Orders,Long> {

    List<Orders> findByUserId(Long userId);
    Page<Orders> findByUserId(Long userId, Pageable pageable);

    List<Orders> findByMerchantId(Long merchantId);

    Page<Orders> findByMerchantId(Long merchantId, Pageable pageable);

    List<Orders> findByDateBetween(Date startDate, Date endDate);

    @Query(value = "SELECT o.* FROM orders o WHERE o.merchant_id = :merchantId", nativeQuery = true)
    List<Orders> findOrdersByMerchantId(@Param("merchantId") Long merchantId);

    @Query(value = "SELECT op.* FROM order_product op JOIN orders o ON op.order_id = o.id WHERE o.id = :orderId", nativeQuery = true)
    List<OrderProduct> findProductsByOrderId(@Param("orderId") Long orderId);

    @Query(value = "SELECT p.*, COUNT(op.product_id) as order_count " +
            "FROM order_product op " +
            "JOIN product_variant pv ON op.variant_id = pv.id " +
            "JOIN product p ON pv.product_id = p.id " +
            "GROUP BY p.id " +
            "ORDER BY order_count DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Object[]> findMostOrderedProducts(@Param("limit") int limit);

    @Query(value = "SELECT SUM(o.total) FROM orders o WHERE o.merchant_id = :merchantId", nativeQuery = true)
    Double calculateTotalRevenueForMerchant(@Param("merchantId") Long merchantId);

    @Query(value = "SELECT DATE(o.date) as order_date, SUM(o.total) as daily_revenue " +
            "FROM orders o " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(o.date) " +
            "ORDER BY o.date", nativeQuery = true)
    List<Object[]> getDailyRevenueBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT YEAR(o.date) as year, MONTH(o.date) as month, SUM(o.total) as monthly_revenue " +
            "FROM orders o " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(o.date), MONTH(o.date)", nativeQuery = true)
    List<Object[]> getMonthlyRevenueBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT m.id as merchant_id, m.name as merchant_name, SUM(o.total) as total_revenue " +
            "FROM orders o " +
            "JOIN merchant m ON o.merchant_id = m.id " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY m.id, m.name " +
            "ORDER BY total_revenue DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Object[]> getTopMerchantsByRevenue(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("limit") Pageable limit);

    @Query(value = "SELECT p.id as product_id, p.name as product_name, SUM(op.quantity * pv.price) as total_revenue " +
            "FROM orders o " +
            "JOIN order_product op ON o.id = op.order_id " +
            "JOIN product_variant pv ON op.variant_id = pv.id " +
            "JOIN product p ON pv.product_id = p.id " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY p.id, p.name " +
            "ORDER BY total_revenue DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Object[]> getTopProductsByRevenue(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("limit") Pageable limit);

    @Query(value = "SELECT " +
            "CASE " +
            "    WHEN o.total BETWEEN 0 AND 100000 THEN '0-100K' " +
            "    WHEN o.total BETWEEN 100001 AND 500000 THEN '100K-500K' " +
            "    WHEN o.total BETWEEN 500001 AND 1000000 THEN '500K-1M' " +
            "    ELSE 'Over 1M' " +
            "END as price_range, " +
            "COUNT(o.id) as order_count, " +
            "SUM(o.total) as total_revenue " +
            "FROM orders o " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY " +
            "CASE " +
            "    WHEN o.total BETWEEN 0 AND 100000 THEN '0-100K' " +
            "    WHEN o.total BETWEEN 100001 AND 500000 THEN '100K-500K' " +
            "    WHEN o.total BETWEEN 500001 AND 1000000 THEN '500K-1M' " +
            "    ELSE 'Over 1M' " +
            "END " +
            "ORDER BY total_revenue DESC", nativeQuery = true)
    List<Object[]> getRevenueByPriceRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT p.id as product_id, p.name as product_name, " +
            "SUM(op.quantity) as total_quantity, " +
            "SUM(op.quantity * pv.price) as total_revenue " +
            "FROM orders o " +
            "JOIN order_product op ON o.id = op.order_id " +
            "JOIN product_variant pv ON op.variant_id = pv.id " +
            "JOIN product p ON pv.product_id = p.id " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY p.id, p.name " +
            "ORDER BY total_revenue DESC", nativeQuery = true)
    List<Object[]> getProductRevenueAnalysis(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT c.id as category_id, c.name as category_name, " +
            "SUM(op.quantity * pv.price) as total_revenue " +
            "FROM orders o " +
            "JOIN order_product op ON o.id = op.order_id " +
            "JOIN product_variant pv ON op.variant_id = pv.id " +
            "JOIN product p ON pv.product_id = p.id " +
            "JOIN category c ON p.category_id = c.id " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY c.id, c.name " +
            "ORDER BY total_revenue DESC", nativeQuery = true)
    List<Object[]> getProductCategoryRevenueAnalysis(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT YEAR(o.date) as year, MONTH(o.date) as month, " +
            "p.id as product_id, p.name as product_name, " +
            "SUM(op.quantity) as total_quantity, " +
            "SUM(op.quantity * pv.price) as total_revenue " +
            "FROM orders o " +
            "JOIN order_product op ON o.id = op.order_id " +
            "JOIN product_variant pv ON op.variant_id = pv.id " +
            "JOIN product p ON pv.product_id = p.id " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(o.date), MONTH(o.date), p.id, p.name " +
            "ORDER BY year, month, total_revenue DESC", nativeQuery = true)
    List<Object[]> getMonthlyProductRevenueAnalysis(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT p.*, SUM(op.quantity) AS total_quantity " +
            "FROM order_product op " +
            "JOIN product_variant pv ON op.variant_id = pv.id " +
            "JOIN product p ON pv.product_id = p.id " +
            "JOIN orders o ON op.order_id = o.id " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY p.id " +
            "ORDER BY total_quantity DESC",
            countQuery = "SELECT COUNT(DISTINCT p.id) " +
                    "FROM order_product op " +
                    "JOIN product_variant pv ON op.variant_id = pv.id " +
                    "JOIN product p ON pv.product_id = p.id " +
                    "JOIN orders o ON op.order_id = o.id " +
                    "WHERE o.date BETWEEN :startDate AND :endDate LIMIT :limit",
            nativeQuery = true)
    Page<Product> findMostOrderedProducts(@Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate,
                                          @Param("limit") Pageable pageable);

}
