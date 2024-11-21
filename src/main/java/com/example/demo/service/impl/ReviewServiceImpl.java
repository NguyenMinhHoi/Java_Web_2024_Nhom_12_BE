package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.ReviewService;
import com.example.demo.service.UserService;
import com.example.demo.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import com.example.demo.model.Product;
import com.example.demo.model.Review;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;


    @Autowired
    private UserService userService;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ProductRepository productRepository;


    // Các phương thức cơ bản từ GenerateService
    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public Review findById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public Review save(Review entity) {
        // luu user tu binh luan gui ve
        entity.setUser(userService.findById(entity.getUser().getId()).get());

        // luu anh neu co
        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            Set<Image> savedImages = entity.getImages().stream()
                    .map(image -> {
                        if(!CommonUtils.isEmpty(image.getPath()))
                           return imageRepository.save(image);
                        else
                            return null;
                    }).collect(Collectors.toSet());
            entity.setImages(savedImages);
        }
        // tim san pham duoc binh luan
        Product product = productRepository.findById(entity.getProduct().getId()).get();
        // luu binh luan
        Review savedReview = reviewRepository.save(entity);

        // tinh lai danh gia
        List<Review> productReviews = findByProductId(product.getId());
        double averageRating = productReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        // luu lai ket qua danh gia
        product.setRating(averageRating);
        entity.setProduct(product);
        entity.setDate(Instant.now());
        return reviewRepository.save(entity);
    }

    // Triển khai các phương thức bổ sung
    @Override
    public List<Review> findByProductId(Long productId) {
        // Lay ra tay ca cac binh luan cua san pham do
        return findAll().stream()
                .filter(review -> review.getProduct().getId().equals(productId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        return findAll().stream()
                .filter(review -> review.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByRating(Integer rating) {
        return findAll().stream()
                .filter(review -> review.getRating().equals(rating))
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> findLatestReviews(int limit) {
        return findAll().stream()
                .sorted(Comparator.comparing(Review::getDate).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageRatingForProduct(Long productId) {
        List<Review> productReviews = findByProductId(productId);
        return productReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    @Override
    public List<Review> findReviewsByDateRange(Date startDate, Date endDate) {
        return findAll().stream()
                .filter(review -> review.getDate().isAfter(startDate.toInstant()) && review.getDate().isBefore(endDate.toInstant()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Review> findPaginatedReviews(int page, int size) {
        List<Review> allReviews = findAll();
        int start = page * size;
        int end = Math.min((start + size), allReviews.size());
        return new PageImpl<>(allReviews.subList(start, end), PageRequest.of(page, size), allReviews.size());
    }

    @Override
    public List<Review> findTopRatedReviews(int limit) {
        return findAll().stream()
                .sorted(Comparator.comparing(Review::getRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> findLowRatedReviews(int limit) {
        return findAll().stream()
                .sorted(Comparator.comparing(Review::getRating))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Long countReviewsForProduct(Long productId) {
        return findByProductId(productId).stream().count();
    }

    @Override
    public Map<Integer, Long> getRatingDistributionForProduct(Long productId) {
        List<Review> productReviews = findByProductId(productId);
        return productReviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));
    }

    @Override
    public List<Review> findReviewsWithImages() {
        return findAll().stream()
                .filter(review -> review.getImages() != null && !review.getImages().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> searchReviewsByKeyword(String keyword) {
        return findAll().stream()
                .filter(review -> review.getContent().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> findReviewsByProductIdSortedByDate(Long productId, Sort.Direction direction) {
        List<Review> productReviews = findByProductId(productId);
        Comparator<Review> comparator = Comparator.comparing(Review::getDate);
        if (direction == Sort.Direction.DESC) {
            comparator = comparator.reversed();
        }
        return productReviews.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public void deleteReviewsByProductId(Long productId) {
        List<Review> productReviews = findByProductId(productId);
        productReviews.forEach(review -> deleteById(review.getId()));
    }


    @Override
    public List<Product> findTopRatedProducts(int limit) {
        // Giả sử Product có phương thức getAverageRating()
        return findAll().stream()
                .map(Review::getProduct)
                .distinct()
                .sorted(Comparator.comparing(Product::getRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findTopReviewers(int limit) {
        Map<User, Long> reviewCounts = findAll().stream()
                .collect(Collectors.groupingBy(Review::getUser, Collectors.counting()));
        
        return reviewCounts.entrySet().stream()
                .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public double calculateProductRatingPercentage(Long productId, int rating) {
        List<Review> productReviews = findByProductId(productId);
        long ratingCount = productReviews.stream()
                .filter(review -> review.getRating() == rating)
                .count();
        return (double) ratingCount / productReviews.size() * 100;
    }

}
