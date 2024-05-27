package portsim.ship;

import portsim.cargo.*;
import portsim.port.Quay;
import portsim.util.*;
import java.util.*;

/**
 * Represents a ship whose movement is managed by the system.
 * <p>
 * Ships store various types of cargo which can be loaded and unloaded at a port.
 *
 * @ass1_partial
 */
public abstract class Ship {
    /**
     * Name of the ship
     */
    private String name;

    /**
     * Unique 7 digit identifier to identify this ship (no leading zero's [0])
     */
    private long imoNumber;

    /**
     * Port of origin of ship
     */
    private String originFlag;

    /**
     * Maritime flag designated for use on this ship
     */
    private NauticalFlag flag;

    /**
     * Database of all ships currently active in the simulation
     */
    private static Map<Long, Ship> shipRegistry = new HashMap<>();

    /**
     * Creates a new ship with the given
     * <a href="https://en.wikipedia.org/wiki/IMO_number">IMO number</a>,
     * name, origin port flag and nautical flag.
     * <p>
     * Finally, the ship should be added to the ship registry with the IMO number as the key.
     *
     * @param imoNumber  unique identifier
     * @param name       name of the ship
     * @param originFlag port of origin
     * @param flag       the nautical flag this ship is flying
     * @throws IllegalArgumentException if a ship already exists with the given imoNumber,
     *                                  imoNumber &lt; 0 or imoNumber is not 7 digits long (no
     *                                  leading zero's [0])
     * @ass1_partial
     */
    public Ship(long imoNumber, String name, String originFlag,
                NauticalFlag flag) throws IllegalArgumentException {
        if (imoNumber < 0) {
            throw new IllegalArgumentException("The imoNumber of the ship "
                    + "must be positive: " + imoNumber);
        }
        if (String.valueOf(imoNumber).length() != 7 || String.valueOf(imoNumber).startsWith("0")) {
            throw new IllegalArgumentException("The imoNumber of the ship "
                    + "must have 7 digits (no leading zero's [0]): " + imoNumber);
        }
        this.imoNumber = imoNumber;
        this.name = name;
        this.originFlag = originFlag;
        this.flag = flag;
        if (shipRegistry.get(imoNumber) != null) {
            throw new IllegalArgumentException("a ship already exists with the given imoNumber");
        } else {
            shipRegistry.put(imoNumber, this);
        }
    }

    /**
     * Check if this ship can dock with the specified quay according to the conditions
     * determined by the ships type.
     *
     * @param quay quay to be checked
     * @return true if the Quay satisfies the conditions else false
     * @ass1
     */
    public abstract boolean canDock(Quay quay);

    /**
     * Checks if the specified cargo can be loaded onto the ship according to the conditions
     * determined by the ships type and contents.
     *
     * @param cargo cargo to be loaded
     * @return true if the Cargo satisfies the conditions else false
     * @ass1
     */
    public abstract boolean canLoad(Cargo cargo);

    /**
     * Loads the specified cargo onto the ship.
     *
     * @param cargo cargo to be loaded
     * @require Cargo given is able to be loaded onto this ship according to the implementation
     * of {@link Ship#canLoad(Cargo)}
     * @ass1
     */
    public abstract void loadCargo(Cargo cargo);

    /**
     * Returns this ship's name.
     *
     * @return name
     * @ass1
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns this ship's IMO number.
     *
     * @return imoNumber
     * @ass1
     */
    public long getImoNumber() {
        return this.imoNumber;
    }

    /**
     * Returns this ship's flag denoting its origin.
     *
     * @return originFlag
     * @ass1
     */
    public String getOriginFlag() {
        return this.originFlag;
    }

    /**
     * Returns the nautical flag the ship is flying.
     *
     * @return flag
     * @ass1
     */
    public NauticalFlag getFlag() {
        return this.flag;
    }

    /**
     * Returns the human-readable string representation of this Ship.
     * <p>
     * The format of the string to return is
     * <pre>ShipClass name from origin [flag]</pre>
     * Where:
     * <ul>
     *   <li>{@code ShipClass} is the Ship class</li>
     *   <li>{@code name} is the name of this ship</li>
     *   <li>{@code origin} is the country of origin of this ship</li>
     *   <li>{@code flag} is the nautical flag of this ship</li>
     * </ul>
     * For example: <pre>BulkCarrier Evergreen from Australia [BRAVO]</pre>
     *
     * @return string representation of this Ship
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %s from %s [%s]",
                this.getClass().getSimpleName(),
                this.name,
                this.originFlag,
                this.flag);
    }

    /**
     * Resets the global ship registry. This utility method is for the testing suite.
     *
     * @given
     */
    public static void resetShipRegistry() {
        Ship.shipRegistry = new HashMap<>();
    }

    /**
     * Checks if a ship exists in the simulation using its IMO number.
     *
     * @param imoNumber unique key to identify ship
     * @return true if there is a ship with key imoNumber else false
     */
    public static boolean shipExists(long imoNumber) {
        return shipRegistry.get(imoNumber) != null;
    }

    /**
     * Returns the ship specified by the IMO number.
     *
     * @param imoNumber unique key to identify ship
     * @return Ship specified by the given IMO number
     * @throws NoSuchShipException if the ship does not exist
     */
    public static Ship getShipByImoNumber(long imoNumber) throws NoSuchShipException {
        if (shipExists(imoNumber)) {
            return shipRegistry.get(imoNumber);
        } else {
            throw new NoSuchShipException("No ship with this imoNumber");
        }
    }

    /**
     * Returns the database of ships currently active in the simulation as a mapping from the
     * ship's IMO number to its Ship instance. Adding or removing elements from the returned
     * map should not affect the original map.
     *
     * @return ship registry database
     */
    public static Map<Long, Ship> getShipRegistry() {
        return new HashMap<>(shipRegistry);
    }

    /**
     * Returns true if and only if this ship is equal to the other given ship. For two ships to
     * be equal, they must have the same name, flag, origin port, and IMO number.
     *
     * @param o other object to check equality
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Ship) {
            Ship other = (Ship) o;
            return name.equals(other.name) && flag.equals(other.flag)
                    && originFlag.equals(other.originFlag) && imoNumber == other.imoNumber;
        }
        return false;
    }

    /**
     * Returns the hash code of this ship. Two ships that are equal according to the
     * equals(Object) method should have the same hash code.
     *
     * @return hash code of this ship.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, flag, originFlag, imoNumber);
    }

    /**
     * Returns the machine-readable string representation of this Ship. The format of the
     * string to return is "ShipClass:imoNumber:name:origin:flag" Where: ShipClass is the Ship
     * class name imoNumber is the IMO number of the ship name is the name of this ship origin
     * is the country of origin of this ship flag is the nautical flag of this ship For
     * example: "Ship:1258691:Evergreen:Australia:BRAVO"
     *
     * @return encoded string representation of this Ship
     */
    public String encode() {
        return String.format("%s:%d:%s:%s:%s", this.getClass().getSimpleName(), imoNumber, name,
                originFlag, flag);
    }

    /**
     * Reads a Ship from its encoded representation in the given string. The format of the
     * string should match the encoded representation of a Ship, as described in encode() (and
     * subclasses).
     * <p>
     * The encoded string is invalid if any of the following conditions are true: The number of
     * colons (:) detected was more/fewer than expected The ship's IMO number is not a long
     * (i.e. cannot be parsed by Long.parseLong(String)) The ship's IMO number is valid
     * according to the constructor The ship's type specified is not one of ContainerShip or
     * BulkCarrier The encoded Nautical flag is not one of NauticalFlag.values() The encoded
     * cargo to add does not exist in the simulation according to Cargo.cargoExists (int) The
     * encoded cargo can not be added to the ship according to canLoad(Cargo) Any of the parsed
     * values given to a subclass constructor causes an IllegalArgumentException.
     *
     * @param string string containing the encoded Ship
     * @return decoded ship instance
     * @throws BadEncodingException if the format of the given string is invalid according to
     *                              the rules above
     */
    public static Ship fromString(String string) throws BadEncodingException {
        Ship ship;
        // start with
        if (!string.startsWith("ContainerShip") && !string.startsWith("BulkCarrier")) {
            throw new BadEncodingException("the starting of string");
        }
        // the number of colons of string
        int colonsNumber = 0;
        for (String c : string.split("")) {
            if (c.equals(":")) {
                colonsNumber++;
            }
        }
        //  The number of colons (:) detected was more/fewer than expected
        String[] pairs = string.split(":");
        if (pairs[0].equals("ContainerShip") && colonsNumber != 7) {
            throw new BadEncodingException("ContainerShip - The number of colons (:)");
        } else if (pairs[0].equals("BulkCarrier") && colonsNumber != 6) {
            throw new BadEncodingException("BulkCarrier - The number of colons (:)");
        }

        // The ship's IMO number is not a long
        long imoNumber;
        try {
            imoNumber = Long.parseLong(pairs[1]);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The ship's IMO number is not a long", n);
        }

        // The ship's IMO number is valid according to the constructor
        if (imoNumber < 0 || String.valueOf(imoNumber).length() != 7
                || String.valueOf(imoNumber).startsWith("0")
                || shipRegistry.get(imoNumber) != null) {
            throw new BadEncodingException("The ship's IMO number is valid");
        }

        // The encoded Nautical flag is not one of NauticalFlag.values()
        NauticalFlag flag;
        try {
            flag = NauticalFlag.valueOf(pairs[4]);
        } catch (IllegalArgumentException i) {
            throw new BadEncodingException("The encoded Nautical flag is not good", i);
        }

        // capacity
        int capacity;
        try {
            capacity = Integer.parseInt(pairs[5]);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("capacity is not an integer", n);
        }

        String name = pairs[2];
        String origin = pairs[3];
        // Any of the parsed values given to a subclass
        // constructor causes an IllegalArgumentException
        try {
            // The ship's type specified is not one of ContainerShip or BulkCarrier
            if (pairs[0].equals("ContainerShip")) {
                ship = new ContainerShip(imoNumber, name, origin, flag, capacity);
            } else if (pairs[0].equals("BulkCarrier")) {
                ship = new BulkCarrier(imoNumber, name, origin, flag, capacity);
            } else {
                throw new BadEncodingException("The ship's type specified is wrong");
            }
        } catch (IllegalArgumentException i) {
            throw new BadEncodingException("a subclass causes an IllegalArgumentException", i);
        }

        Cargo cargo;
        // BulkCarrier
        if (ship instanceof BulkCarrier && pairs.length == 7) {
            // The encoded cargo to add does not exist in the simulation
            try {
                cargo = Cargo.getCargoById(Integer.parseInt(pairs[6]));
            } catch (NoSuchCargoException n) {
                throw new BadEncodingException("The encoded cargo to add does not exist", n);
            } catch (NumberFormatException n) {
                throw new BadEncodingException("BulkCarrier - cargo id isn't an integer", n);
            }

            // The encoded cargo can not be added to the ship according to canLoad(Cargo)
            if (ship.canLoad(cargo)) {
                ship.loadCargo(cargo);
            } else {
                throw new BadEncodingException("The encoded cargo cannot be added to the ship");
            }
        // ContainerShip
        } else if (ship instanceof ContainerShip && pairs.length == 8) {
            int cargoNum;
            try {
                cargoNum = Integer.parseInt(pairs[6]);
            } catch (NumberFormatException n) {
                throw new BadEncodingException("cargoNum doesn't an integer");
            }

            if (cargoNum != pairs[7].split(",").length) {
                throw new BadEncodingException("cargoNum doesn't match the number of cargos");
            }
            for (String cargoId : pairs[7].split(",")) {
                // The encoded cargo to add does not exist in the simulation
                try {
                    cargo = Cargo.getCargoById(Integer.parseInt(cargoId));
                } catch (NoSuchCargoException n) {
                    throw new BadEncodingException("The encoded cargo to add does not exist", n);
                } catch (NumberFormatException n) {
                    throw new BadEncodingException("one of cargo ids isn't an integer", n);
                }

                // The encoded cargo can not be added to the ship according to canLoad(Cargo)
                if (ship.canLoad(cargo)) {
                    ship.loadCargo(cargo);
                } else {
                    throw new BadEncodingException("The encoded cargo cannot be added to "
                            + "ship");
                }
            }
        }
        return ship;
    }

}
