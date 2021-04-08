package cn.edu.neu.control;

import cn.edu.neu.po.*;
import cn.edu.neu.vo.*;
import com.alibaba.fastjson.JSONObject;
import cn.edu.neu.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 32098
 */
@RestController
@RequestMapping("mysqlData")
@CrossOrigin
public class DataViewControl {
    private final List<String> staticNames = Arrays.asList("yearBase", "useTypeLike", "emailLike", "carrierLike", "brandLikeOfClothes", "brandLikeOfBags", "brandLikeOfShoes", "consumptionLevel", "payTypeLike");

    @Autowired
    private DataService dataService;

    @RequestMapping(value = "resultInfoView", method = RequestMethod.POST)
    public String resultInfoView(@RequestBody Map<String, String> staticNameMap){
        String staticName = staticNameMap.get("staticName");
        List<StaticsInfo> list = new ArrayList<StaticsInfo>();

        System.out.println(staticName);

        int i = 0;
        for (String name : staticNames) {
            if(name.equals(staticName)){
                switch (i){
                    case 0: list = dataService.yearBaseStatics();break;
                    case 1: list = dataService.useTypeStatics();break;
                    case 2: list = dataService.emailStatics();break;
                    case 3: list = dataService.carrierStatics();break;
                    case 4: list = dataService.brandLikeOfClothesStatics();break;
                    case 5: list = dataService.brandLikeOfBagsStatics();break;
                    case 6: list = dataService.brandLikeOfShoesStatics();break;
                    case 7: list = dataService.consumptionLevelStatics();break;
                    case 8: list = dataService.payTypeStatics();break;
                    default: break;
                }
            }
            i++;
        }

        Charts charts = new Charts();
        // 存储x轴的值
        List<String> xList = new ArrayList<String>();
        // 存储y轴的值
        List<Long> yList =new ArrayList<Long>();

        for(StaticsInfo result: list){
            System.out.println(result.getStaticName()+" "+result.getStaticDetail()+" "+result.getStaticData());
            xList.add(result.getStaticDetail());
            yList.add(result.getStaticData());
        }

        charts.setXAxisData(xList);
        charts.setYAxisData(yList);

        return JSONObject.toJSONString(charts);
    }

    @RequestMapping(value = "consumptionLevelDetail",method = RequestMethod.POST)
    public List<ConsumptionLevelDetail> consumptionLevelDetail(){
        return dataService.getConsumptionLevelDetail();
    }

    @RequestMapping(value = "productTypeScanCount", method = RequestMethod.POST)
    public ProductTypeScanCount productTypeScanCount(){
        ProductTypeScanCount productTypeScanCount = dataService.productTypeScanCount();
        Map<String, List<SingleTypeScanCount>> map = productTypeScanCount.getTypeAndScanCount();
        map.forEach((k,v)->{
            System.out.println(k);
            for (SingleTypeScanCount singleTypeScanCount : v) {
                System.out.println(singleTypeScanCount.getDealtTime()+" "+singleTypeScanCount.getScanCount());
            }
            System.out.println("###############");
        });
        return productTypeScanCount;
    }

    @RequestMapping(value = "productTypeScanCountNew", method = RequestMethod.POST)
    public ScanCountView productTypeScanCountView(){
        ProductTypeScanCount productTypeScanCount = dataService.productTypeScanCount();
        Map<String, List<SingleTypeScanCount>> map = productTypeScanCount.getTypeAndScanCount();

        List<String> legends = new ArrayList<>();
        // 接下来3个List最终值与顺序一样
        List<String> bagScanTime = new ArrayList<>();
        List<String> clothScanTime = new ArrayList<>();
        List<String> shoeScanTime = new ArrayList<>();
        List<Long> bagScanCount = new ArrayList<>();
        List<Long> clothScanCount = new ArrayList<>();
        List<Long> shoeScanCount = new ArrayList<>();
        List<Long> totalCount = new ArrayList<>();

        map.forEach((k, v)->{
            if("Bags".equals(k)){
                legends.add(k);
                for (SingleTypeScanCount singleTypeScanCount : v) {
                    bagScanTime.add(singleTypeScanCount.getDealtTime());
                    bagScanCount.add(singleTypeScanCount.getScanCount());
                }
            }
            if("Clothes".equals(k)){
                legends.add(k);
                for (SingleTypeScanCount singleTypeScanCount : v) {
                    clothScanTime.add(singleTypeScanCount.getDealtTime());
                    clothScanCount.add(singleTypeScanCount.getScanCount());
                }
            }
            if("Shoes".equals(k)){
                legends.add(k);
                for (SingleTypeScanCount singleTypeScanCount : v) {
                    shoeScanTime.add(singleTypeScanCount.getDealtTime());
                    shoeScanCount.add(singleTypeScanCount.getScanCount());
                }
            }
        });

        for(int i=0; i<bagScanCount.size(); i++){
            totalCount.add(bagScanCount.get(i)+clothScanCount.get(i)+shoeScanCount.get(i));
        }

        return new ScanCountView(legends, bagScanTime, clothScanTime, shoeScanTime, bagScanCount, clothScanCount, shoeScanCount, totalCount);
    }

    @RequestMapping(value = "productTypeSale",method = RequestMethod.POST)
    public SaleCharts productTypeSales(@RequestBody Map<String, Integer> topN){
        int top = topN.get("topN");

        // 存储x轴的值
        List<String> xList = new ArrayList<>();
        // 存储y轴的值
        List<Double> yList =new ArrayList<>();
        List<ProductTypeSale> productTypeSales = dataService.getProductTypeSale();
        int stop = Math.min(top, productTypeSales.size());
        for(int i=0;i<stop;i++){
            System.out.println(productTypeSales.get(i).getProductType());
            xList.add(productTypeSales.get(i).getProductType());
            yList.add(productTypeSales.get(i).getTotalSale());
        }
        return new SaleCharts(xList, yList);
    }
}

