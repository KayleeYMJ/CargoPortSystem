package portsim.evaluators;

import portsim.movement.*;
import java.util.*;

/**
 * Gathers data on how many ships pass through the port over time. This evaluator only counts
 * ships that have passed through the port in the last hour (60 minutes)
 */
public class ShipThroughputEvaluator extends StatisticsEvaluator {
    /**
     * record the time of ship departed from port
     */
    private Map<Movement, Long> movementTime;

    /**
     * Constructs a new ShipThroughputEvaluator. Immediately after creating a new
     * ShipThroughputEvaluator, getThroughputPerHour() should return 0.
     */
    public ShipThroughputEvaluator() {
        super();
        movementTime = new HashMap<>();
    }

    /**
     * Return the number of ships that have passed through the port in the last 60 minutes.
     *
     * @return ships throughput
     */
    public int getThroughputPerHour() {
        return movementTime.size();
    }

    /**
     * Updates the internal count of ships that have passed through the port using the given
     * movement. If the movement is not an OUTBOUND ShipMovement, this method returns
     * immediately without taking any action.
     * <p>
     * Otherwise, the internal state of this evaluator should be modified such that
     * getThroughputPerHour() should return a value 1 more than before this method was called.
     *
     * @param movement movement to read
     */
    public void onProcessMovement(Movement movement) {
        if (movement.getDirection() == MovementDirection.OUTBOUND
                && movement.getClass().getSimpleName().equals("ShipMovement")
                && getTime() == movement.getTime()) {
            movementTime.put(movement, movement.getTime());
        }
    }

    /**
     * Simulate a minute passing. The time since the evaluator was created should be
     * incremented by one. If it has been more than 60 minutes since a ship exited the port, it
     * should no longer be counted towards the count returned by getThroughputPerHour().
     */
    @Override
    public void elapseOneMinute() {
        super.elapseOneMinute();
        List<Movement> movements = new ArrayList<>();
        if (movementTime.size() != 0) {

            // find movements -  more than 60 minutes since a ship exited the port
            for (Map.Entry<Movement, Long> entry : movementTime.entrySet()) {
                if (getTime() - entry.getValue() > 60) {
                    movements.add(entry.getKey());
                }
            }
        }
        // remove the movement
        if (movements.size() != 0) {
            for (Movement movement : movements) {
                movementTime.remove(movement);
            }
        }
    }
}
