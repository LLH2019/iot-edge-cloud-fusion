package cloud.global;

import lombok.Data;

/**
 * @author ：LLH
 * @date ：Created in 2021/5/9 22:11
 * @description：全局kafka配置
 */
public class GlobalKafkaConfig {
    public final static String server = "192.168.123.131:9092";
    public final static String groupId = "1";
    public final static String cloud_in_topic = "cloud.*";
}
