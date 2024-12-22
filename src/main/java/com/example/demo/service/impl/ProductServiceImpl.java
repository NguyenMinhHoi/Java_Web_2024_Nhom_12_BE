package com.example.demo.service.impl;


import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.MerchantService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import com.example.demo.service.dto.FilterDTO;
import com.example.demo.service.dto.MerchantDTO;
import com.example.demo.service.dto.ProductDTO;
import com.example.demo.utils.AccessUtils;
import com.example.demo.utils.CodeUtils;
import com.example.demo.utils.CommonUtils;
import com.example.demo.utils.Const;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final GroupOptionRepository groupOptionRepository;
    private final OptionRepository optionRepository;
    private final ReviewRepository reviewRepository;
    private final CartRepository cartRepository;
    private final ImageRepository imageRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final UserService userService;
    private final MerchantService merchantService;


    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public ProductDTO toProductDTO(Product product) {
        ProductDTO productDTO = ProductDTO.builder()
                .sold(product.getSold())
                .name(product.getName())
                .image(product.getImage())
                .category(product.getCategory())
                .description(product.getDescription())
                .rating(product.getRating())
                .id(product.getId())
                .groupOptions(product.getGroupOptions())
                .createdAt(product.getCreatedDate())
                .build();
        List<Variant> variants = variantRepository.findVariantByProductId(product.getId());
        if(!CommonUtils.isEmpty(product.getIsDiscount()) && product.getIsDiscount()){
            Optional<Variant> maxDiscountVariant = variants.stream()
                    .filter(v -> v.getSalePrice() != null && v.getPrice() != null)
                    .max(Comparator.comparingDouble(v -> v.getPrice() - v.getSalePrice()));

            if (maxDiscountVariant.isPresent()) {
                Variant variant = maxDiscountVariant.get();
                productDTO.setSalePrice(String.valueOf(maxDiscountVariant.get().getSalePrice()));
                productDTO.setDiscount(String.valueOf((1 - variant.getSalePrice()/variant.getPrice())*100));
            }
            productDTO.setIsDiscount(product.getIsDiscount());
        }
        
        if (!CommonUtils.isEmpty(product.getMaxPrice()) && !CommonUtils.isEmpty(product.getMinPrice())){
            productDTO.setMaxPrice(product.getMaxPrice());
            productDTO.setMinPrice(product.getMinPrice());
        } else {
            productDTO.setMaxPrice(null);
            productDTO.setMinPrice(null);
        }
        productDTO.setMerchantAddress(product.getMerchant().getAddress().getProvince());

        return productDTO;
    }

    @Override
    public HashMap<String, Object> getDetailsProducts(Long productId) {
        // hashmap tra ve
        HashMap<String,Object> result = new HashMap<>();

        // tim san pham tu id
        Product product = productRepository.findById(productId).orElse(null);

        //put product vao ket qua tra ve
        ProductDTO productDTO = toProductDTO(product);
        result.put("product",productDTO);

        // tim cac phan loai tu san pham roi put vao kq tra ve
        List<Variant> variants = variantRepository.findVariantByProductId(productId);
        result.put("variants",variants);

        // tim cac san pham lien quan roi put vao kq tra ve
        List<ProductDTO> relatedProducts = getRelatedProducts(productId);
        result.put("relatedProducts",relatedProducts);

        // tim nguoi ban tu san pham roi put vao kq tra ve
        MerchantDTO merchantDTO = merchantService.getMerchantByMerchantID(product.getMerchant().getId());
        merchantDTO.setComments(null);
        merchantDTO.setVariants(null);
        result.put("merchant", merchantDTO);
        return result;
    }

    @Override
    public List<ProductDTO> getRelatedProducts(Long productId) {
        // Find the current product
        Product currentProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Find products in the same category, excluding the current product
        List<Product> relatedProducts = productRepository.findByCategoryAndIdNot(
                currentProduct.getCategory(),
                currentProduct.getId(),
                PageRequest.of(0, 10) // Limit to 10 related products
        );

        // If we don't have enough related products, find more based on other criteria
        if (relatedProducts.size() < 10) {
            List<Product> additionalProducts = productRepository.findByIdNot(
                    currentProduct.getId(),
                    PageRequest.of(0, 10 - relatedProducts.size())
            );
            relatedProducts.addAll(additionalProducts);
        }

        // Sort related products by relevance (you can define your own sorting logic)
        relatedProducts.sort((p1, p2) -> {
            // Compare ratings, handling null values
            if (p1.getRating() == null && p2.getRating() == null) {
                // If both ratings are null, compare by sold count
                return compareSoldCounts(p1, p2);
            } else if (p1.getRating() == null) {
                return 1; // p2 comes first if only p1's rating is null
            } else if (p2.getRating() == null) {
                return -1; // p1 comes first if only p2's rating is null
            }

            int ratingComparison = Double.compare(p2.getRating(), p1.getRating());
            if (ratingComparison != 0) {
                return ratingComparison;
            }

            // If ratings are equal, compare by sold count
            return compareSoldCounts(p1, p2);
        });
        // Convert to DTOs and return
        return relatedProducts.stream()
                .map(this::toProductDTO)
                .collect(Collectors.toList());
    }

    private int compareSoldCounts(Product p1, Product p2) {
        if (p1.getSold() == null && p2.getSold() == null) {
            return 0; // If both sold counts are null, consider them equal
        } else if (p1.getSold() == null) {
            return 1; // p2 comes first if only p1's sold count is null
        } else if (p2.getSold() == null) {
            return -1; // p1 comes first if only p2's sold count is null
        }
        return Long.compare(p2.getSold(), p1.getSold()); // Compare non-null sold counts
    }
        @Override
        public List<ProductDTO> findAllPage(int page, int size) {

            List<Product> products = productRepository.findAll();
            products = products.subList((page - 1) * size, Math.min(page * size, products.size()));

            return products.stream()
                    .map(this::toProductDTO)
                    .collect(Collectors.toList());
        }


    @Override
    public Product save(Product entity) {
        return entity;
    }

    @Override
    public Product createProduct(Product product) {
        AccessUtils.setAccessMerchant(namedParameterJdbcTemplate,product.getMerchant().getId());
        // Tạo và lưu Image
        Set<Image> savedImages = product.getImage().stream().map(image1 ->
            imageRepository.save(image1)
        ).collect(Collectors.toSet());
        // Cập nhật tham chiếu Image trong Product
        product.setImage(savedImages);
        product.setCreatedDate(new Date(System.currentTimeMillis()));
        product.setUpdatedDate(new Date(System.currentTimeMillis()));

        // Lưu Product
        Product savedProduct = productRepository.save(product);

        // Tạo và lưu Variant mặc định
        Variant variant = new Variant();
        Set<OptionProduct> optionProducts = new HashSet<>();

        // Tạo và lưu OptionProduct
        OptionProduct defaultOption = OptionProduct.builder().name(Const.DEFAULT).build();
        OptionProduct savedOption = optionRepository.save(defaultOption);

        optionProducts.add(savedOption);
        variant.setOptions(optionProducts);
        variant.setProduct(savedProduct);
        Image variantCloneImage = new Image();
        variantCloneImage.setPath(savedImages.stream().findFirst().get().getPath());
        variantCloneImage.setId(null);
        variantCloneImage = imageRepository.save(variantCloneImage);
        variant.setImage(variantCloneImage);
        variantRepository.save(variant);

        return savedProduct;
    }

    @Override
    public List<Variant> getVariantsByProductId(Long productId) {
        List<Variant> variants = variantRepository.findVariantByProductId(productId);
        variants.stream().forEach(variant -> {
            variant.getProduct().setMerchant(null);
            variant.setProduct(null);
        });
        return variants;
    }

    private List<Variant> generateVariants(List<GroupOption> groupOptions, Product product) {
        List<Variant> variants = new ArrayList<>();
        generateVariantsRecursive(groupOptions, 0, new HashSet<>(), variants, product);
        return variants;
    }

    private void generateVariantsRecursive(List<GroupOption> groupOptions, int groupIndex,
                                           Set<OptionProduct> currentOptions,
                                           List<Variant> variants, Product product) {
        if (groupIndex == groupOptions.size()) {
            Variant variant = new Variant();
            variant.setOptions(new HashSet<>(currentOptions));
            variant.setProduct(product);
            variants.add(variant);
            return;
        }

        GroupOption currentGroup = groupOptions.get(groupIndex);
        for (OptionProduct option : currentGroup.getOptions()) {
            currentOptions.add(option);
            generateVariantsRecursive(groupOptions, groupIndex + 1, currentOptions, variants, product);
            currentOptions.remove(option);
        }
    }

    /**
     * Saves variants for a product based on the provided group options.
     * This method creates and saves new variants for a product by combining all possible
     * option combinations from the given group options.
     *
     * @param groupOptions A list of GroupOption objects representing the different option groups for the product.
     * @param productId The ID of the product (contract) for which variants are being saved.
     *
     * This method doesn't return a value, but it performs the following operations:
     * 1. Updates the product's group options.
     * 2. Creates new variants by combining all possible options from different groups.
     * 3. Saves the newly created variants to the database.
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Variant> saveVariants(List<GroupOption> groupOptions, Long productId) {
        if(!CommonUtils.isEmpty(userService.getCurrentUser().getId())){
            AccessUtils.setAccessMerchant(namedParameterJdbcTemplate,userService.getCurrentUser().getId());
        }
        try {
            for (GroupOption groupOption : groupOptions) {
                if(groupOption.getOptions().size()  == 0){
                    throw new RuntimeException("Group option must have at least one option");
                }
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            product.setUpdatedDate(new Date(System.currentTimeMillis()));
    
            List<Variant> productVariants = variantRepository.findVariantByProductId(productId);

            productVariants.forEach(variant -> {
                if(variant.getOptions().size() < groupOptions.size()){
                    variantRepository.delete(variant);
                }
            });
    
            Map<Set<String>, Variant> optionToMap = new HashMap<>();
            for (Variant variant : productVariants) {
                Set<String> optionName = variant.getOptions().stream()
                        .map(OptionProduct::getName)
                        .collect(Collectors.toSet());
                optionToMap.put(optionName, variant);
            }
    
            groupOptions.stream().flatMap(group -> group.getOptions().stream())
                    .forEach(optionRepository::save);

            groupOptions.forEach(groupOption -> groupOption.getOptions()
                    .forEach(optionProduct -> optionProduct.setGroupName(groupOption.getName())));
            List<GroupOption> savedGroupOptions = groupOptionRepository.saveAll(groupOptions);
    
            product.setGroupOptions(new HashSet<>(savedGroupOptions));
            product.setUpdatedDate(new Date(System.currentTimeMillis()));
            productRepository.save(product);
    
            List<Variant> newVariants = generateVariants(savedGroupOptions, product);

            List<Variant> updatedVariants = new ArrayList<>();
            for (Variant newVariant : newVariants) {
                Set<String> newVariantOptionNames = newVariant.getOptions().stream()
                        .map(OptionProduct::getName)
                        .collect(Collectors.toSet());

                Variant matchingvariant = optionToMap.get(newVariantOptionNames);
                if(!CommonUtils.isEmpty(matchingvariant)){
                    updatedVariants.add(matchingvariant);
                } else {
                    updatedVariants.add(newVariant);
                }
            }
            newVariants = updatedVariants;
            updatedVariants.forEach(variant -> {
                if(!CommonUtils.isEmpty(variant.getImage())){
                    variant.getImage().setId(null);
                }
                variant.setVariantCode(CodeUtils.generateVariantCode(String.valueOf(variant.getProduct().getCategory().getId())));
                variant.setProduct(product);
            });
            return variantRepository.saveAll(updatedVariants);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Variant getVariantByOption(List<OptionProduct> options) {
        AccessUtils.setAccessMerchant(namedParameterJdbcTemplate,userService.getCurrentUser().getId());
        Set<Long> optionIds = options.stream()
                .map(OptionProduct::getId)
                .collect(Collectors.toSet());
        Long variantId = variantRepository.findVariantByExactOptions(optionIds, options.size());
        return variantRepository.findVariantById(variantId);
    }

    @Override
    public void updateVariant(List<Variant> variants) {
        AccessUtils.setAccessMerchant(namedParameterJdbcTemplate,userService.getCurrentUser().getId());
        var ref = new Object() {
            double maxPrice = 0;
            double minPrice = 0;
            Boolean isSale = false;
        };

        variants.forEach(variant -> {
            if (!CommonUtils.isEmpty(variant.getPrice()) ) {
                if(variant.getPrice() > ref.maxPrice){
                    ref.maxPrice = variant.getPrice();
                }
                if (variant.getPrice() < ref.minPrice || ref.minPrice == 0) {
                    ref.minPrice = variant.getPrice();
                }
            }
            if(!CommonUtils.isEmpty(variant.getSalePrice())){
                ref.isSale = true;
            }
        });
        Long productId = variants.stream().findFirst().get().getProduct().getId();
        Product product = productRepository.findById(productId).get();
        product.setMinPrice(ref.minPrice);
        product.setMaxPrice(ref.maxPrice);
        product.setIsDiscount(ref.isSale);
        productRepository.save(product);
        variantRepository.saveAll(variants);
    }

    @Override
    public List<Product> getAllProductByShopId(Long shopId) {
        if(!CommonUtils.isEmpty(userService.getCurrentUser())){
            AccessUtils.setAccessMerchant(namedParameterJdbcTemplate,userService.getCurrentUser().getId());
        }
        List<Product> products = productRepository.findAllByMerchantId(shopId);
        return products;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).get();
    }
    /**
     * Searches for products based on specified criteria and filters the results by price range.
     * 
     * This method first retrieves products matching the given keyword and category from the repository.
     * It then filters these products based on their variants' price range to ensure they fall within
     * the specified minimum and maximum price bounds.
     *
     * @param keyword    The search term to match against product names or descriptions.
     * @param categoryId The ID of the category to search within. Use null to search all categories.
     * @param minPrice   The minimum price for the product variants. Products with all variants below this price are excluded.
     * @param maxPrice   The maximum price for the product variants. Products with all variants above this price are excluded.
     * @return A list of Product objects that match the search criteria and fall within the specified price range.
     */
    @Override
    public List<Product> searchProducts(String keyword, Long categoryId, Double minPrice, Double maxPrice) {
        // Implement logic to search products based on keyword, category, and price range
        AccessUtils.setAccessMerchant(namedParameterJdbcTemplate,userService.getCurrentUser().getId());
        List<Product> products = productRepository.searchProducts(keyword, categoryId);
        for (Product product: products){
            List<Variant> variants = variantRepository.findVariantByProductId(product.getId());
            Double highestCost = variants.stream().max(new Comparator<Variant>() {
                @Override
                public int compare(Variant o1, Variant o2) {
                    return (int) (o1.getPrice()-o2.getPrice());
                }
            }).get().getPrice();
            Double lowestCost = variants.stream().min(new Comparator<Variant>() {
                @Override
                public int compare(Variant o1, Variant o2) {
                    return (int) (o1.getPrice()-o2.getPrice());
                }
            }).get().getPrice();
            if(!(minPrice <= lowestCost && highestCost <= maxPrice)){
                products.remove(product);
            };
        }
        return products;
    }
    /**
     * Retrieves a list of featured products, sorted based on merchant status, sales, and ratings.
     * 
     * This method fetches a limited number of featured products from the repository and then
     * sorts them according to the following criteria:
     * 1. Products from royal merchants are prioritized.
     * 2. Products are then sorted by the number of sales in descending order.
     * 3. If sales are equal, products are sorted by their ratings in descending order.
     *
     * @param limit The maximum number of featured products to retrieve.
     * @return A sorted list of featured Product objects, limited to the specified size.
     */
    @Override
    public List<Product> getFeaturedProducts(int limit) {
        // Implement logic to get featured products, possibly based on ratings or sales
        List<Product> productList = productRepository.findFeaturedProducts(Pageable.ofSize(limit));
        productList.sort(new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                if(o1.getMerchant().getIsRoyal()){
                    return 1;
                }else if(o2.getMerchant().getIsRoyal()){
                    return -1;
                }
                return 0;
            }
        });
        return productList;
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId, int page, int size) {
        // Implement logic to get products by category with pagination
        return productRepository.findByCategoryId(categoryId, PageRequest.of(page, size));
    }
    @Override
    public List<Product> getDiscountedProducts(int limit) {
        // Implement logic to get products with active discounts
        List<Product> products = productRepository.findDiscountedProducts(Pageable.ofSize(limit));
        products.sort(new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                if(o1.getMerchant().getIsRoyal()){
                    return 1;
                }else if(o2.getMerchant().getIsRoyal()){
                    return -1;
                }
                if(o1.getSold() > o2.getSold()){
                    return (int) (o1.getSold()-o2.getSold());
                }
                else if(o1.getSold() == o2.getSold()){
                    return (int) (o1.getRating() - o2.getRating());
                }
                return (int) (o1.getSold()-o2.getSold());
            }
        });
        return products;
    }

        /**
     * Updates the stock quantity of a specific product variant and adjusts the sold count of the product.
     * This method is typically used when processing orders or managing inventory.
     *
     * @param productId      The unique identifier of the product to be updated.
     * @param quantityChange The change in quantity. Positive values indicate a reduction in stock (e.g., for sales),
     *                       while negative values indicate an increase (e.g., for restocking).
     * @param variantId      The unique identifier of the specific product variant to be updated.
     * @throws RuntimeException if the product with the given productId is not found.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStock(Long productId, int quantityChange, Long variantId) {
        Variant variant = variantRepository.findVariantById(variantId);
        if(variant.getQuantity() < quantityChange){
            throw new RuntimeException("Insufficient stock");
        }
        variant.setQuantity(variant.getQuantity() - quantityChange);
        variant.getProduct().getMerchant().setTotalSold(variant.getProduct().getMerchant().getTotalSold()  == null ? 0 : variant.getProduct().getMerchant().getTotalSold() + quantityChange);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setSold(CommonUtils.isEmpty(product.getSold()) ? quantityChange : product.getSold() + quantityChange);
        productRepository.save(product);
    }

    /**
     * Retrieves paginated and sorted product reviews for a specific product.
     * 
     * This method fetches a page of reviews for the given product ID, sorts them
     * based on the presence of images and review date, and returns the sorted list.
     * 
     * @param productId The unique identifier of the product for which to retrieve reviews.
     * @param page The page number of reviews to retrieve (zero-based).
     * @param size The number of reviews to include per page.
     * @return A sorted List of Review objects for the specified product, paginated
     *         according to the given page and size parameters. Reviews with images
     *         are prioritized, followed by sorting based on review date.
     */
    @Override
    public List<Review> getProductReviews(Long productId, int page, int size) {
        // Implement logic to get product reviews with pagination
        List<Review> reviews = reviewRepository.findByProductId(productId, PageRequest.of(page, size));
        reviews.sort(new Comparator<Review>() {
            @Override
            public int compare(Review o1, Review o2) {
                if(CommonUtils.isEmpty(o1.getImages())){
                    return 1;
                }
                if(CommonUtils.isEmpty(o2.getImages())){
                    return 1;
                }
                return o1.getDate().compareTo( o1.getDate());
            }
        });
        return reviewRepository.findByProductId(productId, PageRequest.of(page, size));
    }

    @Override
    public void addToCart(Long userId, Long productId, int quantity) {

    }

    @Override
    public Variant getOneVariant(Long id) {
        List<Variant> variants = variantRepository.findVariantByProductId(id);
        return variants.get(0);
    }

    /**
     * Finds and filters products based on specified criteria.
     * 
     * This method applies filters to the product list based on category, price range,
     * merchant address, and product rating. It then paginates the filtered results.
     *
     * @param filterDTO A FilterDTO object containing the filter criteria:
     *                  - category: filters products by category
     *                  - priceMin and priceMax: filters products within the specified price range
     *                  - address: filters products by merchant's address (province)
     *                  - rating: filters products with ratings greater than or equal to the specified value
     * @param pageable A Pageable object specifying the pagination information
     * @return A Page of Product objects that match the filter criteria and pagination settings.
     *         The Page object contains a subset of the filtered products based on the
     *         pageable parameters, along with pagination metadata.
     */
    @Override
    public Page<ProductDTO> findProductsByFilter(FilterDTO filterDTO, Pageable pageable) {
        List<Product> filteredProducts = productRepository.findAll();
        if (!CommonUtils.isEmpty(filterDTO.getCategoryId())) {
            filteredProducts = filteredProducts.stream()
                    .filter(product -> product.getCategory().getId() == (filterDTO.getCategoryId()))
                    .collect(Collectors.toList());
        }
        
        if (!CommonUtils.isEmpty(filterDTO.getPriceMax()) && !CommonUtils.isEmpty(filterDTO.getPriceMin())) {
            double minPrice = filterDTO.getPriceMin();
            double maxPrice = filterDTO.getPriceMax();
            filteredProducts = filteredProducts.stream()
                    .filter(product -> product.getMinPrice() != null && product.getMaxPrice() != null &&
                            product.getMinPrice() >= minPrice && product.getMaxPrice() <= maxPrice)
                    .collect(Collectors.toList());
        }
        
        if (!CommonUtils.isEmpty(filterDTO.getAddress())) {
            filteredProducts = filteredProducts.stream()
                    .filter(product -> product.getMerchant().getAddress().getProvince().equals(filterDTO.getAddress()))
                    .collect(Collectors.toList());
        }
        
        if (!CommonUtils.isEmpty(filterDTO.getRating())) {
            filteredProducts = filteredProducts.stream()
                    .filter(product -> {
                        double rate = !CommonUtils.isEmpty(product.getRating()) ? product.getRating() : 0;
                            if(rate >= filterDTO.getRating()){
                                return true;
                            }
                        return false;
                    })
                    .collect(Collectors.toList());
        }
        
        if (!CommonUtils.isEmpty(filterDTO.getIsSale()) && filterDTO.getIsSale()) {
            filteredProducts = filteredProducts.stream()
                .filter(product -> product.getIsDiscount() != null && product.getIsDiscount())
                .collect(Collectors.toList());
        }


        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredProducts.size());

        List<ProductDTO> pageContent;
        if (start < end) {
            pageContent = filteredProducts.subList(start, end).stream()
                    .map(this::toProductDTO)
                    .collect(Collectors.toList());
        } else {
            pageContent = new ArrayList<>();
        }

        return new PageImpl<>(pageContent, pageable, filteredProducts.size());
    }

        @Override
    public Page<ProductDTO> getAllProductByShopId(Long shopId, long page, int size) {
        if(!CommonUtils.isEmpty(userService.getCurrentUser())){
            AccessUtils.setAccessMerchant(namedParameterJdbcTemplate,userService.getCurrentUser().getId());
        }
        AccessUtils.setAccessMerchant(namedParameterJdbcTemplate, userService.getCurrentUser().getId());

        Pageable pageable = PageRequest.of((int) page, size, Sort.by("createdDate").descending());
        Page<Product> productPage = productRepository.findAllByMerchantId(shopId, pageable);

        List<ProductDTO> products = productPage.getContent().stream().map(this::toProductDTO).collect(Collectors.toList());
        Page<ProductDTO> productDTOPage = new PageImpl<>(products, pageable, productPage.getTotalElements());

        return productDTOPage;
    }
}
