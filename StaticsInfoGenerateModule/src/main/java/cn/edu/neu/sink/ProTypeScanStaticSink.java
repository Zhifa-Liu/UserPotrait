package cn.edu.neu.sink;

import cn.edu.neu.bean.ProductTypeScanCountStatic;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

/**
 * @author 32098
 */
public class ProTypeScanStaticSink extends RichSinkFunction<ProductTypeScanCountStatic> {
    private long lastInvokeTime = 0;
    private SimpleDateFormat dateFormat = null;

    private Connection conn = null;
    private PreparedStatement ps = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        conn = DriverManager.getConnection("jdbc:mysql://master:3306/user_portrait", "root", "Hive@2020");
        String sql = "";

        sql = "insert into product_type_scan_count(product_type, dealt_time, scan_count) values (?,?,?) on duplicate key update scan_count=?";

        ps = conn.prepareStatement(sql);

        dateFormat = new SimpleDateFormat("ss");
        lastInvokeTime = System.currentTimeMillis();
    }

    @Override
    public void invoke(ProductTypeScanCountStatic value, Context context) throws Exception {
        long invokeTime = System.currentTimeMillis();
        if(Integer.parseInt(dateFormat.format(invokeTime)) - Integer.parseInt(dateFormat.format(lastInvokeTime))>5){
            String sqlDelete = "delete from product_type_scan_count";
            PreparedStatement psDelete = conn.prepareStatement(sqlDelete);
            psDelete.executeUpdate();
        }
        ps.setString(1, value.getProductType());
        ps.setString(2, value.getDealtTime());
        ps.setLong(3, value.getScanCount());
        ps.setLong(4, value.getScanCount());
        ps.executeUpdate();
        lastInvokeTime = System.currentTimeMillis();
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
