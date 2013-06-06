import java.util.*;

class Member
{
	private String name;
	private ArrayList<Integer> values;
	
	Member (String name, ArrayList<Integer> values)
	{
		this.name = name;
		this.values = values;
	}
	
	String getName ()
	{
		return name;
	}
	
	ArrayList<Integer> getValues ()
	{
		return values;
	}
	
	int getValueAtLocation (int loc)
	{
		if (loc >= values.size())
		{
			return 0;
		}
		return values.get(loc).intValue();
	}
	
	void setName (String name)
	{
		this.name = name;
	}
	
	void setValues (ArrayList<Integer> values)
	{
		this.values = values;
	}
	
	void setValueAtLocation (int loc, int val)
	{
		if (loc >= values.size())
		{
			return;
		}
		values.set(loc, new Integer (val));
	}
	
	public String toString ()
	{
		String ret = name;
		for (int i = 0; i < values.size(); i++)
		{
			ret = ret + ";;" + values.get(i);
		}
		return ret;
	}
}
