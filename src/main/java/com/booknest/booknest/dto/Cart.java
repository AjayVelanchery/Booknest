package com.booknest.booknest.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class Cart implements Serializable {
    private Map<Long, CartItem> items = new HashMap<>();

    public void addItem(CartItem item) {
        CartItem existing = items.get(item.getBookId());
        if (existing == null) {
            items.put(item.getBookId(), item);
        } else {
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
        }
    }

    public void removeItem(Long bookId) {
        items.remove(bookId);
    }

    public void updateQuantity(Long bookId, int quantity) {
        CartItem item = items.get(bookId);
        if (item != null) {
            if (quantity <= 0) {
                items.remove(bookId);
            } else {
                item.setQuantity(quantity);
            }
        }
    }

    public BigDecimal getTotal() {
        return items.values().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
