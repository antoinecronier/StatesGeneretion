package com.tactfactory.statesgeneration.jasperreports;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateReports {

	public final static String JASPER_FOLDER_PATH = ".\\src\\main\\jasper\\";
	public final static String JASPER_OUTPUT_PATH = ".\\jasperreports\\";

	private static final Logger logger = LoggerFactory
			.getLogger(GenerateReports.class);

	// This method generates a PDF report
	public void generateReport(String jrxmlFile, String exportFile,
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
		} catch (Exception e) {
			logger.info("Error: ", e);
		}
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
