package com.tactfactory.statesgeneration;

public class StatesGeneration {

	public static void main(String[] args) {
		// First, load JasperDesign from XML and compile it into JasperReport
		JasperDesign jasperDesign = JasperManager.loadXmlDesign("");
		JasperReport jasperReport = JasperManager.compileReport(jasperDesign);
		// Second, create a map of parameters to pass to the report.
		Map parameters = new HashMap();
		parameters.put("employeeChart", createEmployeeChartImage());
		// Third, get a database connection
		Connection conn = Database.getConnection();
		// Fourth, create JasperPrint using fillReport() method
		JasperPrint jasperPrint = JasperManager.fillReport(jasperReport, parameters, conn);
		// You can use JasperPrint to create PDF
		JasperManager.printReportToPdfFile(jasperPrint, "desired-path\\SampleReport.pdf");
		// Or to view report in the JasperViewer
		JasperViewer.viewReport(jasperPrint);
		// Or create HTML Report
		JasperExportManager.exportReportToHtmlFile(jasperPrint, "desired-path\\SampleReport.h");
	}

}
