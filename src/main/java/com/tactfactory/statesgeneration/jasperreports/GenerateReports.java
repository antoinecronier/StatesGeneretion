package com.tactfactory.statesgeneration.jasperreports;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateReports {

	public final static String JASPER_FOLDER_PATH = ".\\src\\main\\jasper\\";
	public final static String JASPER_OUTPUT_PATH = ".\\jasperreports\\";

	private static final Logger logger = LoggerFactory
			.getLogger(GenerateReports.class);

	// This method generates a PDF report
	public JasperPrint generateReportJson(String jrxmlFile, String exportFile,
			JasperReportTypes reportType, ReportDataSourceExtracter extracter) {
		try {
			JasperDesign design = JRXmlLoader.load(JASPER_FOLDER_PATH
					+ jrxmlFile);
			JasperReport jasperReport = JasperCompileManager
					.compileReport(design);
			logger.info("Report compiled");
			String data = extracter.getStringDatas();
			logger.info("data = " + data);
			// It is possible to generate Jasper reports from a JSON source.
			JsonDataSource jsonDataSource = new JsonDataSource(
					new ByteArrayInputStream(data.getBytes()));
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport, new HashMap(), jsonDataSource);
			logger.info("JasperPrint" + jasperPrint);

			switch (reportType) {
			case PDF:
				GeneratePDF(jasperPrint, exportFile);
				break;
			case HTML:
				GenerateHTML(jasperPrint, exportFile);
				break;
			case ALL:
				GeneratePDF(jasperPrint, exportFile);
				GenerateHTML(jasperPrint, exportFile);
				break;

			default:
				break;
			}

			logger.info("Completed Successfully: ");

			return jasperPrint;
		} catch (Exception e) {
			logger.info("Error: ", e);
		}
		return null;
	}

	public JasperPrint generateReportWithMysqlDB(String jrxmlFile, String exportFile,
			JasperReportTypes reportType,String databaseName, String login, String password) {
		return generateReportWithMysqlDB(jrxmlFile, exportFile,
				reportType,databaseName, login, password, 3306,"localhost");
	}

	public JasperPrint generateReportWithMysqlDB(String jrxmlFile, String exportFile,
			JasperReportTypes reportType,String databaseName, String login, String password, int port,String address) {
		try {
			JasperDesign design = JRXmlLoader.load(JASPER_FOLDER_PATH
					+ jrxmlFile);
			JasperReport jasperReport = JasperCompileManager
					.compileReport(design);
			logger.info("Report compiled");
			// It is possible to generate Jasper reports from a JSON source.
			Map<String,Object> parameters = new HashMap<String, Object>();
			parameters.put("IS_IGNORE_PAGINATION ", false);
			Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://"+address+":"+port+"/"+databaseName+"?zeroDateTimeBehavior=convertToNull", login, password);
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport,parameters,con);
			logger.info("JasperPrint" + jasperPrint);

			switch (reportType) {
			case PDF:
				GeneratePDF(jasperPrint, exportFile);
				break;
			case HTML:
				GenerateHTML(jasperPrint, exportFile);
				break;
			case ALL:
				GeneratePDF(jasperPrint, exportFile);
				GenerateHTML(jasperPrint, exportFile);
				break;

			default:
				break;
			}

			logger.info("Completed Successfully: ");

			return jasperPrint;
		} catch (Exception e) {
			logger.info("Error: ", e);
		}
		return null;
	}

	public JasperPrint generateComplexeReport(String jrxmlFile, String exportFile,
			JasperReportTypes reportType, ComplexeReport complexeReport) {

		return null;
	}

	private void GenerateHTML(JasperPrint jasperPrint, String exportFile) {
		try {
			String filePath = JASPER_OUTPUT_PATH + exportFile + ".html";
			JasperExportManager.exportReportToHtmlFile(jasperPrint, filePath);
			logger.info("HTML generated to : " + filePath);
		} catch (JRException e) {
			logger.info("Error: ", e);
		}
	}

	private void GeneratePDF(JasperPrint jasperPrint, String exportFile) {
		try {
			String filePath = JASPER_OUTPUT_PATH + exportFile + ".pdf";
			JasperExportManager.exportReportToHtmlFile(jasperPrint,filePath);
			logger.info("HTML generated to : " + filePath);
		} catch (JRException e) {
			logger.info("Error: ", e);
		}
	}
}
