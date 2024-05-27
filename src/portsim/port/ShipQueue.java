package portsim.port;

import portsim.ship.*;
import portsim.util.*;
import java.util.*;

/**
 * Queue of ships waiting to enter a Quay at the port. Ships are chosen based on their
 * priority.
 */
public class ShipQueue {
    /**
     * The queue of ships
     */
    private List<Ship> shipQueue;

    /**
     * Constructs a new ShipQueue with an initially empty queue of ships.
     */
    public ShipQueue() {
        shipQueue = new ArrayList<>();
    }

    /**
     * Gets the next ship to enter the port and removes it from the queue. The same rules as
     * described in peek() should be used for determining which ship to remove and return.
     *
     * @return next ship to dock
     */
    public Ship poll() {
        Ship peekShip = peek();
        shipQueue.remove(peekShip);
        return peekShip;
    }

    /**
     * Returns the next ship waiting to enter the port. The queue should not change. The rules
     * for determining which ship in the queue should be returned next are as follows:
     * <p>
     * If a ship is carrying dangerous cargo, it should be returned. If more than one ship is
     * carrying dangerous cargo return the one added to the queue first. If a ship requires
     * medical assistance, it should be returned. If more than one ship requires medical
     * assistance, return the one added to the queue first. If a ship is ready to be docked, it
     * should be returned. If more than one ship is ready to be docked, return the one added to
     * the queue first. If there is a container ship in the queue, return the one added to the
     * queue first. If this point is reached and no ship has been returned, return the ship
     * that was added to the queue first. If there are no ships in the queue, return null.
     *
     * @return next ship in queue
     */
    public Ship peek() {
        // no ships in the queue
        if (shipQueue.size() == 0) {
            return null;
        }

        // a ship is carrying dangerous cargo
        for (Ship dangerousShip : shipQueue) {
            if (dangerousShip.getFlag() == NauticalFlag.BRAVO) {
                return dangerousShip;
            }
        }

        // a ship requires medical assistance
        for (Ship medicalShip : shipQueue) {
            if (medicalShip.getFlag() == NauticalFlag.WHISKEY) {
                return medicalShip;
            }
        }

        // a ship is ready to be docked
        for (Ship readyShip : shipQueue) {
            if (readyShip.getFlag() == NauticalFlag.HOTEL) {
                return readyShip;
            }
        }

        // a container ship in the queue
        for (Ship containerShip : shipQueue) {
            if (containerShip instanceof ContainerShip) {
                return containerShip;
            }
        }

        // this point is reached and no ship has been returned
        return shipQueue.get(0);
    }

    /**
     * Adds the specified ship to the queue.
     *
     * @param ship to be added to queue
     */
    public void add(Ship ship) {
        shipQueue.add(ship);
    }

    /**
     * Returns a list containing all the ships currently stored in this ShipQueue. The order of
     * the ships in the returned list should be the order in which the ships were added to the
     * queue.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return ships in queue
     */
    public List<Ship> getShipQueue() {
        return new ArrayList<>(shipQueue);
    }

    /**
     * Returns true if and only if this ship queue is equal to the other given ship queue. For
     * two ship queue to be equal, they must have the same ships in the queue.
     *
     * @param o other object to check equality
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ShipQueue) {
            if (((ShipQueue) o).shipQueue.size() == 0 && shipQueue.size() == 0) {
                return true;
            } else if (((ShipQueue) o).shipQueue.size() == shipQueue.size()) {
                for (int index = 0; index < shipQueue.size(); index++) {
                    if (!((ShipQueue) o).shipQueue.get(index).equals(shipQueue.get(index))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the hash code of this ship queue. Two ship queue's that are equal according to
     * equals(Object) method should have the same hash code.
     *
     * @return hash code of this ship queue.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(shipQueue);
    }

    /**
     * Returns the machine-readable string representation of this ShipQueue. The format of the
     * string to return is
     * <p>
     * ShipQueue:numShipsInQueue:shipID,shipID,... Where: numShipsInQueue is the total number
     * of ships in the ship queue in the port If present (numShipsInQueue > 0): shipID is each
     * ship's ID in the aforementioned queue For example: ShipQueue:0: or
     * ShipQueue:2:3456789,1234567
     *
     * @return encoded string representation of this ShipQueue
     */
    public String encode() {
        if (shipQueue.size() == 0) {
            return "ShipQueue:0:";
        } else {
            Iterator<Ship> iterator = shipQueue.iterator();
            String shipsId = "";
            while (iterator.hasNext()) {
                shipsId += iterator.next().getImoNumber();
                shipsId += ",";
            }
            shipsId = shipsId.substring(0, shipsId.length() - 1);
            return String.format("ShipQueue:%d:%s", shipQueue.size(), shipsId);
        }
    }

    /**
     * Creates a ship queue from a string encoding. The format of the string should match the
     * encoded representation of a ship queue, as described in encode().
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <p>
     * The number of colons (:) detected was more/fewer than expected. The string does not
     * start with the literal string "ShipQueue" The number of ships in the shipQueue is not an
     * integer (i.e. cannot be parsed by Integer .parseInt(String)). The number of ships in the
     * shipQueue does not match the number specified. The imoNumber of the ships in the
     * shipQueue are not valid longs. (i.e. cannot be parsed by Long.parseLong(String)). Any
     * imoNumber read does not correspond to a valid ship in the simulation
     *
     * @param string string containing the encoded ShipQueue
     * @return decoded ship queue instance
     * @throws BadEncodingException if the format of the given string is invalid according to
     *                              the rules  above
     */
    public static ShipQueue fromString(String string) throws BadEncodingException {
        ShipQueue newQueue = new ShipQueue();

        // the string does not start with the literal string "ShipQueue"
        if (!string.startsWith("ShipQueue")) {
            throw new BadEncodingException("The string does not start with the literal string "
                    + "\"ShipQueue\"");
        }

        // the number of colons
        int colonsNum = 0;
        for (String character : string.split("")) {
            if (character.equals(":")) {
                colonsNum++;
            }
        }
        if (colonsNum != 2) {
            throw new BadEncodingException("The number of colons (:) detected was more/fewer than"
                    + " expected");
        }

        // the number of ships is not an integer
        String[] pairs = string.split(":");
        int totalShips;
        try {
            totalShips = Integer.parseInt(pairs[1]);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The number of ships in the shipQueue is not an "
                    + "integer", n);
        }

        // the number of ships does not match the number specified
        if (totalShips == 0 && (string.lastIndexOf(":") != string.length() - 1)) {
            throw new BadEncodingException("The number of ships in the shipQueue does not match"
                    + " the number specified");
        }

        if (totalShips != 0) {
            String[] ships = pairs[2].split(",");
            if (totalShips != ships.length) {
                throw new BadEncodingException("The number of ships in the shipQueue does not match"
                        + " the number specified");
            }

            long imoNumber;
            for (String shipImo : ships) {
                // The imoNumber of the ships in the shipQueue are not valid longs
                try {
                    imoNumber = Long.parseLong(shipImo);
                } catch (NumberFormatException n) {
                    throw new BadEncodingException("The imoNumber of the ships in the "
                            + "shipQueue are not valid longs", n);
                }

                Ship ship;
                // Any imoNumber read does not correspond to a valid ship in the simulation
                try {
                    ship = Ship.getShipByImoNumber(imoNumber);
                } catch (NoSuchShipException n) {
                    throw new BadEncodingException("Any imoNumber read does not correspond to "
                            + "a valid ship in the simulation", n);
                }

                // arrive ship
                newQueue.add(ship);
            }
        }
        return newQueue;
    }
}
