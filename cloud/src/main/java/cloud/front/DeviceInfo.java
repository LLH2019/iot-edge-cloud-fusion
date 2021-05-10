package cloud.front;

import lombok.Data;

import java.util.*;

/**
 * @author ：LLH
 * @date ：Created in 2021/5/9 19:53
 * @description：设备相关展示信息
 */
@Data
public class DeviceInfo {
    public String name;
    public List<String> eventList;
    public Map<String, String> propertyMap;
}
