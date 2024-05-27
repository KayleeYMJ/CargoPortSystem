package portsim.evaluators;

import portsim.movement.Movement;
import portsim.port.Port;
import portsim.port.Quay;

/**
 * Evaluator to monitor how many quays are currently occupied at the port.
 */
public class QuayOccupancyEvaluator extends StatisticsEvaluator {
    /**
     * port to monitor quays
     */
    private Port port;

    /**
     * Constructs a new QuayOccupancyEvaluator.
     *
     * @param port port to monitor quays
     */
    public QuayOccupancyEvaluator(Port port) {
        this.port = port;
    }

    /**
     * Return the number of quays that are currently occupied. A quay is occupied if
     * Quay.isEmpty() returns false.
     *
     * @return number of quays
     */
    public int getQuaysOccupied() {
        int occupyNumber = 0;
        if (port.getQuays().size() != 0) {
            for (Quay quay : port.getQuays()) {
                if (!quay.isEmpty()) {
                    occupyNumber++;
                }
            }
        }
        return occupyNumber;
    }

    /**
     * QuayOccupancyEvaluator does not make use of onProcessMovement(), so this method can be
     * left empty. Does nothing. This method is not used by this evaluator.
     *
     * @param movement movement to read
     */
    public void onProcessMovement(Movement movement) {}
}
