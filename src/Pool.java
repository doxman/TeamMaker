import java.util.*;
import java.io.*;

class Pool
{
	
	private ArrayList<String> fields;
	private ArrayList<Member> members;
	private int numFields;
	private int numMembers;
	
	Pool ()
	{
		fields = new ArrayList<String>();
		members = new ArrayList<Member> ();
		numFields = 0;
		numMembers = 0;
	}
	
	ArrayList<Member> getMembers()
	{
		return members;
	}
	
	int getNumFields()
	{
		return numFields;
	}
	
	int getNumMembers()
	{
		return numMembers;
	}
	
	Member getMember (int loc)
	{
		try
		{
			return members.get(loc);
		}
		catch (Exception e)
		{
			// Error message
		}
		return null;
	}
	
	String getField (int loc)
	{
		try
		{
			return fields.get(loc);
		}
		catch (Exception e)
		{
			// Error message
		}
		return null;
	}
	
	boolean setField (int loc, String name)
	{
		int ind = fields.indexOf(name);
		if (ind != -1 && ind != loc)
			return false;
		fields.set(loc, name);
		return true;
	}
	
	boolean setMemberName (int loc, String name)
	{
		for (int i = 0; i < numMembers; i++)
		{
			if (members.get(i).getName().equals(name) && i != loc)
			{
				return false;
			}
		}
		members.get(loc).setName(name);
		return true;
	}
	
	boolean addField (String field)
	{
		if (fields.indexOf(field) != -1)
		{
			return false;
		}
		fields.add (field);
		ArrayList<Integer> temp;
		for (int i = 0; i < numMembers; i++)
		{
			temp = members.get(i).getValues();
			temp.add(new Integer (1));
			members.get(i).setValues(temp);
		}
		numFields++;
		return true;
	}
	
	int removeField (String field)
	{
		int loc = fields.indexOf(field);
		if (loc == -1)
		{
			return loc;
		}
		fields.remove (loc);
		ArrayList<Integer> temp;
		for (int i = 0; i < numMembers; i++)
		{
			temp = members.get(i).getValues();
			temp.remove(loc);
			members.get(i).setValues(temp);
		}
		numFields--;
		return loc;
	}
	
	boolean addMember (String name)
	{
		for (int i = 0; i < numMembers; i++)
		{
			if (members.get(i).getName().equals(name))
			{
				return false;
			}
		}
		ArrayList<Integer> values = new ArrayList<Integer>();
		for (int i = 0; i < numFields; i++)
			values.add(new Integer (1));
		members.add(new Member (name, values));
		numMembers++;
		return true;
	}
	
	int removeMember (String name)
	{
		for (int i = 0; i < numMembers; i++)
		{
			if (members.get(i).getName().equals (name))
			{
				members.remove(i);
				numMembers--;
				return i;
			}
		}
		return -1;
	}
	
	void writeToFile (String fileName)
	{
		try
		{
			PrintWriter out = new PrintWriter (fileName);
			out.print("FIELDS");
			for (int i = 0; i < fields.size(); i++)
			{
				out.print(";;" + fields.get(i));
			}
			out.println();
			for (int j = 0; j < members.size(); j++)
			{
				out.print(members.get(j).toString());
				for (int i = 0; i < fields.size(); i++)
				{
					out.print(";;" + members.get(j).getValueAtLocation(i));
				}
				out.println();
			}
			out.close();
		}
		catch (Exception e)
		{
			// Error message
		}
	}
	
	void readFromFile (String fileName)
	{
		try
		{
			BufferedReader in = new BufferedReader (new FileReader (fileName));
			String [] tempArr = in.readLine().split(";;");
			numFields = tempArr.length - 1;
			fields = new ArrayList<String> (Arrays.asList(tempArr));
			fields.remove(0);
			numMembers = 0;
			members = new ArrayList<Member>();
			while (tempArr != null)
			{
				tempArr = in.readLine().split(";;");
				ArrayList<Integer> tempVals = new ArrayList<Integer>();
				Integer temp;
				for (int i = 0; i < numFields; i++)
				{
					try
					{
						temp = new Integer (tempArr[i+1]);
						tempVals.add(temp);
					}
					catch (Exception e)
					{
						// Error message
					}
				}
				members.add(new Member(tempArr[0], tempVals));
				numMembers++;
			}
			in.close();
		}
		catch (Exception e)
		{
			// Error message
		}
	}
}
