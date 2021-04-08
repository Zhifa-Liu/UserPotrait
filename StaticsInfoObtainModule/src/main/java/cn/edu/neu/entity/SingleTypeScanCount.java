package cn.edu.neu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 32098
 *
 * 产品浏览计数Bean
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SingleTypeScanCount {
    private String dealtTime;
    private long scanCount;
}

