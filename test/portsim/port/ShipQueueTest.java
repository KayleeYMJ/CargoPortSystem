package portsim.port;

import org.junit.*;
import portsim.ship.*;
import portsim.util.BadEncodingException;
import java.util.List;
import static org.junit.Assert.*;

public class ShipQueueTest {

    ShipQueue shipQueue;

    // ContainerShip
    ContainerShip defaultContainerShip;
    ContainerShip dangerousContainerShip;
    ContainerShip medicalContainerShip;
    ContainerShip readyContainerShip;
    ContainerShip defaultContainerShipA;
    ContainerShip dangerousContainerShipA;
    ContainerShip medicalContainerShipA;
    ContainerShip readyContainerShipA;

    // BulkCarrier
    BulkCarrier defaultBulkCarrier;
    BulkCarrier dangerousBulkCarrier;
    BulkCarrier medicalBulkCarrier;
    BulkCarrier readyBulkCarrier;
    BulkCarrier defaultBulkCarrierA;
    BulkCarrier dangerousBulkCarrierA;
    BulkCarrier medicalBulkCarrierA;
    BulkCarrier readyBulkCarrierA;

    @Before
    public void setUp() throws Exception {
        shipQueue = new ShipQueue();

        // ContainerShip
        defaultContainerShip = new ContainerShip(1234567, "Perfect", "US",
                NauticalFlag.NOVEMBER, 100);
        dangerousContainerShip = new ContainerShip(1234560, "Perfect", "US",
                NauticalFlag.BRAVO, 100);
        medicalContainerShip = new ContainerShip(1234561, "Perfect", "US",
                NauticalFlag.WHISKEY, 100);
        readyContainerShip = new ContainerShip(1234562, "Perfect", "US",
                NauticalFlag.HOTEL, 100);
        defaultContainerShipA = new ContainerShip(1434567, "Perfect", "US",
                NauticalFlag.NOVEMBER, 100);
        dangerousContainerShipA = new ContainerShip(1534560, "Perfect", "US",
                NauticalFlag.BRAVO, 100);
        medicalContainerShipA = new ContainerShip(1634561, "Perfect", "US",
                NauticalFlag.WHISKEY, 100);
        readyContainerShipA = new ContainerShip(1734562, "Perfect", "US",
                NauticalFlag.HOTEL, 100);

        // BulkCarrier
        defaultBulkCarrier = new BulkCarrier(7654321, "Perfect", "US",
                NauticalFlag.NOVEMBER, 100);
        dangerousBulkCarrier = new BulkCarrier(7654320, "Perfect", "US",
                NauticalFlag.BRAVO, 100);
        medicalBulkCarrier = new BulkCarrier(7654322, "Perfect", "US",
                NauticalFlag.WHISKEY, 100);
        readyBulkCarrier = new BulkCarrier(7654323, "Perfect", "US",
                NauticalFlag.HOTEL, 100);
        defaultBulkCarrierA = new BulkCarrier(7154321, "Perfect", "US",
                NauticalFlag.NOVEMBER, 100);
        dangerousBulkCarrierA = new BulkCarrier(7254320, "Perfect", "US",
                NauticalFlag.BRAVO, 100);
        medicalBulkCarrierA = new BulkCarrier(7354322, "Perfect", "US",
                NauticalFlag.WHISKEY, 100);
        readyBulkCarrierA = new BulkCarrier(7454323, "Perfect", "US",
                NauticalFlag.HOTEL, 100);
    }

    @After
    public void tearDown() throws Exception {
        Ship.resetShipRegistry();
    }

    @Test
    public void constructorTest() {
        assertEquals(0, shipQueue.getShipQueue().size());
    }

    @Test
    public void pollTest() {
        shipQueue.add(defaultContainerShip);
        shipQueue.add(defaultBulkCarrier);
        assertEquals("shipQueue.size() != 2", 2, shipQueue.getShipQueue().size());
        Ship ship = shipQueue.poll();
        assertEquals("poll() != defaultContainerShip", defaultContainerShip, ship);
        assertEquals("shipQueue.size() != 1", 1, shipQueue.getShipQueue().size());
        assertEquals("shipQueue.getShipQueue().get(0) != defaultBulkCarrier",
                defaultBulkCarrier, shipQueue.getShipQueue().get(0));
    }

    @Test
    public void peekDangerousCargoTest() {
        shipQueue.add(defaultBulkCarrier);
        shipQueue.add(defaultContainerShip);
        shipQueue.add(readyBulkCarrier);
        shipQueue.add(readyContainerShip);
        shipQueue.add(medicalBulkCarrier);
        shipQueue.add(medicalContainerShip);
        shipQueue.add(dangerousBulkCarrierA);
        shipQueue.add(dangerousContainerShipA);
        shipQueue.add(dangerousBulkCarrier);
        shipQueue.add(dangerousContainerShip);

        // peek() cannot change the shipQueue
        assertEquals("shipQueue.size() != 10", 10, shipQueue.getShipQueue().size());
        Ship ship = shipQueue.peek();
        assertEquals("poll() != dangerousBulkCarrierA", dangerousBulkCarrierA, ship);
        assertEquals("shipQueue.size() != 10", 10, shipQueue.getShipQueue().size());
        shipQueue.poll();

        // get first dangerous ship and remove
        ship = shipQueue.peek();
        assertEquals("poll() != dangerousContainerShipA", dangerousContainerShipA, ship);
        assertEquals("shipQueue.size() != 9", 9, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != dangerousBulkCarrier", dangerousBulkCarrier, ship);
        assertEquals("shipQueue.size() != 8", 8, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != dangerousContainerShip", dangerousContainerShip, ship);
        assertEquals("shipQueue.size() != 7", 7, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != medicalBulkCarrier", medicalBulkCarrier, ship);
        assertEquals("shipQueue.size() != 6", 6, shipQueue.getShipQueue().size());

        // add dangerous ship in the queue(no dangerous ship)
        shipQueue.add(dangerousBulkCarrierA);
        assertEquals(dangerousBulkCarrierA, shipQueue.peek());
    }

    @Test
    public void peekMedicalAssistanceTest() {
        shipQueue.add(defaultBulkCarrier);
        shipQueue.add(defaultContainerShip);
        shipQueue.add(readyBulkCarrier);
        shipQueue.add(readyContainerShip);
        shipQueue.add(medicalBulkCarrier);
        shipQueue.add(medicalContainerShip);
        shipQueue.add(medicalBulkCarrierA);
        shipQueue.add(medicalContainerShipA);

        // peek() cannot change the shipQueue
        assertEquals("shipQueue.size() != 8", 8, shipQueue.getShipQueue().size());
        Ship ship = shipQueue.peek();
        assertEquals("poll() != medicalBulkCarrier", medicalBulkCarrier, ship);
        assertEquals("shipQueue.size() != 8", 8, shipQueue.getShipQueue().size());
        shipQueue.poll();

        // get first medical ship and remove
        ship = shipQueue.peek();
        assertEquals("poll() != medicalContainerShip", medicalContainerShip, ship);
        assertEquals("shipQueue.size() != 7", 7, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != medicalBulkCarrierA", medicalBulkCarrierA, ship);
        assertEquals("shipQueue.size() != 6", 6, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != medicalContainerShipA", medicalContainerShipA, ship);
        assertEquals("shipQueue.size() != 5", 5, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != readyBulkCarrier", readyBulkCarrier, ship);
        assertEquals("shipQueue.size() != 4", 4, shipQueue.getShipQueue().size());

        // add medical ship in the queue(no medical ship)
        shipQueue.add(medicalBulkCarrier);
        assertEquals(medicalBulkCarrier, shipQueue.peek());
    }

    @Test
    public void peekReadyDockedTest() {
        shipQueue.add(defaultBulkCarrier);
        shipQueue.add(defaultContainerShip);
        shipQueue.add(readyBulkCarrier);
        shipQueue.add(readyContainerShip);
        shipQueue.add(readyBulkCarrierA);
        shipQueue.add(readyContainerShipA);

        // peek() cannot change the shipQueue
        assertEquals("shipQueue.size() != 6", 6, shipQueue.getShipQueue().size());
        Ship ship = shipQueue.peek();
        assertEquals("poll() != readyBulkCarrier", readyBulkCarrier, ship);
        assertEquals("shipQueue.size() != 6", 6, shipQueue.getShipQueue().size());
        shipQueue.poll();

        // get first ready ship and remove
        ship = shipQueue.peek();
        assertEquals("poll() != readyContainerShip", readyContainerShip, ship);
        assertEquals("shipQueue.size() != 5", 5, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != readyBulkCarrierA", readyBulkCarrierA, ship);
        assertEquals("shipQueue.size() != 4", 4, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != readyContainerShipA", readyContainerShipA, ship);
        assertEquals("shipQueue.size() != 3", 3, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != defaultContainerShip", defaultContainerShip, ship);
        assertEquals("shipQueue.size() != 2", 2, shipQueue.getShipQueue().size());

        // add ready ship in the queue(no ready ship)
        shipQueue.add(readyBulkCarrierA);
        assertEquals(readyBulkCarrierA, shipQueue.peek());
    }

    @Test
    public void peekContainerShipTest() {
        shipQueue.add(defaultBulkCarrier);
        shipQueue.add(defaultContainerShip);
        shipQueue.add(defaultBulkCarrierA);
        shipQueue.add(defaultContainerShipA);

        // peek() cannot change the shipQueue
        assertEquals("shipQueue.size() != 4", 4, shipQueue.getShipQueue().size());
        Ship ship = shipQueue.peek();
        assertEquals("poll() != defaultContainerShip", defaultContainerShip, ship);
        assertEquals("shipQueue.size() != 4", 4, shipQueue.getShipQueue().size());
        shipQueue.poll();

        // get first container ship and remove
        ship = shipQueue.peek();
        assertEquals("poll() != defaultContainerShipA", defaultContainerShipA, ship);
        assertEquals("shipQueue.size() != 3", 3, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != defaultBulkCarrier", defaultBulkCarrier, ship);
        assertEquals("shipQueue.size() != 2", 2, shipQueue.getShipQueue().size());

        // add container ship in the queue(no container ship)
        shipQueue.add(defaultContainerShip);
        assertEquals(defaultContainerShip, shipQueue.peek());
    }

    @Test
    public void peekNoShipReturnedTest() {
        shipQueue.add(defaultBulkCarrier);
        shipQueue.add(defaultBulkCarrierA);

        // peek() cannot change the shipQueue
        assertEquals("shipQueue.size() != 2", 2, shipQueue.getShipQueue().size());
        Ship ship = shipQueue.peek();
        assertEquals("poll() != defaultBulkCarrier", defaultBulkCarrier, ship);
        assertEquals("shipQueue.size() != 2", 2, shipQueue.getShipQueue().size());
        shipQueue.poll();

        // get first ship and remove
        ship = shipQueue.peek();
        assertEquals("poll() != defaultBulkCarrierA", defaultBulkCarrierA, ship);
        assertEquals("shipQueue.size() != 1", 1, shipQueue.getShipQueue().size());

        // add a ship in the empty queue
        shipQueue.poll();
        assertEquals("shipQueue.size() != 0", 0, shipQueue.getShipQueue().size());
        shipQueue.add(defaultBulkCarrierA);
        assertEquals(defaultBulkCarrierA, shipQueue.peek());
    }

    @Test
    public void peekNullTest() {
        // initial is empty
        assertEquals(null, shipQueue.peek());

        // empty after add and remove
        shipQueue.add(defaultContainerShip);
        assertEquals(1, shipQueue.getShipQueue().size());
        shipQueue.poll();
        assertEquals(null, shipQueue.peek());
    }

    @Test
    public void peekAllConditionsShipTest() {
        shipQueue.add(defaultBulkCarrier);
        shipQueue.add(defaultContainerShip);
        shipQueue.add(defaultBulkCarrierA);
        shipQueue.add(defaultContainerShipA);
        shipQueue.add(readyBulkCarrier);
        shipQueue.add(readyContainerShip);
        shipQueue.add(readyBulkCarrierA);
        shipQueue.add(readyContainerShipA);
        shipQueue.add(medicalBulkCarrier);
        shipQueue.add(medicalContainerShip);
        shipQueue.add(medicalBulkCarrierA);
        shipQueue.add(medicalContainerShipA);
        shipQueue.add(dangerousBulkCarrierA);
        shipQueue.add(dangerousContainerShipA);
        shipQueue.add(dangerousBulkCarrier);
        shipQueue.add(dangerousContainerShip);

        // remove
        // carrying dangerous cargo
        assertEquals("shipQueue.size() != 16", 16, shipQueue.getShipQueue().size());
        Ship ship = shipQueue.peek();
        assertEquals("poll() != dangerousBulkCarrierA", dangerousBulkCarrierA, ship);
        assertEquals("shipQueue.size() != 16", 16, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != dangerousContainerShipA", dangerousContainerShipA, ship);
        assertEquals("shipQueue.size() != 15", 15, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != dangerousBulkCarrier", dangerousBulkCarrier, ship);
        assertEquals("shipQueue.size() != 14", 14, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != dangerousContainerShip", dangerousContainerShip, ship);
        assertEquals("shipQueue.size() != 13", 13, shipQueue.getShipQueue().size());
        shipQueue.poll();

        // requiring medical assistance
        ship = shipQueue.peek();
        assertEquals("poll() != medicalBulkCarrier", medicalBulkCarrier, ship);
        assertEquals("shipQueue.size() != 12", 12, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != medicalContainerShip", medicalContainerShip, ship);
        assertEquals("shipQueue.size() != 11", 11, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != medicalBulkCarrierA", medicalBulkCarrierA, ship);
        assertEquals("shipQueue.size() != 10", 10, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != medicalContainerShipA", medicalContainerShipA, ship);
        assertEquals("shipQueue.size() != 9", 9, shipQueue.getShipQueue().size());
        shipQueue.poll();

        // ready to be docked
        ship = shipQueue.peek();
        assertEquals("poll() != readyBulkCarrier", readyBulkCarrier, ship);
        assertEquals("shipQueue.size() != 8", 8, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != readyContainerShip", readyContainerShip, ship);
        assertEquals("shipQueue.size() != 7", 7, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != readyBulkCarrierA", readyBulkCarrierA, ship);
        assertEquals("shipQueue.size() != 6", 6, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != readyContainerShipA", readyContainerShipA, ship);
        assertEquals("shipQueue.size() != 5", 5, shipQueue.getShipQueue().size());
        shipQueue.poll();

        // container ship
        ship = shipQueue.peek();
        assertEquals("poll() != defaultContainerShip", defaultContainerShip, ship);
        assertEquals("shipQueue.size() != 4", 4, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != defaultContainerShipA", defaultContainerShipA, ship);
        assertEquals("shipQueue.size() != 3", 3, shipQueue.getShipQueue().size());
        shipQueue.poll();

        // no returned ship
        ship = shipQueue.peek();
        assertEquals("poll() != defaultBulkCarrier", defaultBulkCarrier, ship);
        assertEquals("shipQueue.size() != 2", 2, shipQueue.getShipQueue().size());
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("poll() != defaultBulkCarrierA", defaultBulkCarrierA, ship);
        assertEquals("shipQueue.size() != 1", 1, shipQueue.getShipQueue().size());

        // no ship in the queue
        shipQueue.poll();
        ship = shipQueue.peek();
        assertEquals("no ship", null, shipQueue.peek());
        assertEquals("shipQueue.size() != 0", 0, shipQueue.getShipQueue().size());
    }

    @Test
    public void addTest() {
        assertEquals(null, shipQueue.peek());
        assertEquals(0, shipQueue.getShipQueue().size());
        shipQueue.add(defaultContainerShip);
        assertEquals(defaultContainerShip, shipQueue.peek());
        assertEquals(1, shipQueue.getShipQueue().size());
    }

    @Test
    public void getShipQueueTest() {
        // empty
        List<Ship> queue = shipQueue.getShipQueue();
        assertEquals(0, queue.size());

        // store all added ships in the queue
        shipQueue.add(defaultContainerShip);
        queue = shipQueue.getShipQueue();
        assertEquals(1,queue.size());
        assertEquals(defaultContainerShip, queue.get(0));
        shipQueue.add(readyBulkCarrier);
        queue = shipQueue.getShipQueue();
        assertEquals(2,queue.size());

        // order is the adding order
        assertEquals(defaultContainerShip, queue.get(0));
        assertEquals(readyBulkCarrier, queue.get(1));

        // cannot store all removed ships in the queue
        shipQueue.poll();
        queue = shipQueue.getShipQueue();
        assertEquals(1, queue.size());
        assertEquals(defaultContainerShip, queue.get(0));

        // Adding or removing elements from the returned list should not affect the original list
        queue.add(defaultContainerShip);
        assertEquals(2, queue.size());
        assertEquals(1, shipQueue.getShipQueue().size());
        queue.remove(0);
        assertEquals(1,queue.size());
        assertEquals(1,shipQueue.getShipQueue().size());
    }

    // two empty ship queue
    @Test
    public void equalsTest1() {
        ShipQueue shipQueueA = new ShipQueue();
        assertEquals(shipQueue, shipQueueA);
    }

    // same non-empty ship queue with same order
    @Test
    public void equalsTest2() {
        shipQueue.add(defaultContainerShip);
        shipQueue.add(dangerousContainerShip);
        ShipQueue shipQueueA = new ShipQueue();
        shipQueueA.add(defaultContainerShip);
        shipQueueA.add(dangerousContainerShip);
        assertTrue(shipQueue.equals(shipQueueA));
    }

    // ship queues with different orders of the same ships waiting
    @Test
    public void equalsTest3() {
        shipQueue.add(defaultContainerShip);
        shipQueue.add(dangerousContainerShip);
        shipQueue.add(readyContainerShip);
        ShipQueue shipQueueA = new ShipQueue();
        shipQueueA.add(dangerousContainerShip);
        shipQueueA.add(readyContainerShip);
        shipQueueA.add(defaultContainerShip);
        assertFalse(shipQueue.equals(shipQueueA));
    }

    // ship queues with different ships(the number of ship is same) waiting
    @Test
    public void equalsTest4() {
        shipQueue.add(defaultContainerShip);
        shipQueue.add(dangerousContainerShip);
        shipQueue.add(medicalContainerShip);
        ShipQueue shipQueueA = new ShipQueue();
        shipQueueA.add(defaultContainerShipA);
        shipQueueA.add(dangerousContainerShip);
        shipQueueA.add(medicalContainerShip);
        assertFalse(shipQueue.equals(shipQueueA));
    }

    // ship queues with different ships(the number of ship differ) waiting
    @Test
    public void equalsTest5() {
        shipQueue.add(defaultContainerShip);
        shipQueue.add(dangerousContainerShip);
        shipQueue.add(medicalContainerShip);
        ShipQueue shipQueueA = new ShipQueue();
        shipQueueA.add(defaultContainerShip);
        shipQueueA.add(dangerousContainerShip);
        assertFalse(shipQueue.equals(shipQueueA));

    }

    // ship queue(arrive ships) and ship
    @Test
    public void equalsTest6() {
        assertFalse(shipQueue.equals(defaultContainerShip));
    }

    // ship queue(no ships docked) and ship
    @Test
    public void equalsTest7() {
        assertFalse(shipQueue.equals(defaultBulkCarrier));
    }

    // ship queue(no ships docked) and ship queue(arrive ships)
    @Test
    public void equalsTest8() {
        ShipQueue shipQueueA = new ShipQueue();
        shipQueueA.add(readyContainerShip);
        assertFalse(shipQueue.equals(shipQueueA));
    }

    // two empty ship queue
    @Test
    public void hashCodeTest1() {
        ShipQueue shipQueueA = new ShipQueue();
        assertEquals(shipQueue.hashCode(), shipQueueA.hashCode());
    }

    // same non-empty ship queue with same order
    @Test
    public void hashCodeTest2() {
        shipQueue.add(defaultContainerShip);
        shipQueue.add(dangerousContainerShip);
        ShipQueue shipQueueA = new ShipQueue();
        shipQueueA.add(defaultContainerShip);
        shipQueueA.add(dangerousContainerShip);
        assertEquals(shipQueue.hashCode(), shipQueueA.hashCode());
    }

    // ship queues with different orders of the same ships waiting
    @Test
    public void hashCodeTest3() {
        shipQueue.add(defaultContainerShip);
        shipQueue.add(dangerousContainerShip);
        shipQueue.add(readyContainerShip);
        ShipQueue shipQueueA = new ShipQueue();
        shipQueueA.add(dangerousContainerShip);
        shipQueueA.add(readyContainerShip);
        shipQueueA.add(defaultContainerShip);
        assertFalse(shipQueue.hashCode() == shipQueueA.hashCode());
    }

    // ship queues with different ships(the number of ship is same) waiting
    @Test
    public void hashCodeTest4() {
        shipQueue.add(defaultContainerShip);
        shipQueue.add(dangerousContainerShip);
        shipQueue.add(medicalContainerShip);
        ShipQueue shipQueueA = new ShipQueue();
        shipQueueA.add(defaultContainerShipA);
        shipQueueA.add(dangerousContainerShip);
        shipQueueA.add(medicalContainerShip);
        assertFalse(shipQueue.hashCode() == shipQueueA.hashCode());
    }

    // ship queues with different ships(the number of ship differ) waiting
    @Test
    public void hashCodeTest5() {
        shipQueue.add(defaultContainerShip);
        shipQueue.add(dangerousContainerShip);
        shipQueue.add(medicalContainerShip);
        ShipQueue shipQueueA = new ShipQueue();
        shipQueueA.add(defaultContainerShip);
        shipQueueA.add(dangerousContainerShip);
        assertFalse(shipQueue.hashCode() == shipQueueA.hashCode());
    }

    // ship queue(arrive ships) and ship
    @Test
    public void hashCodeTest6() {
        assertFalse(shipQueue.hashCode() == defaultContainerShip.hashCode());
    }

    // ship queue(no ships docked) and ship
    @Test
    public void hashCodeTest7() {
        assertFalse(shipQueue.hashCode() == defaultBulkCarrier.hashCode());
    }

    // ship queue(no ships docked) and ship queue(arrive ships)
    @Test
    public void hashCodeTest8() {
        ShipQueue shipQueueA = new ShipQueue();
        shipQueueA.add(readyContainerShip);
        assertFalse(shipQueue.hashCode() == shipQueueA.hashCode());
    }

    @Test
    public void encodeTest() {
        assertEquals("ShipQueue:0:", shipQueue.encode());
        shipQueue.add(defaultContainerShip);
        shipQueue.add(dangerousContainerShip);
        assertEquals("ShipQueue:2:1234567,1234560", shipQueue.encode());
    }

    @Test
    public void fromStringNoShipTest() throws BadEncodingException {
        assertEquals(shipQueue, ShipQueue.fromString("ShipQueue:0:"));
    }

    @Test
    public void fromStringArrivedShipTest() throws BadEncodingException {
        shipQueue.add(dangerousContainerShip);
        shipQueue.add(defaultContainerShip);
        assertEquals(shipQueue, ShipQueue.fromString("ShipQueue:2:1234560,1234567"));
        assertEquals(dangerousContainerShip, shipQueue.poll());
        assertEquals(defaultContainerShip, shipQueue.peek());
        assertEquals(1, shipQueue.getShipQueue().size());
    }

    // start with "ShipQueue"
    @Test(expected = BadEncodingException.class)
    public void fromStringExceptionTest1() throws BadEncodingException {
        assertEquals(shipQueue, ShipQueue.fromString("shipQueue:0:"));
    }

    // the number of colons
    // more one colon at the end
    @Test(expected = BadEncodingException.class)
    public void fromStringExceptionTest2() throws BadEncodingException {
        assertEquals(shipQueue, ShipQueue.fromString("ShipQueue:0::"));
    }

    @Test(expected = BadEncodingException.class)
    public void fromStringExceptionTest12() throws BadEncodingException {
        assertEquals(shipQueue, ShipQueue.fromString("ShipQueue:1:2:1234567:1237689"));
    }

    // more one colon at the beginning
    @Test (expected = BadEncodingException.class)
    public void fromStringExceptionTest3() throws BadEncodingException {
        assertEquals(shipQueue, ShipQueue.fromString(":ShipQueue:0:"));
    }

    // more one colon in the line
    @Test (expected = BadEncodingException.class)
    public void fromStringExceptionTest4() throws BadEncodingException {
        assertEquals(shipQueue, ShipQueue.fromString("ShipQueue::0:"));
    }

    // less one colon at the end
    @Test (expected = BadEncodingException.class)
    public void fromStringExceptionTest5() throws BadEncodingException {
        assertEquals(shipQueue, ShipQueue.fromString("ShipQueue:0"));
    }

    // less one colon in the line
    @Test (expected = BadEncodingException.class)
    public void fromStringExceptionTest6() throws BadEncodingException {
        assertEquals(shipQueue, ShipQueue.fromString("ShipQueue - 0:"));
    }

    // the number of ships in the shipQueue is not an integer
    @Test (expected = BadEncodingException.class)
    public void fromStringExceptionTest7() throws BadEncodingException {
        assertEquals(shipQueue, ShipQueue.fromString("ShipQueue:0.2:"));
    }

    // the number of ships does not match the number specified(no arrived ship)
    @Test (expected = BadEncodingException.class)
    public void fromStringExceptionTest8() throws BadEncodingException {
       assertEquals(shipQueue, ShipQueue.fromString("ShipQueue:0:1234567"));
    }

    // the number of ships does not match the number specified(has arrived ship)
    @Test (expected = BadEncodingException.class)
    public void fromStringExceptionTest9() throws BadEncodingException {
        assertEquals(shipQueue, ShipQueue.fromString("ShipQueue:2:1234567"));
    }

    // The imoNumber of the ships in the shipQueue are not valid longs
    @Test (expected = BadEncodingException.class)
    public void fromStringExceptionTest10() throws BadEncodingException {
        assertEquals(shipQueue, ShipQueue.fromString("ShipQueue:2:1234567, 123f"));
    }

    // Any imoNumber read does not correspond to a valid ship in the simulation
    @Test (expected = BadEncodingException.class)
    public void fromStringExceptionTest11() throws BadEncodingException {
        assertEquals(shipQueue, ShipQueue.fromString("ShipQueue:1:1004567"));
    }
}