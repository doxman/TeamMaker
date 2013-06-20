
public class Field 
{
	private String name;
	private int lowerLimit;
	private int upperLimit;
	
	Field (String name)
	{
		this.name = name;
		lowerLimit = 0;
		upperLimit = 10;
	}
	
	Field (String name, int lowerLimit, int upperLimit)
	{
		this.name = name;
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
	}
	
	int getLowerLimit ()
	{
		return lowerLimit;
	}
	
	int getUpperLimit ()
	{
		return upperLimit;
	}
	
	void setName (String name)
	{
		this.name = name;
	}
	
	void setLimits (int lower, int upper)
	{
		lowerLimit = lower;
		upperLimit = upper;
	}
	
	public String toString()
	{
		return name;
	}
}
