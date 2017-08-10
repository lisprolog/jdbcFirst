package ReadExcelData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.usermodel.DVConstraint.*;
import org.apache.poi.hssf.usermodel.HSSFDataValidation.*;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.hssf.usermodel.HSSFName;


import java.io.FileOutputStream;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.LinkedList;
//import ReadExcelData.Employe;

/**
 * Created by lisprolog on 7/7/2017.
 */
public class ReadExcelAndPostgres {

    static String [] array = new String[3];
    static LinkedList<Employe> list = new LinkedList<Employe>();
    static HSSFWorkbook wb;
    static HSSFSheet sheet1;

    //DataValidation constraint = null;
    //static DVConstraint constraint = DVConstraint.createExplicitListConstraint(stringList1);
    static String[] stringList1 = {"10", "20", "30"};
    static DataValidationConstraint constraint;
    static HSSFDataValidationHelper validationHelper = new HSSFDataValidationHelper(sheet1);
    //(int firstRow, int LastRow, int firstCol, int lastCol)
    static CellRangeAddressList addressList = new CellRangeAddressList(0,0,0,0);
    static DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(stringList1);
    // poi.apache.org/spreadsheet/quick-guide.html#Validation
    //static DataValidation dataValidation2 = new HSSFDataValidation(addressList, dvConstraint);
    //accepted by IDE
    static HSSFDataValidation dataValidation = new HSSFDataValidation(addressList,constraint);
    //dataValidation.setSuppressDropDownArrow(false);
    //sheet1.addValidationData(dataValidation);

    public static void main(String[] args){
        loadExcelSheet();
        readPostgresIntoList();
        writeExcelData();
        //writePostgres();
    }

    // readExcelData from schedule.xls useless
    public static void readExcelData(){

        System.out.println("-------- Excel "
                + " Connection Testing ------------");

        File src = new File("schedule.xls");

        try{

            FileInputStream fis = new FileInputStream(src);
            HSSFWorkbook wb = new HSSFWorkbook(fis);
            HSSFSheet sheet1 = wb.getSheetAt(0);
            String data0 = "" + sheet1.getRow(4).getCell(2);
            System.out.println("Data from Excel is: " + data0);

        }catch(Exception e){
            System.out.println("ReadExcelDataEx:"+e);
        }
    }

    public static void writeExcelData(){
        // Create Blank Workbook
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet spreadsheet = workbook.createSheet("SheetName1");
        //Create file system using specific name
        try{
            //
            Employe e1;
            HSSFRow row;
            int rowCount = 12;
            Cell cell, cell1, cell2;
            String result, result1, result2;
            System.out.println("test1");
            int count = 0;
            while(list.isEmpty() == false){
                System.out.println("testWhileloop:"+count++);
                System.out.println();
                e1 = list.getFirst();
                list.removeFirst();
                row = sheet1.createRow(rowCount++);
                System.out.println(rowCount);
                cell = row.createCell(0);
                cell1 = row.createCell(1);
                cell2 = row.createCell(2);
                result = e1.id + " ";
                result1 = e1.first;
                result2 = e1.last;
                cell.setCellValue(result);
                cell1.setCellValue(result1);
                cell2.setCellValue(result2);
            }
            System.out.println("test2");
//            System.out.println("test2b: "+ dataValidation);

            System.out.println("test3");
            //clean
            FileOutputStream out= new FileOutputStream(
                    new File("gantt-chart_L3.xls")
            );

            // addValidationData null pointer exception
//            System.out.println("Sheet1: " + sheet1);
//            System.out.println("DataValidation: " +dataValidation);
//            sheet1.addValidationData(dataValidation);
//            System.out.println("Sheet1.2: Sheet1 added? nullpointer above.");
            wb.write(out);
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("gantt-chart_L3.xls updated!");
    }

    public static void readPostgresIntoList(){
        System.out.println("-------- PostgreSQL "
                + "JDBC Read Testing ------------");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;
        Statement stmt = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/postgres", "user",
                    "DATABASEPASSWORD");
            // Step4: query
            System.out.println("Create statement");

            stmt = connection.createStatement();
            String sql = "Select employe_id, firstname, lastname FROM employe";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                int id = rs.getInt("employe_id");
                String first = rs.getString("firstname");
                String last = rs.getString("lastname");
                array[0] = "" + id;
                array[1] = first;
                array[2] = last;
                System.out.println(id + " " + first + " " + last);
                Employe next = new Employe(id, first, last);
                list.add(next);
            }
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }
    }

    public static void loadExcelSheet(){
        File src = new File("gantt-chart_L3.xls");

        try{
            System.out.println("Load1");
            FileInputStream fis = new FileInputStream(src);
            System.out.println("Load2");
            wb = new HSSFWorkbook(fis);
            System.out.println("Load3");
            sheet1 = wb.getSheetAt(0);
            System.out.println("Excel sheet loaded.");
        }catch(Exception e){
            System.out.println("loadExcelSheetEx:"+e);
        }
    }

    public static void writePostgres(){
        System.out.println("-------- PostgreSQL "
                + "JDBC Write Testing ------------");
        try {

            Class.forName("org.postgresql.Driver");


        } catch (ClassNotFoundException e) {

            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;

        }

        //System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;
        Statement stmt = null;
        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/postgres", "user",
                    "DATABASEPASSWORD");
            // Step4: query
            System.out.println("insert statement");

            stmt = connection.createStatement();

            String one = "2";
            String two = "Jason";
            String three = "Mewes";

            String sql = "INSERT INTO employe VALUES("+one+", '"+two+"', '"+three+"')";

            stmt.executeUpdate(sql);


        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;

        }

        if (connection != null) {
            System.out.println("You made it, wrote into the table!");
        } else {
            System.out.println("Failed to make connection!");
        }
    }

    public static void test(){
        // setup code
        String[] countryName = {"Austria","Germany","Switzerland","Britain","France"};
        String sname = "TestSheet", cname = "TestName", cvalue = "TestVal";
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sname);
        sheet.createRow(0).createCell((short) 0).setCellValue(cvalue);

        // 1. create named range for a single cell using areareference
        HSSFName namedCell = wb.createName();
        namedCell.setNameName(cname);
        String reference1 = sname+"!A1:A1"; // area reference
        namedCell.setRefersToFormula(reference1);

        // 2. create named range for a single cell using cellreference
        HSSFName namedCel2 = wb.createName();
        namedCel2.setNameName(cname);
        String reference2 = sname+"!A1"; // cell reference
        namedCel2.setRefersToFormula(reference2);

        // 3. create named range for an area using AreaReference
        HSSFName namedCel3 = wb.createName();
        namedCel3.setNameName(cname);
        String reference3 = sname+"!A1:C5"; // area reference
        namedCel3.setRefersToFormula(reference3);

        // 4. create named formula
        HSSFName namedCel4 = wb.createName();
        namedCel4.setNameName("my_sum");
        namedCel4.setRefersToFormula("SUM(sname+!$I$2:$I$6)");

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet realSheet = workbook.createSheet("Sheet xls");
        HSSFSheet hidden = workbook.createSheet("hidden");
        for (int i = 0, length= countryName.length; i < length; i++) {
            String name = countryName[i];
            HSSFRow row = hidden.createRow(i);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(name);
        }
        HSSFName namedCell5 = workbook.createName();
        namedCell.setNameName("hidden");
        namedCell.setRefersToFormula("hidden!$A$1:$A$" + countryName.length);
        DVConstraint constraint = DVConstraint.createFormulaListConstraint("hidden");
        CellRangeAddressList addressList = new CellRangeAddressList(0, 0, 0, 0);
        HSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
        workbook.setSheetHidden(1, true);
        realSheet.addValidationData(validation);
        try {
            FileOutputStream stream = new FileOutputStream("range.xls");
            workbook.write(stream);
            stream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void save(){}

    public static void listAvailableTimes(){}

    public static void testAppointment(){}
}
