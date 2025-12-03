package com.example.product_management.controller;

import com.example.product_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showDashboard(Model model) {
        // 1. Total Products
        long totalProducts = productService.getAllProducts().size();
        model.addAttribute("totalProducts", totalProducts);

        // 2. Total Value
        model.addAttribute("totalValue", productService.getTotalInventoryValue());

        // 3. Average Price
        model.addAttribute("avgPrice", productService.getAveragePrice());

        // 4. Low Stock (Threshold < 10)
        model.addAttribute("lowStockProducts", productService.getLowStockProducts(10));

        // 5. Category Stats (Map for display)
        Map<String, Long> categoryStats = new HashMap<>();
        for (String cat : productService.getAllCategories()) {
            categoryStats.put(cat, productService.getCountByCategory(cat));
        }
        model.addAttribute("categoryStats", categoryStats);

        return "dashboard";
    }
}