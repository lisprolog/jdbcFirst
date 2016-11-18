import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;

public class CalendarProgramN{

	//JDBC drive name and database URL
	static final String JDBC_DRIVER = "org.postgresql.Driver";//"com.mysql.jdbc.Driver"
	static final String DB_URL = ""; 

	//database credentials
	static final String USER = "";	
	static final String PASS = "";	
	//calender
	static JLabel lblMonth, lblYear, lblDay;
	static JButton btnPrev, btnNext;
	static JTable tblCalendar;
	static JComboBox cmbYear, cmbDay, cmbEmpl;
	static JFrame frmMain;
	static Container pane;
	static DefaultTableModel mtblCalendar; //
	static JScrollPane stblCalendar; //The scrollpane
	static JPanel pnlCalendar;
	static int realYear, realMonth, realDay, currentYear, currentMonth, currentDay, columnCount, rowCount;
	static String currentEmp;

	//excel style?
	static final JTable table;
	static final DefaultTableModel model;
    
    public static void main (String args[]){

	columnCount = 8;
    	rowCount = 24;

	// Copied Calender from refreshCalender
	int month, year, day, nod, som;
        GregorianCalendar cal0 = new GregorianCalendar(); //Create calendar
        realDay = cal0.get(GregorianCalendar.DAY_OF_MONTH); //Get day
        realMonth = cal0.get(GregorianCalendar.MONTH); //Get month
        realYear = cal0.get(GregorianCalendar.YEAR); //Get year
        currentMonth = realMonth; //Match month and year
        currentYear = realYear;
	currentDay = realDay;

        nod = cal0.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        som = cal0.get(GregorianCalendar.DAY_OF_WEEK);

        //Look and feel
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
        catch (ClassNotFoundException e) {}
        catch (InstantiationException e) {}
        catch (IllegalAccessException e) {}
        catch (UnsupportedLookAndFeelException e) {}
        
        //Prepare frame
        frmMain = new JFrame ("Group 4 Calendar"); //Create frame
        frmMain.setSize(630, 675); //Set size to 400x400 pixels
        pane = frmMain.getContentPane(); //Get content pane
        pane.setLayout(null); //Apply null layout
        frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close when X is clicked
        
        //Create controls
        lblMonth = new JLabel ("January");
        lblYear = new JLabel ("year:");
	lblDay = new JLabel("day:");
        cmbYear = new JComboBox();
	cmbDay = new JComboBox(); 	// neu: ComboDay
	cmbEmpl = new JComboBox();	// neu: ComboEmpl
        btnPrev = new JButton ("Previous");
        btnNext = new JButton ("Next");
        mtblCalendar = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};//false
        tblCalendar = new JTable(mtblCalendar);
        stblCalendar = new JScrollPane(tblCalendar);
        pnlCalendar = new JPanel(null);
        
        //Set border
        pnlCalendar.setBorder(BorderFactory.createTitledBorder("Calendar"));
        
        //Register action listeners
        btnPrev.addActionListener(new btnPrev_Action());
        btnNext.addActionListener(new btnNext_Action());
        cmbYear.addActionListener(new cmbYear_Action());
	cmbDay.addActionListener(new cmbDay_Action()); 		// neu: ComboDay
//	cmbEmpl.addActionListener(new cmbEmpl_Action());	// neu: ComboEmpl
        
        //Add controls to pane
        pane.add(pnlCalendar);
        pnlCalendar.add(lblMonth);
        pnlCalendar.add(lblYear);
	pnlCalendar.add(lblDay);
        pnlCalendar.add(cmbYear);
	pnlCalendar.add(cmbDay); // neu: ComboDay
	pnlCalendar.add(cmbEmpl);// neu: ComboEmpl
        pnlCalendar.add(btnPrev);
        pnlCalendar.add(btnNext);
        pnlCalendar.add(stblCalendar);
        
        //Set bounds
        pnlCalendar.setBounds(0, 0, 620, 635); // 0, 0
        lblMonth.setBounds(160-lblMonth.getPreferredSize().width/2, 25, 100, 25);
        lblYear.setBounds(10, 605, 80, 20);
	lblDay.setBounds(400, 25, 100, 25);
        cmbYear.setBounds(230, 605, 80, 20);
	cmbDay.setBounds(450, 25, 80,20); 	// neu: ComboDay
	cmbEmpl.setBounds(450, 605, 140, 20);	// neu: ComboEmpl
        btnPrev.setBounds(10, 25, 80, 25);	
        btnNext.setBounds(260, 25, 80, 25);
        stblCalendar.setBounds(10, 50, 600, 550);// 10, 50, 300, 250
        
        //Make frame visible
        frmMain.setResizable(true);
        frmMain.setVisible(true);
        
        //Get real month/year
        GregorianCalendar cal = new GregorianCalendar(); //Create calendar
        realDay = cal.get(GregorianCalendar.DAY_OF_MONTH); //Get day
        realMonth = cal.get(GregorianCalendar.MONTH); //Get month
        realYear = cal.get(GregorianCalendar.YEAR); //Get year
        currentMonth = realMonth; //Match month and year
        currentYear = realYear;
	currentDay = realDay;
        
        //Add headers
        String[] headers = {"Time","Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}; //All headers
        for (int i=0; i<8; i++){
            mtblCalendar.addColumn(headers[i]);
        }

        tblCalendar.getParent().setBackground(tblCalendar.getBackground()); //Set background
        
        //No resize/reorder
        tblCalendar.getTableHeader().setResizingAllowed(true); //false
        tblCalendar.getTableHeader().setReorderingAllowed(true); //false
        
        //Single cell selection
        tblCalendar.setColumnSelectionAllowed(true);
        tblCalendar.setRowSelectionAllowed(true);
        tblCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        //Set row/column count
        tblCalendar.setRowHeight(20); 			// 38
        mtblCalendar.setColumnCount(columnCount); 	// 7
        mtblCalendar.setRowCount(rowCount);		// 6
        
        //Populate table
        for (int i=realYear-100; i<=realYear+100; i++){ //-100
            cmbYear.addItem(String.valueOf(i));
        }

	// adds days in DayDropDownMenu
	for(int i = 1; i <=nod; i++){
		cmbDay.addItem(String.valueOf(i));
	}

	String[] addNames = databaseInteraction();

	// adds Employee in EmployeeDropDownMenu
	for(int i = 0; i < addNames.length; i++){
		cmbEmpl.addItem(addNames[i]);
		System.out.println(addNames[i]);
	}

        //Refresh calendar
        refreshCalendar (realMonth, realYear, realDay); //Refresh calendar
    }
    
    public static void refreshCalendar(int month, int year, int day){
        //Variables
        String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int nod, som; //Number Of Days, Start Of Month
        
        //Allow/disallow buttons
        btnPrev.setEnabled(true);
        btnNext.setEnabled(true);
        if (month == 0 && year <= realYear-10){btnPrev.setEnabled(false);} //Too early
        if (month == 11 && year >= realYear+100){btnNext.setEnabled(false);} //Too late
        lblMonth.setText(months[month]); //Refresh the month label (at the top)
        lblMonth.setBounds(160-lblMonth.getPreferredSize().width/2, 25, 180, 25); //Re-align label with calendar
        cmbYear.setSelectedItem(String.valueOf(year)); //Select the correct year in the combo box
	cmbDay.setSelectedItem(String.valueOf(day));
        
        //Clear table
        for (int i=0; i<6; i++){
            for (int j=0; j<7; j++){
                mtblCalendar.setValueAt(null, i, j);
            }
        }
        
        //Get first day of month and number of days
        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        som = cal.get(GregorianCalendar.DAY_OF_WEEK);
        
        //Draw calendar
        for (int i=1; i<=nod; i++){
            int row = new Integer((i+som-2)/7);
            int column  =  (i+som-2)%7;
//            mtblCalendar.setValueAt(i, row, column); // Datum anzeigen
        }

	for(int i = 0; i < rowCount; i++){
		mtblCalendar.setValueAt(i ,i ,0);
	}
        
        //Apply renderers
        tblCalendar.setDefaultRenderer(tblCalendar.getColumnClass(0), new tblCalendarRenderer());
    }
//************************* database testing *************************************************************
	public static String[] databaseInteraction(){
		Connection conn = null;
		Statement stmt = null;
		String[] names = {"Empty","Empty","Empty","Empty","Empty","Empty","Empty","Empty","Empty","Empty"};
		try{
			//Step 2: Register JDBC driver
			Class.forName("org.postgresql.Driver");

			//Step 3: Open a connection
			System.out.println("Connection to database ...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

			//STEP 4: Execute a query
			System.out.println("Creating a statement...");
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT firstname, lastname FROM friends.test";
			ResultSet rs = stmt.executeQuery(sql);

			names = new String[10];
			int count = 0;
			// STEP 5: Extract data from result set
			while(rs.next()){

				//Retrieve by column name
				String first = rs.getString("firstname");
				String last = rs.getString("lastname");
				names[count++] = last + first;
//				System.out.println(names[count]);
				//Display values
//				System.out.println("FirstName: "+first);
//				System.out.println("LastName: "+last);
			}

			//STEP 6: CLEAN UP ENVIRONMENT
			rs.close();
			stmt.close();
			conn.close();
		}catch(SQLException se){
			// Handle errors for JDBC
			se.printStackTrace();	
		}catch(Exception e){
			// Handle errors for Class.forName
			e.printStackTrace();	
		}finally{
			//finally block used to close resources
			try{
				if(stmt !=null){
					stmt.close();
				}
			}catch(SQLException se2){
				//Nothing to do
			}
			try{
				if(conn != null){
					conn.close();
				}
			}catch(SQLException se){
				se.printStackTrace();
			}//END finally try
			System.out.println("GoodBye!");	
		}//end try
//		System.out.println(names[1]);
		return names;
	}//end method

//********************************** database testing end ************************************************************************     
    static class tblCalendarRenderer extends DefaultTableCellRenderer{
        public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            if (column == 6 || column == 7){ //Week-end
                setBackground(new Color(255, 220, 220));
            } else if (column == 0){ //time
                setBackground(new Color(140, 255, 140));
            }

            else{ //Week
                setBackground(new Color(255, 255, 255));
            }
            if (value != null){
                if (Integer.parseInt(value.toString()) == realDay && currentMonth == realMonth && currentYear == realYear){ //Today
//                    setBackground(new Color(220, 220, 255));
                }
            }
            setBorder(null);
            setForeground(Color.black);
            return this;
        }
    }
   
    static class btnPrev_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 0){ //Back one year
                currentMonth = 11;
                currentYear -= 1;
            }
            else{ //Back one month
                currentMonth -= 1;
            }
            refreshCalendar(currentMonth, currentYear, currentDay);
        }
    }
    static class btnNext_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 11){ //Foward one year
                currentMonth = 0;
                currentYear += 1;
            }
            else{ //Foward one month
                currentMonth += 1;
            }
            refreshCalendar(currentMonth, currentYear, currentDay);
        }
    }
    static class cmbYear_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (cmbYear.getSelectedItem() != null){
                String b = cmbYear.getSelectedItem().toString();
                currentYear = Integer.parseInt(b);
                refreshCalendar(currentMonth, currentYear, currentDay);
            }
        }
    }

    static class cmbDay_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (cmbDay.getSelectedItem() != null){
                String b = cmbDay.getSelectedItem().toString();
                currentDay = Integer.parseInt(b);
                refreshCalendar(currentMonth, currentYear, currentDay);
            }
        }
    }

//    static class cmbEmpl_Action implements ActionListener{
//        public void actionPerformed (ActionEvent e){
//            if (cmbEmpl.getSelectedItem() != null){
//                String b = cmbEmpl.getSelectedItem().toString();
//                currentEmp = b;
 //               refreshCalendar(currentMonth, currentYear, currentDay);
 //           }
//        }
//    }
}
