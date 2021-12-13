package io.magician.network.processing.model;

import io.magician.network.processing.enums.ParamType;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数实体
 */
public class ParamModel {

    /**
     * 参数类型，文件，其他
     */
    private ParamType type;

    /**
     * 参数值集合
     */
    private List value;

    public ParamType getType() {
        return type;
    }

    public void setType(ParamType type) {
        this.type = type;
    }

    public List getValue() {
        return value;
    }

    public void setValue(List value) {
        if (this.value != null) {
            this.value.addAll(value);
        }
        this.value = value;
    }

    public void setValueItem(Object value) {
        if (this.value == null) {
            this.value = new ArrayList();
        }
        this.value.add(value);
    }
}
