package cn.edu.neu.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 32098
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConsumeInfo {
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 商品ID
     */
    private String productId;
    /**
     * 商品类型
     */
    private String productType;
    /**
     * 商品数量
     */
    private int count;
    /**
     * 商品单价
     */
    private double unitPrice;
    /**
     * 支付类型
     */
    private String payType;
    /**
     * 支付时间
     */
    private long payTime;
    /**
     * 优惠券金额
     */
    private double couponAmount;
}

