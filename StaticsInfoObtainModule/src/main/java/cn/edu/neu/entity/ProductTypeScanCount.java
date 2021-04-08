package cn.edu.neu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author 32098
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductTypeScanCount {
    private Map<String, List<SingleTypeScanCount>> typeAndScanCount;
}
