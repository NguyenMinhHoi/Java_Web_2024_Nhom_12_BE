package com.example.demo.repository;

import com.example.demo.model.OrderProduct;
import com.example.demo.model.Orders;
import com.example.demo.model.Product;
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

    List<Orders> findByMerchantId(Long merchantId);

    List<Orders> findByDateBetween(Date startDate, Date endDate);

    @Query("SELECT o FROM Orders o WHERE o.merchant.id = :merchantId")
    List<Orders> findOrdersByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT o.products FROM Orders o WHERE o.id = :orderId")
    List<OrderProduct> findProductsByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT op.productOrderPK.variant.product, COUNT(op) as orderCount " +
            "FROM Orders o JOIN o.products op " +
            "GROUP BY op.productOrderPK.variant.product " +
            "ORDER BY orderCount DESC")
    List<Object[]> findMostOrderedProducts(Pageable pageable);

    @Query("SELECT SUM(o.total) FROM Orders o WHERE o.merchant.id = :merchantId")
    Double calculateTotalRevenueForMerchant(@Param("merchantId") Long merchantId);

    @Query("SELECT DATE(o.date) as orderDate, SUM(o.total) as dailyRevenue " +
            "FROM Orders o " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(o.date) " +
            "ORDER BY o.date")
    List<Object[]> getDailyRevenueBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT FUNCTION('YEAR', o.date) as year, FUNCTION('MONTH', o.date) as month, SUM(o.total) as monthlyRevenue " +
            "FROM Orders o " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('YEAR', o.date), FUNCTION('MONTH', o.date) ")
    List<Object[]> getMonthlyRevenueBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT o.merchant.id as merchantId, o.merchant.name as merchantName, SUM(o.total) as totalRevenue " +
            "FROM Orders o " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY o.merchant.id, o.merchant.name " +
            "ORDER BY totalRevenue DESC")
    List<Object[]> getTopMerchantsByRevenue(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query("SELECT op.productOrderPK.variant.product.id as productId, " +
            "op.productOrderPK.variant.product.name as productName, " +
            "SUM(op.quantity * op.productOrderPK.variant.price) as totalRevenue " +
            "FROM Orders o JOIN o.products op " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY op.productOrderPK.variant.product.id, op.productOrderPK.variant.product.name " +
            "ORDER BY totalRevenue DESC")
    List<Object[]> getTopProductsByRevenue(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query("SELECT CASE " +
            "    WHEN o.total BETWEEN 0 AND 100000 THEN '0-100K' " +
            "    WHEN o.total BETWEEN 100001 AND 500000 THEN '100K-500K' " +
            "    WHEN o.total BETWEEN 500001 AND 1000000 THEN '500K-1M' " +
            "    ELSE 'Over 1M' " +
            "END as priceRange, " +
            "COUNT(o) as orderCount, " +
            "SUM(o.total) as totalRevenue " +
            "FROM Orders o " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY CASE " +
            "    WHEN o.total BETWEEN 0 AND 100000 THEN '0-100K' " +
            "    WHEN o.total BETWEEN 100001 AND 500000 THEN '100K-500K' " +
            "    WHEN o.total BETWEEN 500001 AND 1000000 THEN '500K-1M' " +
            "    ELSE 'Over 1M' " +
            "END " +
            "ORDER BY o.total DESC")
    List<Object[]> getRevenueByPriceRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT op.productOrderPK.variant.product.id as productId, " +
            "op.productOrderPK.variant.product.name as productName, " +
            "SUM(op.quantity) as totalQuantity, " +
            "SUM(op.quantity * op.productOrderPK.variant.price) as totalRevenue " +
            "FROM Orders o JOIN o.products op " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY op.productOrderPK.variant.product.id, op.productOrderPK.variant.product.name " +
            "ORDER BY totalRevenue DESC")
    List<Object[]> getProductRevenueAnalysis(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT op.productOrderPK.variant.product.category.id as categoryId, " +
            "op.productOrderPK.variant.product.category.name as categoryName, " +
            "SUM(op.quantity * op.productOrderPK.variant.price) as totalRevenue " +
            "FROM Orders o JOIN o.products op " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY op.productOrderPK.variant.product.category.id, op.productOrderPK.variant.product.category.name " +
            "ORDER BY totalRevenue DESC")
    List<Object[]> getProductCategoryRevenueAnalysis(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT FUNCTION('YEAR', o.date) as year, " +
            "FUNCTION('MONTH', o.date) as month, " +
            "op.productOrderPK.variant.product.id as productId, " +
            "op.productOrderPK.variant.product.name as productName, " +
            "SUM(op.quantity) as totalQuantity, " +
            "SUM(op.quantity * op.productOrderPK.variant.price) as totalRevenue " +
            "FROM Orders o JOIN o.products op " +
            "WHERE o.date BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('YEAR', o.date), FUNCTION('MONTH', o.date), " +
            "op.productOrderPK.variant.product.id, op.productOrderPK.variant.product.name " +
            "ORDER BY year(o.date), month(o.date), totalRevenue DESC")
    List<Object[]> getMonthlyProductRevenueAnalysis(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT op.productOrderPK.variant.product AS product, SUM(op.quantity) AS totalQuantity " +
            "FROM OrderProduct op " +
            "WHERE op.productOrderPK.order.date BETWEEN :startDate AND :endDate " +
            "GROUP BY op.productOrderPK.variant.product " +
            "ORDER BY totalQuantity DESC")
    Page<Product> findMostOrderedProducts(@Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate,
                                          Pageable pageable);
}
