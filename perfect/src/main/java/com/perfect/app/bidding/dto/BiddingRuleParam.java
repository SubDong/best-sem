package com.perfect.app.bidding.dto;

/**
 * Created by vbzer_000 on 2014/8/28.
 */
public class BiddingRuleParam {
    private Long[] ids;

    private double max;

    private double min;

    private int[] times;

    private boolean run;

    private int timerange;

    private int mode;

    private int device;
    private int target;
    private int failed;
    private int interval;
    private int expPosition;
    private int customPos;

    private int auto;

    public Long[] getIds() {
        return ids;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public int[] getTimes() {
        return times;
    }

    public void setTimes(int[] times) {
        this.times = times;
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public int getTimerange() {
        return timerange;
    }

    public void setTimerange(int timerange) {
        this.timerange = timerange;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getExpPosition() {
        return expPosition;
    }

    public void setExpPosition(int expPosition) {
        this.expPosition = expPosition;
    }

    public int getCustomPos() {
        return customPos;
    }

    public void setCustomPos(int customPos) {
        this.customPos = customPos;
    }

    public int getAuto() {
        return auto;
    }

    public void setAuto(int auto) {
        this.auto = auto;
    }
}
