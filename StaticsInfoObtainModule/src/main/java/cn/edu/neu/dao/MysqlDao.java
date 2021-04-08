package cn.edu.neu.dao;

import cn.edu.neu.entity.ConsumptionLevelDetail;
import cn.edu.neu.entity.ProductTypeSale;
import cn.edu.neu.entity.SingleTypeScanCount;
import cn.edu.neu.entity.StaticsInfo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 32098
 */
@Repository
public interface MysqlDao {
    @Select("select * from statics where static_name=#{name}")
    List<StaticsInfo> getStaticByName(String name);

    @Select("select 'consumptionLevel' as static_name, level as static_detail, count(*) as static_data from user_consumption_level group by level")
    List<StaticsInfo> getConsumptionLevelStatic();

    @Select("select dealt_time, scan_count from product_type_scan_count where product_type=#{productType} order by dealt_time desc limit 6")
    List<SingleTypeScanCount> getSingleTypeScanCount(String productType);

    @Select("select * from user_consumption_level")
    List<ConsumptionLevelDetail> getConsumptionLevelDetail();

    @Select("select * from product_type_total_sale order by total_sale desc")
    List<ProductTypeSale> getProductTypeSale();
}
