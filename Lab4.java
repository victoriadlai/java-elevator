/**
 * @author Victoria Lai 49451704
 * @author Matthew Robinson 73641908
 */

/**
 * Lab 4 Project for Elevator Simulation.
 */
public class Lab4
{
	public static void main(String[] args)
	{
		ElevatorSimulation es = new ElevatorSimulation();
		// Start the elevator simulation.
		es.start();
		
		// Print statistics when the simulation is done.
		es.printBuildingState();
	}
}
