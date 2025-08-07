package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class TreatmentToothId implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID treatment;
    private Integer tooth;
    private String surface;

    public TreatmentToothId() {
        super();
    }

    public TreatmentToothId(UUID treatment, Integer tooth, String surface) {
        this.treatment = treatment;
        this.tooth = tooth;
        this.surface = surface;
    }

	public UUID getTreatment() {
		return treatment;
	}

	public void setTreatment(UUID treatment) {
		this.treatment = treatment;
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
        TreatmentToothId that = (TreatmentToothId) obj;
        return Objects.equals(treatment, that.treatment) && 
               Objects.equals(tooth, that.tooth) && 
               Objects.equals(surface, that.surface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treatment, tooth, surface);
    }
}
