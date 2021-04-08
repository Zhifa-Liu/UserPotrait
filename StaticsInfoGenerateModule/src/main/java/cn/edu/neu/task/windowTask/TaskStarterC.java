package cn.edu.neu.task.windowTask;

import cn.edu.neu.bean.ConsumeInfo;
import cn.edu.neu.bean.ProductTypeSaleStatic;
import cn.edu.neu.bean.Statics;
import cn.edu.neu.bean.UserConsumptionLevelStatics;
import cn.edu.neu.sink.ConsumptionLevelStaticsSink;
import cn.edu.neu.sink.ProductTypeSaleSink;
import cn.edu.neu.sink.StaticsSink;
import cn.edu.neu.source.ConsumeInfoSource;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.ContinuousProcessingTimeTrigger;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * @author 32098
 *
 * PayTypeTask:ProductTypeSaleTask:ConsumptionLevelTask
 */
public class TaskStarterC {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);

        DataStreamSource<ConsumeInfo> consumeInfoDs = env.addSource(new ConsumeInfoSource());

        // PayTypeTask
        // 群体用户画像之支付类型偏好
        SingleOutputStreamOperator<Statics> resultDsA = consumeInfoDs.map(new MapFunction<ConsumeInfo, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(ConsumeInfo consumeInfo) throws Exception {
                String payType = consumeInfo.getPayType();
                return Tuple2.of(payType, 1L);
            }
        }).keyBy(e -> e.f0).window(TumblingProcessingTimeWindows.of(Time.days(1))).trigger(ContinuousProcessingTimeTrigger.of(Time.seconds(10))).reduce(new ReduceFunction<Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> reduce(Tuple2<String, Long> tA, Tuple2<String, Long> tB) throws Exception {
                return Tuple2.of(tA.f0, tA.f1+tB.f1);
            }
        }).map(new MapFunction<Tuple2<String, Long>, Statics>() {
            @Override
            public Statics map(Tuple2<String, Long> tupleIn) throws Exception {
                return new Statics("payType", tupleIn.f0, tupleIn.f1);
            }
        });

        resultDsA.addSink(new StaticsSink());

        // ProductTypeSaleTask
        // 各类产品每天销售额实时统计：10触发一次计算
        SingleOutputStreamOperator<ProductTypeSaleStatic> productTypeTotalSaleDs = consumeInfoDs.map(new MapFunction<ConsumeInfo, Tuple2<String, Double>>() {
            @Override
            public Tuple2<String, Double> map(ConsumeInfo consumeInfo) throws Exception {
                String productType = consumeInfo.getProductType();
                double totalAmount = consumeInfo.getUnitPrice() * consumeInfo.getCount() - consumeInfo.getCouponAmount();
                return Tuple2.of(productType, totalAmount);
            }
        }).keyBy(e -> e.f0).window(TumblingProcessingTimeWindows.of(Time.days(1))).trigger(ContinuousProcessingTimeTrigger.of(Time.seconds(10))
        ).reduce(new ReduceFunction<Tuple2<String, Double>>() {
            @Override
            public Tuple2<String, Double> reduce(Tuple2<String, Double> inA, Tuple2<String, Double> inB) throws Exception {
                return Tuple2.of(inA.f0, inA.f1 + inB.f1);
            }
        }).map(new MapFunction<Tuple2<String, Double>, ProductTypeSaleStatic>() {
            @Override
            public ProductTypeSaleStatic map(Tuple2<String, Double> in) throws Exception {
                return new ProductTypeSaleStatic(in.f0, in.f1);
            }
        });

        productTypeTotalSaleDs.addSink(new ProductTypeSaleSink());

        // ConsumptionLevelTask
        // 1.群体用户画像之每月实时消费水平
        // 2.用户每月实时消费标签
        SingleOutputStreamOperator<Tuple2<String, Double>> userAvgConsumeDs = consumeInfoDs.map(new MapFunction<ConsumeInfo, Tuple3<String, Double, Integer>>() {
            @Override
            public Tuple3<String, Double, Integer> map(ConsumeInfo consumeInfo) throws Exception {
                String userId = consumeInfo.getUserId();
                double totalAmount = consumeInfo.getUnitPrice() * consumeInfo.getCount() - consumeInfo.getCouponAmount();
                return Tuple3.of(userId, totalAmount, 1);
            }
        }).keyBy(e -> e.f0).window(TumblingProcessingTimeWindows.of(Time.days(30))).trigger(ContinuousProcessingTimeTrigger.of(Time.seconds(10))
        ).reduce(new ReduceFunction<Tuple3<String, Double, Integer>>() {
            @Override
            public Tuple3<String, Double, Integer> reduce(Tuple3<String, Double, Integer> inA, Tuple3<String, Double, Integer> inB) throws Exception {
                return Tuple3.of(inA.f0, inA.f1 + inB.f1, inA.f2 + inA.f2);
            }
        }).map(new MapFunction<Tuple3<String, Double, Integer>, Tuple2<String, Double>>() {
            @Override
            public Tuple2<String, Double> map(Tuple3<String, Double, Integer> in) throws Exception {
                return Tuple2.of(in.f0, in.f1 / in.f2);
            }
        });
        SingleOutputStreamOperator<UserConsumptionLevelStatics> resultDsB = userAvgConsumeDs.map(new MapFunction<Tuple2<String, Double>, UserConsumptionLevelStatics>() {
            @Override
            public UserConsumptionLevelStatics map(Tuple2<String, Double> tupleIn) throws Exception {
                String uid = tupleIn.f0;

                double avgAmount = tupleIn.f1;

                String level = "低消费水平";
                if (avgAmount >= 800 && avgAmount < 2100) {
                    level = "中消费水平";
                } else if (avgAmount >= 2100) {
                    level = "高消费水平";
                }

                // 运行时间比较久时，java.lang.NumberFormatException: Infinite or NaN
                BigDecimal bigDecimal = new BigDecimal(avgAmount);

                return new UserConsumptionLevelStatics(uid, bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(), level);
            }
        });

        resultDsB.addSink(new ConsumptionLevelStaticsSink());

        //
        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
