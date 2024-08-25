package org.shmo.icfb.utilities;

public interface Timer {
    public float getGoalTime();
    public void setGoalTime(float goalTime);
    public float getTimeElapsed();
    public void setTimeElapsed(float timeElapsed);
    public boolean advance(float deltaTime);
    public boolean isTriggered();
    public void reset();
}
