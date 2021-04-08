package cn.edu.neu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 32098
 *
 * 用户画像 Statics
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StaticsInfo {
    private String staticName;
    private String staticDetail;
    private Long staticData;
}
