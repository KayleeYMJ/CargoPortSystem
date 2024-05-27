package portsim.evaluators;

import portsim.cargo.*;
import portsim.movement.*;
import portsim.ship.*;

import java.util.*;

/**
 * Collects data on what types of cargo are passing through the port. Gathers data on all
 * derivatives of the cargo class. The data gathered is a count of how many times each type of
 * cargo has entered the port. This includes a count of how many times the port has received
 * "BulkCargo" or "Container" class cargo. As well as a count of how many times the port has
 * seen each cargo subclass type (ContainerType and BulkCargoType).
 */
public class CargoDecompositionEvaluator extends StatisticsEvaluator {
    /**
     * the distribution of which cargo types that have entered the port
     */
    private Map<String, Integer> cargoDistribution;

    /**
     * the distribution of bulk cargo types that have entered the port
     */
    private Map<BulkCargoType, Integer> bulkCargoDistribution;

    /**
     * the distribution of container cargo types that have entered the port.
     */
    private Map<ContainerType, Integer> containerDistribution;

    /**
     * Constructs a new CargoDecompositionEvaluator.
     */
    public CargoDecompositionEvaluator() {
        cargoDistribution = new HashMap<>();
        bulkCargoDistribution = new HashMap<>();
        containerDistribution = new HashMap<>();
    }

    /**
     * Returns the distribution of which cargo types that have entered the port.
     *
     * @return cargo distribution map
     */
    public Map<String, Integer> getCargoDistribution() {
        return new HashMap<>(cargoDistribution);
    }

    /**
     * Returns the distribution of bulk cargo types that have entered the port.
     *
     * @return bulk cargo distribution map
     */
    public Map<BulkCargoType, Integer> getBulkCargoDistribution() {
        return new HashMap<>(bulkCargoDistribution);
    }

    /**
     * Returns the distribution of container cargo types that have entered the port.
     *
     * @return container distribution map
     */
    public Map<ContainerType, Integer> getContainerDistribution() {
        return new HashMap<>(containerDistribution);
    }

    /**
     * Updates the internal distributions of cargo types using the given movement. If the
     * movement is not an INBOUND movement, this method returns immediately without taking any
     * action.
     * <p>
     * If the movement is an INBOUND movement, do the following:
     * <p>
     * If the movement is a ShipMovement, Retrieve the cargo from the ships and for each piece
     * of cargo: If the cargo class (Container / BulkCargo) has been seen before (simple name
     * exists as a key in the cargo map) -> increment that number If the cargo class has not
     * been seen before then add its class simple name as a key in the map with a corresponding
     * value of 1 If the cargo type (Value of ContainerType / BulkCargoType) for the given
     * cargo class has been seen before (exists as a key in the map) increment that number If
     * the cargo type (Value of ContainerType / BulkCargoType) for the given cargo class has
     * not been seen before add as a key in the map with a corresponding value of 1 If the
     * movement is a CargoMovement, Retrieve the cargo from the movement. For the cargo
     * retrieved: Complete steps 1-4 as given above for ShipMovement
     *
     * @param movement movement to read
     */
    public void onProcessMovement(Movement movement) {
        if (movement.getDirection() == MovementDirection.INBOUND) {
            // Cargo Movement
            if (movement.getClass().getSimpleName().equals("CargoMovement")) {
                List<Cargo> cargos = ((CargoMovement) movement).getCargo();
                Iterator<Cargo> allCargos = cargos.iterator();
                while (allCargos.hasNext()) {
                    Cargo nextCargo = allCargos.next();

                    // container
                    if (nextCargo.getClass().getSimpleName().equals("Container")) {
                        if (cargoDistribution.containsKey("Container")) {
                            cargoDistribution.replace("Container",
                                    cargoDistribution.get("Container") + 1);
                        } else {
                            cargoDistribution.put("Container", 1);
                        }

                        ContainerType type = ((Container) nextCargo).getType();
                        if (containerDistribution.containsKey(type)) {
                            containerDistribution.replace(type,
                                    containerDistribution.get(type) + 1);
                        } else {
                            containerDistribution.put(type, 1);
                        }
                    } else {
                        // bulk cargo
                        if (cargoDistribution.containsKey("BulkCargo")) {
                            cargoDistribution.replace("BulkCargo",
                                    cargoDistribution.get("BulkCargo") + 1);
                        } else {
                            cargoDistribution.put("BulkCargo", 1);
                        }

                        BulkCargoType type = ((BulkCargo) nextCargo).getType();
                        if (bulkCargoDistribution.containsKey(type)) {
                            bulkCargoDistribution.replace(type,
                                    bulkCargoDistribution.get(type) + 1);
                        } else {
                            bulkCargoDistribution.put(type, 1);
                        }
                    }
                }
            } else {
                // ship movement
                Ship ship = ((ShipMovement) movement).getShip();
                if (ship.getClass().getSimpleName().equals("ContainerShip")) {
                    if (((ContainerShip) ship).getCargo().size() != 0) {
                        for (Container cargo : ((ContainerShip) ship).getCargo()) {
                            if (cargoDistribution.containsKey("Container")) {
                                cargoDistribution.replace("Container",
                                        cargoDistribution.get("Container") + 1);
                            } else {
                                cargoDistribution.put("Container", 1);
                            }

                            ContainerType type = cargo.getType();
                            if (containerDistribution.containsKey(type)) {
                                containerDistribution.replace(type,
                                        containerDistribution.get(type) + 1);
                            } else {
                                containerDistribution.put(type, 1);
                            }
                        }
                    }
                } else {
                    if (((BulkCarrier) ship).getCargo() != null) {
                        BulkCargo cargo = ((BulkCarrier) ship).getCargo();
                        if (cargoDistribution.containsKey("BulkCargo")) {
                            cargoDistribution.replace("BulkCargo",
                                    cargoDistribution.get("BulkCargo") + 1);
                        } else {
                            cargoDistribution.put("BulkCargo", 1);
                        }

                        BulkCargoType type = cargo.getType();
                        if (bulkCargoDistribution.containsKey(type)) {
                            bulkCargoDistribution.replace(type,
                                    bulkCargoDistribution.get(type) + 1);
                        } else {
                            bulkCargoDistribution.put(type, 1);
                        }
                    }
                }
            }
        }
    }
}
