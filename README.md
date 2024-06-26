# CargoPortSystem
A cargo port system manages the movement of ships around a port, including loading and unloading ships at quays, storing cargo at the port temporarily, and managing a queue of ships waiting offshore to dock at the port. The system also keeps track of statistics relating to the port’s operations.

A port is made up of several quays2, which are designed to accommodate specific types of ships.   Ships are separated into two types: container ships and bulk carriers.   Container ships carry ship- ping containers of various types, while bulk carriers are designed to hold large quantities of bulk cargo, such as grain, minerals, or oil.

Each ship has various characteristics which are known to the port system, including the ship’s name, unique International Maritime Organisation (IMO) number, country of origin, nautical flag, and cargo capacity.   These characteristics determine whether the ship is able to dock at each of the port’s quays, which cargo can be loaded onto the ship, and whether the ship will be prioritised in the queue of ships waiting to dock.

The port system keeps track of all cargo that enters and exits the port.   Each piece of cargo is identified by a unique identifer, and has a destination associated with it.   Cargo is classified as either a shipping container or bulk cargo.

To represent the actions of ships and cargo moving into and out of the port, the port system uses Movement classes, specifically ShipMovement and CargoMovement.   These movements store the direction of the movement (inbound or outbound), the time at which it is occurring and the cargo/ship that is being moved.

An inbound cargo movement represents cargo being delivered at the port by land vehicles (trains, trucks, etc.) to be loaded onto ships.   An outbound cargo movement represents cargo being removed from storage at the port and picked up by land vehicles.   An inbound ship movement represents a ship arriving in the waters nearby the port and wishing to dock.   An outbound ship movement represents a ship departing from its quay and leaving the port.
