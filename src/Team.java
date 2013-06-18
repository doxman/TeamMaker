import java.util.ArrayList;


public class Team extends ArrayList<Member> 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	int [] limits;
	int [] currentValues;
	
	public Team (int [] limits, String name)
	{
		super();
		this.limits = limits;
		currentValues = new int[limits.length];
		for (int i = 0; i < limits.length; i++)
		{
			currentValues [i] = 0;
		}
		this.name = name;
	}
	
	void incrementLimits ()
	{
		for (int i = 0; i < limits.length; i++)
		{
			limits [i] ++;
		}
	}
	
	// Assumes that the member has the same number of fields as limits and currentValues
	boolean testFit (Member m)
	{
		for (int i = 0; i < limits.length; i++)
		{
			if (m.getValueAtLocation(i) + currentValues [i] > limits [i])
				return false;
		}
		return true;
	}
	
	public boolean add (Member m)
	{
		for (int i = 0; i < limits.length; i++)
		{
			currentValues[i] += m.getValueAtLocation(i);
		}
		return super.add(m);
	}
	
	void printMembers ()
	{
		for (int i = 0; i < size(); i++)
		{
			System.out.println(get(i).toString());
		}
	}
	
	public String toString ()
	{
		return name;
	}
}
