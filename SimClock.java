/**
 * @author Victoria Lai 49451704
 * @author Matthew Robinson 73641908
 */

/**
 * A Simulation Clock that is shared by all threads of this project.
 */
public class SimClock
{
	private static int simTime = 0;
	private static int lineNumbers = 1;
	
	/**
	 * Allows the main thread to tick the clock once.
	 * Not thread safe. Only main (or one specific) thread should be calling this.
	 */
	public static void tick()
	{
		simTime++;
	}
	
	/**
	 * Thread safe function to return the SimClock time.
	 * @return the time.
	 */
	public static int getTime()
	{
		return simTime;
	}
	
	/**
	 * Nice function for all threads to print out values with line numbers and the simulated time.
	 * Prints in the form:
	 * "[N] X: arg"
	 * Where N is the line number, X is the SimClock time value, arg is the string to print with it.
	 * @param arg [in] String to print out
	 */
	public static synchronized void printWithTime(String arg) {
		System.out.println("[" + lineNumbers++ + "] Time " + simTime + ": " + arg);
	}
}
