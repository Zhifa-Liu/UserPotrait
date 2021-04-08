package cn.edu.neu.task.noneWindowTask;

import cn.edu.neu.bean.Statics;
import cn.edu.neu.bean.UserBasicInfo;
import cn.edu.neu.sink.StaticsSink;
import cn.edu.neu.source.UserBasicInfoSource;
import cn.edu.neu.util.CarrierUtils;
import cn.edu.neu.util.DateUtils;
import cn.edu.neu.util.EmailUtils;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author 32098
 *
 * CarrierTask:EmailTask;YearBaseTask
 */
public class TaskStarterA {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);

        DataStreamSource<UserBasicInfo> infoDs = env.addSource(new UserBasicInfoSource());

        // 群体用户画像之手机运营商偏好
        SingleOutputStreamOperator<Statics> resultDsA = infoDs.map(new MapFunction<UserBasicInfo, Statics>() {
            @Override
            public Statics map(UserBasicInfo userBasicInfo) throws Exception {
                String telephone = userBasicInfo.getTelephone();
                String carrier = CarrierUtils.getCarrierByTel(telephone);

                return new Statics("carrier", carrier, 1L);
            }
        }).keyBy(Statics::getStaticsDetail).reduce(
                new ReduceFunction<Statics>() {
                    @Override
                    public Statics reduce(Statics staticsA, Statics staticsB) throws Exception {
                        String staticsName = staticsA.getStaticsName();
                        String staticsDetail = staticsA.getStaticsDetail();
                        Long data1 = staticsA.getStaticsData();
                        Long data2 = staticsB.getStaticsData();
                        return new Statics(staticsName, staticsDetail, data1+data2);
                    }
                }
        );

        resultDsA.addSink(new StaticsSink());

        // 群体用户画像之邮件运营商偏好
        SingleOutputStreamOperator<Statics> resultDsB = infoDs.map(new MapFunction<UserBasicInfo, Statics>() {
            @Override
            public Statics map(UserBasicInfo userBasicInfo) throws Exception {
                String email = userBasicInfo.getEmail();
                String emailType = EmailUtils.getEmailtypeBy(email);

                return new Statics("email", emailType, 1L);
            }
        }).keyBy(Statics::getStaticsDetail).reduce(
                new ReduceFunction<Statics>() {
                    @Override
                    public Statics reduce(Statics staticsA, Statics staticsB) throws Exception {
                        String staticsName = staticsA.getStaticsName();
                        String staticsDetail = staticsA.getStaticsDetail();
                        Long data1 = staticsA.getStaticsData();
                        Long data2 = staticsB.getStaticsData();
                        return new Statics(staticsName, staticsDetail, data1+data2);
                    }
                }
        );

        resultDsB.addSink(new StaticsSink());

        // 群体用户画像之年代标签
        SingleOutputStreamOperator<Statics> resultDsC = infoDs.map(new MapFunction<UserBasicInfo, Statics>() {
            @Override
            public Statics map(UserBasicInfo userBasicInfo) throws Exception {
                int age = userBasicInfo.getAge();
                String yearBaseType = DateUtils.getYearBaseByAge(age);

                return new Statics("yearBase", yearBaseType, 1L);
            }
        }).keyBy(Statics::getStaticsDetail).reduce(
                new ReduceFunction<Statics>() {
                    @Override
                    public Statics reduce(Statics staticsA, Statics staticsB) throws Exception {
                        String staticsName = staticsA.getStaticsName();
                        String staticsDetail = staticsA.getStaticsDetail();
                        Long data1 = staticsA.getStaticsData();
                        Long data2 = staticsB.getStaticsData();
                        return new Statics(staticsName, staticsDetail, data1+data2);
                    }
                }
        );

        resultDsC.addSink(new StaticsSink());

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
