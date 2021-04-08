package cn.edu.neu.task.windowTask;

import cn.edu.neu.bean.ConsumeInfo;
import cn.edu.neu.bean.Statics;
import cn.edu.neu.bean.UserConsumptionLevelStatics;
import cn.edu.neu.sink.ConsumptionLevelStaticsSink;
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
import scala.tools.nsc.Global;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 *
 * @author 32098
 *
 * 1.群体用户画像之每月实时消费水平
 * 2.用户每月实时消费标签
 */
public class ConsumptionLevelTask {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);

        DataStreamSource<ConsumeInfo> consumeInfoDs = env.addSource(new ConsumeInfoSource());

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
        NumberFormat nbf= NumberFormat.getInstance();
        SingleOutputStreamOperator<UserConsumptionLevelStatics> resultDs = userAvgConsumeDs.map(new MapFunction<Tuple2<String, Double>, UserConsumptionLevelStatics>() {
            @Override
            public UserConsumptionLevelStatics map(Tuple2<String, Double> tupleIn) throws Exception {
                String uid = tupleIn.f0;

                double avgAmount = tupleIn.f1;

                String level = "低消费水平";
                if (avgAmount >= 800 && avgAmount < 1800) {
                    level = "中消费水平";
                } else if (avgAmount >= 1800) {
                    level = "高消费水平";
                }

                BigDecimal bigDecimal = new BigDecimal(avgAmount);

                return new UserConsumptionLevelStatics(uid, bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(), level);
            }
        });

        resultDs.addSink(new ConsumptionLevelStaticsSink());

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


