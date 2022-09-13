package org.example.entities;

import lombok.Data;
import lombok.experimental.Accessors;

import java.text.DateFormat;

@Data
@Accessors(chain = true)
public class Order {
    private int id;
    private int petId;
    private int quantity;
    private String shipDate;
    private String status;
    private boolean complete;
}
