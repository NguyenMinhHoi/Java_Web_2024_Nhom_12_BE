package com.example.demo.controller;

import com.example.demo.model.Variant;
import com.example.demo.model.Voucher;
import com.example.demo.model.Voucher;
import com.example.demo.service.VoucherService;
import com.example.demo.model.VoucherCondition;
import com.example.demo.utils.enumeration.VoucherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;

@RestController
@RequestMapping("/vouchers")
@CrossOrigin("*")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @GetMapping
    public ResponseEntity<List<Voucher>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voucher> getVoucherById(@PathVariable Long id) {
        Voucher voucher = voucherService.findById(id);
        return voucher != null ? ResponseEntity.ok(voucher) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Voucher> createVoucher(@RequestBody Voucher voucher) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voucherService.createVoucher(voucher));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voucher> updateVoucher(@PathVariable Long id, @RequestBody Voucher voucher) {
        Voucher updatedVoucher = voucherService.updateVoucher(id, voucher);
        return updatedVoucher != null ? ResponseEntity.ok(updatedVoucher) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long id) {
        voucherService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<Voucher>> getActiveVouchers() {
        return ResponseEntity.ok(voucherService.findActiveVouchers());
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Voucher>> getExpiredVouchers() {
        return ResponseEntity.ok(voucherService.findExpiredVouchers());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Voucher> getVoucherByCode(@PathVariable String code) {
        Voucher voucher = voucherService.findByCode(code);
        return voucher != null ? ResponseEntity.ok(voucher) : ResponseEntity.notFound().build();
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<Boolean> validateVoucher(@PathVariable String code) {
        return ResponseEntity.ok(voucherService.isVoucherValid(code));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateVoucher(@PathVariable Long id) {
        voucherService.deactivateVoucher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Voucher>> getVouchersByType(@PathVariable VoucherType type) {
        return ResponseEntity.ok(voucherService.findVouchersByType(type));
    }

    @GetMapping("/condition/{condition}")
    public ResponseEntity<List<Voucher>> getVouchersByCondition(@PathVariable VoucherCondition condition) {
        return ResponseEntity.ok(voucherService.findVouchersByCondition(condition));
    }

    @PostMapping("/able")
    public ResponseEntity<HashMap<String, List<Voucher>>> getAbleVouchers(@RequestBody List<Variant> variants) {
        return ResponseEntity.ok(voucherService.getAbleVoucher(variants));
    }

    @PostMapping("/able/shop")
    public ResponseEntity<List<Voucher>> getAbleVouchersByShop(@RequestBody List<Variant> variants) {
        return ResponseEntity.ok(voucherService.getAbleVoucherByShop(variants));
    }

    @PostMapping("/able/system")
    public ResponseEntity<List<Voucher>> getAbleVouchersBySystem(@RequestBody List<Variant> variants) {
        return ResponseEntity.ok(voucherService.getAbleVoucherBySystem(variants));
    }
}