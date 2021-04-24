package things.model.bean;

import lombok.Data;

import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/24 13:59
 * @description：保留基本信息的物模型抽象
 */
@Data
public class AbstractModel {
    private String name;

    private Profile profile;

    private List<Property> properties;

    private List<Event> events;


    @Override
    public String toString() {
        return "AbstractModel{" +
                "name='" + name + '\'' +
                ", profile=" + profile +
                ", properties=" + properties +
                ", events=" + events +
                '}';
    }
}
