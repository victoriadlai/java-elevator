/**
 * @author Victoria Lai 49451704
 * @author Matthew Robinson 73641908
 */

/**
 * 
 * An ElevatorEvent represents the movement of an Elevator throughout the
 * simulation. It contains information regarding which floor the Elevator
 * should move to and at what time it should do so. 
 *
 */
public class ElevatorEvent
{
	private int destination;
	private int expectedArrival;
	
	/**
	 * Default constructor.
	 * -1 is set as the ElevatorEvent's default destination to be overwritten
	 * after the elevator has processed passenger requests.
	 */
	public ElevatorEvent() {
		destination = -1;
		expectedArrival = 0;
	}
	
	/**
	 * Sets the destination floor for the elevator to go to.
	 * @param floorNumber
	 */
	public void setDestination(int floorNumber) {
		destination = floorNumber;
	}
	
	/**
	 * Sets the expected time in which the elevator will arrive at the 
	 * destination floor. Expected time is the the total time it will take 
	 * the elevator to traverse floors and the time it will take to
	 * unload/load passengers at the destination floor ADDED to the current
	 * time on the SimClock.
	 * @param timeOfArrival
	 */
	public void setExpectedArrival (int timeOfArrival) {
		expectedArrival = timeOfArrival;
	}
	
	/**
	 * @returns the destination floor maintained by the ElevatorEvent.
	 */
	public int getDestination() {
		return destination;
	}
	
	/**
	 * @returns the expected time of arrival.
	 */
	public int getExpectedArrival() {
		return expectedArrival;
	}
}
