package com.tactfactory.statesgeneration;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

		reporter1();

		reportJson();

		reportDatabase();

		reportComposed();
	}

	private static void reportComposed() {
		String databaseName = "disco";
		GenerateReports reports = new GenerateReports();
		ArrayList<JasperPrint> printer = new ArrayList<JasperPrint>();
		printer.add(reports.generateReportWithMysqlDB("DatabaseReport.jrxml",
				"dbReport", JasperReportTypes.PDF, databaseName, "root", ""));

		MySQLDBAccess access = new MySQLDBAccess(databaseName, "root", "", 3306);
		ResultSet result = access.executeStatement("SELECT TABLE_NAME "
				+ "FROM INFORMATION_SCHEMA.TABLES "
				+ "WHERE TABLE_SCHEMA = '"+databaseName+"';");

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
						}else{
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
