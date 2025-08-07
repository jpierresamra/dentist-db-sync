package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class OperationToothId implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID operation;
    private Integer tooth;
    private String surface;

    public OperationToothId() {
        super();
    }

    public OperationToothId(UUID operation, Integer tooth, String surface) {
        this.operation = operation;
        this.tooth = tooth;
        this.surface = surface;
    }

    public UUID getOperation() {
        return operation;
    }

    public void setOperation(UUID operation) {
        this.operation = operation;
    }

    public Integer getTooth() {
        return tooth;
    }

    public void setTooth(Integer tooth) {
        this.tooth = tooth;
    }    
    
    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OperationToothId that = (OperationToothId) obj;
        return Objects.equals(operation, that.operation) && 
               Objects.equals(tooth, that.tooth) && 
               Objects.equals(surface, that.surface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, tooth, surface);
    }
}
