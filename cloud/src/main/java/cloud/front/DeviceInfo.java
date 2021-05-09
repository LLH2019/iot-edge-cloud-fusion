package cloud.front;

import lombok.Data;

import java.util.Map;

/**
 * @author ：LLH
 * @date ：Created in 2021/5/9 19:53
 * @description：设备相关展示信息
 */
@Data
public class DeviceInfo {
    public String name;
    public Map<String, String> instructs;
    public Map<String, String> values;
}
