package io.magician.network.processing.model;

import io.magician.network.processing.enums.ParamType;
import io.netty.handler.codec.http.multipart.MixedFileUpload;

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
    private List values;

    /**
     * set of parameter files
     */
    private List<MixedFileUpload> files;

    public ParamType getType() {
        return type;
    }

    public void setType(ParamType type) {
        this.type = type;
    }

    public List getValues() {
        return values;
    }

    public void setValue(List value) {
        if (this.values != null) {
            this.values.addAll(value);
            return;
        }
        this.values = value;
    }

    public void setValueItem(Object value) {
        if (this.values == null) {
            this.values = new ArrayList();
        }
        this.values.add(value);
    }

    public List<MixedFileUpload> getFiles() {
        return files;
    }

    public void setFiles(List<MixedFileUpload> files) {
        if(this.files != null){
            this.files.addAll(files);
            return;
        }
        this.files = files;
    }

    public void setFileItem(MixedFileUpload file) {
        if(this.files == null){
            this.files = new ArrayList<>();
        }
        this.files.add(file);
    }
}
