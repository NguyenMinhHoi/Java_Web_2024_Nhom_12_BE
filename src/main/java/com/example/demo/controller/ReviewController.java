package com.example.demo.controller;

import com.example.demo.model.Review;
import com.example.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reviews")
@CrossOrigin("*")
public class ReviewController {

      @Autowired
      private ReviewService reviewService;

      @GetMapping("/product/{productId}")
      public ResponseEntity<List<Review>> getReviewsByProductId(@PathVariable Long productId) {
             return new ResponseEntity<>(reviewService.findByProductId(productId),HttpStatus.OK);
      }

      @PostMapping("")
      public ResponseEntity<Review> createReview(@RequestBody Review review) {
             return new ResponseEntity<>(reviewService.save(review), HttpStatus.CREATED);
      }
}
