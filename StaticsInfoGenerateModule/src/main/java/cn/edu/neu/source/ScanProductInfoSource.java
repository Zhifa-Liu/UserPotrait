package cn.edu.neu.source;

import cn.edu.neu.bean.ScanProductInfo;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author 32098
 *
 * 模拟商品浏览数据
 */
public class ScanProductInfoSource extends RichParallelSourceFunction<ScanProductInfo> {
    private boolean keepMockData;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        keepMockData = true;
    }

    @Override
    public void run(SourceContext<ScanProductInfo> sourceContext) throws Exception {
        // 用户ID
        String userId;
        // 商品ID
        String productId;
        // 商品类型
        List<String> productTypeList = Arrays.asList("Clothes", "Shoes", "Bags");
        String productType = "";
        // 浏览商品开始的时间
        long scanBeginTime;
        // 停留时间
        long stayTime;
        // 用户使用的终端
        List<String> useTypeList = Arrays.asList("PC端", "小程序端", "移动端");
        String useType;
        // 用户浏览的商品品牌
        List<List<String>> brandsOfProductType = Arrays.asList(
                Arrays.asList("UNIQLO优衣库", "PEACEBIRD太平鸟", "每依站", "LEDIN乐町", "VEROMODA"),
                Arrays.asList("LINING李宁", "鸿星尔克ERKE", "ANTA安踏", "Nike耐克", "XTEP特步", "NewBalance", "鸿星尔克ERKE", "361°"),
                Arrays.asList("LV路易威登", "Prada普拉达", "Hermes爱马仕", "Gucci古驰", "Armani阿玛尼", "BALLY巴利", "Fendi芬迪", "纪梵希")
        );
        String brand;

        Random r = new Random();

        userId = RandomStringUtils.randomNumeric(3);
        productId = RandomStringUtils.randomAlphabetic(6);
        int productTypeIdx = r.nextInt(productTypeList.size());
        productType = productTypeList.get(productTypeIdx);
        List<String> brandsOfCurProType = brandsOfProductType.get(productTypeIdx);
        brand = brandsOfCurProType.get(r.nextInt(brandsOfCurProType.size()));
        stayTime = r.nextInt(5000)+500;
        scanBeginTime = System.currentTimeMillis();
        double prob = r.nextDouble();
        if(prob>0.45){
            useType = useTypeList.get(2);
        }else if(prob>0.18){
            useType = useTypeList.get(1);
        }else{
            useType = useTypeList.get(0);
        }
        sourceContext.collect(new ScanProductInfo(userId, productId, productType, brand, scanBeginTime, stayTime, useType));

        while (keepMockData) {
            for(int i=0; i<r.nextInt(20); i++){
                userId = RandomStringUtils.randomNumeric(3);
                productId = RandomStringUtils.randomAlphabetic(6);

                if((!"".equals(productType)) && r.nextDouble()<0.2){
                    productTypeIdx = r.nextInt(productTypeList.size());
                    productType = productTypeList.get(productTypeIdx);
                    brandsOfCurProType = brandsOfProductType.get(productTypeIdx);
                    brand = brandsOfCurProType.get(r.nextInt(brandsOfCurProType.size()));
                }

                stayTime = r.nextInt(5000)+500;
                scanBeginTime = System.currentTimeMillis() - stayTime;

                prob = r.nextDouble();
                if(prob>0.45){
                    useType = useTypeList.get(2);
                }else if(prob>0.18){
                    useType = useTypeList.get(1);
                }else{
                    useType = useTypeList.get(0);
                }

                sourceContext.collect(new ScanProductInfo(userId, productId, productType, brand, scanBeginTime, stayTime, useType));
            }
            Thread.sleep((r.nextInt(20)+1)*100);
        }
    }

    @Override
    public void cancel() {
        keepMockData = false;
    }
}
