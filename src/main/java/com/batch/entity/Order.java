package com.batch.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private Integer customerId;

    private Integer itemId;

    private String itemName;

    private Integer itemPrice;

    private String purchaseDate;
}
