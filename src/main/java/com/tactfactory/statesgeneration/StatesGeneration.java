package com.tactfactory.statesgeneration;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.ImageBanner;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Stretching;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

import com.tactfactory.statesgeneration.entities.Product;
import com.tactfactory.statesgeneration.jasperreports.BuildReports;
import com.tactfactory.statesgeneration.jasperreports.GenerateReports;
import com.tactfactory.statesgeneration.jasperreports.JasperReportTypes;
import com.tactfactory.statesgeneration.jasperreports.ReportDataSourceExtracter;
import com.tactfactory.statesgeneration.jasperreports.ReportPart;
import com.tactfactory.statesgeneration.utils.database.MySQLDBAccess;
import com.tactfactory.statesgeneration.utils.introspection.DumpFields;
import com.tactfactory.statesgeneration.utils.webservice.WebService;

public class StatesGeneration {

	public static void main(String[] args) {

		manualReport();

		// reporter1();
		//
		// reportJson();
		//
		// reportDatabase();
		//
		// reportComposed();
	}

	private static void manualReport() {
		Collection<Product> datas = new ArrayList<Product>();
		datas.add(new Product("state", "branch", "productLine", "item", 1, 1, 1));
		datas.add(new Product("state", "branch", "productLine", "item", 1, 1, 3));

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
		columnStyle.setBorder(Border.THIN());

		FastReportBuilder drb = new FastReportBuilder();
		new ColumnBuilder();
		AbstractColumn colToSum = ColumnBuilder.getNew()
				.setColumnProperty("amount", Float.class.getName())
				.setTitle("Amount").setWidth(new Integer(85))
				.setStyle(columnStyle).setHeaderStyle(headerStyle).build();

		DynamicReport dr = drb
				.addColumn("State", "state", String.class.getName(), 8,
						columnStyle)
				.addColumn("Item Code", "id", Long.class.getName(), 30,
						columnStyle)
				.addColumn("Quantity", "quantity", Long.class.getName(), 60,
						columnStyle)
				.addColumn(colToSum)
				.addGlobalFooterVariable(colToSum, DJCalculation.SUM)
				.setTitle("test")
				.setSubtitle("This report was generated at " + new Date())
				.setPrintBackgroundOnOddRows(true)
				.setUseFullPageWidth(true)
				.addFirstPageImageBanner(".\\resources\\pexels-photo.jpg",
						1000, 600, ImageBanner.Alignment.Center)
				.addAutoText("coucou", (byte)1, (byte)1)
				.build();

		JasperPrint jp = new JasperPrint();

		try {
			jp = DynamicJasperHelper.generateJasperPrint(dr,
					new ClassicLayoutManager(), datas);
		} catch (JRException e) {
			e.printStackTrace();
		}

		JasperViewer.viewReport(jp);
	}

	private static void reportComposed() {
		String databaseName = "disco";
		GenerateReports reports = new GenerateReports();
		ArrayList<JasperPrint> printer = new ArrayList<JasperPrint>();
		printer.add(reports.generateReportWithMysqlDB("DatabaseReport.jrxml",
				"dbReport", JasperReportTypes.PDF, databaseName, "root", ""));

		MySQLDBAccess access = new MySQLDBAccess(databaseName, "root", "", 3306);
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

		BuildReports builder = new BuildReports();
		for (String table : tables) {
			ResultSet subResult = access.executeStatement("SELECT * " + "FROM "
					+ table);

			ArrayList<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
			ArrayList<String> columns = new ArrayList<String>();
			ResultSetMetaData rsmd;
			try {
				rsmd = subResult.getMetaData();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					columns.add(rsmd.getColumnLabel(i));
				}
				while (subResult.next()) {
					Map<String, Object> subMap = new HashMap<String, Object>();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						String em = "";
						if (subResult.getString(rsmd.getColumnLabel(i)) != null) {
							em = subResult.getString(rsmd.getColumnLabel(i));
						} else {
							em = "null";
						}
						subMap.put(rsmd.getColumnLabel(i), em);
					}
					datas.add(subMap);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			printer.add(builder.generateDefaultOneTab(table, columns, datas));
		}

		JasperPrint finalForm = builder.generateMultiTabFromJasperPrint(
				"Results", printer);
		JasperViewer.viewReport(finalForm);
	}

	private static void reportDatabase() {
		GenerateReports reports = new GenerateReports();
		JasperViewer.viewReport(reports.generateReportWithMysqlDB(
				"DatabaseReport.jrxml", "dbReport", JasperReportTypes.PDF,
				"pokemon", "root", ""));
	}

	private static void reportJson() {
		GenerateReports reports = new GenerateReports();
		JasperViewer.viewReport(reports.generateReportJson(
				"WaveMakerExample.jrxml", "output", JasperReportTypes.HTML,
				new ReportDataSourceExtracter() {

					@Override
					public String getStringDatas() {
						// https://www.wavemaker.com/learn/how-tos/generate-pdf-file-using-jasper-reports/
						WebService service = new WebService();
						return service
								.invokeService("http://e1255fbaf8cb8.cloud.wavemakeronline.com/RestJasper/services/hrdb/Department?sort=location&r=json");
					}
				}));
		GenerateReports reports1 = new GenerateReports();
		JasperViewer.viewReport(reports1.generateReportJson(
				"ComplexeExample.jrxml", "export complexe",
				JasperReportTypes.PDF, new ReportDataSourceExtracter() {

					@Override
					public String getStringDatas() {
						WebService service = new WebService();
						return service
								.invokeService("http://e1255fbaf8cb8.cloud.wavemakeronline.com/RestJasper/services/hrdb/Department?sort=location&r=json");
					}
				}));
	}

	private static void reporter1() {
		ArrayList<Product> products = new ArrayList<Product>();
		products.add(new Product("state", "branch", "productLine", "item", 1,
				10, (float) 1.30));
		products.add(new Product("state2", "branch", "productLine", "item", 1,
				10, (float) 1.30));
		products.add(new Product("state3", "branch", "productLine", "item", 1,
				10, (float) 1.30));
		products.add(new Product("state4", "branch", "productLine", "item", 1,
				10, (float) 1.30));
		products.add(new Product("state5", "branch", "productLine", "item", 1,
				10, (float) 1.30));
		ArrayList<Map<String, Object>> listFielder = DumpFields
				.<Product> listFielder(products);

		ArrayList<String> baseAtt = DumpFields.inspectBaseAttribut(
				Product.class, Object.class);

		BuildReports reportsBuilder = new BuildReports();
		JasperViewer.viewReport(reportsBuilder.generateDefaultOneTab("Test",
				baseAtt, listFielder));

		ArrayList<ReportPart> reportParts = new ArrayList<ReportPart>();

		ReportPart report1 = new ReportPart();
		report1.setTitle("Test");
		report1.setFields(baseAtt);
		report1.setMapList(listFielder);
		reportParts.add(report1);

		ReportPart report2 = new ReportPart();
		report2.setTitle("Test1");
		report2.setFields(DumpFields.inspectBaseAttribut(Product.class,
				Object.class));
		report2.setMapList(DumpFields.<Product> listFielder(products));
		reportParts.add(report2);

		JasperViewer.viewReport(reportsBuilder.generateMultiTabFromReportPart(
				"Grouped Reports", reportParts));
	}
}
