package cn.edu.neu.task.windowTask;

import cn.edu.neu.bean.ConsumeInfo;
import cn.edu.neu.bean.ProductTypeSaleStatic;
import cn.edu.neu.sink.ProductTypeSaleSink;
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
 * 各类产品每天销售额实时统计：10触发一次计算
 */
public class ProductTypeSaleTask {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);

        DataStreamSource<ConsumeInfo> consumeInfoDs = env.addSource(new ConsumeInfoSource());

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

        env.execute();
    }
}

