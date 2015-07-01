import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Main implements ActionListener {

	private Pool pool;

	private JFrame window;
	private JMenuBar menuBar;
	private JMenu menu;
	private SpringLayout scrollLayout;
	private JPanel buttonPane;
	private JPanel generatePane;
	private JPanel current;
	private JScrollPane scrollPane;
	private JSpinner numTeamspinner;
	private JFileChooser fileChooser;

	private TeamWindow teamWindow;
	private JButton printButton;
	private JTable teamTable;
	
	private JDialog modifyField;
	private String currentField;
	private boolean isField;
	private int currentLoc;
	
	private boolean isBoolean;
	private int lastLower;
	private int lastUpper;
	
	ArrayList<Integer> columnWidths;

	private void storeValues()
	{
		for (int i = 1; i < pool.getNumFields() + 1; i++)
		{
			String name = ((JButton)current.getComponent(i)).getText();
			pool.setFieldName(i - 1, name);
		}
		for (int i = 1; i < pool.getNumMembers() + 1; i++)
		{
			String name = ((JButton)current.getComponent(i * (pool.getNumFields() + 1))).getText();
			pool.setMemberName(i - 1, name);
		}
		for (int i = 1; i < pool.getNumMembers() + 1; i++) {
			for (int j = 1; j < pool.getNumFields() + 1; j++) {
				String temp = ((String) ((JSpinner) current.getComponent(i
						* (pool.getNumFields() + 1) + j)).getValue());
				int num;
				if (temp == "True")
				{
					num = 1;
				}
				else if (temp == "False")
				{
					num = 0;
				}
				else
				{
					try
					{
						num = new Integer(temp);
					}
					catch (Exception x)
					{
						num = 0;
					}
				}
				pool.getMember(i - 1).setValueAtLocation(j - 1, num);
			}
		}
	}
	
	private boolean checkAdd (String s)
	{
		if (s == null)
			return false;
		s = s.trim();
		if (s.equals("Name")) {
			JOptionPane.showMessageDialog(window,
					"'Name' is already being used by the system! Sorry!");
			return false;
		} else if (s.length() == 0) {
			JOptionPane.showMessageDialog(window,
					"No name was entered!");
			return false;
		} else if (s.length() >= 30) {
			JOptionPane.showMessageDialog(window,
					"The name was too long!");
			return false;
		}
		else if (s.indexOf(';') != -1)
		{
			JOptionPane.showMessageDialog(window, 
					"Sorry, the semicolon character is reserved by the program.\n" +
					"I'm sure you have plenty of other options though...");
			return false;
		}
		return true;
	}
	
	private boolean checkRemove (String s)
	{
		if (s == null)
			return false;
		s = s.trim();
		if (s.equals("Name")) {
			JOptionPane
					.showMessageDialog(window,
							"Can't remove 'Name'!\n It is required for the program to function!");
			return false;
		} else if (s.length() == 0 || s.length() >= 30) {
			JOptionPane.showMessageDialog(window,
					"Not a possible name!");
			return false;
		}else if (s.indexOf(';') != -1)
		{
			JOptionPane.showMessageDialog(window, 
					"There is no way you could possibly have a semicolon in a name!\n" +
					"Seriously, have you been looking at my code or something...?");
			return false;
		}
		return true;
	}

	private Main() {
		window = new JFrame("Team Maker");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new BoxLayout(window.getContentPane(),
				BoxLayout.Y_AXIS));
		
		pool = new Pool(window);

		menuBar = new JMenuBar();
		menu = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem load = new JMenuItem("Load");

		save.addActionListener(this);
		load.addActionListener(this);
		
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		menu.add(save);
		menu.add(load);
		menuBar.add(menu);

		buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton addFieldButton = new JButton("Add Field");
		JButton removeFieldButton = new JButton("Remove Field");
		JButton addMemberButton = new JButton("Add Member");
		JButton removeMemberButton = new JButton("Remove Member");
		JButton quickFieldButton = new JButton ("Add Field (quick)");

		addFieldButton.addActionListener(this);
		removeFieldButton.addActionListener(this);
		addMemberButton.addActionListener(this);
		removeMemberButton.addActionListener(this);
		quickFieldButton.addActionListener(this);
		
		addFieldButton.setMnemonic(KeyEvent.VK_B);
		removeFieldButton.setMnemonic(KeyEvent.VK_Q);
		addMemberButton.setMnemonic(KeyEvent.VK_M);
		removeMemberButton.setMnemonic(KeyEvent.VK_W);
		quickFieldButton.setMnemonic(KeyEvent.VK_N);

		current = new JPanel();
		scrollLayout = new SpringLayout();
		current.setLayout(scrollLayout);
		scrollPane = new JScrollPane(current);
		current.add(new JLabel("Name"));
		scrollLayout.putConstraint(SpringLayout.WEST, current.getComponent(0),
				140, SpringLayout.WEST, current);
		scrollLayout.putConstraint(SpringLayout.NORTH, current.getComponent(0),
				5, SpringLayout.NORTH, current);
		current.setPreferredSize(new Dimension(900, 230));

		buttonPane.add(addFieldButton);
		buttonPane.add(removeFieldButton);
		buttonPane.add(addMemberButton);
		buttonPane.add(removeMemberButton);
		buttonPane.add(quickFieldButton);
		buttonPane.setVisible(true);

		JButton generateTeamsButton = new JButton("Generate Teams");
		generateTeamsButton.addActionListener(this);
		generateTeamsButton.setMnemonic(KeyEvent.VK_G);

		String[] temp = new String[98];
		for (int i = 2; i < 100; i++) {
			temp[i - 2] = "" + i;
		}
		numTeamspinner = new JSpinner(new SpinnerListModel(temp));
		numTeamspinner.setPreferredSize(new Dimension(40, 25));

		generatePane = new JPanel();
		generatePane.setLayout(new FlowLayout(FlowLayout.CENTER));
		generatePane.add(generateTeamsButton);
		generatePane.add(numTeamspinner);

		fileChooser = new JFileChooser();
		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("tmkr");
		fileChooser.setFileFilter(filter);

		window.setJMenuBar(menuBar);
		window.add(buttonPane);
		window.add(scrollPane);
		window.add(generatePane);
		window.setVisible(true);
		window.pack();
		
		isBoolean = false;
		lastLower = 0;
		lastUpper = 10;
		
		columnWidths = new ArrayList<Integer> ();
		columnWidths.add(new Integer(305));
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Member> randomizePool()
	{
		ArrayList<Member> initial = (ArrayList<Member>) pool.getMembers()
				.clone();
		ArrayList<Member> ret = new ArrayList<Member>();
		for (int i = 0; i < pool.getNumMembers(); i++) {
			int rand = (int) (initial.size() * Math.random());
			Member temp = initial.remove(rand);
			ret.add(temp);
		}
		return ret;
	}
	
	private void addField ()
	{
		String s = (String) JOptionPane.showInputDialog(window,
				"Please enter the name of the new field:\n",
				"Enter field name", JOptionPane.PLAIN_MESSAGE, null, null,
				"");
		if (!checkAdd(s))
			return;

		boolean attempt = pool.addField(s);
		if (!attempt) {
			JOptionPane.showMessageDialog(window,
					"Duplicate fields are not allowed!");
			return;
		}
		int lowerLimit = 0, upperLimit = 10;
		int num = JOptionPane.showOptionDialog(window,
				"What type of field would you like?",
				"Choose field type",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE,
				null, new Object [] {"Numeric", "True/False"},
				"Numeric");
		if (num == JOptionPane.CLOSED_OPTION)
		{
			pool.removeField(s);
			return;
		}
		else if (num == JOptionPane.YES_OPTION)
		{
			boolean proper = false;
			while (proper != true)
			{
				String t = (String) JOptionPane.showInputDialog(window, 
					"Please enter the lower limit for the field:\n",
					"Enter lower limit", JOptionPane.PLAIN_MESSAGE,
					null, null, "" + lastLower);
				try
				{
					if (t == null)
					{
						pool.removeField(s);
						return;
					}
					lowerLimit = Integer.parseInt(t);
					if (lowerLimit >= 0)
						proper = true;
					else
						JOptionPane.showMessageDialog(window,
							"Please enter a positive integer!");
				}
				catch (Exception x)
				{
					JOptionPane.showMessageDialog(window,
							"Please enter a positive integer!");
				}
			}
			proper = false;
			while (proper != true)
			{
				String t = (String) JOptionPane.showInputDialog(window, 
						"Please enter the upper limit for the field:\n",
						"Enter upper limit", JOptionPane.PLAIN_MESSAGE,
						null, null, "" + lastUpper);
				try
				{
					if (t == null)
					{
						pool.removeField(s);
						return;
					}
					upperLimit = Integer.parseInt(t);
					if (upperLimit > lowerLimit)
						proper = true;
					else
						JOptionPane.showMessageDialog(window, 
								"Upper limit must be greater than lower limit!");
				}
				catch (Exception x)
				{
					JOptionPane.showMessageDialog(window,
							"Please enter a positive integer!");
				}
			}
			isBoolean = false;
		}
		else
		{
			lowerLimit = 0;
			upperLimit = 1;
			isBoolean = true;
		}
		lastLower = lowerLimit;
		lastUpper = upperLimit;
		
		pool.getField(pool.getNumFields() - 1).setLimits(lowerLimit, upperLimit);
		if (isBoolean)
			pool.getField(pool.getNumFields() - 1).setBoolean(true);
		JButton jb = new JButton (s);
		jb.addActionListener(this);
		current.add(jb, pool.getNumFields());
		int horDistance = 0;
		for (int i = 0; i < columnWidths.size(); i++)
		{
			horDistance += columnWidths.get(i).intValue();
		}
		scrollLayout.putConstraint(SpringLayout.WEST,
				current.getComponent(pool.getNumFields()),
				horDistance, SpringLayout.WEST, current);
		scrollLayout.putConstraint(SpringLayout.NORTH,
				current.getComponent(pool.getNumFields()), 5,
				SpringLayout.NORTH, current);
		for (int i = 0; i < pool.getNumMembers(); i++) {
			String [] options = new String[upperLimit - lowerLimit + 1];
			for (int j = 0; j < options.length; j++)
			{
				options[j] = "" + (lowerLimit + j);
			}
			if (isBoolean)
			{
				options[0] = "True";
				options[1] = "False";
			}
			JSpinner temp = new JSpinner(new SpinnerListModel(options));
			temp.setPreferredSize(new Dimension(75, 25));
			current.add(temp, (i + 2) * pool.getNumFields() + (i + 1));
			scrollLayout.putConstraint(
					SpringLayout.WEST,
					current.getComponent((i + 2) * pool.getNumFields()
							+ (i + 1)), horDistance,
					SpringLayout.WEST, current);
			scrollLayout.putConstraint(
					SpringLayout.NORTH,
					current.getComponent((i + 2) * pool.getNumFields()
							+ (i + 1)), 25 * (i + 1) + 5,
					SpringLayout.NORTH, current);
		}
	}
	
	private void addField (boolean isBoolean, int lowerLimit, int upperLimit)
	{
		String s = (String) JOptionPane.showInputDialog(window,
				"Please enter the name of the new field:\n",
				"Enter field name", JOptionPane.PLAIN_MESSAGE, null, null,
				"");
		if (!checkAdd(s))
			return;

		boolean attempt = pool.addField(s);
		if (!attempt) {
			JOptionPane.showMessageDialog(window,
					"Duplicate fields are not allowed!");
			return;
		}
		pool.getField(pool.getNumFields() - 1).setLimits(lowerLimit, upperLimit);
		if (isBoolean)
			pool.getField(pool.getNumFields() - 1).setBoolean(true);
		JButton jb = new JButton (s);
		jb.addActionListener(this);
		current.add(jb, pool.getNumFields());
		int horDistance = 0;
		for (int i = 0; i < columnWidths.size(); i++)
		{
			horDistance += columnWidths.get(i).intValue();
		}
		scrollLayout.putConstraint(SpringLayout.WEST,
				current.getComponent(pool.getNumFields()),
				horDistance, SpringLayout.WEST, current);
		scrollLayout.putConstraint(SpringLayout.NORTH,
				current.getComponent(pool.getNumFields()), 5,
				SpringLayout.NORTH, current);
		for (int i = 0; i < pool.getNumMembers(); i++) {
			String [] options = new String[upperLimit - lowerLimit + 1];
			for (int j = 0; j < options.length; j++)
			{
				options[j] = "" + (lowerLimit + j);
			}
			if (isBoolean)
			{
				options [0] = "True";
				options [1] = "False";
			}
			JSpinner temp = new JSpinner(new SpinnerListModel(options));
			temp.setPreferredSize(new Dimension(75, 25));
			current.add(temp, (i + 2) * pool.getNumFields() + (i + 1));
			scrollLayout.putConstraint(
					SpringLayout.WEST,
					current.getComponent((i + 2) * pool.getNumFields()
							+ (i + 1)), horDistance,
					SpringLayout.WEST, current);
			scrollLayout.putConstraint(
					SpringLayout.NORTH,
					current.getComponent((i + 2) * pool.getNumFields()
							+ (i + 1)), 25 * (i + 1) + 5,
					SpringLayout.NORTH, current);
		}
	}
	
	private void addMember ()
	{
		String s = ((String) JOptionPane.showInputDialog(window,
				"Please enter the name of the new member:\n",
				"Enter member name", JOptionPane.PLAIN_MESSAGE, null, null,
				""));
		if (!checkAdd(s))
			return;

		boolean attempt = pool.addMember(s);
		if (!attempt) {
			JOptionPane.showMessageDialog(window,
					"Duplicate members are not allowed!");
			return;
		}

		JButton jb = new JButton(s);
		jb.addActionListener(this);
		jb.setPreferredSize(new Dimension(300, 25));
		current.add(jb);
		scrollLayout.putConstraint(SpringLayout.WEST,
				current.getComponent(current.getComponentCount() - 1), 5,
				SpringLayout.WEST, current);
		scrollLayout.putConstraint(SpringLayout.NORTH,
				current.getComponent(current.getComponentCount() - 1),
				25 * pool.getNumMembers() + 5, SpringLayout.NORTH, current);

		for (int i = 0; i < pool.getNumFields(); i++) {
			String [] options = new String [pool.getField(i).getUpperLimit() - pool.getField(i).getLowerLimit() + 1];
			for (int j = 0; j < options.length; j++)
			{
				options[j] = "" + (pool.getField(i).getLowerLimit() + j);
			}
			if (pool.getField(i).isBoolean())
			{
				options[0] = "True";
				options[1] = "False";
			}
			JSpinner temp = new JSpinner(new SpinnerListModel(options));
			temp.setPreferredSize(new Dimension(75, 25));
			current.add(temp);
			int distance = 0;
			for (int j = 0; j <= i; j++)
			{
				distance += columnWidths.get(j).intValue();
			}
			scrollLayout.putConstraint(SpringLayout.WEST,
					current.getComponent(current.getComponentCount() - 1),
					distance, SpringLayout.WEST, current);
			scrollLayout.putConstraint(SpringLayout.NORTH,
					current.getComponent(current.getComponentCount() - 1),
					25 * pool.getNumMembers() + 5, SpringLayout.NORTH,
					current);
		}
		if (pool.getNumMembers() > 8)
			current.setPreferredSize(new Dimension(current.getWidth(),
					current.getHeight() + 25));
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonPane.getComponent(0)) {
			addField();
		} else if (e.getSource() == buttonPane.getComponent(1)) {
			String s = ((String) JOptionPane.showInputDialog(window,
					"Please enter the name of the field to be removed:\n",
					"Enter field name", JOptionPane.PLAIN_MESSAGE, null, null,
					""));
			if(!checkRemove(s))
				return;

			int loc = pool.removeField(s);
			if (loc == -1) {
				JOptionPane.showMessageDialog(window, "No such field exists!");
				return;
			}
			columnWidths.remove(loc + 1);
			for (int i = 0; i < pool.getNumMembers() + 1; i++) {
				current.remove(i * (pool.getNumFields() + 1) + (loc + 1));
				if (loc != pool.getNumFields())
				{
					for (int j = loc; j < pool.getNumFields(); j++)
					{
						int distance = 0;
						for (int k = 0; k <= j; k++)
						{
							distance += columnWidths.get(k).intValue();
						}
						if (current.getComponent(j + 1).getWidth() > 75 && i != 0)
							distance += (current.getComponent(j + 1).getWidth() - 75)/2;
						scrollLayout.putConstraint(
								SpringLayout.WEST,
								current.getComponent(i
										* (pool.getNumFields() + 1) + (j + 1)),
								distance, SpringLayout.WEST, current);
					}
				}
			}			
			int distance = 0;
			for (int i = 0; i < columnWidths.size(); i++)
			{
				distance += columnWidths.get(i).intValue();
			}
			if (current.getWidth() > distance && current.getWidth() > 900)
				current.setPreferredSize(new Dimension(
						Math.max(900, distance), current.getHeight()));
		} else if (e.getSource() == buttonPane.getComponent(2)) {
			addMember();
		} else if (e.getSource() == buttonPane.getComponent(3)) {
			String s = ((String) JOptionPane.showInputDialog(window,
					"Please enter the name of the member to be removed:\n",
					"Enter member name", JOptionPane.PLAIN_MESSAGE, null, null,
					""));
			if (!checkRemove(s))
				return;

			int loc = pool.removeMember(s);
			if (loc == -1) {
				JOptionPane.showMessageDialog(window, "No such member exists!");
				return;
			}

			for (int i = 0; i < pool.getNumFields() + 1; i++) {
				current.remove((loc + 1) * (pool.getNumFields() + 1));
				if (loc != pool.getNumMembers()) {
					for (int j = loc; j < pool.getNumMembers(); j++) {
						scrollLayout.putConstraint(
								SpringLayout.NORTH,
								current.getComponent((j + 2)
										* (pool.getNumFields() + 1) - 1),
								25 * (j + 1) + 5, SpringLayout.NORTH, current);
					}
				}
			}
			if (pool.getNumMembers() > 7)
				current.setPreferredSize(new Dimension(current.getWidth(),
						current.getHeight() - 25));
		} 
		else if (e.getSource() == buttonPane.getComponent(4))
		{
			addField(isBoolean, lastLower, lastUpper);
		}
		else if (e.getSource() == menu.getItem(0)) {
			storeValues();
			String fileName;
			int ret = fileChooser.showSaveDialog(window);
			if (ret == JFileChooser.APPROVE_OPTION) {
				try {
					fileName = fileChooser.getSelectedFile().getCanonicalPath();
				} catch (IOException x) {
					JOptionPane.showMessageDialog(window, "Sorry, something went wrong! The program could not save to this location!");
					return;
				}
					if (fileName.indexOf(".tmkr") == -1)
						fileName = fileName + ".tmkr";
					pool.writeToFile(fileName);
			}
		} else if (e.getSource() == menu.getItem(1)) {
			String fileName;
			int ret = fileChooser.showOpenDialog(window);
			if (ret == JFileChooser.APPROVE_OPTION) {
					try {
						fileName = fileChooser.getSelectedFile().getCanonicalPath();
					} catch (IOException x) {
						JOptionPane.showMessageDialog(window, "Sorry, something went wrong! The program could not load from this location!");
						return;
					}
					pool.readFromFile(fileName);
					current.removeAll();
					columnWidths = new ArrayList<Integer> ();
					current.add(new JLabel("Name"));
					columnWidths.add(new Integer (305));
					scrollLayout.putConstraint(SpringLayout.WEST,
							current.getComponent(0), 140, SpringLayout.WEST,
							current);
					scrollLayout.putConstraint(SpringLayout.NORTH,
							current.getComponent(0), 5, SpringLayout.NORTH,
							current);
					for (int i = 1; i <= pool.getNumFields(); i++) {
						JButton temp = new JButton(pool.getField(i - 1).toString());
						temp.addActionListener(this);
						current.add(temp);
						int distance = 0;
						for (int j = 0; j < i; j++)
						{
							distance += current.getComponent(j).getWidth();
						}
						scrollLayout.putConstraint(SpringLayout.WEST,
								current.getComponent(i), distance,
								SpringLayout.WEST, current);
						scrollLayout.putConstraint(SpringLayout.NORTH,
								current.getComponent(i), 5,
								SpringLayout.NORTH, current);
					}
					current.revalidate();
					window.validate();
					for (int i = 1; i <= pool.getNumFields(); i++)
					{
						if (current.getComponent(i).getWidth() < 75)
							current.getComponent(i).setPreferredSize (new Dimension (75, 25));
					}
					current.revalidate();
					window.validate();
					int width = 305;
					for (int i = 1; i <= pool.getNumFields(); i++)
					{
						columnWidths.add(new Integer(current.getComponent(i).getWidth()));
						width += columnWidths.get(i).intValue();
					}
					current.setPreferredSize(new Dimension(width, 30));
					for (int j = 0; j < pool.getNumMembers(); j++) {
						JButton temp = new JButton(pool.getMember(j).getName());
						temp.setPreferredSize(new Dimension(300, 25));
						temp.addActionListener(this);
						current.add(temp);
						scrollLayout.putConstraint(SpringLayout.WEST, current
								.getComponent(current.getComponentCount() - 1),
								5, SpringLayout.WEST, current);
						scrollLayout.putConstraint(SpringLayout.NORTH, current
								.getComponent(current.getComponentCount() - 1),
								25 * (j + 1) + 5, SpringLayout.NORTH, current);
						for (int k = 0; k < pool.getNumFields(); k++) {
							int val = pool.getMember(j).getValueAtLocation(k);
							String [] options = new String [pool.getField(k).getUpperLimit() - pool.getField(k).getLowerLimit() + 1];
							for (int i = 0; i < options.length; i++)
							{
								options[i] = "" + (pool.getField(k).getLowerLimit() + i);
							}
							if(pool.getField(k).isBoolean())
							{
								options[0] = "True";
								options[1] = "False";
							}
							JSpinner spin = new JSpinner(new SpinnerListModel(options));
							spin.setPreferredSize(new Dimension(75, 25));
							if (!pool.getField(k).isBoolean())
								spin.setValue("" + val);
							else
							{
								if(val == 1)
									spin.setValue("True");
								else
									spin.setValue("False");
							}
							current.add(spin);
							int dist = 0;
							for (int i = 0; i <= k; i++)
							{
								dist += columnWidths.get(i).intValue();
							}
							if (columnWidths.get(k).intValue() > 75)
								dist += (columnWidths.get(k).intValue() - 75)/2;
							scrollLayout.putConstraint(SpringLayout.WEST,
									current.getComponent(current
											.getComponentCount() - 1),
									dist, SpringLayout.WEST,
									current);
							scrollLayout.putConstraint(SpringLayout.NORTH,
									current.getComponent(current
											.getComponentCount() - 1),
									25 * (j + 1) + 5, SpringLayout.NORTH,
									current);
						}
					}
					int height = Math.max(230, 25 * (pool.getNumMembers() + 1));
					current.setPreferredSize(new Dimension(width, height));
					current.revalidate();
					window.validate();
			}
		} else if (e.getSource() == printButton) {
			try 
			{
				teamTable.print(JTable.PrintMode.NORMAL);
			} catch (PrinterException x) {
				JOptionPane.showMessageDialog(window, "Sorry, something went wrong! The program could not print the file!");
			}
		} else if (e.getSource() == generatePane.getComponent(0)){
			storeValues();
			if (pool.getNumMembers() == 0) {
				JOptionPane.showMessageDialog(window,
						"No members have been entered!");
				return;
			}
			if (pool.getNumFields() == 0) {
				JOptionPane.showMessageDialog(window,
						"No fields have been entered!");
				return;
			}
			Integer tempNum = new Integer((String) numTeamspinner.getValue());
			int numTeams = tempNum.intValue();
			if (numTeams > pool.getNumMembers())
			{
				JOptionPane.showMessageDialog(window, "You can't have more teams than members!");
				return;
			}
			ArrayList<Member> sorted = randomizePool();
			int numFields = pool.getNumFields();
			int numMembers = pool.getNumMembers();
			int[] totals = new int[numFields];
			for (int i = 0; i < numFields; i++) {
				int num = 0;
				for (int j = 0; j < numMembers; j++)
					num += pool.getMember(j).getValueAtLocation(i);
				totals[i] = num;
			}
			int[] limits = new int[numFields];
			for (int i = 0; i < numFields; i++) {
				int num = totals[i] / numTeams;
				if (totals[i] % numTeams != 0)
					num += 1;
				limits[i] = num;
			}
			boolean done = false;
			ArrayList<Team> teams = new ArrayList<Team>();
			while (!done) {
				int currentSize = sorted.size();
				int counter = 0;
				boolean fit = false;
				while (counter != currentSize) {
					Member temp = sorted.get(counter);
					for (int i = 0; i < teams.size(); i++) {
						if (teams.get(i).testFit(temp)) {
							teams.get(i).add(temp);
							sorted.remove(counter);
							currentSize--;
							fit = true;
							break;
						}
					}
					if (!fit) {
						if (teams.size() < numTeams) {
							teams.add(new Team(limits, "Team"
									+ (teams.size() + 1)));
							if (teams.get(teams.size() - 1).testFit(temp)) {
								teams.get(teams.size() - 1).add(temp);
								sorted.remove(counter);
								currentSize--;
							} else
								counter++;
						} else
							counter++;
					} else
						fit = false;
				}
				if (sorted.isEmpty())
					done = true;
				else {
					for (int i = 0; i < teams.size(); i++)
						teams.get(i).incrementLimits();
				}
			}
			for (int i = teams.size(); i < numTeams; i++) {
				teams.add(new Team(limits, "Team" + (i + 1)));
			}
			int largestTeam = 0;
			for (int i = 0; i < numTeams; i++) {
				if (teams.get(i).size() > largestTeam) {
					largestTeam = teams.get(i).size();
				}
			}
			Object[][] teamMembers = new Object[largestTeam][numTeams];
			for (int i = 0; i < numTeams; i++) {
				Object[] arr = teams.get(i).toArray();
				int count;
				for (count = 0; count < arr.length; count++)
					teamMembers[count][i] = arr[count];
				for (count = arr.length; count < largestTeam; count++)
					teamMembers[count][i] = null;
			}
			teamWindow = new TeamWindow(window, true);
			printButton = new JButton("Print Teams");
			printButton.addActionListener(this);
			DefaultTableModel model = new DefaultTableModel();
	        for (int i = 0; i < numTeams; i++) {
	            model.addColumn("Team " + (i + 1));
	        }
	        for (int i = 0; i < largestTeam; i++)
	        {
	            Vector<Object> row = new Vector<Object>();
	            for (int j = 0; j < numTeams; j++)
	            {
	                row.add(teamMembers[i][j]);
	            }
	            model.addRow(row);
	        }
			JPanel teamPanel = new JPanel(new GridLayout(1, 0));
			teamTable = new JTable(model);
			teamTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			FontMetrics metrics = teamTable.getFontMetrics(teamTable.getFont());
			int widest = metrics.stringWidth("Team " + numTeams);
			for (int i = 0; i < largestTeam; i++)
			{
				for (int j = 0; j < numTeams; j++)
				{
					if (teamMembers[i][j] == null)
						continue;
					else if (metrics.stringWidth(((Member)teamMembers[i][j]).toString()) > widest)
						widest = metrics.stringWidth(((Member)teamMembers[i][j]).toString());
				}
			}
			widest += 10;
			for (int i = 0; i < numTeams; i++)
			{
				teamTable.getColumnModel().getColumn(i).setPreferredWidth(widest);
			}
			JScrollPane teamScroll = new JScrollPane(teamTable);
			teamScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			teamScroll.setPreferredSize(new Dimension (teamTable.getColumnCount() * 230, teamTable.getRowCount() * 16));
			teamPanel.add(teamScroll);
			teamWindow.add(teamPanel, BorderLayout.CENTER);
			teamWindow.add(printButton, BorderLayout.SOUTH);
			teamWindow.setSize(920, 500);
			teamWindow.setVisible(true);
		}
		else if (e.getActionCommand().equals("Remove") && e.getSource() == modifyField.getContentPane().getComponent(0))
		{
			if (isField)
			{
				int loc = pool.removeField(currentField);
				columnWidths.remove(loc + 1);
				for (int i = 0; i < pool.getNumMembers() + 1; i++) {
					current.remove(i * (pool.getNumFields() + 1) + (loc + 1));
					if (loc != pool.getNumFields()) {
						for (int j = loc; j < pool.getNumFields(); j++)
						{
							int distance = 0;
							for (int k = 0; k <= j; k++)
							{
								distance += columnWidths.get(k).intValue();
							}
							if (current.getComponent(j + 1).getWidth() > 75 && i != 0)
								distance += (current.getComponent(j + 1).getWidth() - 75)/2;
							scrollLayout.putConstraint(
									SpringLayout.WEST,
									current.getComponent(i
											* (pool.getNumFields() + 1) + (j + 1)),
									distance, SpringLayout.WEST, current);
						}
					}
				}
				int distance = 0;
				for (int i = 0; i < columnWidths.size(); i++)
				{
					distance += columnWidths.get(i).intValue();
				}
				if (current.getWidth() > distance && current.getWidth() > 900)
					current.setPreferredSize(new Dimension(
							Math.max(900, distance), current.getHeight()));
			}
			else
			{
				int loc = pool.removeMember(currentField);
				for (int i = 0; i < pool.getNumFields() + 1; i++) {
					current.remove((loc + 1) * (pool.getNumFields() + 1));
					if (loc != pool.getNumMembers()) {
						for (int j = loc; j < pool.getNumMembers(); j++)
						{
							scrollLayout.putConstraint(
									SpringLayout.NORTH,
									current.getComponent((j + 2)
											* (pool.getNumFields() + 1) - 1),
									25 * (j + 1) + 5, SpringLayout.NORTH, current);
						}
					}
				}
				if (pool.getNumMembers() > 7)
					current.setPreferredSize(new Dimension(current.getWidth(),
							current.getHeight() - 25));
			}
			modifyField.dispose();
		}
		else if (e.getActionCommand().equals("Change") && e.getSource() == modifyField.getContentPane().getComponent(1))
		{
			String s = ((String) JOptionPane.showInputDialog(window,
					"Please enter the new name:\n",
					"Enter new name", JOptionPane.PLAIN_MESSAGE, null, null,
					currentField));
			if (!checkAdd(s))
			{
				return;
			}
			if (isField)
			{
				boolean attempt = pool.setFieldName(currentLoc, s);
				if (!attempt) {
					JOptionPane.showMessageDialog(window,
							"Duplicate fields are not allowed!");
					return;
				}
				((JButton)current.getComponent(currentLoc + 1)).setText(s);
				FontMetrics metrics = current.getComponent(currentLoc + 1).getFontMetrics(current.getComponent(currentLoc + 1).getFont());
				int width = metrics.stringWidth(s);
				if (width > 35)
					current.getComponent(currentLoc + 1).setPreferredSize(new Dimension(width + 40, 25));
				else
					current.getComponent(currentLoc + 1).setPreferredSize(new Dimension(75, 25));
				columnWidths.set(currentLoc + 1, new Integer(Math.max (width + 40, 75)));
			}
			else
			{
				boolean attempt = pool.setMemberName(currentLoc, s);if (!attempt) {
					JOptionPane.showMessageDialog(window,
							"Duplicate members are not allowed!");
					return;
				}
				((JButton)current.getComponent((currentLoc + 1) * (pool.getNumFields() + 1))).setText(s);
			}
			modifyField.dispose();
		}
		else
		{
			isField = false;
			JButton temp = (JButton) e.getSource();
			for (int i = 1; i < pool.getNumFields() + 1; i++)
			{
				if (current.getComponent(i) == temp)
				{
					isField = true;
					break;
				}
			}
			currentField = e.getActionCommand();
			currentLoc = -1;
			if (isField)
			{
				for (int i = 0; i < pool.getNumFields(); i++)
				{
					if (pool.getField(i).toString().equals(currentField))
					{
						currentLoc = i;
						break;
					}
				}
			}
			else
			{
				for (int i = 0; i < pool.getNumMembers(); i++)
				{
					if (pool.getMember(i).getName().equals(currentField))
					{
						currentLoc = i;
						break;
					}
				}
			}
			modifyField = new JDialog (window, currentField, true);
			modifyField.setLayout(new FlowLayout());
			modifyField.setLocationRelativeTo(window);
			JButton removeButton = new JButton ("Remove");
			JButton changeButton = new JButton ("Change");
			removeButton.addActionListener(this);
			changeButton.addActionListener(this);
			modifyField.add(removeButton);
			modifyField.add(changeButton);
			modifyField.setSize(new Dimension (200, 75));
			modifyField.setResizable (false);
			modifyField.setVisible(true);
		}
		current.revalidate();
		window.validate();
		if (pool.getNumFields() == columnWidths.size())
		{
			int newWidth = current.getComponent(pool.getNumFields()).getWidth();
			if (newWidth < 75)
			{
				current.getComponent(pool.getNumFields()).setPreferredSize(new Dimension(75, 25));
			}
			current.revalidate();
			window.validate();
			columnWidths.add(new Integer(Math.max(newWidth, 75)));
		}
		for (int j = 1; j <= pool.getNumFields(); j++)
		{
			int distance = 0;
			for (int i = 0; i < j; i++)
			{
				distance += columnWidths.get(i).intValue();
			}
			scrollLayout.putConstraint(SpringLayout.WEST,
					current.getComponent(j),
					distance, SpringLayout.WEST, current);
			if (current.getComponent(j).getWidth() > 75)
				distance += (current.getComponent(j).getWidth() - 75)/2;
			for (int i = 1; i <= pool.getNumMembers(); i++)
			{
					scrollLayout.putConstraint(SpringLayout.WEST,
						current.getComponent(i * (pool.getNumFields() + 1) + j),
						distance, SpringLayout.WEST, current);
			}
		}
		int distance = 0;
		for (int i = 0; i < columnWidths.size(); i++)
		{
			distance += columnWidths.get(i).intValue();
		}
		if (distance > 900)
			current.setPreferredSize(new Dimension(
					distance, current.getHeight()));
		else
			current.setPreferredSize(new Dimension(
					900, current.getHeight()));
		current.revalidate();
		window.validate();
		window.repaint();
	}

	public static void main(String[] args) {
		new Main();
	}

}