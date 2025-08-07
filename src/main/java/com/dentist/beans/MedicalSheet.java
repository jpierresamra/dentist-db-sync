package com.dentist.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "medical_sheets")
public class MedicalSheet implements Serializable, Persistable<UUID>, ComparableSyncItem {

    private static final long serialVersionUID = 1L;

    public static final int STATUS_CREATED = 1;
    public static final int STATUS_DELETED = 2;

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID customerId;

    @Column(name = "account_id", nullable = false)
    private int accountId;

    @Column(name = "blood_test")
    private boolean bloodTest;

    @Column(name = "cardiac")
    private boolean cardiac;

    @Column(name = "hematologic")
    private boolean hematologic;

    @Column(name = "hepatic")
    private boolean hepatic;

    @Column(name = "endocrine")
    private boolean endocrine;

    @Column(name = "digestive")
    private boolean digestive;

    @Column(name = "respiratory")
    private boolean respiratory;

    @Column(name = "allergic")
    private boolean allergic;

    @Column(name = "std")
    private boolean std;

    @Column(name = "psychological")
    private boolean psychological;

    @Column(name = "neurological")
    private boolean neurological;

    @Column(name = "severe_illnesses")
    private boolean severeIllnesses;

    @Column(name = "surgical_intervention")
    private boolean surgicalIntervention;

    @Column(name = "radiotherapy")
    private boolean radiotherapy;

    @Column(name = "unexplained_weight_loss")
    private boolean unexplainedWeightLoss;

    @Column(name = "medication")
    private boolean medication;

    @Column(name = "abnormal_bleeding")
    private boolean abnormalBleeding;

    @Column(name = "previous_anesthesia")
    private boolean previousAnesthesia;

    @Column(name = "previous_dental_treatments")
    private boolean previousDentalTreatments;

    @Column(name = "smoking")
    private boolean smoking;

    @Column(name = "sports")
    private boolean sports;

    @Column(name = "travel")
    private boolean travel;

    @Column(name = "other_activity")
    private boolean otherActivity;

    @Column(name = "menstruation")
    private boolean menstruation;

    @Column(name = "pregnancy")
    private boolean pregnancy;

    @Column(name = "breastfeeding")
    private boolean breastfeeding;

    @Column(name = "other_female")
    private boolean otherFemale;

    @Column(name = "create_date", updatable = false, insertable = true)
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;
    
    @Column(name = "notes")
    private String notes;

    @Column(name = "status", nullable = false)
    private int status = STATUS_CREATED;

    @Transient
    private boolean isNew = false;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public boolean isBloodTest() {
        return bloodTest;
    }

    public void setBloodTest(boolean bloodTest) {
        this.bloodTest = bloodTest;
    }

    public boolean isCardiac() {
        return cardiac;
    }

    public void setCardiac(boolean cardiac) {
        this.cardiac = cardiac;
    }

    public boolean isHematologic() {
        return hematologic;
    }

    public void setHematologic(boolean hematologic) {
        this.hematologic = hematologic;
    }

    public boolean isHepatic() {
        return hepatic;
    }

    public void setHepatic(boolean hepatic) {
        this.hepatic = hepatic;
    }

    public boolean isEndocrine() {
        return endocrine;
    }

    public void setEndocrine(boolean endocrine) {
        this.endocrine = endocrine;
    }

    public boolean isDigestive() {
        return digestive;
    }

    public void setDigestive(boolean digestive) {
        this.digestive = digestive;
    }

    public boolean isRespiratory() {
        return respiratory;
    }

    public void setRespiratory(boolean respiratory) {
        this.respiratory = respiratory;
    }

    public boolean isAllergic() {
        return allergic;
    }

    public void setAllergic(boolean allergic) {
        this.allergic = allergic;
    }

    public boolean isStd() {
        return std;
    }

    public void setStd(boolean std) {
        this.std = std;
    }

    public boolean isPsychological() {
        return psychological;
    }

    public void setPsychological(boolean psychological) {
        this.psychological = psychological;
    }

    public boolean isNeurological() {
        return neurological;
    }

    public void setNeurological(boolean neurological) {
        this.neurological = neurological;
    }

    public boolean isSevereIllnesses() {
        return severeIllnesses;
    }

    public void setSevereIllnesses(boolean severeIllnesses) {
        this.severeIllnesses = severeIllnesses;
    }

    public boolean isSurgicalIntervention() {
        return surgicalIntervention;
    }

    public void setSurgicalIntervention(boolean surgicalIntervention) {
        this.surgicalIntervention = surgicalIntervention;
    }

    public boolean isRadiotherapy() {
        return radiotherapy;
    }

    public void setRadiotherapy(boolean radiotherapy) {
        this.radiotherapy = radiotherapy;
    }

    public boolean isUnexplainedWeightLoss() {
        return unexplainedWeightLoss;
    }

    public void setUnexplainedWeightLoss(boolean unexplainedWeightLoss) {
        this.unexplainedWeightLoss = unexplainedWeightLoss;
    }

    public boolean isMedication() {
        return medication;
    }

    public void setMedication(boolean medication) {
        this.medication = medication;
    }

    public boolean isAbnormalBleeding() {
        return abnormalBleeding;
    }

    public void setAbnormalBleeding(boolean abnormalBleeding) {
        this.abnormalBleeding = abnormalBleeding;
    }

    public boolean isPreviousAnesthesia() {
        return previousAnesthesia;
    }

    public void setPreviousAnesthesia(boolean previousAnesthesia) {
        this.previousAnesthesia = previousAnesthesia;
    }

    public boolean isPreviousDentalTreatments() {
        return previousDentalTreatments;
    }

    public void setPreviousDentalTreatments(boolean previousDentalTreatments) {
        this.previousDentalTreatments = previousDentalTreatments;
    }

    public boolean isSmoking() {
        return smoking;
    }

    public void setSmoking(boolean smoking) {
        this.smoking = smoking;
    }

    public boolean isSports() {
        return sports;
    }

    public void setSports(boolean sports) {
        this.sports = sports;
    }

    public boolean isTravel() {
        return travel;
    }

    public void setTravel(boolean travel) {
        this.travel = travel;
    }

    public boolean isOtherActivity() {
        return otherActivity;
    }

    public void setOtherActivity(boolean otherActivity) {
        this.otherActivity = otherActivity;
    }

    public boolean isMenstruation() {
        return menstruation;
    }

    public void setMenstruation(boolean menstruation) {
        this.menstruation = menstruation;
    }

    public boolean isPregnancy() {
        return pregnancy;
    }

    public void setPregnancy(boolean pregnancy) {
        this.pregnancy = pregnancy;
    }

    public boolean isBreastfeeding() {
        return breastfeeding;
    }

    public void setBreastfeeding(boolean breastfeeding) {
        this.breastfeeding = breastfeeding;
    }

    public boolean isOtherFemale() {
        return otherFemale;
    }

    public void setOtherFemale(boolean otherFemale) {
        this.otherFemale = otherFemale;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    
	public String getNotes() {
		return notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
    
    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public String toString() {
        return "MedicalSheet [id=" + id + ", customerId=" + customerId + ", accountId=" + accountId + ", bloodTest="
                + bloodTest + ", cardiac=" + cardiac + ", hematologic=" + hematologic + ", hepatic=" + hepatic
                + ", endocrine=" + endocrine + ", digestive=" + digestive + ", respiratory=" + respiratory
                + ", allergic=" + allergic + ", std=" + std + ", psychological=" + psychological + ", neurological="
                + neurological + ", severeIllnesses=" + severeIllnesses + ", surgicalIntervention="
                + surgicalIntervention + ", radiotherapy=" + radiotherapy + ", unexplainedWeightLoss="
                + unexplainedWeightLoss + ", medication=" + medication + ", abnormalBleeding=" + abnormalBleeding
                + ", previousAnesthesia=" + previousAnesthesia + ", previousDentalTreatments="
                + previousDentalTreatments + ", smoking=" + smoking + ", sports=" + sports + ", travel=" + travel
                + ", otherActivity=" + otherActivity + ", menstruation=" + menstruation + ", pregnancy=" + pregnancy
                + ", breastfeeding=" + breastfeeding + ", otherFemale=" + otherFemale + ", createDate=" + createDate
                + ", updateDate=" + updateDate + ", notes=" + notes + ", status=" + status + "]";
    }
}
