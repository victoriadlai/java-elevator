import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Victoria Lai 49451704
 * @author Matthew Robinson 73641908
 */

/**
 * This BuildingManager class collectively watches the state of all the Building Floors.
 * Elevators tell the building manager that it is waiting for passenger pickup when it calls
 * requestFloorAccess(). The building manager makes sure that multiple elevators
 * are not heading to the same floor.
 */
public class BuildingManager {	
	public static final int FLOOR_COUNT = 5;
	private BuildingFloor[] floors;
	private Lock passengerRequestsLock;
	
	/**
	 * Default constructor that initializes floors to a new BuildingFloor.
	 */
	public BuildingManager() {
		floors = new BuildingFloor[5];
		for (int i = 0; i < floors.length; i++) {
			floors[i] = new BuildingFloor();
		}
		
		passengerRequestsLock = new ReentrantLock();
	}
	
	/**
	 * Function that puts in new passenger requests for a building floor.
	 * It also updates the total destination requests.
	 * @param floor [in] building floor the passengers are spawning on.
	 * @param goingToFloor [in] building floor the passengers wish to go to.
	 * @param numPassengers [in] the number of passengers.
	 */
	public void spawnPassengers(int floor, int goingToFloor, int numPassengers) {
		// Gather the current passengers that are waiting for a floor.
		int[] previousPassengerRequests = floors[floor].getPassengerRequests();
		
		// Adding more passengers that are waiting at the same floor.
		passengerRequestsLock.lock();
		previousPassengerRequests[goingToFloor] += numPassengers;
		passengerRequestsLock.unlock();
		// Why do we need this lock?
		// When the elevator clears the passengerRequests, it sets it to 0.
		// If we do += while another thread is manipulating the data, we'll have bad values.
		
		updateTotalDestinationRequests(goingToFloor, floor, numPassengers);
		
		SimClock.printWithTime("There are " + numPassengers + " passengers on Floor " + floor + " requesting to go to Floor " + goingToFloor + ".");
	}
	
	/**
	 * This method is called by each elevator. It will scan the building for any elevator requests.
	 * If there is a request, then the building manager will set the building floor's approaching elevator.
	 * Then the method will return which floor the building manager is telling the elevator to go to.
	 * @param elevatorID [in] elevator id that is requesting building floor information.
	 * @return the building floor the elevator should go to.
	 * 		   -1 if there are no valid requests.
	 */
	public synchronized int requestFloorAccess(int elevatorID) {
		// DATA RACE:
		// * BuildingFloor.approachingElevator
		
		// Iterate through the floors
		for (int i = 0; i < FLOOR_COUNT; i++) {
			// If the floor has requests
			if (areThereElevatorRequests(i)) {
				// And there is no other elevator approaching
				if (!isElevatorApproaching(i)) {
					// Set this elevator to pickup the passengers.
					setPickupRequest(i, elevatorID);
					return i;
				}
			}
		}
		
		return -1;
	}
	
	/**
	 * Clears the passenger requests at building floor going to a floor.
	 * We lock the passenger requests so it doesn't interfere with spawning.
	 * @param buildingFloor [in] the building floor the passengers spawned.
	 * @param goingToFloor [in] the floor the passengers wished to go to.
	 */
	public void clearPassengerRequests(int buildingFloor, int goingToFloor) {
		passengerRequestsLock.lock();
		floors[buildingFloor].getPassengerRequests()[goingToFloor] = 0;
		passengerRequestsLock.unlock();
	}
	
	/**
	 * @param floor [in] building floor of the passenger requests.
	 * @return the passengerRequests at a certain floor.
	 * Do not write into this data. spawnPassengers() and clearPassengerRequests() should be used to manipulate data.
	 */
	public int[] getPassengerRequestsAtFloor(int floor) {
		// This method will only be used by the identified approaching elevator for a building floor.
		return floors[floor].getPassengerRequests();
	}
	
	/**
	 * Clear the approaching elevator of a floor.
	 * @param floor [in] the building floor to clear the approaching elevator.
	 */
	public void clearApproachingElevator(int floor) {
		// Precondition that if the value isn't -1
		// there is no possible way that any other thread will want to use this method.
		floors[floor].setApproachingElevator(-1);
	}
	
	/**
	 * Updates the amount of arrived passengers at a floor.
	 * @param currentFloor [in] the building floor.
	 * @param elevatorID [in] the elevator id that is dropping off passengers.
	 * @param numberOfPassengersDroppedOff [in] the amount of passengers.
	 */
	public synchronized void updateArrivedPassengers(int currentFloor, int elevatorID, int numberOfPassengersDroppedOff) {
		int newTotalArrivedPassengers;
		newTotalArrivedPassengers = floors[currentFloor].getTotalArrivedPassengers()[elevatorID] 
									+ numberOfPassengersDroppedOff;
		floors[currentFloor].setArrivedPassengers(elevatorID, newTotalArrivedPassengers);
		
	}
	
	/**
	 * Increments the total destination requests by the number of passengers.
	 * @param destinationFloor [in] the floor which passengers are going to.
	 * @param buildingFloor [in] the building floor the passengers spawned on.
	 * @param numPassengers [in] the amount of passengers.
	 */
	public void updateTotalDestinationRequests(int destinationFloor, int buildingFloor, int numPassengers) {
		int totalDestinationRequests = floors[buildingFloor].getTotalDestinationRequests()[destinationFloor];
		totalDestinationRequests += numPassengers;
		floors[buildingFloor].setTotalDestinationRequests(destinationFloor, totalDestinationRequests);
	}
	
	/**
	 * @param floor [in] specific building floor.
	 * @return the total arrived passengers from floor.
	 */
	public int[] getArrivedPassengersAtFloor(int floor) {
		return floors[floor].getTotalArrivedPassengers();
	}
	
	/**
	 * @param floor [in] specific building floor.
	 * @return the total destination requests for a certain floor.
	 */
	public int[] getTotalDestinationRequestsAtFloor(int floor) {
		return floors[floor].getTotalDestinationRequests();
	}
	
	/**
	 * @param floor [in] specific building floor.
	 * @return the approaching elevator for floor.
	 */
	public int getApproachingElevatorAtFloor(int floor) {
		return floors[floor].getApproachingElevator();
	}
	
	/**
	 * @param floor [in] specific building floor.
	 * @return whether or not a building floor has passenger requests.
	 */
	private boolean areThereElevatorRequests(int floor) {
		int temp = 0;
		for (int i = 0; i < floors[floor].getPassengerRequests().length; i++) {
			
			temp += floors[floor].getPassengerRequests()[i];
		}
		
		return temp != 0;
	}
	
	/**
	 * Tell the floor which elevator is going to be approaching it.
	 * @param floor [in] specific building floor.
	 * @param elevatorID [in] the elevator ID that is approaching the floor.
	 */
	private void setPickupRequest(int floor, int elevatorID) {
		floors[floor].setApproachingElevator(elevatorID);
	}
	
	/**
	 * @param floor [in] specific building floor.
	 * @return whether or not an elevator is approaching the floor.
	 */
	private boolean isElevatorApproaching(int floor) {
		return floors[floor].getApproachingElevator() != -1;
	}
}
