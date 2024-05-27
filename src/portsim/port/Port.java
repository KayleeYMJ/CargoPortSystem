package portsim.port;

import portsim.cargo.Cargo;
import portsim.evaluators.*;
import portsim.movement.*;
import portsim.ship.*;
import portsim.util.*;
import java.io.*;
import java.util.*;

/**
 * A place where ships can come and dock with Quays to load / unload their cargo.
 * <p>
 * Ships can enter a port through its queue. Cargo is stored within the port at warehouses.
 *
 * @ass1_partial
 */
public class Port {
    /**
     * The name of this port used for identification
     */
    private String name;

    /**
     * The quays associated with this port
     */
    private List<Quay> quays;

    /**
     * The cargo currently stored at the port at warehouses. Cargo unloaded from trucks /
     * ships
     */
    private List<Cargo> storedCargo;

    /**
     * The number of minutes since simulation started
     */
    private long time;

    /**
     * ships waiting to enter the port
     */
    private ShipQueue shipQueue;

    /**
     * a PriorityQueue should be initialised to store movements ordered by the time of the
     * movement
     */
    private PriorityQueue<Movement> priorityQueue;

    /**
     * The list of statistics evaluators
     */
    private List<StatisticsEvaluator> evaluators;

    /**
     * compare time of movements
     */
    private static Comparator<Movement> timeCompare = new Comparator<Movement>() {
        public int compare(Movement m1, Movement m2) {
            return Long.compare(m1.getTime(), m2.getTime());
        }
    };

    /**
     * Creates a new port with the given name.
     * <p>
     * // The time since the simulation was started should be initialised as 0.
     * <p>
     * The list of quays in the port, stored cargo (warehouses) and statistics evaluators
     * should be initialised as empty lists.
     * <p>
     * An empty ShipQueue should be initialised, and a PriorityQueue should be initialised to
     * store movements ordered by the time of the movement (see {@link Movement#getTime()}).
     *
     * @param name name of the port
     * @ass1_partial
     */
    public Port(String name) {
        this.name = name;
        this.quays = new ArrayList<>();
        this.storedCargo = new ArrayList<>();
        time = 0;
        shipQueue = new ShipQueue();
        priorityQueue = new PriorityQueue<>(timeCompare);
        evaluators = new ArrayList<>();
    }

    /**
     * Creates a new port with the given name, time elapsed, ship queue, quays and stored
     * cargo. The list of statistics evaluators should be initialised as an empty list.
     * <p>
     * An empty ShipQueue should be initialised, and a PriorityQueue should be initialised to
     * store movements ordered by the time of the movement (see Movement.getTime()).
     *
     * @param name        name of the port
     * @param time        number of minutes since simulation started
     * @param shipQueue   ships waiting to enter the port
     * @param quays       the port's quays
     * @param storedCargo the cargo stored at the port
     * @throws IllegalArgumentException if time < 0
     */
    public Port(String name, long time, ShipQueue shipQueue, List<Quay> quays,
                List<Cargo> storedCargo) throws IllegalArgumentException {
        if (time < 0) {
            throw new IllegalArgumentException("time < 0: " + time);
        }
        this.name = name;
        this.time = time;
        this.shipQueue = shipQueue;
        this.quays = quays;
        this.storedCargo = storedCargo;
        priorityQueue = new PriorityQueue<>(timeCompare);
        evaluators = new ArrayList<>();
    }

    /**
     * Returns the name of this port.
     *
     * @return port's name
     * @ass1
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a list of all quays associated with this port.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     * <p>
     * The order in which quays appear in this list should be the same as the order in which
     * they were added by calling {@link #addQuay(Quay)}.
     *
     * @return all quays
     * @ass1
     */
    public List<Quay> getQuays() {
        return new ArrayList<>(this.quays);
    }

    /**
     * Returns the cargo stored in warehouses at this port.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return port cargo
     * @ass1
     */
    public List<Cargo> getCargo() {
        return new ArrayList<>(this.storedCargo);
    }

    /**
     * Adds a quay to the ports control.
     *
     * @param quay the quay to add
     * @ass1
     */
    public void addQuay(Quay quay) {
        this.quays.add(quay);
    }

    /**
     * Adds a movement to the PriorityQueue of movements. If the given movement's action time
     * is less than the current number of minutes elapsed than an IllegalArgumentException
     * should be thrown.
     *
     * @param movement movement to add
     * @throws IllegalArgumentException If the given movement's action time is less than the
     *                                  current number of minutes elapsed
     */
    public void addMovement(Movement movement) throws IllegalArgumentException {
        if (movement.getTime() < time) {
            throw new IllegalArgumentException("the given movement's action time is less than the"
                    + " current number of minutes elapsed");
        } else {
            priorityQueue.add(movement);
        }
    }

    /**
     * Processes a movement. The action taken depends on the type of movement to be processed.
     * <p>
     * If the movement is a ShipMovement:
     * <p>
     * If the movement direction is INBOUND then the ship should be added to the ship queue. If
     * the movement direction is OUTBOUND then any cargo stored in the port whose destination
     * is the ship's origin port should be added to the ship according to Ship.canLoad(Cargo).
     * Next, the ship should be removed from the quay it is currently docked in (if any). If
     * the movement is a CargoMovement: If the movement direction is INBOUND then all of the
     * cargo that is being moved should be added to the port's stored cargo. If the movement
     * direction is OUTBOUND then all cargo with the given IDs should be removed from the
     * port's stored cargo. Finally, the movement should be forwarded onto each statistics
     * evaluator stored by the port by calling StatisticsEvaluator.onProcessMovement(Movement).
     *
     * @param movement movement to execute
     */
    public void processMovement(Movement movement) {
        // cargo movement
        if (movement instanceof ShipMovement) {
            Ship ship = ((ShipMovement) movement).getShip();
            if (movement.getDirection().equals(MovementDirection.INBOUND)) {
                shipQueue.add(ship);
            } else {

                // load cargo
                for (Cargo cargo : storedCargo) {
                    if (cargo.getDestination().equals(ship.getOriginFlag())
                            && ship.canLoad(cargo)) {
                        ship.loadCargo(cargo);
                    }

                    // ship departure
                    for (Quay quay : quays) {
                        if (!quay.isEmpty() && quay.getShip().equals(ship)) {
                            quay.shipDeparts();
                        }
                    }
                }
            }
        } else {
            // ship movement
            List<Cargo> cargos = ((CargoMovement) movement).getCargo();
            for (Cargo cargo : cargos) {
                if (movement.getDirection().equals(MovementDirection.INBOUND)) {
                    storedCargo.add(cargo);
                } else {
                    storedCargo.remove(cargo);
                }
            }
        }

        for (StatisticsEvaluator eval : evaluators) {
            eval.onProcessMovement(movement);
        }
    }

    /**
     * Adds the given statistics evaluator to the port's list of evaluators. If the port
     * already has an evaluator of that type, no action should be taken.
     *
     * @param eval statistics evaluator to add to the port
     */
    public void addStatisticsEvaluator(StatisticsEvaluator eval) {
        if (!evaluators.contains(eval)) {
            evaluators.add(eval);
        }
    }

    /**
     * Returns the time since simulation started.
     *
     * @return time in minutes
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns the queue of ships waiting to be docked at this port.
     *
     * @return port's queue of ships
     */
    public ShipQueue getShipQueue() {
        return shipQueue;
    }

    /**
     * Returns the queue of movements waiting to be processed.
     *
     * @return movements queue
     */
    public PriorityQueue<Movement> getMovements() {
        return priorityQueue;
    }

    /**
     * Returns the list of evaluators at the port. Adding or removing elements from the
     * returned list should not affect the original list.
     *
     * @return the ports evaluators
     */
    public List<StatisticsEvaluator> getEvaluators() {
        return new ArrayList<>(evaluators);
    }

    /**
     * Advances the simulation by one minute. On each call to elapseOneMinute(), the following
     * actions should be completed by the port in order:
     * <p>
     * Advance the simulation time by 1 If the time is a multiple of 10, attempt to bring a
     * ship from the ship queue to any empty quay that matches the requirements from
     * Ship.canDock(Quay). The ship should only be docked to one quay. If the time is a
     * multiple of 5, all quays must unload the cargo from ships docked (if any) and add it to
     * warehouses at the port (the Port's list of stored cargo) All movements stored in the
     * queue whose action time is equal to the current time should be processed by
     * processMovement(Movement) Call StatisticsEvaluator.elapseOneMinute() on all statistics
     * evaluators
     */
    public void elapseOneMinute() {
        time++;
        if (time % 10 == 0) {
            if (shipQueue.getShipQueue().size() > 0) {
                Ship ship = shipQueue.poll();
                Boolean shipDocked = false;

                // check ship is arrived or not
                for (Quay quay : quays) {
                    if (!quay.isEmpty() && quay.getShip().equals(ship)) {
                        shipDocked = true;
                    }
                }

                // dock ship
                if (!shipDocked) {
                    for (Quay quay : quays) {
                        if (quay.isEmpty() && ship.canDock(quay)) {
                            quay.shipArrives(ship);
                        }
                    }
                }
            }
        } else if (time % 5 == 0) {
            for (Quay quay : quays) {
                if (!quay.isEmpty()) {
                    if (quay.getShip() instanceof BulkCarrier) {
                        if (((BulkCarrier) quay.getShip()).getCargo() != null) {
                            try {
                                storedCargo.add(((BulkCarrier) quay.getShip()).getCargo());
                                ((BulkCarrier) quay.getShip()).unloadCargo();
                            } catch (NoSuchCargoException n) {
                                n.printStackTrace();
                            }
                        }
                    } else {
                        if (((ContainerShip) quay.getShip()).getCargo().size() != 0) {
                            try {
                                for (Cargo cargo : ((ContainerShip) quay.getShip()).getCargo()) {
                                    storedCargo.add(cargo);
                                }
                                ((ContainerShip) quay.getShip()).unloadCargo();
                            } catch (NoSuchCargoException n) {
                                n.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        for (Movement movement : priorityQueue) {
            if (movement.getTime() == time) {
                processMovement(movement);
            }
        }
        for (StatisticsEvaluator evl : evaluators) {
            evl.elapseOneMinute();
        }
    }

    /**
     * Returns the machine-readable string representation of this Port. The format of the
     * string to return is
     * <p>
     * Name Time numCargo EncodedCargo EncodedCargo... numShips EncodedShip EncodedShip...
     * numQuays EncodedQuay EncodedQuay... ShipQueue:numShipsInQueue:shipID,shipID,...
     * StoredCargo:numCargo:cargoID,cargoID,... Movements:numMovements EncodedMovement
     * EncodedMovement... Evaluators:numEvaluators:EvaluatorSimpleName,EvaluatorSimpleName,...
     * <p>
     * Where: Name is the name of the Port Time is the time elapsed since the simulation
     * started numCargo is the total number of cargo in the simulation If present (numCargo >
     * 0): EncodedCargo is the encoded representation of each individual cargo in the
     * simulation numShips is the total number of ships in the simulation If present (numShips
     * > 0): EncodedShip is the encoded representation of each individual ship encoding in the
     * simulation numQuays is the total number of quays in the Port If present (numQuays > 0):
     * EncodedQuay is the encoded representation of each individual quay in the simulation
     * numShipsInQueue is the total number of ships in the ship queue in the port If present
     * (numShipsInQueue > 0): shipID is each ship's ID in the aforementioned queue numCargo is
     * the total amount of stored cargo in the Port If present (numCargo > 0): cargoID is each
     * cargo's ID in the stored cargo list of Port numMovements is the number of movements in
     * the list of movements in Port If present (numMovements > 0): EncodedMovement is the
     * encoded representation of each individual Movement in the aforementioned list
     * numEvaluators is the number of statistics evaluators in the Port evaluators list If
     * present (numEvaluators > 0): EvaluatorSimpleName is the name given by
     * Class.getSimpleName() for each evaluator in the aforementioned list separated by a comma
     * Each line is separated by a System.lineSeparator() For example the minimum / default
     * encoding would be:
     * <p>
     * PortName 0 0 0 0 ShipQueue:0: StoredCargo:0: Movements:0 Evaluators:0:
     *
     * @return encoded string representation of this Port
     */
    public String encode() {
        String allCargos = "";
        if (Cargo.getCargoRegistry().size() > 0) {
            for (Cargo cargo : Cargo.getCargoRegistry().values()) {
                allCargos += cargo.encode();
                allCargos += System.lineSeparator();
            }
        }

        String allShips = "";
        if (Ship.getShipRegistry().size() > 0) {
            for (Ship ship : Ship.getShipRegistry().values()) {
                allShips += ship.encode();
                allShips += System.lineSeparator();
            }
        }

        String allQuays = "";
        if (quays.size() > 0) {
            for (Quay quay : quays) {
                allQuays += quay.encode();
                allQuays += System.lineSeparator();
            }
        }

        String storedCargos = "StoredCargo:" + storedCargo.size() + ":";
        if (storedCargo.size() > 0) {
            for (Cargo cargo : storedCargo) {
                storedCargos += cargo.getId();
                storedCargos += ",";
            }
            storedCargos = storedCargos.substring(0, storedCargos.length() - 1);
        }

        String movements = "";
        if (priorityQueue.size() > 0) {
            for (Movement movement : priorityQueue) {
                movements += movement.encode();
                movements += System.lineSeparator();
            }
        }

        String allEvaluators = "Evaluators:" + evaluators.size() + ":";
        if (evaluators.size() > 0) {
            for (StatisticsEvaluator evaluator : evaluators) {
                allEvaluators += evaluator.getClass().getSimpleName();
                allEvaluators += ",";
            }
            allEvaluators = allEvaluators.substring(0, allEvaluators.length() - 1);
        }

        return "" + name + System.lineSeparator() + time + System.lineSeparator()
                + Cargo.getCargoRegistry().size() + System.lineSeparator() + allCargos
                + Ship.getShipRegistry().size() + System.lineSeparator() + allShips + quays.size()
                + System.lineSeparator() + allQuays + shipQueue.encode() + System.lineSeparator()
                + storedCargos + System.lineSeparator() + "Movements:" + priorityQueue.size()
                + System.lineSeparator() + movements + allEvaluators;
    }

    /**
     * Creates a port instance by reading various ship, quay, cargo, movement and evaluator
     * entities from the given reader. The provided file should be in the format:
     * <p>
     * Name Time numCargo EncodedCargo EncodedCargo... numShips EncodedShip EncodedShip...
     * numQuays EncodedQuay EncodedQuay... ShipQueue:NumShipsInQueue:shipID,shipId
     * StoredCargo:numCargo:cargoID,cargoID Movements:numMovements EncodedMovement
     * EncodedMovement... Evaluators:numEvaluators:EvaluatorSimpleName, EvaluatorSimpleName
     * <p>
     * As specified by encode() The encoded string is invalid if any of the following
     * conditions are true:
     * <p>
     * The time is not a valid long (i.e. cannot be parsed by Long.parseLong(String)). The
     * number of cargo is not an integer (i.e. cannot be parsed by Integer.parseInt(String)).
     * The number of cargo to be read in does not match the number specified above. (ie. too
     * many / few encoded cargo following the number) An encoded cargo line throws a
     * BadEncodingException The number of ships is not an integer (i.e. cannot be parsed by
     * Integer.parseInt(String)). The number of ship to be read in does not match the number
     * specified above. (ie. too many / few encoded ships following the number) An encoded ship
     * line throws a BadEncodingException The number of quays is not an integer (i.e. cannot be
     * parsed by Integer.parseInt(String)). The number of quays to be read in does not match
     * the number specified above. (ie. too many / few encoded quays following the number) An
     * encoded quay line throws a BadEncodingException The shipQueue does not follow the last
     * encoded quay The number of ships in the shipQueue is not an integer (i.e. cannot be
     * parsed by Integer.parseInt(String)). The imoNumber of the ships in the shipQueue are not
     * valid longs. (i.e. cannot be parsed by Long.parseLong(String)). Any imoNumber read does
     * not correspond to a valid ship in the simulation The storedCargo does not follow the
     * encoded shipQueue The number of cargo in the storedCargo is not an integer (i.e. cannot
     * be parsed by Integer.parseInt(String)). The id of the cargo in the storedCargo are not
     * valid Integers. (i.e. cannot be parsed by Integer.parseInt(String)). Any cargo id read
     * does not correspond to a valid cargo in the simulation The movements do not follow the
     * encoded storedCargo The number of movements is not an integer (i.e. cannot be parsed by
     * Integer.parseInt(String)). The number of movements to be read in does not match the
     * number specified above. (ie. too many / few encoded movements following the number) An
     * encoded movement line throws a BadEncodingException The evaluators do not follow the
     * encoded movements The number of evaluators is not an integer (i.e. cannot be parsed by
     * Integer.parseInt(String)). The number of evaluators to be read in does not match the
     * number specified above. (ie. too many / few encoded evaluators following the number) An
     * encoded evaluator name does not match any of the possible evaluator classes If any of
     * the following lines are missing: Name Time Number of Cargo Number of Ships Number of
     * Quays ShipQueue StoredCargo Movements Evaluators
     *
     * @param reader reader from which to load all info
     * @return port created by reading from given reader
     * @throws IOException          if an IOException is encountered when reading from the
     *                              reader
     * @throws BadEncodingException if the reader reads a line that does not adhere to the
     *                              rules above indicating that the contents of the reader are
     *                              invalid
     */
    public static Port initialisePort(Reader reader) throws IOException, BadEncodingException {
        Port port;
        String content;
        int lineNum = 0;

        try {
            BufferedReader read = new BufferedReader(reader);

            final String portName = read.readLine(); // port's name line
            lineNum++;

            // time line
            content = read.readLine();
            lineNum++;
            final long portTime = decodeTime(content);

            // the number of cargo line
            int numCargos;
            try {
                numCargos = Integer.parseInt(read.readLine());
                lineNum++;
            } catch (NumberFormatException n) {
                throw new BadEncodingException("The number of cargo is not an integer", n);
            }

            // encoded cargo line
            if (numCargos != 0) {
                for (int index = 0; index < numCargos; index++) {
                    content = read.readLine();
                    if (content.startsWith("Container") || content.startsWith("BulkCargo")) {
                        try {
                            Cargo.fromString(content);
                        } catch (BadEncodingException b) {
                            throw b;
                        }
                    } else {
                        throw new BadEncodingException("The number of cargo doesn't match");
                    }
                }
            }

            // the number of Ships line
            content = read.readLine();
            lineNum++;
            int numShips = decodeTotalShips(content);

            // encoded ships lines
            if (numShips != 0) {
                for (int index = 0; index < numShips; index++) {
                    content = read.readLine();
                    if (content.startsWith("ContainerShip") || content.startsWith("BulkCarrier")) {
                        try {
                            Ship.fromString(content);
                        } catch (BadEncodingException b) {
                            throw b;
                        }
                    } else {
                        throw new BadEncodingException("The number of ship does not match");
                    }
                }
            }

            // the number of Quays line
            content = read.readLine();
            lineNum++;
            int numQuays = decodeTotalQuays(content);

            List<Quay> portQuays = new ArrayList<>();
            // encoded quays lines
            if (numQuays != 0) {
                for (int index = 0; index < numQuays; index++) {
                    content = read.readLine();
                    if (content.startsWith("BulkQuay") || content.startsWith("ContainerQuay")) {
                        try {
                            portQuays.add(Quay.fromString(content));
                        } catch (BadEncodingException b) {
                            throw b;
                        }
                    } else {
                        throw new BadEncodingException("The number of quays does not match");
                    }
                }
            }

            // ShipQueue line
            content = read.readLine();
            lineNum++;
            ShipQueue ships = decodeShipQueue(content);

            // stored Cargo line
            content = read.readLine();
            lineNum++;
            List<Cargo> cargos = decodeStoredCargo(content);

            port = new Port(portName, portTime, ships, portQuays, cargos);

            // movements line
            content = read.readLine();
            lineNum++;
            int numMovements = decodeMovements(content);

            // encoded movements lines
            if (numMovements != 0) {
                for (int index = 0; index < numMovements; index++) {
                    content = read.readLine();
                    if (content.startsWith("ShipMovement")) {
                        port.addMovement(ShipMovement.fromString(content));
                    } else if (content.startsWith("CargoMovement")) {
                        port.addMovement(CargoMovement.fromString(content));
                    } else {
                        throw new BadEncodingException("The number of movements does not match");
                    }
                }
            }

            // Evaluators line
            content = read.readLine();
            lineNum++;
            decodeEvaluators(content, port);
        } catch (IOException i) {
            throw i;
        }

        // missing something for necessary lines
        if (lineNum != 9) {
            throw new BadEncodingException("missing something");
        }
        return port;
    }

    /**
     * decode the time from reader
     *
     * @param content the content of read line
     * @return the time of simulation
     * @throws BadEncodingException if the time is not a valid long or the time is less than
     * zero
     */
    private static long decodeTime(String content) throws BadEncodingException {
        long portTime;

        try {
            portTime = Long.parseLong(content);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The time is not a valid long", n);
        }

        if (portTime < 0) {
            throw new BadEncodingException("The time is less than zero");
        }
        return portTime;
    }

    /**
     * decode the number of ships in the simulation from reader
     *
     * @param content the content of read line
     * @return the number of ships in the simulation
     * @throws BadEncodingException if The number of ships is not an integer or The number
     * of ship to be read in does not match the number specified above
     */
    private static int decodeTotalShips(String content) throws BadEncodingException {
        if (content.startsWith("Container") || content.startsWith("BulkCargo")) {
            throw new BadEncodingException("The number of cargo doesn't match");
        }

        int numShips;
        try {
            numShips = Integer.parseInt(content);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The number of ships is not an integer", n);
        }
        return numShips;
    }

    /**
     * decode the number of quays in the port from reader
     *
     * @param content the content of read line
     * @return the number of quays in the port
     * @throws BadEncodingException if The number of quays is not an integer or The number
     * of quays to be read in does not match the number specified above
     */
    private static int decodeTotalQuays(String content) throws BadEncodingException {
        if (content.startsWith("ContainerShip") || content.startsWith("BulkCarrier")) {
            throw new BadEncodingException("The number of ship does not match");
        }

        int numQuays;
        try {
            numQuays = Integer.parseInt(content);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The number of quays is not an integer", n);
        }
        return numQuays;
    }

    /**
     * decode the ship queue from reader
     *
     * @param content the content of read line
     * @return the ship queue in the simulation
     * @throws BadEncodingException if The shipQueue does not follow the last encoded quay,
     * The number of ships in the shipQueue is not an integer, The imoNumber of the ships in
     * the shipQueue are not valid longs, Any imoNumber read does not correspond to a valid
     * ship in the simulation or The number of quays does not match
     */
    private static ShipQueue decodeShipQueue(String content) throws BadEncodingException {
        if (content.startsWith("BulkQuay") || content.startsWith("ContainerQuay")) {
            throw new BadEncodingException("The number of quays does not match");
        }
        if (!content.startsWith("ShipQueue")) {
            throw new BadEncodingException("The shipQueue does not follow encoded quay");
        }
        String[] pairs = content.split(":");
        if (!pairs[0].equals("ShipQueue")) {
            throw new BadEncodingException("The shipQueue does not follow encoded quay");
        }
        ShipQueue ships = new ShipQueue();

        int shipsWaiting;
        try {
            shipsWaiting = Integer.parseInt(pairs[1]);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The number of ships - shipQueue not integer", n);
        }

        if (shipsWaiting != 0) {
            String[] imoNumbers = pairs[2].split(",");
            long imoNumber;
            for (String imo : imoNumbers) {
                try {
                    imoNumber = Long.parseLong(imo);
                } catch (NumberFormatException n) {
                    throw new BadEncodingException("imoNumber of ships not valid longs", n);
                }
                try {
                    ships.add(Ship.getShipByImoNumber(imoNumber));
                } catch (NoSuchShipException n) {
                    throw new BadEncodingException("Any imoNumber not a valid ship", n);
                }
            }
        }
        return ships;
    }

    /**
     * decode the list of stored cargo from reader
     *
     * @param content the content of read line
     * @return the list of stored cargo in the simulation
     * @throws BadEncodingException if The storedCargo does not follow the encoded
     * shipQueue, The number of cargo in the storedCargo is not an integer , The id of the
     * cargo in the storedCargo are not valid Integers, Any cargo id read does not
     * correspond to a valid cargo in the simulation
     */
    private static List<Cargo> decodeStoredCargo(String content) throws BadEncodingException {
        if (!content.startsWith("StoredCargo")) {
            throw new BadEncodingException("The storedCargo not follow shipQueue");
        }
        String[] pairs = content.split(":");
        if (!pairs[0].equals("StoredCargo")) {
            throw new BadEncodingException("The storedCargo not follow shipQueue");
        }

        int numStoredCargos;
        try {
            numStoredCargos = Integer.parseInt(pairs[1]);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The number of cargo not an integer", n);
        }

        List<Cargo> cargos = new ArrayList<>();
        if (numStoredCargos != 0) {
            String[] cargoIds = pairs[2].split(",");
            int cargoId;
            for (String id : cargoIds) {
                try {
                    cargoId = Integer.parseInt(id);
                } catch (NumberFormatException n) {
                    throw new BadEncodingException("The id of the cargo not valid Integers");
                }

                try {
                    cargos.add(Cargo.getCargoById(cargoId));
                } catch (NoSuchCargoException n) {
                    throw new BadEncodingException("Any cargo id not a valid cargo", n);
                }
            }
        }
        return cargos;
    }

    /**
     * decode the number of movements from reader
     *
     * @param content the content of read line
     * @return the number of movements in the simulation
     * @throws BadEncodingException if The movements do not follow the encoded storedCargo or
     * The number of movements is not an integer
     */
    private static int decodeMovements(String content) throws BadEncodingException {
        if (!content.startsWith("Movements")) {
            throw new BadEncodingException("The movements not follow the storedCargo");
        }
        String[] pairs = content.split(":");
        if (!pairs[0].equals("Movements")) {
            throw new BadEncodingException("The movements not follow the storedCargo");
        }

        int numMovements;
        try {
            numMovements = Integer.parseInt(pairs[1]);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The number of movements is not an integer", n);
        }
        return numMovements;
    }

    /**
     * decode the list of evaluators from reader
     *
     * @param content the content of read line
     * @throws BadEncodingException if The number of movements does not match, The
     * evaluators do not follow the encoded movements, The number of evaluators is not an
     * integer, The number of evaluators to be read in does not match the number specified
     * above or An encoded evaluator name does not match any of the possible evaluator
     * classes
     */
    private static void  decodeEvaluators(String content, Port port) throws BadEncodingException {
        if (content.startsWith("ShipMovement") || content.startsWith("CargoMovement")) {
            throw new BadEncodingException("The number of movements does not match");
        }
        if (!content.startsWith("Evaluators")) {
            throw new BadEncodingException("The evaluators not follow the movements");
        }

        String[] pairs = content.split(":");
        if (!pairs[0].equals("Evaluators")) { // the number of evaluators
            throw new BadEncodingException("The evaluators not follow the movements");
        }
        int numEvals;
        try {
            numEvals = Integer.parseInt(pairs[1]);
        } catch (NumberFormatException n) {
            throw new BadEncodingException("The number of evaluators is not an integer");
        }
        if (numEvals != 0) {
            if (pairs[2].split(",").length != numEvals) {
                throw new BadEncodingException("The number of evaluators does not match");
            }
            for (String eval : pairs[2].split(",")) {
                if (eval.equals("ShipThroughputEvaluator")) {
                    port.addStatisticsEvaluator(new ShipThroughputEvaluator());
                } else if (eval.equals("ShipFlagEvaluator")) {
                    port.addStatisticsEvaluator(new ShipFlagEvaluator());
                } else if (eval.equals("QuayOccupancyEvaluator")) {
                    port.addStatisticsEvaluator(new QuayOccupancyEvaluator(port));
                } else if (eval.equals("CargoDecompositionEvaluator")) {
                    port.addStatisticsEvaluator(new CargoDecompositionEvaluator());
                } else {
                    throw new BadEncodingException("An evaluator name doesn't match");
                }
            }
        }
    }
}
