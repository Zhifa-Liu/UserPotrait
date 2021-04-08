package cn.edu.neu.source;

import cn.edu.neu.bean.ConsumeInfo;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author 32098
 */
public class ConsumeInfoSource extends RichParallelSourceFunction<ConsumeInfo> {
    private boolean keepMockData;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        keepMockData = true;
    }

    @Override
    public void run(SourceContext<ConsumeInfo> sourceContext) throws Exception {
        // 订单ID
        String orderId;
        // 用户ID
        String userId;
        // 商品ID
        String productId;
        // 商品类型
        List<String> productTypeList = Arrays.asList("休闲服类", "运动服类", "西服类", "运动鞋类", "休闲鞋类", "包类", "皮鞋类", "拖鞋类", "靴类");
        String productType;
        // 商品数量
        int count;
        // 商品单价
        double unitPrice;
        // 支付类型
        List<String> payTypeList = Arrays.asList("微信", "微信", "微信", "支付宝", "支付宝", "掌上银行");
        String payType;
        // 支付时间
        long payTime;
        // 优惠券金额
        double couponAmount;

        Random r = new Random();
        productType = productTypeList.get(r.nextInt(productTypeList.size()));
        payType = payTypeList.get(r.nextInt(payTypeList.size()));
        while (keepMockData) {
            for(int i=0; i<r.nextInt(20); i++){
                orderId = RandomStringUtils.randomAlphanumeric(12);
                userId = RandomStringUtils.randomNumeric(3);
                productId = RandomStringUtils.randomAlphabetic(8);
                if(r.nextDouble()>0.5){
                    productType = productTypeList.get(r.nextInt(productTypeList.size()));
                }
                count = r.nextInt(10)+1;
                unitPrice = Math.abs(r.nextGaussian()*30+300);
                if(r.nextDouble()>0.5){
                    payType = payTypeList.get(r.nextInt(payTypeList.size()));
                }
                payTime = System.currentTimeMillis() + 2000;
                couponAmount = r.nextInt(10);
                sourceContext.collect(new ConsumeInfo(orderId, userId, productId, productType, count, unitPrice, payType, payTime, couponAmount));
            }
            Thread.sleep(2000);
        }
    }

    @Override
    public void cancel() {
        keepMockData = false;
    }
}


