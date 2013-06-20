
public class Field 
{
	private String name;
	private int lowerLimit;
	private int upperLimit;
	private boolean isBoolean;
	
	Field (String name)
	{
		this.name = name;
		lowerLimit = 0;
		upperLimit = 10;
		isBoolean = false;
	}
	
	Field (String name, int lowerLimit, int upperLimit)
	{
		this.name = name;
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		isBoolean = false;
	}
	
	Field (String name, int lowerLimit, int upperLimit, boolean isBoolean)
	{
		this.name = name;
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.isBoolean = isBoolean;
	}
	
	int getLowerLimit ()
	{
		return lowerLimit;
	}
	
	int getUpperLimit ()
	{
		return upperLimit;
	}
	
	boolean isBoolean ()
	{
		return isBoolean;
	}
	
	void setBoolean (boolean isBoolean)
	{
		this.isBoolean = isBoolean;
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
