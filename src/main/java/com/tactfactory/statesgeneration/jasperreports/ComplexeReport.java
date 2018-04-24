package com.tactfactory.statesgeneration.jasperreports;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.tactfactory.statesgeneration.utils.database.MySQLDBAccess;
import com.tactfactory.statesgeneration.utils.file.FileManager;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.core.layout.HorizontalBandAlignment;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Stretching;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;

public class ComplexeReport {

	public JasperPrint compile() {

		// Title style
		final Style titleStyle = new Style();
		titleStyle.setVerticalAlign(VerticalAlign.MIDDLE);
		titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		titleStyle.setTransparent(false);
		titleStyle.setBackgroundColor(Color.BLACK);
		titleStyle.setBorder(Border.PEN_2_POINT());
		titleStyle.setTextColor(Color.WHITE);
		titleStyle.setStreching(Stretching.RELATIVE_TO_TALLEST_OBJECT);
		titleStyle.setFont(Font.ARIAL_BIG_BOLD);
		titleStyle.setRadius(20);

		// Style header
		final Style headerStyle = new Style();
		headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
		headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		headerStyle.setTransparent(false);
		headerStyle.setBackgroundColor(Color.LIGHT_GRAY);
		headerStyle.setBorder(Border.DOTTED());
		headerStyle.setTextColor(Color.MAGENTA);
		headerStyle.setStreching(Stretching.RELATIVE_TO_TALLEST_OBJECT);
		headerStyle.setFont(Font.ARIAL_BIG_BOLD);
		headerStyle.setRadius(20);

		// Style column
		final Style columnStyle = new Style();
		columnStyle.setVerticalAlign(VerticalAlign.MIDDLE);
		columnStyle.setHorizontalAlign(HorizontalAlign.CENTER);

		FastReportBuilder drb = new FastReportBuilder();

		// Add title for report
		AutoText textTitle = new AutoText("My Report",
				AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		textTitle.setWidth(8000);
		textTitle.setHeight(50);
		textTitle.setStyle(titleStyle);
		drb.addAutoText(textTitle);

		// Add datas for file society
		FileManager managerFile = new FileManager("./reportsInputs", "society");
		for (String iterable_element : managerFile.loadFromFile()) {
			AutoText text1 = new AutoText(iterable_element,
					AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
			text1.setWidth(8000);
			drb.addAutoText(text1);
		}

		DynamicReport dr = drb.build();

		JasperPrint jp = new JasperPrint();

		try {
			jp = DynamicJasperHelper.generateJasperPrint(dr,
					new ClassicLayoutManager(), new JREmptyDataSource());
		} catch (JRException e) {
			e.printStackTrace();
		}

		String databaseName = "disco";
		BuildReports builder = new BuildReports();
		ArrayList<JasperPrint> printer = new ArrayList<JasperPrint>();

		MySQLDBAccess access = new MySQLDBAccess(databaseName, "root", "", 3306);

		ResultSet q1 = access
				.executeStatement("SELECT CONCAT(employee.lastname, ' ', employee.firstname) AS Employee, "
						+ "employee.salary AS Salary, "
						+ "CONCAT(schedule.start_at, ' => ', schedule.end_at) AS Work, "
						+ "post.name AS Post "
						+ ",room.name AS Workplace "
						+ "FROM room "
						+ "INNER JOIN employee_room ON employee_room.id_room = room.id "
						+ "INNER JOIN employee ON employee.id = employee_room.id_employee "
						+ "INNER JOIN post ON employee.id_post = post.id "
						+ "INNER JOIN schedule ON post.id_schedule = schedule.id "
						+ "GROUP BY employee.id;");

		ArrayList<Map<String, Object>> q1Datas = new ArrayList<Map<String, Object>>();
		ArrayList<String> q1Columns = new ArrayList<String>();
		dataExtractor(q1, q1Datas, q1Columns);
		printer.add(builder.generateDefaultOneTab("Employee Works", q1Columns,
				q1Datas));

		ResultSet q2 = access
				.executeStatement(" SELECT building.name AS building, "
						+ "room.name AS Room, "
						+ "room.nb_people AS Capacity, "
						+ "room_gear.quantity AS Quantity, "
						+ "gear.name AS Equipment, "
						+ "state.name AS State "
						+ "FROM building "
						+ "INNER JOIN room ON building.id = room.id_building "
						+ "INNER JOIN room_gear ON room.id = room_gear.id_room "
						+ "INNER JOIN gear ON room_gear.id_gear = gear.id "
						+ "INNER JOIN gear_state ON gear.id = gear_state.id_gear "
						+ "INNER JOIN state ON gear_state.id_state = state.id "
						+ "GROUP BY building.id, room.id, gear.id, state.id "
						+ "ORDER BY state.name = 'NOK' DESC;");

		ArrayList<Map<String, Object>> q2Datas = new ArrayList<Map<String, Object>>();
		ArrayList<String> q2Columns = new ArrayList<String>();
		dataExtractor(q2, q2Datas, q2Columns);
		printer.add(builder.generateDefaultOneTab("Room gears", q2Columns,
				q2Datas));

		access
		.executeStatement("SET @dateYear='2015'; ");
		access
		.executeStatement("SET @dateStart=CONCAT(@dateYear,'-01-01'); ");
		access
		.executeStatement("SET @dateEnd=CONCAT(@dateYear,'-12-31'); ");

		ResultSet q3 = access
				.executeStatement("SELECT Q1.Returned_Gear, Q4.Returned_Building, Q5.Returned_Room, ( "
						+ "(IF(Q1.Returned_Gear != NULL,0,Q1.Returned_Gear) + IF(Q4.Returned_Building != NULL,0,Q4.Returned_Building) + IF(Q5.Returned_Room != NULL,0,Q5.Returned_Room))) AS Result  "
						+ "FROM "
						+ "( "
						+ "        SELECT SUM(gear.price + room.entrance_price) AS Returned_Gear "
						+ "        FROM customer  "
						+ "        INNER JOIN customer_room ON customer.id = customer_room.id_customer  "
						+ "        INNER JOIN gear ON customer_room.id_gear = gear.id  "
						+ "        INNER JOIN room ON customer_room.id_room = room.id  "
						+ "        WHERE customer_room.buy_at >= @dateStart AND customer_room.buy_at <= @dateEnd "
						+ ") AS Q1, "
						+ "( "
						+ "        SELECT SUM(leasing_building.price) AS Returned_Building "
						+ "        FROM leasing_building "
						+ "        WHERE leasing_building.start_at >= @dateStart AND leasing_building.end_at <= @dateEnd "
						+ ") AS Q4,  "
						+ "( "
						+ "        SELECT SUM(leasing_room.price) AS Returned_Room "
						+ "        FROM leasing_room "
						+ "        WHERE leasing_room.start_at >= @dateStart AND leasing_room.end_at <= @dateEnd "
						+ " ) AS Q5;");

		ArrayList<Map<String, Object>> q3Datas = new ArrayList<Map<String, Object>>();
		ArrayList<String> q3Columns = new ArrayList<String>();
		dataExtractor(q3, q3Datas, q3Columns);
		printer.add(builder.generateDefaultOneTab("Chiffre d affaire", q3Columns,
				q3Datas));

		// DB
		ResultSet result = access.executeStatement("SELECT TABLE_NAME "
				+ "FROM INFORMATION_SCHEMA.TABLES " + "WHERE TABLE_SCHEMA = '"
				+ databaseName + "';");

		ArrayList<String> tables = new ArrayList<String>();
		try {
			while (result.next()) {
				String em = "";
				em = result.getString("TABLE_NAME");
				tables.add(em.replace("\n", ","));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (String table : tables) {
			ResultSet subResult = access.executeStatement("SELECT * " + "FROM "
					+ table);

			ArrayList<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
			ArrayList<String> columns = new ArrayList<String>();
			ResultSetMetaData rsmd;
			dataExtractor(subResult, datas, columns);

			printer.add(builder.generateDefaultOneTab(table, columns, datas));
		}

		JasperPrint finalForm = builder.generateMultiTabFromJasperPrint(dr,
				"Results", printer);
		JasperViewer.viewReport(finalForm);

		return finalForm;
	}

	/**
	 * @param q1
	 * @param q1Datas
	 * @param q1Columns
	 */
	public void dataExtractor(ResultSet q1,
			ArrayList<Map<String, Object>> q1Datas, ArrayList<String> q1Columns) {
		ResultSetMetaData q1Rsmd;
		try {
			q1Rsmd = q1.getMetaData();
			for (int i = 1; i <= q1Rsmd.getColumnCount(); i++) {
				q1Columns.add(q1Rsmd.getColumnLabel(i));
			}
			while (q1.next()) {
				Map<String, Object> subMap = new HashMap<String, Object>();
				for (int i = 1; i <= q1Rsmd.getColumnCount(); i++) {
					String em = "";
					if (q1.getString(q1Rsmd.getColumnLabel(i)) != null) {
						em = q1.getString(q1Rsmd.getColumnLabel(i));
					} else {
						em = "null";
					}
					subMap.put(q1Rsmd.getColumnLabel(i), em);
				}
				q1Datas.add(subMap);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
