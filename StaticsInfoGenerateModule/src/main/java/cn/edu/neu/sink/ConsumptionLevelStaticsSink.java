package cn.edu.neu.sink;

import cn.edu.neu.bean.Statics;
import cn.edu.neu.bean.UserConsumptionLevelStatics;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * @author 32098
 */
public class ConsumptionLevelStaticsSink extends RichSinkFunction<UserConsumptionLevelStatics> {
    private Connection conn = null;
    private PreparedStatement ps = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        conn = DriverManager.getConnection("jdbc:mysql://master:3306/user_portrait", "root", "Hive@2020");
        String sql = "";

        sql = "insert into user_consumption_level(userId, avg_amount, level) values (?,?,?) on duplicate key update avg_amount=?, level=?";

        ps = conn.prepareStatement(sql);
    }

    @Override
    public void invoke(UserConsumptionLevelStatics statics, Context context) throws Exception {
        ps.setString(1, statics.getUid());
        ps.setDouble(2, statics.getAvgAmount());
        ps.setString(3, statics.getLevel());
        ps.setDouble(4, statics.getAvgAmount());
        ps.setString(5, statics.getLevel());
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
