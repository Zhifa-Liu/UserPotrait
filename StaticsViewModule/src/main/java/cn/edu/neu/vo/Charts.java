package cn.edu.neu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author 32098
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Charts {
    /**
     * x轴的值
     */
    private List<String> xAxisData;
    /**
     * y轴的值
     */
    private List<Long> yAxisData;
}
