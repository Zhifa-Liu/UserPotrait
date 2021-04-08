package cn.edu.neu.util;

/**
 *
 * @author 32098
 */
public class EmailUtils {
    /**
     * @param email email
     * @return emailType
     */
    public static String getEmailtypeBy(String email){
        String emailtye = "其他邮箱用户";
        if(email.contains("@163.com")||email.contains("@126.com")){
            emailtye = "网易邮箱用户";
        }else if (email.contains("@139.com")){
            emailtye = "移动邮箱用户";
        }else if (email.contains("@sohu.com")){
            emailtye = "搜狐邮箱用户";
        }else if (email.contains("@qq.com")){
            emailtye = "QQ邮箱用户";
        }else if (email.contains("@189.cn")){
            emailtye = "189邮箱用户";
        }else if (email.contains("@aliyun.com")){
            emailtye = "阿里邮箱用户";
        }else if (email.contains("@sina.com")){
            emailtye = "新浪邮箱用户";
        }
        return emailtye;
    }
}
