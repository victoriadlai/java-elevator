import java.util.ArrayList;

/**
 * @author Victoria Lai 49451704
 * @author Matthew Robinson 73641908
 */

/**
 * 
 * An Elevator object represents a single Elevator in the ElevatorSimulation.
 * Each elevator is given an elevatorID (0-4) so that we can distinguish 
 * between and collect data for each Elevator. Each Elevator object essentially
 * keeps track of the Elevator's State, which includes:
 * 		- the current floor the elevator is on
 * 		- the total number of passengers that are inside the elevator
 * 		- a running count of the number of passengers the elevator has LOADED
 * 		  throughout the simulation
 * 		- a running count of the number of passengers the elevator has UNLOADED
 * 		  throughout the simulation
 * 		- a moveQueue of ElevatorEvent objects that control the movement of the
 * 		  elevator
 * 		- an array that maintains the number of passengers inside the elevator
 * 		  and each floor they are requesting to go to
 *
 */
public class Elevator implements Runnable
{
	private final static int traversalTime = 5;
	private final static int loadUnloadTime = 10;
	
	private int elevatorID;
	private int currentFloor;
	private int numPassengers;  // IN the elevator
	private int totalLoadedPassengers;
	private int totalUnloadedPassengers;
	private ArrayList<ElevatorEvent> moveQueue;
	private int[] passengerDestinations;
	BuildingManager manager;
	
	/**
	 * Default constructor.
	 * Each elevator begins on Floor 0...with 0 passengers, 0 totalLoadedPassengers, 
	 * 0 totalUnloadedPassengers, and an empty moveQueue that can only hold 
	 * ElevatorEvents.
	 * @param elevatorID
	 * @param manager
	 */
	public Elevator(int ID, BuildingManager sharedManager)
	{
		elevatorID = ID;
		currentFloor = 0;
		numPassengers = 0;
		totalLoadedPassengers = 0;
		totalUnloadedPassengers = 0;
		moveQueue = new ArrayList<ElevatorEvent>();
		passengerDestinations = new int[5];
		manager = sharedManager;
	}
	
	/**
	 * Method required for Runnable implementation. Instance of method continuously 
	 * loops and processes ElevatorEvents in moveQueue
	 */
	public void run() {
		while (!Thread.interrupted()) {
		// If the moveQueue is empty, we are in IDLE state.
			if (moveQueue.isEmpty()) {
				// Ask the manager if there is a floor where passengers are requesting an elevator.
				int requestingFloor = manager.requestFloorAccess(elevatorID);
				// If there is a floor requesting an elevator, create a new elevator event for pick up.
				if (requestingFloor != -1) {
					ElevatorEvent newEvent = new ElevatorEvent();
					
					// Create a move event that travels to the floor
					// w/o picking up passengers
					newEvent.setDestination(requestingFloor);
					
					// Calculate expected arrival time.
					int expectedArrival;
					expectedArrival = SimClock.getTime() + Math.abs(newEvent.getDestination() - currentFloor) * traversalTime;
					expectedArrival += loadUnloadTime;
					
					newEvent.setExpectedArrival(expectedArrival);
					
					// Add event to queue
					moveQueue.add(newEvent);
					
					SimClock.printWithTime("Elevator " + elevatorID + " is heading to Floor " + requestingFloor + " to pick up passengers.");
				}
			}
			// else, we are in PICKUP or DROPOFF state.
			else {
				// If we aren't carrying passengers, we are in PICKUP state.
				if (numPassengers == 0) {
					ElevatorEvent event = moveQueue.get(0);
					
					// If it is time to PICKUP
					if (event.getExpectedArrival() == SimClock.getTime()) {
						// Get the floor the elevator needs to head to for pickup.
						currentFloor = event.getDestination();
						
						moveQueue.remove(0);  // Clear the queue for the DROPOFF events.
						
						// Get the amount of passengers at the floor.
						int[] passengerRequests = manager.getPassengerRequestsAtFloor(currentFloor);
						
						SimClock.printWithTime("Elevator " + elevatorID + " has arrived at Floor " + currentFloor + " and has loaded passengers.");
						// First, we figure out if there are any passengers that want to go UP.
						// If there are, create elevator events for them ONLY.
						for (int i = currentFloor + 1; i < BuildingManager.FLOOR_COUNT; i++) {
							if (passengerRequests[i] > 0) {
								createElevatorEventForDropoff(passengerRequests, i);
							}
						}
						
						// If the moveQueue is empty, there were no passengers that want to go UP.
						// Take care of the passengers that want to go down.
						if (moveQueue.isEmpty()) {  	// go downwards instead
							for (int i = currentFloor - 1; i > -1; i--) {
								if (passengerRequests[i] > 0) {
									createElevatorEventForDropoff(passengerRequests, i);
								}
							}
						}
						
						// Allow other elevators to come pick up at this floor.
						manager.clearApproachingElevator(currentFloor);
					}
				}
				// if we do have passengers, then we are in DROPOFF state.
				else {
					// If the simulation time is equal to expectedArrival time of elevator event.
					if (SimClock.getTime() == moveQueue.get(0).getExpectedArrival()) {
						// Get the floor that the elevator should be at.
						currentFloor = moveQueue.get(0).getDestination();
						// Update the total unloaded passengers at that floor.
						totalUnloadedPassengers += passengerDestinations[currentFloor];
						// Remove the passengers from the elevator.
						numPassengers -= passengerDestinations[currentFloor];
						
						// Update the specific building floor's total arrived passengers for this elevator.
						manager.updateArrivedPassengers(currentFloor, elevatorID, passengerDestinations[currentFloor]);
						
						SimClock.printWithTime("Elevator " + elevatorID + " has arrived at Floor " + currentFloor +
											   " and has unloaded " + passengerDestinations[currentFloor] + " passengers.");
						
						// Clear the passengers that requested to go to currentFloor in the elevator's passengerDestination array.
						passengerDestinations[currentFloor] = 0;
						// Remove this elevatorEvent from the queue.
						moveQueue.remove(0);
					}
				}
			}
		}
	}
	
	/**
	 * Method used to create ElevatorEvents specifically for passenger unloads to 
	 * be added into the moveQueue.
	 * @param passengerRequests
	 * @param floor
	 */
	public void createElevatorEventForDropoff(int[] passengerRequests, int floor) {
		SimClock.printWithTime("There are " + passengerRequests[floor] + " passengers in Elevator " + elevatorID +
			   " requesting to go to Floor " + floor + ".");
		
		passengerDestinations[floor] = passengerRequests[floor];
		numPassengers += passengerDestinations[floor];
		totalLoadedPassengers += passengerDestinations[floor];
		
		ElevatorEvent newEvent = new ElevatorEvent();
		
		newEvent.setDestination(floor);
		
		int expectedTime = SimClock.getTime() + Math.abs(floor - currentFloor) * traversalTime;
		expectedTime += loadUnloadTime;
		expectedTime += moveQueue.size() * loadUnloadTime;
		
		newEvent.setExpectedArrival(expectedTime);
		
		moveQueue.add(newEvent);
		
		manager.clearPassengerRequests(currentFloor, floor);
	}
	
	/**
	 * @returns the elevator's total number of passengers the elevator has
	 * LOADED throughout the simulation at the specific instance in which 
	 * the method is called
	 */
	public int getTotalLoadedPassengers() {
		return totalLoadedPassengers;
	}
	
	/**
	 * @returns the elevator's total number of passengers the elevator has
	 * UNLOADED throughout the simulation at the specific instance in which 
	 * the method is called
	 */
	public int getTotalUnloadedPassengers() {
		return totalUnloadedPassengers;
	}
	
	/**
	 * @returns the number of passengers currently in the elevator
	 */
	public int getNumPassengers() {
		return numPassengers;
	}
}
