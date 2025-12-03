package com.example.product_management.controller;

import com.example.product_management.entity.Product;
import com.example.product_management.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    // List all products
    @GetMapping("/search")
        public String listProducts(
                @RequestParam(required = false) String name,
                @RequestParam(required = false) String category,
                @RequestParam(required = false) BigDecimal minPrice,
                @RequestParam(required = false) BigDecimal maxPrice,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "5") int size, 
                @RequestParam(defaultValue = "id") String sortBy,
                @RequestParam(defaultValue = "asc") String sortDir,
                Model model) {

            
            if (name != null && name.isEmpty()) name = null;
            if (category != null && category.isEmpty()) category = null;

           
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            
            Page<Product> productPage = productService.searchProducts(name, category, minPrice, maxPrice, pageable);

           
            model.addAttribute("products", productPage.getContent()); 
            model.addAttribute("categories", productService.getAllCategories());
            
        
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("totalItems", productPage.getTotalElements());
            
           
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("paramName", name); 
            model.addAttribute("paramCategory", category);
            
            return "product-list";
        }
    
    @GetMapping
    public String home() {
        return "redirect:/products/search";
    }

    // Exercise 6: Validation Logic
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        if (result.hasErrors()) {
            return "product-form"; // Return to form if validation fails
        }
        try {
            productService.saveProduct(product);
            
            redirectAttributes.addFlashAttribute("message", 
                product.getId() == null ? "Product added successfully!" : "Product updated successfully!");
        } catch (Exception e) {
           
            model.addAttribute("error", "Error saving product: " + e.getMessage());
            return "product-form";
        }
        return "redirect:/products";
    }

    // Show form for new product
    @GetMapping("/new")
    public String showNewForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "product-form";
    }
    
    // Show form for editing product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Product not found");
                    return "redirect:/products";
                });
    }
    
    // Delete product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }
    
}
