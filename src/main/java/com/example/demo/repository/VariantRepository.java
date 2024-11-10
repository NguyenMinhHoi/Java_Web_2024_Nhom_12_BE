package com.example.demo.repository;

import com.example.demo.model.OptionProduct;
import com.example.demo.model.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {
    @Query(value = "SELECT v.id FROM variants v " +
            "JOIN variant_options vo ON v.id = vo.variant_id " +
            "WHERE vo.options_id IN :optionIds " +
            "GROUP BY v.id " +
            "HAVING COUNT(DISTINCT vo.options_id) = :size",
            nativeQuery = true)
    Long findVariantByExactOptions(@Param("optionIds") Set<Long> optionIds, @Param("size") int size);

    List<Variant> findVariantByProductId(Long id);

    Variant findVariantById(Long id);
}