package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.Review;
import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReviewService extends GenerateService<Review> {
    List<Review> findByProductId(Long productId);
    List<Review> findByUserId(Long userId);
    List<Review> findByRating(Integer rating);
    List<Review> findLatestReviews(int limit);
    Double getAverageRatingForProduct(Long productId);
    List<Review> findReviewsByDateRange(Date startDate, Date endDate);
    Page<Review> findPaginatedReviews(int page, int size);
    List<Review> findTopRatedReviews(int limit);
    List<Review> findLowRatedReviews(int limit);
    Long countReviewsForProduct(Long productId);
    Map<Integer, Long> getRatingDistributionForProduct(Long productId);
    List<Review> findReviewsWithImages();
    List<Review> searchReviewsByKeyword(String keyword);
    List<Review> findReviewsByProductIdSortedByDate(Long productId, Sort.Direction direction);
    void deleteReviewsByProductId(Long productId);
    List<Product> findTopRatedProducts(int limit);
    List<User> findTopReviewers(int limit);
    double calculateProductRatingPercentage(Long productId, int rating);
}
