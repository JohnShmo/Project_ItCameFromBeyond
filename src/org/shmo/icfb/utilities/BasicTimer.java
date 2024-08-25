package org.shmo.icfb.utilities;

public class BasicTimer implements Timer {
    private float _timeElapsed = 0f;
    private float _goalTime = 0f;

    public BasicTimer() {}
    public BasicTimer(float goalTime) {
        setGoalTime(goalTime);
    }

    public float getGoalTime() { return _goalTime; }
    public void setGoalTime(float goalTime) { _goalTime = goalTime; }
    public float getTimeElapsed() { return _timeElapsed; }
    public void setTimeElapsed(float timeElapsed) { _timeElapsed = timeElapsed; }

    public boolean advance(float deltaTime) {
        advanceTimeElapsed(deltaTime);
        if (isTriggered()) {
            reset();
            return true;
        }
        return false;
    }

    public boolean isTriggered() {
        return getTimeElapsed() >= getGoalTime();
    }

    public void reset() {
        setTimeElapsed(0);
    }

    protected void advanceTimeElapsed(float deltaTime) { _timeElapsed += deltaTime; }
}
