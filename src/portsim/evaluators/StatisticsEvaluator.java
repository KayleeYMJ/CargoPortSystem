package portsim.evaluators;

import portsim.movement.Movement;

/**
 * A base class representing an object that gathers and reports data on various aspects of the
 * port's operation.
 */
public abstract class StatisticsEvaluator {
    /**
     * the time of a statistics evaluator
     */
    private long time;

    /**
     * Creates a statistics evaluator and initialises the time since the evaluator was created
     * to zero.
     */
    public StatisticsEvaluator() {
        this.time = 0;
    }

    /**
     * Return the time since the evaluator was created.
     *
     * @return time since created
     */
    public long getTime() {
        return time;
    }

    /**
     * Read a movement to update the relevant evaluator data. This method is called by the
     * Port.processMovement(Movement) method.
     *
     * @param movement movement to read
     */
    public abstract void onProcessMovement(Movement movement);

    /**
     * Simulate a minute passing. The time since the evaluator was created should be
     * incremented by one.
     */
    public void elapseOneMinute() {
        time++;
    }
}
