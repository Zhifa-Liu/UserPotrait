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
public class UserConsumptionLevelStatics {
    private String uid;
    private double avgAmount;
    private String level;
}

