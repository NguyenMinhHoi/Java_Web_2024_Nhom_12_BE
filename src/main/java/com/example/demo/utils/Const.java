package com.example.demo.utils;

public class Const {
    public static String DEFAULT = "DEFAULT";

    public static String MAIL = "MAIL";

    public static String VALUE_FROM_KEY = "select value_parameter from configurations where key_parameter = :key";

    public static String UPDATE_ORDER_STATUS_LOG = "INSERT INTO order_status_log (order_id, status) VALUES (:orderId, :status)";
    public static String UPDATE_CART_NEW_VARIANT = "UPDATE cart_variants SET quantity = :quantity, variant_id = :variantId WHERE variant_id = :oldVariantId";
}
