package com.ochafik.util.progress;

public interface ProgressModel {
    public void setIndeterminate(boolean value);
    public boolean isIndeterminate();
    public void addProgress(long value);
    public void setProgress(long value);
    public long getProgress();
    public void addMaximum(long value);
    public void setMaximum(long max);
    public long getMaximum();
    public String getTitle();
    public String getComment();
    public void setTitle(String t);
    public void setComment(String t);
    public void setShowRemainingTime(boolean value);
    public boolean getShowRemainingTime();

    public boolean isInterrupted();
    public void setInterrupted(boolean value);
}
