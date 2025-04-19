package org.irs.dto;


public class FollowUpDTO {
    public boolean firRegistered;
    public String firNumber;
    public boolean challanIssued;
    public String challanNumber;
    public Integer caseReferredTo;
    public boolean isFirRegistered() {
        return firRegistered;
    }
    public void setFirRegistered(boolean firRegistered) {
        this.firRegistered = firRegistered;
    }
    public String getFirNumber() {
        return firNumber;
    }
    public void setFirNumber(String firNumber) {
        this.firNumber = firNumber;
    }
    public boolean isChallanIssued() {
        return challanIssued;
    }
    public void setChallanIssued(boolean challanIssued) {
        this.challanIssued = challanIssued;
    }
    public String getChallanNumber() {
        return challanNumber;
    }
    public void setChallanNumber(String challanNumber) {
        this.challanNumber = challanNumber;
    }
    public Integer getCaseReferredTo() {
        return caseReferredTo;
    }
    public void setCaseReferredTo(Integer caseReferredTo) {
        this.caseReferredTo = caseReferredTo;
    }
    }
    
