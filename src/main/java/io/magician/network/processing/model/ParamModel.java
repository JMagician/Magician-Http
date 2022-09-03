package io.magician.network.processing.model;

import io.magician.network.processing.enums.ParamType;

import java.util.ArrayList;
import java.util.List;

/**
 * parameter entity
 */
public class ParamModel {

    /**
     * parameter type, file, other
     */
    private ParamType type;

    /**
     * set of parameter values
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
