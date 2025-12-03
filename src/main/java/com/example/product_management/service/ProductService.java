package com.example.product_management.service;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.Pageable; // Import Pageable
import com.example.product_management.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    Page<Product> getAllProducts(Pageable pageable);

    List<Product> getAllProducts();
    
    Optional<Product> getProductById(Long id);
    
    Product saveProduct(Product product);
    
    void deleteProduct(Long id);
    
    List<Product> searchProducts(String keyword);
    
    List<Product> getProductsByCategory(String category);

    // Search & Filter
    Page<Product> searchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    List<String> getAllCategories();
    
    // Statistics
    long getCountByCategory(String category);
    BigDecimal getTotalInventoryValue();
    BigDecimal getAveragePrice();
    List<Product> getLowStockProducts(int threshold);
}

