package com.example.demo.repository;

import com.example.demo.model.Merchant;
import com.example.demo.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
      Double countByShopsContaining(Merchant Merchant);

    @Query(value = "SELECT COUNT(*) FROM wishlist w JOIN wishlist_shops ws ON w.id = ws.wishlist_id WHERE ws.shops_id = :merchantId", nativeQuery = true)
    Long countByShopsContaining(@Param("merchantId") Long merchantId);

    Wishlist findByUserId(Long id);
}
