package com.dentist.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "operations_teeth")
@IdClass(OperationToothId.class)
public class OperationTooth implements Serializable {

    private static final long serialVersionUID = 1L;    
    
    @Id
    @ManyToOne
    @JoinColumn(name = "operation_id", referencedColumnName = "id")
    @JsonBackReference  // This breaks the circular reference
    private Operation operation;
    
    @Id
    @ManyToOne(cascade = {})  // Explicitly no cascade operations to Tooth
    @JoinColumn(name = "teeth_id", referencedColumnName = "id")
    private Tooth tooth;

    @Id
    @Column(name = "surface", nullable = true)
    private String surface;

    public OperationTooth() {
        super();
    }

    public OperationTooth(Operation operation, Tooth tooth) {
        this.operation = operation;
        this.tooth = tooth;
    }    
    
    public OperationTooth(Operation operation, Tooth tooth, String surface) {
        this.operation = operation;
        this.tooth = tooth;
        this.surface = surface != null ? surface : "ALL"; // Default to "ALL" if null
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Tooth getTooth() {
        return tooth;
    }

    public void setTooth(Tooth tooth) {
        this.tooth = tooth;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    @Override
    public String toString() {
        return "OperationTooth [operation=" + (operation != null ? operation.getId() : null) 
               + ", tooth=" + (tooth != null ? tooth.getId() : null) 
               + ", surface=" + surface + "]";
    }
}
