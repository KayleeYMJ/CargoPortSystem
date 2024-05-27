package portsim.movement;

import portsim.cargo.Cargo;
import portsim.util.BadEncodingException;
import portsim.util.NoSuchCargoException;
import java.util.*;

/**
 * The movement of cargo coming into or out of the port.
 *
 * @ass1_partial
 */
public class CargoMovement extends Movement {
    /**
     * The cargo that will be involved in the movement
     */
    private List<Cargo> cargo;

    /**
     * Creates a new cargo movement with the given action time and direction to be undertaken
     * with the given cargo.
     *
     * @param time      the time the movement should occur
     * @param direction the direction of the movement
     * @param cargo     the cargo to be moved
     * @throws IllegalArgumentException if time &lt; 0
     * @ass1
     */
    public CargoMovement(long time, MovementDirection direction,
                         List<Cargo> cargo) throws IllegalArgumentException {
        super(time, direction);
        this.cargo = cargo;
    }

    /**
     * Returns the cargo that will be moved.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return all cargo in the movement
     * @ass1
     */
    public List<Cargo> getCargo() {
        return new ArrayList<>(cargo);
    }

    /**
     * Returns the human-readable string representation of this CargoMovement.
     * <p>
     * The format of the string to return is
     * <pre>
     * DIRECTION CargoMovement to occur at time involving num piece(s) of cargo </pre>
     * Where:
     * <ul>
     *   <li>{@code DIRECTION} is the direction of the movement </li>
     *   <li>{@code time} is the time the movement is meant to occur </li>
     *   <li>{@code num} is the number of cargo pieces that are being moved</li>
     * </ul>
     * <p>
     * For example: <pre>
     * OUTBOUND CargoMovement to occur at 135 involving 5 piece(s) of cargo </pre>
     *
     * @return string representation of this CargoMovement
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s involving %d piece(s) of cargo",
                super.toString(),
                this.cargo.size());
    }

    /**
     * Returns the machine-readable string representation of this movement. The format of the
     * string to return is
     * <p>
     * CargoMovement:time:direction:numCargo:ID1,ID2,... Where: time is the time that the
     * movement will be actioned direction is the direction of the movement numCargo is the
     * number of the cargo in the movement ID1,ID2,... are the IDs of the cargo in the movement
     * separated by a comma ','. There should be no trailing comma after the last ID. For
     * example: CargoMovement:120:INBOUND:3:22,23,12
     *
     * @return encoded string representation of this movement
     */
    @Override
    public String encode() {
        String cargosId = "";
        Iterator<Cargo> iterator = cargo.iterator();
        while (iterator.hasNext()) {
            cargosId += iterator.next().getId();
            cargosId += ",";
        }
        cargosId = cargosId.substring(0, cargosId.length() - 1);
        return String.format("%s:%d:%s", super.encode(), cargo.size(), cargosId);
    }

    /**
     * Creates a cargo movement from a string encoding. The format of the string should match
     * the encoded representation of a cargo movement, as described in encode().
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <p>
     * The number of colons (:) detected was more/fewer than expected. The given string is not
     * a CargoMovement encoding The time is not a long (i.e. cannot be parsed by
     * Long.parseLong(String)). The time is less than zero (0). The movementDirection is not
     * one of the valid directions (See MovementDirection). The number of ids is not an int
     * (i.e. cannot be parsed by Integer.parseInt(String)). The number of ids is less than one
     * (1). An id is not an int (i.e. cannot be parsed by Integer.parseInt(String)). An id is
     * less than zero (0) There is no cargo that exists with a specified id. The number of id's
     * does not match the number specified.
     *
     * @param string string containing the encoded CargoMovement
     * @return decoded CargoMovement instance
     * @throws BadEncodingException if the format of the given string is invalid according to
     *                              the rules above
     */
    public static CargoMovement fromString(String string) throws BadEncodingException {
        // check a CargoMovement encoding
        if (!string.startsWith("CargoMovement")) {
            throw new BadEncodingException("The given string is not a CargoMovement encoding");
        }

        // check the number of colons
        String[] pairs = string.split(":");
        if (pairs.length != 5 || string.lastIndexOf(":") == string.length() - 1) {
            throw new BadEncodingException("The number of colons (:) detected was more/fewer than"
                    + " expected");
        }

        // check the movement class name - "CargoMovement"
        if (!pairs[0].equals("CargoMovement")) {
            throw new BadEncodingException("Not a class name of CargoMovement");
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

        // The number of ids is not an int
        int cargoIds;
        try {
            cargoIds = Integer.parseInt(pairs[3]);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The number of ids is not an int", n);
        }

        // The number of ids is less than one (1)
        if (cargoIds < 1) {
            throw new BadEncodingException("The number of ids is less than one (1)");
        }

        int id;
        List<Cargo> cargos = new ArrayList<>();
        for (String pair : pairs[4].split(",")) {
            // An id is not an int
            try {
                id = Integer.parseInt(pair);
            } catch (NumberFormatException n) {
                throw new BadEncodingException("An id is not an int", n);
            }

            //  An id is less than zero (0)
            if (id < 0) {
                throw new BadEncodingException("An id is less than zero (0)");
            }

            // There is no cargo that exists with a specified id
            try {
                cargos.add(Cargo.getCargoById(id));
            } catch (NoSuchCargoException n) {
                throw new BadEncodingException("There is no cargo that exists with a specified "
                        + "id", n);
            }
        }

        // The number of id's does not match the number specified
        if (cargos.size() != cargoIds) {
            throw new BadEncodingException("The number of id's does not match the"
                    + " number specified");
        }
        return new CargoMovement(time, direction, cargos);
    }
}
