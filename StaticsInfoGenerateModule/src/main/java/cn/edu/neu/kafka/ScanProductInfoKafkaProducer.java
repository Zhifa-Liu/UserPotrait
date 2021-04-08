package cn.edu.neu.kafka;

import cn.edu.neu.bean.ScanProductInfo;
import cn.edu.neu.source.ScanProductInfoSource;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Properties;

/**
 * @author 32098
 */
public class ScanProductInfoKafkaProducer {
    public static void main(String[] args) throws Exception {
        // 1. env：创建流式执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2. source：添加自定义产生广告点击模拟数据的Source
        DataStreamSource<ScanProductInfo> advertiseClickDataStream = env.addSource(new ScanProductInfoSource());

        // 3. transformation
        SingleOutputStreamOperator<String> advertiseClickDataJsonStream = advertiseClickDataStream.map(new MapFunction<ScanProductInfo, String>() {
            @Override
            public String map(ScanProductInfo advertiseClickBean) throws Exception {
                return JSON.toJSONString(advertiseClickBean);
            }
        });

        // 4. sink to kafka
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "master:9092");
        FlinkKafkaProducer<String> kafkaSink = new FlinkKafkaProducer<>("flink_kafka", new SimpleStringSchema(), props);

        advertiseClickDataJsonStream.addSink(kafkaSink);

        // 5. execute
        env.execute();
    }
}
