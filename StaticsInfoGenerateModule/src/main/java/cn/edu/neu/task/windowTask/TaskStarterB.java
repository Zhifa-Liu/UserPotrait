package cn.edu.neu.task.windowTask;

import cn.edu.neu.bean.ProductTypeScanCountStatic;
import cn.edu.neu.bean.ScanProductInfo;
import cn.edu.neu.bean.Statics;
import cn.edu.neu.sink.ProTypeScanStaticSink;
import cn.edu.neu.sink.StaticsSink;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.ContinuousProcessingTimeTrigger;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @author 32098
 *
 * BrandLikeTask:UseTypeTask:ScanProductNearlyMinuteCountTask
 */
public class TaskStarterB {
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

        // BrandLikeTask
        // ???????????????????????????????????????
        SingleOutputStreamOperator<Statics> resultDsA = scanProductInfoDataStream.map(new MapFunction<ScanProductInfo, Tuple2<String, Long>>() {
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

        resultDsA.addSink(new StaticsSink());

        // UseTypeTask
        // ???????????????????????????????????????
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        SingleOutputStreamOperator<Statics> resultDsB = scanProductInfoDataStream.map(new MapFunction<ScanProductInfo, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(ScanProductInfo scanProductInfo) throws Exception {
                String useType = scanProductInfo.getUseType();
                return Tuple2.of(useType, 1L);
            }
        }).keyBy(e -> e.f0).window(TumblingProcessingTimeWindows.of(Time.days(1))).trigger(ContinuousProcessingTimeTrigger.of(Time.seconds(10))).reduce(new ReduceFunction<Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> reduce(Tuple2<String, Long> tA, Tuple2<String, Long> tB) throws Exception {
                return Tuple2.of(tA.f0, tA.f1+tB.f1);
            }
        }).map(new MapFunction<Tuple2<String, Long>, Statics>() {
            @Override
            public Statics map(Tuple2<String, Long> tupleIn) throws Exception {
                return new Statics("useType", tupleIn.f0, tupleIn.f1);
            }
        });

        resultDsB.addSink(new StaticsSink());

        // ScanProductNearlyMinuteCountTask
        // ????????????????????????????????????????????????10s????????????
        SingleOutputStreamOperator<ScanProductInfo> wateredProductScanDs = scanProductInfoDataStream.assignTimestampsAndWatermarks(
                WatermarkStrategy.<ScanProductInfo>forBoundedOutOfOrderness(Duration.ofSeconds(1)).withTimestampAssigner((scanProductInfo, timeStamp) -> scanProductInfo.getBeginScanTime())
        );

        //
        SingleOutputStreamOperator<ScanProductNearlyMinuteCountTask.TimeProductTypeCount> dealtProductScanDs = wateredProductScanDs.map(new MapFunction<ScanProductInfo, Tuple4<Long, String, String, Long>>() {
            @Override
            public Tuple4<Long, String, String, Long> map(ScanProductInfo scanProductInfo) throws Exception {
                Long eventTime = scanProductInfo.getBeginScanTime();
                String timeDealt = timeProcess(scanProductInfo.getBeginScanTime());
                String productType = scanProductInfo.getProductType();
                return Tuple4.of(eventTime, timeDealt, productType, 1L);
            }
        }).map(new MapFunction<Tuple4<Long, String, String, Long>, ScanProductNearlyMinuteCountTask.TimeProductTypeCount>() {
            @Override
            public ScanProductNearlyMinuteCountTask.TimeProductTypeCount map(Tuple4<Long, String, String, Long> inTuple) throws Exception {
                return new ScanProductNearlyMinuteCountTask.TimeProductTypeCount(inTuple.f0, inTuple.f1, inTuple.f2, inTuple.f3);
            }
        });

        // Cannot apply '$HOP' to arguments of type '$HOP(<BIGINT>, <INTERVAL SECOND>, <INTERVAL SECOND>)'. Supported form(s): '$HOP(<DATETIME>, <DATETIME_INTERVAL>, <DATETIME_INTERVAL>)'
        tEnv.createTemporaryView("temp", dealtProductScanDs, $("eventTime").rowtime(), $("dealtTime"), $("productType"), $("scanCount"));

        Table queryResultTable = tEnv.sqlQuery(
                "SELECT productType, dealtTime, count(scanCount) as scanCount FROM temp GROUP BY productType, dealtTime, HOP(eventTime, interval '10' SECOND, interval '60' SECOND)"
        );

        DataStream<ProductTypeScanCountStatic> resultDs = tEnv.toRetractStream(queryResultTable, ProductTypeScanCountStatic.class).filter(e->e.f0).map(e->e.f1);

        resultDs.addSink(new ProTypeScanStaticSink());

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class TimeProductTypeCount{
        private Long eventTime;
        private String dealtTime;
        private String productType;
        private long scanCount;
    }

    /**
     *
     * ??????????????????????????????->HH:mm:ss->HH:mm:ss'???ss->ss' ????????????????????????
     *
     *         9s(1-10) => 10s
     *         13s(11-20) => 20s
     *         24s(21-30) => 30s
     *         32s(31-40) => 40s
     *         48s(41-50) => 50s
     *         56s(51-60) => 60s(0)
     *         (s / 10 (??????) + 1)*10 : (56/10+1)=60
     *
     * @return ???????????????????????????=>12:12:12->12:12:20
     */
    private static String timeProcess(long ts){
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date(ts));
        String[] hms = time.split(":");
        int s = (Integer.parseInt(hms[2])/10+1)*10;
        int m = Integer.parseInt(hms[1]);
        int h = Integer.parseInt(hms[0]);
        if(s == 60){
            m = m + 1;
            s = 0;
            if(m == 60){
                h = h + 1;
                if(h == 24){
                    h = 0;
                }
            }
        }
        String hStr, mStr, sStr;
        if(h < 10){
            hStr = "0" + h;
        }else{
            hStr = String.valueOf(h);
        }
        if(m < 10){
            mStr = "0" + m;
        }else{
            mStr = String.valueOf(m);
        }
        if(s == 0){
            sStr = "00";
        }else{
            sStr = String.valueOf(s);
        }
        return hStr+":"+mStr+":"+sStr;
    }
}
