package com.uvt.newcomerassistant.demo.controller;

@SuppressWarnings("unused")
public class ChecklistItemStatusRequest {
    private Long userId;
    private String checklistItemId;
    private boolean isChecked;

    public ChecklistItemStatusRequest() {
    }

    public ChecklistItemStatusRequest(Long userId, String checklistItemId, boolean isChecked) {
        this.userId = userId;
        this.checklistItemId = checklistItemId;
        this.isChecked = isChecked;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getChecklistItemId() {
        return checklistItemId;
    }

    public void setChecklistItemId(String checklistItemId) {
        this.checklistItemId = checklistItemId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
