package cn.edu.neu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 32098
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScanCountView {
    private List<String> legends;
    private List<String> bagScanTimes;
    private List<String> clothScanTimes;
    private List<String> shoeScanTimes;
    private List<Long> bagScanCounts;
    private List<Long> clothScanCounts;
    private List<Long> shoeScanCounts;
    private List<Long> totalCounts;
}
