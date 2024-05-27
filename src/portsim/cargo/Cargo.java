package portsim.cargo;

import portsim.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Denotes a cargo whose function is to be transported via a Ship or land transport.
 * <p>
 * Cargo is kept track of via its ID.
 *
 * @ass1_partial
 */
public abstract class Cargo {
    /**
     * The ID of the cargo instance
     */
    private int id;

    /**
     * Destination for this cargo
     */
    private String destination;

    /**
     * Database of all cargo currently active in the simulation
     */
    private static Map<Integer, Cargo> cargoRegistry = new HashMap<>();

    /**
     * Creates a new Cargo with the given ID and destination port.
     * <p>
     * When a new piece of cargo is created, it should be added to the cargo registry.
     *
     * @param id          cargo ID
     * @param destination destination port
     * @throws IllegalArgumentException if a cargo already exists with the given ID or ID &lt;
     *                                  0
     * @ass1_partial
     */
    public Cargo(int id, String destination) throws IllegalArgumentException {
        if (id < 0) {
            throw new IllegalArgumentException("Cargo ID must be greater than"
                    + " or equal to 0: " + id);
        }
        this.id = id;
        this.destination = destination;
        if (cargoRegistry.get(id) != null) {
            throw new IllegalArgumentException("a cargo already exists with the given ID");
        } else {
            cargoRegistry.put(id, this);
        }
    }

    /**
     * Retrieve the ID of this piece of cargo.
     *
     * @return the cargo's ID
     * @ass1
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieve the destination of this piece of cargo.
     *
     * @return the cargo's destination
     * @ass1
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Returns the human-readable string representation of this cargo.
     * <p>
     * The format of the string to return is
     * <pre>CargoClass id to destination</pre>
     * Where:
     * <ul>
     *   <li>{@code CargoClass} is the cargo class name</li>
     *   <li>{@code id} is the id of this cargo </li>
     *   <li>{@code destination} is the destination of the cargo </li>
     * </ul>
     * <p>
     * For example: <pre>Container 55 to New Zealand</pre>
     *
     * @return string representation of this Cargo
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %d to %s",
                this.getClass().getSimpleName(),
                this.id,
                this.destination);
    }

    /**
     * Resets the global cargo registry. This utility method is for the testing suite.
     *
     * @given
     */
    public static void resetCargoRegistry() {
        Cargo.cargoRegistry = new HashMap<>();
    }

    /**
     * Returns the global registry of all pieces of cargo, as a mapping from cargo IDs to Cargo
     * instances.
     * <p>
     * Adding or removing elements from the returned map should not affect the
     * original map.
     *
     * @return cargo registry
     * @ass2
     */
    public static Map<Integer, Cargo> getCargoRegistry() {
        return new HashMap<>(cargoRegistry);
    }

    /**
     * Checks if a cargo exists in the simulation using its ID.
     *
     * @param id unique key to identify cargo
     * @return true if there is a cargo stored in the registry with key id; false otherwise
     * @ass2
     */
    public static boolean cargoExists(int id) {
        return cargoRegistry.get(id) != null;
    }

    /**
     * Returns the cargo specified by the given ID.
     *
     * @param id unique key to identify cargo
     * @return cargo specified by the id
     * @throws NoSuchCargoException if the cargo does not exist in the registry
     * @ass2
     */
    public static Cargo getCargoById(int id) throws NoSuchCargoException {
        if (cargoExists(id)) {
            return cargoRegistry.get(id);
        } else {
            throw new NoSuchCargoException("no cargo with this id: " + id);
        }
    }

    /**
     * Returns true if and only if this cargo is equal to the other given cargo.
     * <P>
     * For two cargo to be equal, they must have the same ID and destination.
     *
     * @param o other object to check equality
     * @return true if equal, false otherwise
     * @ass2
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Cargo) {
            return this.destination.equals(((Cargo) o).destination) && this.id == ((Cargo) o).id;
        }
        return false;
    }

    /**
     * Returns the hash code of this cargo.
     * <P>
     * Two cargo are equal according to equals(Object) method should have the same hash code.
     *
     * @return hash code of this cargo.
     * @ass2
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.destination);
    }

    /**
     * Returns the machine-readable string representation of this Cargo.
     * <P>
     * The format of the string to return is
     * <Pre>CargoClass:id:destination</Pre>
     * Where:
     * <ul>
     *     <li>{@code CargoClass} is the Cargo class</li>
     *     <li>{@code name} id is the id of this cargo</li>
     *     <li>{@code destination} is the destination of this cargo</li>
     * </ul>
     * <P>
     * For example: <pre>Container:3:Australia</pre> OR <pre>BulkCargo:2:France</pre>
     *
     * @return encoded string representation of this Cargo
     * @ass2
     */
    public String encode() {
        return String.format("%s:%d:%s", this.getClass().getSimpleName(), id, destination);
    }

    /**
     * Reads a piece of cargo from its encoded representation in the given string.
     * <p>
     * The format of the given string should match the encoded representation of a Cargo, as
     * described in encode() (and subclasses).
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <ul>
     *     <li> The number of colons (:) detected was more/fewer than expected.</li>
     *     <li> The cargo id is not an integer (i.e.cannot be parsed by Integer.parseInt
     *     (String)).</li>
     *     <li> The cargo id is less than zero (0). </li>
     *     <li> A piece of cargo with the specified ID already exists The cargo type specified
     *     is not one of BulkCargoType or ContainerType </li>
     *     <li>If the cargo type is a BulkCargo:
     *         <li>The cargo weight in tonnes is not an integer (i.e. cannot be parsed by
     *         Integer.parseInt (String)).</li>
     *         <li>The cargo weight in tonnes is less than zero (0).</li></li>
     *</ul>
     *
     * @param string string containing the encoded cargo
     * @return decoded cargo instance
     * @throws BadEncodingException if the format of the given string is invalid according to
     *                              the rules above
     * @ass2
     */
    public static Cargo fromString(String string) throws BadEncodingException {
        Cargo cargo;

        if (string.startsWith("BulkCargo")) {
            String[] pairs = string.split(":");

            // check the number of colons
            if (pairs.length != 5) {
                throw new BadEncodingException("The number of colons (:) detected was "
                        + "more/fewer than expected");
            }
            // "BulkCargo:2:Germany:GRAIN:50:"
            if (string.lastIndexOf(":") == string.length() - 1) {
                throw new BadEncodingException("The number of colons (:) detected was "
                        + "more/fewer than expected");
            }

            // check the cargo class name - "BulkCargo"
            if (!pairs[0].equals("BulkCargo")) {
                throw new BadEncodingException("Not a class name of BulkCargo");
            }

            // check the cargo id isn't an integer
            int cargoId;
            try {
                cargoId = Integer.parseInt(pairs[1]);
            } catch (NumberFormatException n) {
                throw new BadEncodingException("The cargo id is not an integer", n);
            }

            // check the cargo id is less zero
            if (cargoId < 0) {
                throw new BadEncodingException("The cargo id is less than zero (0)");
            }

            // check cargo with the specified ID already exists
            if (cargoExists(cargoId)) {
                throw new BadEncodingException("A piece of cargo with the specified ID already "
                        + "exists");
            }

            // check the BulkCargoType
            BulkCargoType type;
            try {
                type = BulkCargoType.valueOf(pairs[3]);
            } catch (IllegalArgumentException i) {
                throw new BadEncodingException("The cargo type specified is not one of "
                        + "BulkCargoType", i);
            }

            // check the cargo tonnage is not an integer
            int tonnage;
            try {
                tonnage = Integer.parseInt(pairs[4]);
            } catch (NumberFormatException n) {
                throw new BadEncodingException("The cargo weight in tonnes is not an integer", n);
            }

            // check the cargo tonnage is less than zero
            if (tonnage < 0) {
                throw new BadEncodingException("The cargo weight in tonnes is "
                        + "less than zero (0)");
            }

            // decode
            cargo = new BulkCargo(cargoId, pairs[2], tonnage, type);

            // check the Container cargo
        } else if (string.startsWith("Container")) {
            String[] pairs = string.split(":");

            // check the number of colons
            if (pairs.length != 4) {
                throw new BadEncodingException("The number of colons (:) detected was "
                        + "more/fewer than expected");
            }

            // "Container:2:France:OPEN_TOP:"
            if (string.lastIndexOf(":") == string.length() - 1) {
                throw new BadEncodingException("The number of colons (:) detected was "
                        + "more/fewer than expected");
            }

            // check the cargo class name - "Container"
            if (!pairs[0].equals("Container")) {
                throw new BadEncodingException("Not a class name of Container");
            }

            // check the cargo id isn't an integer
            int cargoId;
            try {
                cargoId = Integer.parseInt(pairs[1]);
            } catch (NumberFormatException n) {
                throw new BadEncodingException("The cargo id is not an integer", n);
            }

            // check the cargo id is less zero
            if (cargoId < 0) {
                throw new BadEncodingException("The cargo id is less than zero (0)");
            }

            // check cargo with the specified ID already exists
            if (cargoExists(cargoId)) {
                throw new BadEncodingException("A piece of cargo with the specified ID already "
                        + "exists");
            }

            // check the ContainerType
            ContainerType type;
            try {
                type = ContainerType.valueOf(pairs[3]);
            } catch (IllegalArgumentException i) {
                throw new BadEncodingException("The cargo type specified is not one of "
                        + "ContainerType.", i);
            }

            // decode
            cargo = new Container(cargoId, pairs[2], type);
        } else {
            throw new BadEncodingException("Neither BulkCargo nor Container");
        }
        return cargo;
    }
}
