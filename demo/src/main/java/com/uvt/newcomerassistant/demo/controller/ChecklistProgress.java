package com.uvt.newcomerassistant.demo.controller;

@SuppressWarnings("unused")
public class ChecklistProgress {
    private final Long userId;
    private final int checkedCount;
    private final int totalCount;

    public ChecklistProgress(Long userId, int checkedCount, int totalCount) {
        this.userId = userId;
        this.checkedCount = checkedCount;
        this.totalCount = totalCount;
    }

    public Long getUserId() {
        return userId;
    }

    public int getCheckedCount() {
        return checkedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public double getProgressPercentage() {
        return totalCount == 0 ? 0 : (checkedCount * 100.0) / totalCount;
    }
}
