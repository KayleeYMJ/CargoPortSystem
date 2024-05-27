package portsim.port;

import portsim.ship.*;
import portsim.util.*;
import java.util.Objects;

/**
 * Quay is a platform lying alongside or projecting into the water where ships are moored for
 * loading or unloading.
 *
 * @ass1_partial
 */
public abstract class Quay {
    /**
     * The ID of the quay
     */
    private int id;

    /**
     * The ship currently in the Quay
     */
    private Ship ship;

    /**
     * Creates a new Quay with the given ID, with no ship docked at the quay.
     *
     * @param id quay ID
     * @throws IllegalArgumentException if ID &lt; 0
     * @ass1
     */
    public Quay(int id) throws IllegalArgumentException {
        if (id < 0) {
            throw new IllegalArgumentException("Quay ID must be greater than"
                    + " or equal to 0: " + id);
        }
        this.id = id;
        this.ship = null;
    }

    /**
     * Get the id of this quay
     *
     * @return quay id
     * @ass1
     */
    public int getId() {
        return id;
    }

    /**
     * Docks the given ship at the Quay so that the quay becomes occupied.
     *
     * @param ship ship to dock to the quay
     * @ass1
     */
    public void shipArrives(Ship ship) {
        this.ship = ship;
    }

    /**
     * Removes the current ship docked at the quay. The current ship should be set to {@code
     * null}.
     *
     * @return the current ship or null if quay is empty.
     * @ass1
     */
    public Ship shipDeparts() {
        Ship current = this.ship;
        this.ship = null;
        return current;
    }

    /**
     * Returns whether a ship is currently docked at this quay.
     *
     * @return true if there is no ship docked else false
     * @ass1
     */
    public boolean isEmpty() {
        return this.ship == null;
    }

    /**
     * Returns the ship currently docked at the quay.
     *
     * @return ship at quay or null if no ship is docked
     * @ass1
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Returns the human-readable string representation of this quay.
     * <p>
     * The format of the string to return is
     * <pre>QuayClass id [Ship: imoNumber]</pre>
     * Where:
     * <ul>
     * <li>{@code id} is the ID of this quay</li>
     * <li>{@code imoNumber} is the IMO number of the ship docked at this
     * quay, or {@code None} if the quay is unoccupied.</li>
     * </ul>
     * <p>
     * For example: <pre>BulkQuay 1 [Ship: 2313212]</pre> or
     * <pre>ContainerQuay 3 [Ship: None]</pre>
     *
     * @return string representation of this quay
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %d [Ship: %s]",
                this.getClass().getSimpleName(),
                this.id,
                (this.ship != null ? this.ship.getImoNumber() : "None"));
    }

    /**
     * Returns true if and only if this Quay is equal to the other given Quay. For two Quays to
     * be equal, they must have the same ID and ship docked status (must either both be empty
     * or both be occupied).
     *
     * @param o other object to check equality
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Quay) {
            return (id == ((Quay) o).id) && (isEmpty() == ((Quay) o).isEmpty());
        }
        return false;
    }

    /**
     * Returns the hash code of this quay. Two quays that are equal according to equals(Object)
     * method should have the same hash code.
     *
     * @return hash code of this quay.
     */
    public int hashCode() {
        return Objects.hash(id, isEmpty());
    }

    /**
     * Returns the machine-readable string representation of this Quay. The format of the
     * string to return is
     * <p>
     * QuayClass:id:imoNumber Where: QuayClass is the Quay class name id is the ID of this quay
     * imoNumber is the IMO number of the ship docked at this quay, or None if the quay is
     * unoccupied. For example: BulkQuay:3:1258691 or ContainerQuay:3:None
     *
     * @return encoded string representation of this quay
     */
    public String encode() {
        String imoNumber = "None";
        if (!isEmpty()) {
            imoNumber = Objects.toString(ship.getImoNumber());
        }
        return String.format("%s:%d:%s", this.getClass().getSimpleName(), id, imoNumber);
    }

    /**
     * Reads a Quay from its encoded representation in the given string. The format of the
     * string should match the encoded representation of a Quay, as described in encode() (and
     * subclasses).
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <p>
     * The number of colons (:) detected was more/fewer than expected. The quay id is not an
     * Integer (i.e. cannot be parsed by Integer.parseInt(String)). The quay id is less than
     * (0). The quay type specified is not one of BulkQuay or ContainerQuay If the encoded ship
     * is not None then the ship must exist and the imoNumber specified must be a long (i.e.
     * can be parsed by Long.parseLong(String)). The quay capacity is not an integer (i.e.
     * cannot be parsed by Integer.parseInt(String)).
     *
     * @param string string containing the encoded Quay
     * @return decoded Quay instance
     * @throws BadEncodingException if the format of the given string is invalid according to
     *                              the rules above
     */
    public static Quay fromString(String string) throws BadEncodingException {

        //  The number of colons (:) detected was more/fewer than expected
        String[] pairs = string.split(":");
        if (pairs.length != 4 || string.lastIndexOf(":") == string.length() - 1) {
            throw new BadEncodingException(" The number of colons (:) detected was more/fewer "
                    + "than expected");
        }

        // The quay id is not an Integer
        int quayId;
        try {
            quayId = Integer.parseInt(pairs[1]);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The quay id is not an Integer", n);
        }

        // The quay id is less than (0)
        if (quayId < 0) {
            throw new BadEncodingException("The quay id is less than (0)");
        }

        // If the encoded ship is not None
        long imoNumber = 0;
        if (!pairs[2].equals("None")) {
            // the imoNumber specified must be a long
            try {
                imoNumber = Long.parseLong(pairs[2]);
            } catch (NumberFormatException n) {
                throw new BadEncodingException("the imoNumber specified must be a long", n);
            }

        }

        // The quay capacity is not an integer
        int capacity;
        try {
            capacity = Integer.parseInt(pairs[3]);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The quay capacity is not an integer", n);
        }

        Quay quay;
        // The quay type specified is not one of BulkQuay or ContainerQuay
        if (pairs[0].equals("BulkQuay")) {
            quay = new BulkQuay(quayId, capacity);
        } else if (pairs[0].equals("ContainerQuay")) {
            quay = new ContainerQuay(quayId, capacity);
        } else {
            throw new BadEncodingException("The quay type specified is not one of BulkQuay or "
                    + "ContainerQuay");
        }

        Ship ship;
        if (!pairs[2].equals("None")) {
            // the ship must exist
            try {
                ship = Ship.getShipByImoNumber(imoNumber);
            } catch (NoSuchShipException n) {
                throw new BadEncodingException("the ship must exist");
            }

            // arrive ship correctly
            if (quay instanceof ContainerQuay && ship instanceof ContainerShip) {
                quay.shipArrives(ship);
            } else if (quay instanceof BulkQuay && ship instanceof BulkCarrier) {
                quay.shipArrives(ship);
            } else {
                throw new BadEncodingException("quay mismatch ship");
            }
        }

        return quay;
    }
}
