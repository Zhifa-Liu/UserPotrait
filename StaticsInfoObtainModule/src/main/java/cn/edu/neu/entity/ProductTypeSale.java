package cn.edu.neu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 32098
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductTypeSale {
    private String productType;
    private Double totalSale;
}

