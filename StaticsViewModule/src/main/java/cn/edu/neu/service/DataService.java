package cn.edu.neu.service;

import cn.edu.neu.po.ProductTypeScanCount;
import cn.edu.neu.po.SingleTypeScanCount;
import cn.edu.neu.po.ConsumptionLevelDetail;
import cn.edu.neu.po.ProductTypeSale;
import cn.edu.neu.po.StaticsInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 *
 * @author 32098
 */
@FeignClient(value = "StaticsInfoObtainModule")
public interface DataService {

    @RequestMapping(value = "mysqlData/yearBase", method = RequestMethod.POST)
    public List<StaticsInfo> yearBaseStatics();

    @RequestMapping(value = "mysqlData/useTypeLike", method = RequestMethod.POST)
    public List<StaticsInfo> useTypeStatics();

    @RequestMapping(value = "mysqlData/payTypeLike", method = RequestMethod.POST)
    public List<StaticsInfo> payTypeStatics();

    @RequestMapping(value = "mysqlData/emailLike", method = RequestMethod.POST)
    public List<StaticsInfo> emailStatics();

    @RequestMapping(value = "mysqlData/carrierLike",method = RequestMethod.POST)
    public List<StaticsInfo> carrierStatics();

    @RequestMapping(value = "mysqlData/brandLikeOfClothes",method = RequestMethod.POST)
    public List<StaticsInfo> brandLikeOfClothesStatics();

    @RequestMapping(value = "mysqlData/brandLikeOfBags",method = RequestMethod.POST)
    public List<StaticsInfo> brandLikeOfBagsStatics();

    @RequestMapping(value = "mysqlData/brandLikeOfShoes",method = RequestMethod.POST)
    public List<StaticsInfo> brandLikeOfShoesStatics();

    @RequestMapping(value = "mysqlData/consumptionLevel",method = RequestMethod.POST)
    public List<StaticsInfo> consumptionLevelStatics();

    @RequestMapping(value = "mysqlData/bagTypeScanCount",method = RequestMethod.POST)
    public List<SingleTypeScanCount> bagTypeScanCount();

    @RequestMapping(value = "mysqlData/clothTypeScanCount",method = RequestMethod.POST)
    public List<SingleTypeScanCount> clothTypeScanCount();

    @RequestMapping(value = "mysqlData/shoeTypeScanCount",method = RequestMethod.POST)
    public List<SingleTypeScanCount> shoeTypeScanCount();

    @RequestMapping(value = "mysqlData/productTypeScanCount",method = RequestMethod.POST)
    public ProductTypeScanCount productTypeScanCount();

    @RequestMapping(value = "mysqlData/consumptionLevelDetail",method = RequestMethod.POST)
    public List<ConsumptionLevelDetail> getConsumptionLevelDetail();

    @RequestMapping(value = "mysqlData/productTypeSale",method = RequestMethod.POST)
    public List<ProductTypeSale> getProductTypeSale();
}

