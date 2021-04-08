package cn.edu.neu.service;

import cn.edu.neu.dao.MysqlDao;
import cn.edu.neu.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *
 * @author 32098
 */
@Service
public class MysqlDataServiceImpl{
    @Autowired
    private MysqlDao mysqlDao;

    public List<StaticsInfo> getStaticsBy(String staticName) {
        return mysqlDao.getStaticByName(staticName);
    }

    public List<StaticsInfo> getConsumptionLevelStatics(){
        return mysqlDao.getConsumptionLevelStatic();
    }

    public List<SingleTypeScanCount> getSingleTypeScanCount(String productType){
        return mysqlDao.getSingleTypeScanCount(productType);
    }

    public ProductTypeScanCount getProductTypeScanCount(){
        List<SingleTypeScanCount> bagTypeScanCount = this.getSingleTypeScanCount("Bags");
        List<SingleTypeScanCount> clothTypeScanCount = this.getSingleTypeScanCount("Clothes");
        List<SingleTypeScanCount> shoeTypeScanCount = this.getSingleTypeScanCount("Shoes");

        Collections.reverse(bagTypeScanCount);
        Collections.reverse(clothTypeScanCount);
        Collections.reverse(clothTypeScanCount);

        Map<String, List<SingleTypeScanCount>> productTypeScanCounts = new HashMap<>();
        productTypeScanCounts.put("Bags", bagTypeScanCount);
        productTypeScanCounts.put("Clothes", clothTypeScanCount);
        productTypeScanCounts.put("Shoes", shoeTypeScanCount);

        return new ProductTypeScanCount(productTypeScanCounts);
    }

    public List<ConsumptionLevelDetail> getConsumptionLevelDetail(){
        return mysqlDao.getConsumptionLevelDetail();
    }

    public List<ProductTypeSale> getProductTypeSale(){
        return mysqlDao.getProductTypeSale();
    }
}
