package cn.edu.neu.sink;

import cn.edu.neu.bean.Statics;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * @author 32098
 */
public class StaticsSink extends RichSinkFunction<Statics> {
    private Connection conn = null;
    private PreparedStatement ps = null;
//    private boolean overwrite = false;
//    private String staticNamePrefix = "";

    public StaticsSink(){}

//    public StaticsSink(boolean overwrite, String staticNamePrefix){
//        this.overwrite = overwrite;
//        this.staticNamePrefix = staticNamePrefix;
//    }

    @Override
    public void open(Configuration parameters) throws Exception {
        conn = DriverManager.getConnection("jdbc:mysql://master:3306/user_portrait", "root", "Hive@2020");
        String sql = "";
//        if(this.overwrite){
//            sql = "delete from statics where static_name like ?";
//            ps = conn.prepareStatement(sql);
//            ps.setString(1, staticNamePrefix+"%");
//            ps.executeUpdate();
//            sql = "insert into statics(static_name, static_detail, static_data) values (?,?,?) on duplicate key update static_data=?";
//        }else{
//            sql = "insert into statics(static_name, static_detail, static_data) values (?,?,?) on duplicate key update static_data=?";
//        }
        sql = "insert into statics(static_name, static_detail, static_data) values (?,?,?) on duplicate key update static_data=?";
        ps = conn.prepareStatement(sql);
    }

    @Override
    public void invoke(Statics statics, Context context) throws Exception {
        ps.setString(1, statics.getStaticsName());
        ps.setString(2, statics.getStaticsDetail());
        ps.setLong(3, statics.getStaticsData());
        ps.setLong(4, statics.getStaticsData());
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

