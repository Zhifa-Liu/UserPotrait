package cn.edu.neu.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 32098
 *
 * 用户商品浏览
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScanProductInfo {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 商品ID
     */
    private String productId;
    /**
     * 商品类别
     */
    private String productType;
    /**
     * 浏览的商品品牌
     */
    private String brand;
    /**
     * 商品浏览的开始时间
     */
    private long beginScanTime;
    /**
     * 停留在商品的时间：ms
     */
    private long stayTime;
    /**
     * 浏览使用的终端类型
     */
    private String useType;
}
