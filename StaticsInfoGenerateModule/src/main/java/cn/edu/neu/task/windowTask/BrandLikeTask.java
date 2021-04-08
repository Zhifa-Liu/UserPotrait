package cn.edu.neu.task.windowTask;

import cn.edu.neu.bean.ScanProductInfo;
import cn.edu.neu.bean.Statics;
import cn.edu.neu.sink.StaticsSink;
import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.ContinuousProcessingTimeTrigger;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author 32098
 *
 * 群体用户画像之实时品牌偏好
 */
public class BrandLikeTask {
    public static void main(String[] args) {
        Properties pros = new Properties();
        pros.setProperty("bootstrap.servers", "master:9092");
        pros.setProperty("group.id", "flink");
        pros.setProperty("auto.offset.reset","latest");
        pros.setProperty("flink.partition-discovery.interval-millis","5000");
        pros.setProperty("enable.auto.commit", "true");
        pros.setProperty("auto.commit.interval.ms", "2000");

        FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<String>(
                "flink_kafka",
                new SimpleStringSchema(),
                pros
        );
        kafkaSource.setStartFromLatest();

        // 1. env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 2. source
        DataStreamSource<String> kafkaDataStream = env.addSource(kafkaSource);

        // 3. transformation
        // to java object
        SingleOutputStreamOperator<ScanProductInfo> scanProductInfoDataStream = kafkaDataStream.map(new MapFunction<String, ScanProductInfo>() {
            @Override
            public ScanProductInfo map(String s) throws Exception {
                return JSON.parseObject(s, ScanProductInfo.class);
            }
        });

        // Exception in thread "main" org.apache.flink.api.common.functions.InvalidTypesException: The generic type parameters of 'Tuple2' are missing.
        // In many cases lambda methods don't provide enough information for automatic type extraction when Java generics are involved. An easy workaround is to use an (anonymous) class instead that implements the 'org.apache.flink.api.java.functions.KeySelector' interface.
        // Otherwise the type has to be specified explicitly using type information.
//        SingleOutputStreamOperator<Statics> resultDs = scanProductInfoDataStream.map(new MapFunction<ScanProductInfo, Tuple2<Tuple2<String, String>, Long>>() {
//            @Override
//            public Tuple2<Tuple2<String, String>, Long> map(ScanProductInfo scanProductInfo) throws Exception {
//                String productType = scanProductInfo.getProductType();
//                String brand = scanProductInfo.getBrand();
//                return Tuple2.of(Tuple2.of("brandLikeOf" + productType, brand), 1L);
//            }
//        }).keyBy(e -> e.f0).window(SlidingProcessingTimeWindows.of(Time.minutes(6), Time.minutes(1))).reduce(new ReduceFunction<Tuple2<Tuple2<String, String>, Long>>() {
//            @Override
//            public Tuple2<Tuple2<String, String>, Long> reduce(Tuple2<Tuple2<String, String>, Long> tuple1, Tuple2<Tuple2<String, String>, Long> tuple2) throws Exception {
//                return Tuple2.of(tuple1.f0, tuple1.f1 + tuple2.f1);
//            }
//        }).map(new MapFunction<Tuple2<Tuple2<String, String>, Long>, Statics>() {
//            @Override
//            public Statics map(Tuple2<Tuple2<String, String>, Long> tupleIn) throws Exception {
//                return new Statics(tupleIn.f0.f0, tupleIn.f0.f1, tupleIn.f1);
//            }
//        });

        SingleOutputStreamOperator<Statics> resultDs = scanProductInfoDataStream.map(new MapFunction<ScanProductInfo, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(ScanProductInfo scanProductInfo) throws Exception {
                String productType = scanProductInfo.getProductType();
                String brand = scanProductInfo.getBrand();
                return Tuple2.of("brandLikeOf" + productType+"#"+brand, 1L);
            }
        }).keyBy(e -> e.f0).window(TumblingProcessingTimeWindows.of(Time.days(1))).trigger(ContinuousProcessingTimeTrigger.of(Time.seconds(10))).reduce(new ReduceFunction<Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> reduce(Tuple2<String, Long> t1, Tuple2<String, Long> t2) throws Exception {
                return Tuple2.of(t1.f0, t1.f1+t2.f1);
            }
        }).map(new MapFunction<Tuple2<String, Long>, Statics>() {
            @Override
            public Statics map(Tuple2<String, Long> tuple) throws Exception {
                String[] staticNameAndDetail = tuple.f0.split("#");
                return new Statics(staticNameAndDetail[0], staticNameAndDetail[1], tuple.f1);
            }
        });

        resultDs.addSink(new StaticsSink());

        try {
            env.execute("useType analysis");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
