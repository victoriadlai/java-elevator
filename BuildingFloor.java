/**
 * @author Victoria Lai 49451704
 * @author Matthew Robinson 73641908
 */

/**
 * A BuildingFloor object represents a single floor in a building.
 * It contains the total amount of people that requested for an elevator
 * and how many people exited. 
 */
public class BuildingFloor
{	
	// Data members
	private int[] totalDestinationRequests;
	private int[] arrivedPassengers; 
	private int[] passengerRequests;
	private int approachingElevator;
	
	/**
	 * Default constructor.
	 * When approachingElevator = -1, there is no elevator approaching the floor.
	 */
	public BuildingFloor() {
		totalDestinationRequests = new int[5];
		arrivedPassengers = new int[5];
		passengerRequests = new int[5];
		approachingElevator = -1;
	}
	
	/**
	 * Sets the total destination requests that the Building Floor received.
	 * @param destinationFloor [in] the floor in which passengers request to go to.
	 * @param destinationRequests [in] the total amount of passengers.
	 */
	public void setTotalDestinationRequests(int destinationFloor, int destinationRequests) {
		totalDestinationRequests[destinationFloor] = destinationRequests;
	}
	
	/**
	 * Sets the total arrived passengers at the current building floor.
	 * @param elevatorID [in] the elevator ID that dropped off the passengers.
	 * @param totalArrivedPassengers [in] the total amount of passengers arriving.
	 */
	public void setArrivedPassengers(int elevatorID, int totalArrivedPassengers) {
		arrivedPassengers[elevatorID] = totalArrivedPassengers;
	}
	
	/**
	 * Sets the passenger requests for the building floor.
	 * @param passengerRequests [in] array of passenger requests.
	 */
	public void setPassengerRequests(int[] iPassengerRequests) {
		passengerRequests = iPassengerRequests.clone();
	}
	
	/**
	 * Sets the approaching elevator ID.
	 * @param approachingElevator [in] elevatorID of the approaching elevator.
	 */
	public void setApproachingElevator(int iApproachingElevator) {
		approachingElevator = iApproachingElevator;
	}
	
	/**
	 * @return the total destination requests for all floors.
	 */
	public int[] getTotalDestinationRequests() {
		return totalDestinationRequests;
	}
	
	/**
	 * @return the total arrived passengers for all elevator drop offs.
	 */
	public int[] getTotalArrivedPassengers() {
		return arrivedPassengers;
	}
	
	/**
	 * @return current passenger requests information.
	 */
	public int[] getPassengerRequests() {
		return passengerRequests;
	}
	
	/**
	 * @return the approaching elevator ID.
	 */
	public int getApproachingElevator() {
		return approachingElevator;
	}
}
