/**
 * @author Victoria Lai 49451704
 * @author Matthew Robinson 73641908
 */

/**
 * A passenger arrival object is supposed to help with the automation of the simulation.
 * Most of the data should be immutable except for expectedTimeOfArrival.
 */
public class PassengerArrival
{
	// Data Members
	private int numPassenger;
	private int destinationFloor;
	private int timePeriod;
	private int expectedTimeOfArrival;
	
	/**
	 * Default constructor
	 * @param passengers [in] number of passengers to spawn.
	 * @param iDestinationFloor [in] the floor they want to go to.
	 * @param timeInterval [in] time interval for spawn.
	 */
	public PassengerArrival(int passengers, int iDestinationFloor, int timeInterval) 
	{
		numPassenger = passengers;
		destinationFloor = iDestinationFloor;
		timePeriod = timeInterval;
		expectedTimeOfArrival = timePeriod;
	}
	
	/**
	 * toString override
	 */
	@Override
	public String toString() {
		return "\"" + numPassenger + " " + destinationFloor + " " + timePeriod + " " + expectedTimeOfArrival + "\"";
	}
	
	/**
	 * Set the new expected time of arrival for the passengers.
	 * @param newTimeOfArrival [in] the new time.
	 */
	public void setExpectedTimeOfArrival(int newTimeOfArrival)
	{
		expectedTimeOfArrival = newTimeOfArrival;
	}
	
	/**
	 * @return the number of passengers.
	 */
	public int getNumPassengers()
	{
		return numPassenger;
	}
	/**
	 * @return the destination floor.
	 */
	public int getDestinationFloor()
	{
		return destinationFloor;
	}
	/**
	 * @return the time interval to spawn.
	 */
	public int getTimePeriod()
	{
		return timePeriod;
	}
	/**
	 * @return the actual time against SimClock to spawn.
	 */
	public int getExpectedTimeOfArrival()
	{
		return expectedTimeOfArrival;
	}
}
