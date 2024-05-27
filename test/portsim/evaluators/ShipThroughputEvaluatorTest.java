package portsim.evaluators;

import org.junit.*;
import portsim.cargo.*;
import portsim.movement.*;
import portsim.port.*;
import portsim.ship.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class ShipThroughputEvaluatorTest {

    ShipThroughputEvaluator evaluator;
    Port port;
    BulkCargo bulkCargoA;
    Container cargoA;
    BulkCarrier bulkCarrier;
    ContainerShip containerShip;

    @Before
    public void setUp() throws Exception {
        evaluator = new ShipThroughputEvaluator();
        port = new Port("a");
        cargoA = new Container(1,"US", ContainerType.TANKER);
        bulkCargoA = new BulkCargo(4, "China", 100, BulkCargoType.COAL);
        bulkCarrier = new BulkCarrier(1258691, "Perfect", "US",
                NauticalFlag.WHISKEY, 100);
        containerShip = new ContainerShip(1234567, "Perfect", "US",
                NauticalFlag.NOVEMBER, 100);
    }

    @After
    public void tearDown() throws Exception {
        Cargo.resetCargoRegistry();
        Ship.resetShipRegistry();
    }

    @Test
    public void constructorTest() {
        assertEquals(0, evaluator.getTime());
        assertEquals(0, evaluator.getThroughputPerHour());
    }

    // ShipMovement - OUTBOUND - depart less 60 mins
    @Test
    public void getThroughputPerHourTest1() {
        assertEquals(0, evaluator.getTime());
        assertEquals(0, evaluator.getThroughputPerHour());
        ShipMovement shipMovement = new ShipMovement(1, MovementDirection.OUTBOUND,
                bulkCarrier);
        evaluator.elapseOneMinute();
        evaluator.onProcessMovement(shipMovement);
        assertEquals(1, evaluator.getThroughputPerHour());
        for (int time = 0; time < 60; time++) {
            evaluator.elapseOneMinute();
        }
        assertEquals(1, evaluator.getThroughputPerHour());
    }

    // ShipMovement - OUTBOUND - depart more than 60 mins
    @Test
    public void getThroughputPerHourTest2() {
        assertEquals(0, evaluator.getTime());
        assertEquals(0, evaluator.getThroughputPerHour());
        ShipMovement shipMovement = new ShipMovement(1, MovementDirection.OUTBOUND,
                bulkCarrier);
        evaluator.elapseOneMinute();
        evaluator.onProcessMovement(shipMovement);
        assertEquals(1, evaluator.getThroughputPerHour());
        for (int time = 0; time <= 60; time++) {
            evaluator.elapseOneMinute();
        }
        assertEquals(0, evaluator.getThroughputPerHour());
    }

    // ShipMovement - INBOUND - depart less 60 mins
    @Test
    public void getThroughputPerHourTest3() {
        assertEquals(0, evaluator.getTime());
        assertEquals(0, evaluator.getThroughputPerHour());
        ShipMovement shipMovement = new ShipMovement(1, MovementDirection.INBOUND,
                bulkCarrier);
        evaluator.elapseOneMinute();
        evaluator.onProcessMovement(shipMovement);
        assertEquals(0, evaluator.getThroughputPerHour());
        for (int time = 0; time < 60; time++) {
            evaluator.elapseOneMinute();
        }
        assertEquals(0, evaluator.getThroughputPerHour());
    }

    // ShipMovement - INBOUND - depart more than 60 mins
    @Test
    public void getThroughputPerHourTest4() {
        assertEquals(0, evaluator.getTime());
        assertEquals(0, evaluator.getThroughputPerHour());
        ShipMovement shipMovement = new ShipMovement(1, MovementDirection.INBOUND,
                bulkCarrier);
        evaluator.elapseOneMinute();
        evaluator.onProcessMovement(shipMovement);
        assertEquals(0, evaluator.getThroughputPerHour());
        for (int time = 0; time <= 60; time++) {
            evaluator.elapseOneMinute();
        }
        assertEquals(0, evaluator.getThroughputPerHour());
    }

    // CargoMovement - INBOUND - depart less 60 mins
    @Test
    public void getThroughputPerHourTest5() {
        assertEquals(0, evaluator.getTime());
        assertEquals(0, evaluator.getThroughputPerHour());
        List<Cargo> containers = new ArrayList<>();
        containers.add(cargoA);
        CargoMovement movement = new CargoMovement(1, MovementDirection.INBOUND,
                containers);
        evaluator.elapseOneMinute();
        evaluator.onProcessMovement(movement);
        assertEquals(0, evaluator.getThroughputPerHour());
        for (int time = 0; time < 60; time++) {
            evaluator.elapseOneMinute();
        }
        assertEquals(0, evaluator.getThroughputPerHour());
    }

    // CargoMovement - OUTBOUND - depart less 60 mins
    @Test
    public void getThroughputPerHourTest6() {
        assertEquals(0, evaluator.getTime());
        assertEquals(0, evaluator.getThroughputPerHour());
        List<Cargo> containers = new ArrayList<>();
        containers.add(cargoA);
        CargoMovement movement = new CargoMovement(1, MovementDirection.OUTBOUND,
                containers);
        evaluator.elapseOneMinute();
        evaluator.onProcessMovement(movement);
        assertEquals(0, evaluator.getThroughputPerHour());
        for (int time = 0; time < 60; time++) {
            evaluator.elapseOneMinute();
        }
        assertEquals(0, evaluator.getThroughputPerHour());
    }

    // ShipMovement - OUTBOUND
    @Test
    public void onProcessMovementTest1() {
        assertEquals(0, evaluator.getTime());
        assertEquals(0, evaluator.getThroughputPerHour());
        ShipMovement shipMovement = new ShipMovement(1, MovementDirection.OUTBOUND,
                bulkCarrier);
        evaluator.elapseOneMinute();
        evaluator.onProcessMovement(shipMovement);
        assertEquals(1, evaluator.getThroughputPerHour());
    }

    // ShipMovement - INBOUND
    @Test
    public void onProcessMovementTest2() {
        assertEquals(0, evaluator.getTime());
        assertEquals(0, evaluator.getThroughputPerHour());
        ShipMovement shipMovement = new ShipMovement(1, MovementDirection.INBOUND,
                bulkCarrier);
        evaluator.elapseOneMinute();
        evaluator.onProcessMovement(shipMovement);
        assertEquals(0, evaluator.getThroughputPerHour());
    }

    // CargoMovement - INBOUND
    @Test
    public void onProcessMovementTest3() {
        assertEquals(0, evaluator.getTime());
        assertEquals(0, evaluator.getThroughputPerHour());
        List<Cargo> containers = new ArrayList<>();
        CargoMovement movement = new CargoMovement(1, MovementDirection.INBOUND,
                containers);
        evaluator.elapseOneMinute();
        evaluator.onProcessMovement(movement);
        assertEquals(0, evaluator.getThroughputPerHour());
    }

    // CargoMovement - OUTBOUND
    @Test
    public void onProcessMovementTest4() {
        assertEquals(0, evaluator.getTime());
        assertEquals(0, evaluator.getThroughputPerHour());
        List<Cargo> containers = new ArrayList<>();
        CargoMovement movement = new CargoMovement(1, MovementDirection.OUTBOUND,
                containers);
        evaluator.elapseOneMinute();
        evaluator.onProcessMovement(movement);
        assertEquals(0, evaluator.getThroughputPerHour());
    }

    // time + 1
    @Test
    public void elapseOneMinuteTest1() {
        assertEquals(0, evaluator.getTime());
        evaluator.elapseOneMinute();
        assertEquals(1, evaluator.getTime());
    }

    // more than 60 minutes since a ship exited the port
    @Test
    public void elapseOneMinuteTest2() {
        assertEquals(0, evaluator.getTime());
        assertEquals(0, evaluator.getThroughputPerHour());
        ShipMovement shipMovement = new ShipMovement(1, MovementDirection.OUTBOUND,
                bulkCarrier);
        evaluator.elapseOneMinute();
        evaluator.onProcessMovement(shipMovement);
        assertEquals(1, evaluator.getThroughputPerHour());
        for (int time = 0; time < 60; time++) {
            evaluator.elapseOneMinute();
        }
        assertEquals(1, evaluator.getThroughputPerHour());
        evaluator.elapseOneMinute();
        assertEquals(0, evaluator.getThroughputPerHour());
    }

    @Test
    public void getTimeTest() {
        assertEquals(0, evaluator.getTime());
        ShipMovement shipMovement = new ShipMovement(1, MovementDirection.OUTBOUND,
                bulkCarrier);
        evaluator.elapseOneMinute();
        evaluator.onProcessMovement(shipMovement);
        assertEquals(1,evaluator.getTime());
    }
}