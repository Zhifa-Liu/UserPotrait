package cn.edu.neu.source;

import cn.edu.neu.bean.UserBasicInfo;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author 32098
 *
 * 模拟系统用户基本信息
 */
public class UserBasicInfoSource extends RichParallelSourceFunction<UserBasicInfo> {
    private boolean keepMockData;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        keepMockData = true;
    }

    private String getRandomDate(String beginDate, String endDate){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);

            if(start.getTime() >= end.getTime()){
                return null;
            }
            long date = start.getTime() + (long)(Math.random() * (end.getTime() - start.getTime()));
            return format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run(SourceContext<UserBasicInfo> sourceContext) throws Exception {
        // 用户ID
        String uid;
        // 用户姓名
        String uName;
        // 用户性别
        List<String> uGenderList = Arrays.asList("man", "woman");
        String uGender;
        // 用户手机号码
        List<String> telFirstList = Arrays.asList(
                ("133,153,180,181,189,177,1700,173,199,130,131,132,155,156,185,186,145,176," +
                        "134,135,136,137,138,139,150,151,152,157,158,159,182,183,184,187,188,147,178").split(","));
        String uTelephone;
        // 用户邮箱
        List<String> emailLast = Arrays.asList(
                "@163.com, @126.com, @139.com, @sohu.com, @qq.com, @189.cn, @aliyun.com, @sina.com".split(",")
        );
        String uEmail;
        // 用户年龄
        int uAge;
        String registerTime;


        Random r = new Random();

        for(int i=0; i<r.nextInt(1000); i++){
            uid = RandomStringUtils.randomNumeric(4);
            uName = RandomStringUtils.randomAlphabetic(6);
            uGender = uGenderList.get(r.nextInt(2));
            uTelephone = telFirstList.get(r.nextInt(telFirstList.size()))
                    +String.valueOf((r.nextInt(9999)+1)+10000).substring(1)+
                    String.valueOf((r.nextInt(9999)+1)+10000).substring(1);
            uEmail = RandomStringUtils.randomNumeric(6) + emailLast.get(r.nextInt(emailLast.size()));
            uAge = (int) (r.nextGaussian()*10+25);
            registerTime = getRandomDate("2012-01-01", "2015-12-31");
            sourceContext.collect(new UserBasicInfo(uid, uName, uGender, uTelephone, uEmail, uAge, registerTime));
        }

        // 模拟后续注册用户
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        while (keepMockData) {
            uid = RandomStringUtils.randomNumeric(4);
            uName = RandomStringUtils.randomAlphabetic(6);
            uGender = uGenderList.get(r.nextInt(2));
            uTelephone = telFirstList.get(r.nextInt(telFirstList.size()))
                    +String.valueOf((r.nextInt(9999)+1)+10000).substring(1)+
                    String.valueOf((r.nextInt(9999)+1)+10000).substring(1);
            uEmail = RandomStringUtils.randomNumeric(6) + emailLast.get(r.nextInt(emailLast.size()));
            uAge = Math.abs((int) (r.nextGaussian()*12+25));
            // registerTime = getRandomDate("2016-01-01", "2018-12-31");
            registerTime = format.format(System.currentTimeMillis());
            sourceContext.collect(new UserBasicInfo(uid, uName, uGender, uTelephone, uEmail, uAge, registerTime));

            Thread.sleep((r.nextInt(60)+1)*1000);
        }
    }

    @Override
    public void cancel() {
        keepMockData = false;
    }
}
