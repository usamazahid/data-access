package org.irs.dto;


public class EvidenceDTO {
    public boolean photosTaken;
    public boolean videosRecorded;
    public boolean sketchPrepared;
    public String id;
    public boolean isPhotosTaken() {
        return photosTaken;
    }
    public void setPhotosTaken(boolean photosTaken) {
        this.photosTaken = photosTaken;
    }
    public boolean isVideosRecorded() {
        return videosRecorded;
    }
    public void setVideosRecorded(boolean videosRecorded) {
        this.videosRecorded = videosRecorded;
    }
    public boolean isSketchPrepared() {
        return sketchPrepared;
    }
    public void setSketchPrepared(boolean sketchPrepared) {
        this.sketchPrepared = sketchPrepared;
    }
}
    
 