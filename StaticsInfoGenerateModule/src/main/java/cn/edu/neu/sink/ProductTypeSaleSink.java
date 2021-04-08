package cn.edu.neu.sink;

import cn.edu.neu.bean.ProductTypeSaleStatic;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * @author 32098
 */
public class ProductTypeSaleSink extends RichSinkFunction<ProductTypeSaleStatic> {
    private Connection conn = null;
    private PreparedStatement ps = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        conn = DriverManager.getConnection("jdbc:mysql://master:3306/user_portrait", "root", "Hive@2020");
        String sql = "";

        sql = "insert into product_type_total_sale(product_type, total_sale) values (?,?) on duplicate key update total_sale=?";

        ps = conn.prepareStatement(sql);
    }

    @Override
    public void invoke(ProductTypeSaleStatic value, Context context) throws Exception {
        ps.setString(1, value.getProductType());
        ps.setDouble(2, value.getTotalSale());
        ps.setDouble(3, value.getTotalSale());
        ps.executeUpdate();
    }

    @Override
    public void close() throws Exception {
        if (conn != null) {
            conn.close();
        }
        if (ps != null) {
            ps.close();
        }
    }
}


