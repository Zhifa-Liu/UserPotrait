package cn.edu.neu.control;

import cn.edu.neu.entity.*;
import cn.edu.neu.service.MysqlDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @author 32098
 */
@RestController
@RequestMapping("mysqlData")
public class MysqlDataControl {
    @Autowired
    private MysqlDataServiceImpl mysqlDataServiceImpl;

    @RequestMapping(value = "yearBase", method = RequestMethod.POST)
    public List<StaticsInfo> getYearBaseStatics(){
        return mysqlDataServiceImpl.getStaticsBy("yearBase");
    }

    @RequestMapping(value = "useTypeLike",method = RequestMethod.POST)
    public List<StaticsInfo> getUseTypeStatics(){
        return mysqlDataServiceImpl.getStaticsBy("useType");
    }

    @RequestMapping(value = "payTypeLike",method = RequestMethod.POST)
    public List<StaticsInfo> getPayTypeStatics(){
        return mysqlDataServiceImpl.getStaticsBy("payType");
    }

    @RequestMapping(value = "emailLike", method = RequestMethod.POST)
    public List<StaticsInfo> getEmailLikeStatics(){
        return mysqlDataServiceImpl.getStaticsBy("email");
    }

    @RequestMapping(value = "carrierLike", method = RequestMethod.POST)
    public List<StaticsInfo> getCarrierLikeStatics(){
        return mysqlDataServiceImpl.getStaticsBy("carrier");
    }

    @RequestMapping(value = "brandLikeOfClothes", method = RequestMethod.POST)
    public List<StaticsInfo> getBrandLikeOfClothesStatics(){
        return mysqlDataServiceImpl.getStaticsBy("brandLikeOfClothes");
    }

    @RequestMapping(value = "brandLikeOfBags", method = RequestMethod.POST)
    public List<StaticsInfo> getBrandLikeOfBagsStatics(){
        return mysqlDataServiceImpl.getStaticsBy("brandLikeOfBags");
    }

    @RequestMapping(value = "brandLikeOfShoes", method = RequestMethod.POST)
    public List<StaticsInfo> getBrandLikeOfShoesStatics(){
        return mysqlDataServiceImpl.getStaticsBy("brandLikeOfShoes");
    }

    @RequestMapping(value = "consumptionLevel",method = RequestMethod.POST)
    public List<StaticsInfo> searchConsumptionLevel(){
        return mysqlDataServiceImpl.getConsumptionLevelStatics();
    }

    @RequestMapping(value = "bagTypeScanCount", method = RequestMethod.POST)
    public List<SingleTypeScanCount> getBagTypeScanCount(){
        return mysqlDataServiceImpl.getSingleTypeScanCount("Bags");
    }

    @RequestMapping(value = "clothTypeScanCount", method = RequestMethod.POST)
    public List<SingleTypeScanCount> getClothTypeScanCount(){
        return mysqlDataServiceImpl.getSingleTypeScanCount("Clothes");
    }

    @RequestMapping(value = "shoeTypeScanCount", method = RequestMethod.POST)
    public List<SingleTypeScanCount> getShoeTypeCount(){
        return mysqlDataServiceImpl.getSingleTypeScanCount("Shoes");
    }

    @RequestMapping(value = "productTypeScanCount", method = RequestMethod.POST)
    public ProductTypeScanCount getProductTypeScanCount(){
        return mysqlDataServiceImpl.getProductTypeScanCount();
    }

    @RequestMapping(value = "consumptionLevelDetail", method = RequestMethod.POST)
    public List<ConsumptionLevelDetail> getConsumptionLevelDetail(){
        return mysqlDataServiceImpl.getConsumptionLevelDetail();
    }

    @RequestMapping(value = "productTypeSale", method = RequestMethod.POST)
    public List<ProductTypeSale> getProductTypeSale(){
        return mysqlDataServiceImpl.getProductTypeSale();
    }
}
