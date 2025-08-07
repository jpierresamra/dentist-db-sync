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
@Table(name = "treatments_teeth")
@IdClass(TreatmentToothId.class)
public class TreatmentTooth implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "treatment_id", referencedColumnName = "id")
    @JsonBackReference  // This breaks the circular reference
    private Treatment treatment;
    
    @Id
    @ManyToOne(cascade = {})  // Explicitly no cascade operations to Tooth
    @JoinColumn(name = "teeth_id", referencedColumnName = "id")
    private Tooth tooth;

    @Id
    @Column(name = "surface", nullable = true)
    private String surface;

	public TreatmentTooth() {
		super();
	}
	
    public TreatmentTooth(Treatment treatment, Tooth tooth) {
        this.treatment = treatment;
        this.tooth = tooth;
    } 
	
    public TreatmentTooth(Treatment treatment, Tooth tooth, String surface) {
        this.treatment = treatment;
        this.tooth = tooth;
        this.surface = surface != null ? surface : "ALL"; // Default to "ALL" if null
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
    
	public Treatment getTreatment() {
		return treatment;
	}

	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}

}
