
/**
 * @author Victoria Lai 49451704
 * @author Matthew Robinson 73641908
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Main simulation thread of the program.
 */
public class ElevatorSimulation
{
	// Amount of elevators in the building.
	public static final int MAX_ELEVATORS = 5;
	
	// Data Members
	private BuildingManager manager;
	private Elevator[] elevatorObjects;
	
	/**
	 * Default constructor that creates the elevator objects and building manager.
	 */
	public ElevatorSimulation() {
		manager = new BuildingManager();
		elevatorObjects = new Elevator[MAX_ELEVATORS];
		for (int i = 0; i < MAX_ELEVATORS; i++) {
			elevatorObjects[i] = new Elevator(i, manager);
		}
	}
	
	/**
	 * Starts the simulation.
	 */
	public void start()
	{
		// Read in a configuration file called "ElevatorConfig.txt"
		Scanner configScanner = null;
		String fileName = "ElevatorConfig.txt";
		try
		{
			configScanner = new Scanner(new File(fileName));
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Matthew Says: Cannot find file " + fileName);
		}
		
		// Calculate the simulationLength and secondRate from the config file.
		int simulationLength;
		int simulatedSecondRate;
		
		simulationLength = configScanner.nextInt();
		configScanner.nextLine();
		
		simulatedSecondRate = configScanner.nextInt();
		configScanner.nextLine();
		
		// Take each of the lines and store it into configInput
		ArrayList<ArrayList<String>> configInput = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < MAX_ELEVATORS; i++) {
			String nextLine = configScanner.nextLine();  // Get the line
			configInput.add(new ArrayList<String>(Arrays.asList(nextLine.split(";"))));
		}
		
		// Close the scanner as we read all the data in.
		configScanner.close();
		
		// After we have the lines in configInput, we can parse it properly.
		ArrayList<ArrayList<PassengerArrival>> arrivalList = new ArrayList<ArrayList<PassengerArrival>>();
		for (int i = 0; i < configInput.size(); i++) {
			ArrayList<PassengerArrival> tempListPa = new ArrayList<PassengerArrival>();
			
			for (int j = 0; j < configInput.get(i).size(); j++) {
				String[] tempString = configInput.get(i).get(j).split(" ");
				
				
				// Create appropriate passenger arrival objects based on data.
				PassengerArrival tempPa = new PassengerArrival(
										  Integer.parseInt(tempString[0]), Integer.parseInt(tempString[1]), Integer.parseInt(tempString[2]));
				
				tempListPa.add(tempPa);
			}
			
			// Add it into our arrivalList
			arrivalList.add(tempListPa);
		}
		
		// Create a list of elevator object threads
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		for (int i = 0; i < MAX_ELEVATORS; i++) {
			threadList.add(new Thread(elevatorObjects[i]));
		}
		
		// Start all of the elevators
		for (Thread t : threadList) {
			t.start();
		}
		
		// While we haven't passed our simulationLength 
		while (SimClock.getTime() <= simulationLength) {
			// For each building floor
			for (int i = 0; i < arrivalList.size(); i++) {
				// For each passengerArrival in a building floor
				for (int j = 0; j < arrivalList.get(i).size(); j++) {
					// Get the info
					PassengerArrival info = arrivalList.get(i).get(j);
					
					// If it is time to spawn passengers
					if (info.getExpectedTimeOfArrival() == SimClock.getTime()) {
						// Spawn the passengers
						manager.spawnPassengers(i, info.getDestinationFloor(), info.getNumPassengers());
						
						// And update the next expected time of arrival
						info.setExpectedTimeOfArrival(info.getTimePeriod() + info.getExpectedTimeOfArrival());
					}
				}
			}
			
			try {
				// Sleep based on simulatedSecondRate
				Thread.sleep(simulatedSecondRate);
			}
			catch (InterruptedException e) { } 
			
			// Tick the clock
			SimClock.tick();
		}
		
		// When we pass the simulation duration time, interrupt all of the threads.
		for (Thread t : threadList) {
			t.interrupt();
		}
		
		try {
			// Wait for all elevator events to finish their last cycle before continuing.
			for (Thread t : threadList) {
				t.join();
			}
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Prints the entire building state. Especially called after the simulation is over.
	 */
	public void printBuildingState() {
		//BUILDING INFO
		//total number of passengers requesting elevator access on the floor -- destination requests
		//total number of passengers that exited an elevator on the floor -- arrivedPassengers
		//current number of passengers waiting for an elevator on the floor -- passenger requests
		//current elevator heading towards the floor for passenger pickup -- 
		System.out.println();
		System.out.println("---------------BUILDING STATE---------------");
		
		for (int i = 0; i < BuildingManager.FLOOR_COUNT; i++) {
			System.out.println("Floor " + i);
			int[] totalPassengerRequests = manager.getTotalDestinationRequestsAtFloor(i);
			int[] totalArrivedPassengers = manager.getArrivedPassengersAtFloor(i);
			int[] currentPassengersWaiting = manager.getPassengerRequestsAtFloor(i);
			
			int totalPassengerRequestsAtFloor = 0;
			int totalArrivedPassengersAtFloor = 0;
			int currentPassengersWaitingAtFloor = 0;
			
			for (int j = 0; j < BuildingManager.FLOOR_COUNT; j++) {
				totalPassengerRequestsAtFloor += totalPassengerRequests[j];
				totalArrivedPassengersAtFloor += totalArrivedPassengers[j];
				currentPassengersWaitingAtFloor += currentPassengersWaiting[j];
			}
			
			System.out.println("Total Number of Passengers Requesting Elevator Access: " + 
					totalPassengerRequestsAtFloor);
			System.out.println("Total Number of Passengers that Exited On This Floor: " + 
					totalArrivedPassengersAtFloor);
			System.out.println("Current Number of Passengers Waiting for Elevator On This Floor: " + 
					currentPassengersWaitingAtFloor);
			System.out.println("Elevator coming for passenger pickup: " + manager.getApproachingElevatorAtFloor(i));
			System.out.println();
		}
		
		//ELEVATOR INFO
		//total number of passengers that entered elevator throughout sim
		//total number of passengers that exited elevator on specific floor
		//current number of passengers heading to any floor
		System.out.println("---------------ELEVATOR STATE---------------");
		
		for (int i = 0; i < MAX_ELEVATORS; i++) {
			System.out.println("Elevator #" + i);
			System.out.println("Total Number of Passengers that Entered Elevator: " + elevatorObjects[i].getTotalLoadedPassengers());
			System.out.println("Total Number of Passengers that Exited Elevator: " + elevatorObjects[i].getTotalUnloadedPassengers());
			System.out.println("Current Number of Passengers in Elevator: " + elevatorObjects[i].getNumPassengers());
			System.out.println();
		}
	}
}
