package cn.edu.neu.util;

import java.util.regex.Pattern;

/**
 *
 * @author 32098
 */
public class CarrierUtils {

    /**
     * 中国电信号码格式验证 手机段： 133,153,180,181,189,177,1700,173,199
     **/
    private static final String CHINA_TELECOM_PATTERN = "(^1(33|53|77|73|99|8[019])\\d{8}$)|(^1700\\d{7}$)";

    /**
     * 中国联通号码格式验证 手机段：130,131,132,155,156,185,186,145,176,1709
     **/
    private static final String CHINA_UNICOM_PATTERN = "(^1(3[0-2]|4[5]|5[56]|7[6]|8[56])\\d{8}$)|(^1709\\d{7}$)";

    /**
     * 中国移动号码格式验证
     * 手机段：134,135,136,137,138,139,150,151,152,157,158,159,182,183,184,187,188,147,178,1705
     **/
    private static final String CHINA_MOBILE_PATTERN = "(^1(3[4-9]|4[7]|5[0-27-9]|7[8]|8[2-478])\\d{8}$)|(^1705\\d{7}$)";


    /**
     * 0、未知 1、移动 2、联通 3、电信
     * @param telephone tel
     * @return int
     */
    public static String getCarrierByTel(String telephone){
        boolean b1 = telephone == null || telephone.trim().equals("") ? false : match(CHINA_MOBILE_PATTERN, telephone);
        if (b1) {
            return "移动";
        }
        b1 = telephone == null || telephone.trim().equals("") ? false : match(CHINA_UNICOM_PATTERN, telephone);
        if (b1) {
            return "联通";
        }
        b1 = telephone == null || telephone.trim().equals("") ? false : match(CHINA_TELECOM_PATTERN, telephone);
        if (b1) {
            return "电信";
        }
        return "未知";
    }

    /**
     * 正则匹配
     * @param regex regex
     * @param tel tel
     * @return bool
     */
    private static boolean match(String regex, String tel) {
        return Pattern.matches(regex, tel);
    }

}
