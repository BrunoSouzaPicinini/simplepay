package com.simplepay.controller;

import com.simplepay.domain.entity.Seller;
import com.simplepay.domain.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sellers")
@Tag(name = "Seller", description = "CRUD operations for Seller")
public class SellerController {
    @Autowired
    private SellerService sellerService;

    @Operation(summary = "Get all sellers")
    @GetMapping
    public List<Seller> getAllSellers() {
        return sellerService.findAll();
    }

    @Operation(summary = "Get seller by id")
    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) {
        return sellerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new seller")
    @PostMapping
    public Seller createSeller(@RequestBody Seller seller) {
        return sellerService.save(seller);
    }

    @Operation(summary = "Update seller by id")
    @PutMapping("/{id}")
    public ResponseEntity<Seller> updateSeller(@PathVariable Long id, @RequestBody Seller seller) {
        return sellerService.update(id, seller)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete seller by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        if (sellerService.delete(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
