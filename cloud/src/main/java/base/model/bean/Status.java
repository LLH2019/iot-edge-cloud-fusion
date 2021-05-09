package base.model.bean;

import lombok.Data;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 11:00
 * @description：状态
 */
@Data
public class Status {

    private ThingStatus status = ThingStatus.OFFLINE;

    public enum ThingStatus {
        ONLINE,OFFLINE,NOT_ACTIVATION
    }
}
