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

	private void storeValues() {
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
				try {
					num = new Integer(temp);
				} catch (Exception x) {
					num = 1;
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
		else if (s.indexOf(178) != -1)
		{
			JOptionPane.showMessageDialog(window, 
					"Sorry, this character is reserved by the program.\n" +
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
		}else if (s.indexOf(178) != -1)
		{
			JOptionPane.showMessageDialog(window, 
					"There is no way you could possibly have this name!\n" +
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

		menu.add(save);
		menu.add(load);
		menuBar.add(menu);

		buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton addFieldButton = new JButton("Add Field");
		JButton removeFieldButton = new JButton("Remove Field");
		JButton addMemberButton = new JButton("Add Member");
		JButton removeMemberButton = new JButton("Remove Member");

		addMemberButton.addActionListener(this);
		removeMemberButton.addActionListener(this);
		addFieldButton.addActionListener(this);
		removeFieldButton.addActionListener(this);

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
		buttonPane.setVisible(true);

		JButton generateTeamsButton = new JButton("Generate Teams");
		generateTeamsButton.addActionListener(this);

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
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Member> randomizePool() {
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

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonPane.getComponent(0)) {
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
						null, null, "0");
					try
					{
						if (t == null)
						{
							pool.removeField(s);
							return;
						}	
						lowerLimit = Integer.parseInt(t);
						proper = true;
					}
					catch (Exception x)
					{
						JOptionPane.showMessageDialog(window,
								"Please enter an integer!");
					}
				}
				proper = false;
				while (proper != true)
				{
					String t = (String) JOptionPane.showInputDialog(window, 
							"Please enter the upper limit for the field:\n",
							"Enter upper limit", JOptionPane.PLAIN_MESSAGE,
							null, null, "10");
					try
					{
						if (t == null)
						{
							pool.removeField(s);
							return;
						}
						upperLimit = Integer.parseInt(t);
						proper = true;
						if (upperLimit < lowerLimit)
						{
							JOptionPane.showMessageDialog(window, 
									"Upper limit must be at least as large as lower limit!");
							proper = false;
						}
					}
					catch (Exception x)
					{
						JOptionPane.showMessageDialog(window,
								"Please enter an integer!");
					}
				}
			}
			else
			{
				lowerLimit = 0;
				upperLimit = 1;
			}
			pool.getField(pool.getNumFields() - 1).setLimits(lowerLimit, upperLimit);
			
			JButton jb = new JButton (s);
			jb.addActionListener(this);
			jb.setPreferredSize(new Dimension(300, 25));
			current.add(jb, pool.getNumFields());
			scrollLayout.putConstraint(SpringLayout.WEST,
					current.getComponent(pool.getNumFields()),
					300 * pool.getNumFields() + 5, SpringLayout.WEST, current);
			scrollLayout.putConstraint(SpringLayout.NORTH,
					current.getComponent(pool.getNumFields()), 5,
					SpringLayout.NORTH, current);
			for (int i = 0; i < pool.getNumMembers(); i++) {
				String [] options = new String[upperLimit - lowerLimit + 1];
				for (int j = lowerLimit; j <= upperLimit; j++)
				{
					options[j - lowerLimit] = "" + (lowerLimit + j);
				}
				JSpinner temp = new JSpinner(new SpinnerListModel(options));
				temp.setPreferredSize(new Dimension(40, 25));
				current.add(temp, (i + 2) * pool.getNumFields() + (i + 1));
				scrollLayout.putConstraint(
						SpringLayout.WEST,
						current.getComponent((i + 2) * pool.getNumFields()
								+ (i + 1)), 300 * pool.getNumFields() + 140,
						SpringLayout.WEST, current);
				scrollLayout.putConstraint(
						SpringLayout.NORTH,
						current.getComponent((i + 2) * pool.getNumFields()
								+ (i + 1)), 25 * (i + 1) + 5,
						SpringLayout.NORTH, current);
			}
			if (pool.getNumFields() > 2)
				current.setPreferredSize(new Dimension(
						current.getWidth() + 300, current.getHeight()));
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
			for (int i = 0; i < pool.getNumMembers() + 1; i++) {
				current.remove(i * (pool.getNumFields() + 1) + (loc + 1));
				if (loc != pool.getNumFields()) {
					for (int j = loc; j < pool.getNumFields(); j++) {
						scrollLayout.putConstraint(
								SpringLayout.WEST,
								current.getComponent(i
										* (pool.getNumFields() + 1) + (j + 1)),
								300 * (j + 1) + 5, SpringLayout.WEST, current);
					}
				}
			}
			if (pool.getNumFields() > 1)
				current.setPreferredSize(new Dimension(
						current.getWidth() - 300, current.getHeight()));
		} else if (e.getSource() == buttonPane.getComponent(2)) {
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
				JSpinner temp = new JSpinner(new SpinnerListModel(options));
				temp.setPreferredSize(new Dimension(40, 25));
				current.add(temp);
				scrollLayout.putConstraint(SpringLayout.WEST,
						current.getComponent(current.getComponentCount() - 1),
						300 * (i + 1) + 140, SpringLayout.WEST, current);
				scrollLayout.putConstraint(SpringLayout.NORTH,
						current.getComponent(current.getComponentCount() - 1),
						25 * pool.getNumMembers() + 5, SpringLayout.NORTH,
						current);
			}
			if (pool.getNumMembers() > 8)
				current.setPreferredSize(new Dimension(current.getWidth(),
						current.getHeight() + 25));
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
		} else if (e.getSource() == menu.getItem(0)) {
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
						JOptionPane.showMessageDialog(window, "Sorry, something went wrong! The program was could not load from this location!");
						return;
					}
					pool.readFromFile(fileName);
					current.removeAll();
					current.add(new JLabel("Name"));
					scrollLayout.putConstraint(SpringLayout.WEST,
							current.getComponent(0), 5, SpringLayout.WEST,
							current);
					scrollLayout.putConstraint(SpringLayout.NORTH,
							current.getComponent(0), 5, SpringLayout.NORTH,
							current);
					for (int i = 0; i < pool.getNumFields(); i++) {
						JButton temp = new JButton(pool.getField(i).toString());
						temp.addActionListener(this);
						temp.setPreferredSize(new Dimension(300, 25));
						current.add(temp);
						scrollLayout.putConstraint(SpringLayout.WEST,
								current.getComponent(i + 1), 300 * (i + 1) + 5,
								SpringLayout.WEST, current);
						scrollLayout.putConstraint(SpringLayout.NORTH,
								current.getComponent(i + 1), 5,
								SpringLayout.NORTH, current);
					}
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
							JSpinner spin = new JSpinner(new SpinnerListModel(options));
							spin.setPreferredSize(new Dimension(40, 25));
							spin.setValue("" + val);
							current.add(spin);
							scrollLayout.putConstraint(SpringLayout.WEST,
									current.getComponent(current
											.getComponentCount() - 1),
									300 * (k + 1) + 140, SpringLayout.WEST,
									current);
							scrollLayout.putConstraint(SpringLayout.NORTH,
									current.getComponent(current
											.getComponentCount() - 1),
									25 * (j + 1) + 5, SpringLayout.NORTH,
									current);
						}
					}
					int width = Math.max(715, 300 * (pool.getNumFields() + 1));
					int height = Math.max(230, 25 * (pool.getNumMembers() + 1));
					current.setPreferredSize(new Dimension(width, height));
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
			for (int i = 0; i < numTeams; i++)
				teamTable.getColumnModel().getColumn(i).setPreferredWidth(230);
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
				for (int i = 0; i < pool.getNumMembers() + 1; i++) {
					current.remove(i * (pool.getNumFields() + 1) + (loc + 1));
					if (loc != pool.getNumFields()) {
						for (int j = loc; j < pool.getNumFields(); j++)
						{
							scrollLayout.putConstraint(
									SpringLayout.WEST,
									current.getComponent(i
											* (pool.getNumFields() + 1) + (j + 1)),
									300 * (j + 1) + 5, SpringLayout.WEST, current);
						}
					}
				}
				if (pool.getNumFields() > 1)
					current.setPreferredSize(new Dimension(
							current.getWidth() - 300, current.getHeight()));
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
					""));
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
			JButton removeButton = new JButton ("Remove");
			JButton changeButton = new JButton ("Change");
			removeButton.addActionListener(this);
			changeButton.addActionListener(this);
			modifyField.add(removeButton);
			modifyField.add(changeButton);
			modifyField.pack();
			modifyField.setVisible(true);
		}
		current.revalidate();
		window.validate();
		window.repaint();
	}

	public static void main(String[] args) {
		new Main();
	}

}