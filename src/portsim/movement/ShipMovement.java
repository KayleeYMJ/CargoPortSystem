package portsim.movement;

import portsim.ship.Ship;
import portsim.util.BadEncodingException;
import portsim.util.NoSuchShipException;

/**
 * The movement of a ship coming into or out of the port.
 *
 * @ass1_partial
 */
public class ShipMovement extends Movement {
    /**
     * The ship entering of leaving the Port
     */
    private Ship ship;

    /**
     * Creates a new ship movement with the given action time and direction to be undertaken
     * with the given ship.
     *
     * @param time      the time the movement should occur
     * @param direction the direction of the movement
     * @param ship      the ship which that is waiting to move
     * @throws IllegalArgumentException if time &lt; 0
     * @ass1
     */
    public ShipMovement(long time, MovementDirection direction, Ship ship)
            throws IllegalArgumentException {
        super(time, direction);
        this.ship = ship;
    }

    /**
     * Returns the ship undertaking the movement.
     *
     * @return movements ship
     * @ass1
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Returns the human-readable string representation of this ShipMovement.
     * <p>
     * The format of the string to return is
     * <pre>
     * DIRECTION ShipMovement to occur at time involving the ship name </pre>
     * Where:
     * <ul>
     *   <li>{@code DIRECTION} is the direction of the movement </li>
     *   <li>{@code time} is the time the movement is meant to occur </li>
     *   <li>{@code name} is the name of the ship that is being moved</li>
     * </ul>
     * For example:
     * <pre>
     * OUTBOUND ShipMovement to occur at 135 involving the ship Voyager </pre>
     *
     * @return string representation of this ShipMovement
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s involving the ship %s",
                super.toString(),
                this.ship.getName());
    }

    /**
     * Returns the machine-readable string representation of this ship movement. The format of
     * the string to return is
     * <p>
     * ShipMovement:time:direction:imoNumber Where: time is the time that the movement will be
     * actioned direction is the direction of the movement imoNumber is the imoNumber of the
     * ship that is moving For example: ShipMovement:120:INBOUND:1258691
     *
     * @return encoded string representation of this movement
     */
    @Override
    public String encode() {
        return String.format("%s:%s", super.encode(), ship.getImoNumber());
    }

    /**
     * Creates a ship movement from a string encoding. The format of the string should match
     * the encoded representation of a ship movement, as described in encode().
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <p>
     * The number of colons (:) detected was more/fewer than expected. The time is not a long
     * (i.e. cannot be parsed by Long.parseLong(String)). The time is less than zero (0). The
     * movementDirection is not one of the valid directions (See MovementDirection). The
     * imoNumber is not a long (i.e. cannot be parsed by Long.parseLong(String)). There is no
     * ship that exists with the specified imoNumber.
     *
     * @param string string containing the encoded ShipMovement
     * @return decoded ShipMovement instance
     * @throws BadEncodingException if the format of the given string is invalid according to
     *                              the rules above
     */
    public static ShipMovement fromString(String string) throws BadEncodingException {
        // check the number of colons
        String[] pairs = string.split(":");
        if (pairs.length != 4 || string.lastIndexOf(":") == string.length() - 1) {
            throw new BadEncodingException("The number of colons (:) detected was more/fewer than"
                    + " expected");
        }

        // check if the time is not a long
        long time;
        try {
            time = Long.parseLong(pairs[1]);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The time is not a long", n);
        }

        // check if the time is less than zero (0).
        if (time < 0) {
            throw new BadEncodingException("The time is less than zero (0)");
        }

        // The movementDirection is not one of the valid directions
        MovementDirection direction;
        try {
            direction = MovementDirection.valueOf(pairs[2]);
        } catch (IllegalArgumentException i) {
            throw new BadEncodingException("The movementDirection is not one of the valid "
                    + "directions", i);
        }

        // The imoNumber is not a long
        long imoNumber;
        try {
            imoNumber = Integer.parseInt(pairs[3]);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The imoNumber is not a long", n);
        }

        // There is no ship that exists with the specified imoNumber.
        Ship ship;
        try {
            ship = Ship.getShipByImoNumber(imoNumber);
        } catch (NoSuchShipException n) {
            throw new BadEncodingException("There is no ship that exists with the specified "
                    + "imoNumber", n);
        }

        return new ShipMovement(time, direction, ship);
    }
}
