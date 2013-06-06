import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main implements ActionListener {
	
	private Pool pool;
	
	private JFrame window;
	private GridLayout scrollLayout;
	private JPanel buttonPane;
	private JPanel generatePane;
	private JPanel labels;
	private JPanel current;
	private JScrollPane scrollPane;
	
	private Main ()
	{
		pool = new Pool ();
		
		window = new JFrame ("Team Maker");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout (new BoxLayout (window.getContentPane(), BoxLayout.Y_AXIS));
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem load = new JMenuItem("Load");
		
		save.addActionListener(this);
		load.addActionListener(this);
		
		menu.add(save);
		menu.add(load);
		menuBar.add(menu);
		
		buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton addFieldButton = new JButton ("Add Field");
		JButton removeFieldButton = new JButton ("Remove Field");
		JButton addMemberButton = new JButton ("Add Member");
		JButton removeMemberButton = new JButton ("Remove Member");
		JButton storeValuesButton = new JButton ("Store Values");
				
		addMemberButton.addActionListener(this);
		removeMemberButton.addActionListener (this);
		addFieldButton.addActionListener (this);
		removeFieldButton.addActionListener (this);
		storeValuesButton.addActionListener(this);

		labels = new JPanel ();
		labels.setLayout(new GridLayout (1, 1));
		labels.add (new JLabel ("Name"));
		
		current = new JPanel ();
		scrollLayout = new GridLayout (0, 1);
		current.setLayout(scrollLayout);
		scrollPane = new JScrollPane (current);
		
		buttonPane.add(addFieldButton);
		buttonPane.add(removeFieldButton);
		buttonPane.add(addMemberButton);
		buttonPane.add(removeMemberButton);
		buttonPane.add(storeValuesButton);
		buttonPane.setVisible (true);
		
		JButton generateTeamsButton = new JButton ("Generate Teams");
		generateTeamsButton.addActionListener(this);
		
		generatePane = new JPanel();
		generatePane.setLayout(new FlowLayout(FlowLayout.CENTER));
		generatePane.add(generateTeamsButton);
		
		window.setJMenuBar(menuBar);
		window.add(buttonPane);
		window.add(labels);
		window.add(scrollPane);
		window.add(generatePane);
        window.setVisible(true);
        window.setResizable(false);
		window.pack ();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand ().equals ("Add Field"))
		{
			String s = ((String)JOptionPane.showInputDialog(
			                    window,
			                    "Please enter the name of the new field:\n",
			                    "Enter field name",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    null,
			                    ""));
			if (s == null)
				return;
			s = s.trim();
			if (s.equals("Name"))
			{
				JOptionPane.showMessageDialog(window, "Duplicate fields are not allowed!");
				return;
			}
			else if (s.length() == 0)
			{
				JOptionPane.showMessageDialog(window, "No field name was entered!");
				return;
			}
			else if (s.length() >= 30)
			{
				JOptionPane.showMessageDialog(window, "Field name was too long!");
				return;
			}
			
			boolean attempt = pool.addField (s);
			if (!attempt)
			{
				JOptionPane.showMessageDialog (window, "Duplicate fields are not allowed!");
				return;
			}
			labels.add(new JLabel(s));
			scrollLayout.setColumns(scrollLayout.getColumns() + 1);
			for (int i = 0; i < scrollLayout.getRows(); i++)
				current.add(new JSpinner(new SpinnerListModel (new String [] {"0","1","2","3","4","5", "6","7","8","9","10"})), 
						(i + 1) * scrollLayout.getColumns() - 1);
		}
		else if (e.getActionCommand ().equals ("Remove Field"))
		{
			String s = ((String)JOptionPane.showInputDialog(
                    window,
                    "Please enter the name of the field to be removed:\n",
                    "Enter field name",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    ""));
			if (s == null)
				return;
			s = s.trim();
			if (s.equals("Name"))
			{
				JOptionPane.showMessageDialog(window, "Can't remove name field!\n It is required for the program to function!");
				return;
			}
			else if (s.length () == 0 || s.length() >= 30)
			{
				JOptionPane.showMessageDialog (window, "Not a possible field name!");
				return;
			}
			
			int loc = pool.removeField(s);
			if (loc == -1)
			{
				JOptionPane.showMessageDialog (window, "No such field exists!");
				return;
			}
			labels.remove (loc + 1);
			scrollLayout.setColumns (scrollLayout.getColumns () - 1);
			for (int i = scrollLayout.getRows() - 1; i >= 0; i--)
				current.remove((i + 1) * (scrollLayout.getColumns() + 1) - 1);
		}
		else if (e.getActionCommand().equals ("Add Member"))
		{
			String s = ((String)JOptionPane.showInputDialog(
                    window,
                    "Please enter the name of the new member:\n",
                    "Enter member name",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    ""));
			if (s == null)
				return;
			s = s.trim();
			if (s.length () == 0)
			{
				JOptionPane.showMessageDialog(window, "No member name was entered!");
				return;
			}
			else if (s.length () >= 30)
			{
				JOptionPane.showMessageDialog(window, "Member name was too long!");
				return;
			}
			
			boolean attempt = pool.addMember (s);
			if (!attempt)
			{
				JOptionPane.showMessageDialog (window, "Duplicate members are not allowed!");
				return;
			}
			
			scrollLayout.setRows (scrollLayout.getRows () + 1);
			current.add (new JLabel (s));
			for (int i = 0; i < scrollLayout.getColumns() - 1; i++)
				current.add (new JSpinner(new SpinnerListModel (new String [] {"0","1","2","3","4","5", "6","7","8","9","10"})));
		}
		else if (e. getActionCommand ().equals ("Remove Member"))
		{
			String s = ((String)JOptionPane.showInputDialog(
                    window,
                    "Please enter the name of the member to be removed:\n",
                    "Enter member name",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    ""));
			if (s == null)
				return;
			s = s.trim();
			if (s.length () == 0 || s.length () >= 30)
			{
				JOptionPane.showMessageDialog (window, "Not a possible member name!");
				return;
			}
			
			int loc = pool.removeMember(s);
			if (loc == -1)
			{
				JOptionPane.showMessageDialog (window, "No such member exists!");
				return;
			}
			
			scrollLayout.setRows (scrollLayout.getRows () - 1);
			for (int i = 0; i < scrollLayout.getColumns(); i++)
				current.remove(loc * (scrollLayout.getColumns()));
		}
		else if (e.	getActionCommand ().equals ("Store Values"))
		{
			for (int i = 0; i < scrollLayout.getRows(); i++)
			{
				for (int j = 1; j < scrollLayout.getColumns(); j++)
				{
					String temp = ((String) ((JSpinner)current.getComponent(i * scrollLayout.getColumns() + j)).getValue());
					int num;
					try
					{
						num = new Integer (temp);
					}
					catch (Exception x)
					{
						// Error message
						num = 0;
					}
					pool.getMember(i).setValueAtLocation(j - 1, num);
				}
			}
		}
		else if (e.getActionCommand().equals("Save"))
		{
			pool.writeToFile("TestFile.txt");
		}
		else if (e.getActionCommand().equals("Load"))
		{
			pool.readFromFile("TestFile.txt");
			labels.removeAll();
			labels.add(new JLabel("Name"));
			for (int i = 0; i < pool.getNumFields(); i++)
			{
				labels.add(new JLabel (pool.getField(i)));
			}
			current.removeAll();
			scrollLayout.setColumns(pool.getNumFields() + 1);
			for (int j = 0; j < pool.getNumMembers(); j++)
			{
				current.add(new JLabel (pool.getMember(j).getName()));
				for (int k = 0; k < pool.getNumFields(); k++)
				{
					int val = pool.getMember(j).getValueAtLocation(k);
					JSpinner spin = new JSpinner(new SpinnerListModel (new String [] {"0","1","2","3","4","5", "6","7","8","9","10"}));
					spin.setValue("" + val);
					current.add(spin);
				}
			}
		}
		else
		{
			// Generate teams
		}
		window.validate();
	}
	
	public static void main(String[] args)
	{
		new Main ();
	}

}
