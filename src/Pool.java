import java.io.*;
import java.util.*;
import javax.swing.*;

class Pool
{
	
	private ArrayList<Field> fields;
	private ArrayList<Member> members;
	private int numFields;
	private int numMembers;
	private JFrame parent;
	
	Pool (JFrame frame)
	{
		fields = new ArrayList<Field>();
		members = new ArrayList<Member> ();
		numFields = 0;
		numMembers = 0;
		parent = frame;
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
		}
		return null;
	}
	
	Field getField (int loc)
	{
		try
		{
			return fields.get(loc);
		}
		catch (Exception e)
		{
		}
		return null;
	}
	
	boolean setFieldName (int loc, String name)
	{
		for (int i = 0; i < numFields; i++)
		{
			if (fields.get(i).toString().equals(name)
				&& i != loc)
			{
				return false;
			}
		}
		fields.get(loc).setName(name);
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
		boolean found = false;
		for (int i = 0; i < numFields; i++)
		{
			if (fields.get(i).toString().equals(field))
			{
				found = true;
				break;
			}
		}
		if (found)
		{
			return false;
		}
		fields.add (new Field (field));
		ArrayList<Integer> temp;
		for (int i = 0; i < numMembers; i++)
		{
			temp = members.get(i).getValues();
			temp.add(new Integer (0));
			members.get(i).setValues(temp);
		}
		numFields++;
		return true;
	}
	
	int removeField (String field)
	{
		int loc = -1;
		for (int i = 0; i < numFields; i++)
		{
			if (fields.get(i).toString().equals(field))
			{
				loc = i;
				break;
			}
		}
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
				out.print("" + (char)178 + fields.get(i));
			}
			out.println();
			out.print("LOWER");
			for (int i = 0; i < fields.size(); i++)
			{
				out.print("" + (char)178 + fields.get(i).getLowerLimit());
			}
			out.println();
			out.print("UPPER");
			for (int i = 0; i < fields.size(); i++)
			{
				out.print("" + (char)178 + fields.get(i).getUpperLimit());
			}
			out.println();
			out.print("BOOLEAN?");
			for (int i = 0; i < fields.size(); i++)
			{
				out.print("" + (char)178 + fields.get(i).isBoolean());
			}
			out.println();
			for (int j = 0; j < members.size(); j++)
			{
				out.print(members.get(j).toString());
				for (int i = 0; i < fields.size(); i++)
				{
					out.print("" + (char)178 + members.get(j).getValueAtLocation(i));
				}
				out.println();
			}
			out.close();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(parent, "Sorry, something went wrong! The file was not formatted correctly!");
		}
	}
	
	void readFromFile (String fileName)
	{
		try
		{
			BufferedReader in = new BufferedReader (new FileReader (fileName));
			String [] tempArr = in.readLine().split("" + (char)178);
			numFields = tempArr.length - 1;
			ArrayList<String> fieldNames = new ArrayList<String> (Arrays.asList(tempArr));
			fieldNames.remove(0);
			tempArr = in.readLine().split("" + (char)178);
			ArrayList<String> lowerLimits = new ArrayList<String> (Arrays.asList(tempArr));
			lowerLimits.remove(0);
			tempArr = in.readLine().split("" + (char)178);
			ArrayList<String> upperLimits = new ArrayList<String> (Arrays.asList(tempArr));
			upperLimits.remove(0);
			tempArr = in.readLine().split("" + (char)178);
			ArrayList<String> booleans = new ArrayList<String> (Arrays.asList(tempArr));
			booleans.remove(0);
			fields = new ArrayList<Field> ();
			for (int i = 0; i < numFields; i++)
				fields.add(new Field(fieldNames.get(i), Integer.parseInt(lowerLimits.get(i)), 
									 Integer.parseInt(upperLimits.get(i)), Boolean.parseBoolean(booleans.get(i))));
			numMembers = 0;
			members = new ArrayList<Member>();
			String buffer = in.readLine();
			while (buffer != null)
			{
				tempArr = buffer.split("" + (char)178);
				ArrayList<Integer> tempVals = new ArrayList<Integer>();
				Integer temp;
				for (int i = 0; i < numFields; i++)
				{
					temp = new Integer (tempArr[i+1]);
					tempVals.add(temp);
				}
				members.add(new Member(tempArr[0], tempVals));
				numMembers++;
				buffer = in.readLine();
			}
			in.close();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(parent, "Sorry, something went wrong! The file was not formatted correctly!");
		}
	}
}
