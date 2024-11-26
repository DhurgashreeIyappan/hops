package com.example.myapplication;

public class OutpassRequest {
    private String requestId;
    private String userEmail;
    private String reason;
    private String dateFrom;
    private String dateTo;
    private String outTime;
    private String inTime;
    private long timestamp;
    private String advisorEmail;
    private String wardenEmail;
    private String status;
    private String name;
    private String rollNumber;
    private String roomNumber;
    private String wstatus;// New field

    // No-argument constructor
    public OutpassRequest() {
        // Required for Firebase DataSnapshot
    }

    // Parameterized constructor
    public OutpassRequest(String requestId, String userEmail, String reason,
                          String dateFrom, String dateTo, String outTime,
                          String inTime, String advisorEmail, String status,
                          String name, String rollNumber, String roomNumber,String wstatus,String wardenEmail) { // Updated constructor
        this.requestId = requestId;
        this.userEmail = userEmail;
        this.reason = reason;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.outTime = outTime;
        this.inTime = inTime;
        this.advisorEmail = advisorEmail;
        this.wardenEmail = wardenEmail;
        this.status = status;
        this.name = name;
        this.rollNumber = rollNumber;
        this.roomNumber = roomNumber;
        this.wstatus= wstatus;// Initialize new field
    }

    public OutpassRequest(String userEmail, String reason, String dateFrom, String dateTo, String outTime, String inTime) {
        // Constructor implementation (if needed)
    }

    // Getters and Setters for all fields
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAdvisorEmail() {
        return advisorEmail;
    }

    public void setAdvisorEmail(String advisorEmail) {
        this.advisorEmail = advisorEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getRoomNumber() { // Getter for roomNumber
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) { // Setter for roomNumber
        this.roomNumber = roomNumber;
    }

    public String getWstatus() {
        return wstatus;
    }

    public void setWstatus(String wstatus) {
        this.wstatus = wstatus;
    }

    public String getWardenEmail() {
        return wardenEmail;
    }

    public void setWardenEmail(String wardenEmail) {
        this.wardenEmail = wardenEmail;
    }
}
