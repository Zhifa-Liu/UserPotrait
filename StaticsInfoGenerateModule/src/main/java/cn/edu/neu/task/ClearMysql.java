package cn.edu.neu.task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author 32098
 */
public class ClearMysql {
    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://master:3306/user_portrait", "root", "Hive@2020");
        PreparedStatement ps;
        String sql = "delete from statics";
        ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        sql = "delete from product_type_scan_count";
        ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        sql = "delete from user_consumption_level";
        ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        sql = "delete from product_type_total_sale";
        ps = conn.prepareStatement(sql);
        ps.executeUpdate();
    }
}

