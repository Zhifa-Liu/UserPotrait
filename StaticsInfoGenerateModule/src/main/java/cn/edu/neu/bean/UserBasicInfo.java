package cn.edu.neu.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 32098
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicInfo {
    private String userId;
    private String username;
    private String gender;
    private String telephone;
    private String email;
    private int age;
    private String registerTime;
}

