package cn.edu.neu.task.windowTask;

import cn.edu.neu.bean.ConsumeInfo;
import cn.edu.neu.bean.Statics;
import cn.edu.neu.sink.StaticsSink;
import cn.edu.neu.source.ConsumeInfoSource;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.ContinuousProcessingTimeTrigger;

/**
 * @author 32098
 *
 * 群体用户画像之支付类型偏好
 */
public class PayTypeTask {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);

        DataStreamSource<ConsumeInfo> consumeInfoDs = env.addSource(new ConsumeInfoSource());

        SingleOutputStreamOperator<Statics> resultDs = consumeInfoDs.map(new MapFunction<ConsumeInfo, Tuple2<String, Long>>() {
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

        resultDs.addSink(new StaticsSink());

        try {
            env.execute("payType analysis");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



