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
public class ProductTypeScanCountStatic {
    private String productType;
    private String dealtTime;
    private Long scanCount;
}
