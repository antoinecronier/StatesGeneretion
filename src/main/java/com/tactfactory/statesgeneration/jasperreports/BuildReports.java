package com.tactfactory.statesgeneration.jasperreports;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.tactfactory.statesgeneration.utils.StringManager;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.ImageBanner;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;

public class BuildReports {

	ArrayList<ReportPart> reports = new ArrayList<ReportPart>();

	public BuildReports() {

	}

	public JasperPrint generateDefaultOneTab(String title,
			ArrayList<String> fields, ArrayList<Map<String, Object>> mapList) {
		FastReportBuilder drb = new FastReportBuilder();
		return generateBlock(title, fields, mapList, drb);
	}

	public JasperPrint generateMultiTabFromJasperPrint(String title,
			ArrayList<JasperPrint> reportPrintParts) {

		FastReportBuilder drb = new FastReportBuilder();
		DynamicReport dr = drb.setTitle(title)
				.setSubtitle("This report was generated at " + new Date())
				.setPrintBackgroundOnOddRows(true).setUseFullPageWidth(true)
				.addFirstPageImageBanner(".\\resources\\pexels-photo.jpg", 1000, 600, ImageBanner.Alignment.Center)
				.build();

		JasperPrint jp = new JasperPrint();

		try {
			jp = DynamicJasperHelper.generateJasperPrint(dr,
					new ClassicLayoutManager(), new JREmptyDataSource());
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (JasperPrint jasperPrint : reportPrintParts) {
			if (jasperPrint != null) {
				for (JRPrintPage jasperPrintPage : jasperPrint.getPages()) {
					jp.addPage(jasperPrintPage);
				}
			}
		}

		return jp;
	}

	public JasperPrint generateMultiTabFromReportPart(String title,
			ArrayList<ReportPart> reportParts) {

		FastReportBuilder drb = new FastReportBuilder();
		DynamicReport dr = drb.setTitle(title)
				.setSubtitle("This report was generated at " + new Date())
				.setPrintBackgroundOnOddRows(true).setUseFullPageWidth(true)
				.build();

		JasperPrint jp = new JasperPrint();

		try {
			jp = DynamicJasperHelper.generateJasperPrint(dr,
					new ClassicLayoutManager(), new JREmptyDataSource());
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<JasperPrint> jasperPrints = new ArrayList<JasperPrint>();
		for (ReportPart report : reportParts) {
			jasperPrints.add(generateOneTab(report));
		}

		for (JasperPrint jasperPrint : jasperPrints) {
			for (JRPrintPage jasperPrintPage : jasperPrint.getPages()) {
				jp.addPage(jasperPrintPage);
			}
		}

		return jp;
	}

	private JasperPrint generateOneTab(ReportPart reportPart) {
		FastReportBuilder drb = new FastReportBuilder();
		return generateBlock(reportPart.getTitle(), reportPart.getFields(),
				reportPart.getMapList(), drb);
	}

	private JasperPrint generateBlock(String title, ArrayList<String> fields,
			ArrayList<Map<String, Object>> mapList, FastReportBuilder drb) {
		DynamicReport dr;
		try {
			for (String field : fields) {
				String columGodName = StringManager.toTitle(field);
				if (!mapList.isEmpty()) {
					drb.addColumn(columGodName, field, mapList.get(0)
							.get(field).getClass(), 30);
				} else {
					drb.addColumn(columGodName, field, "", 30);
				}
			}
			dr = drb.setTitle(title)
					.setSubtitle("This report was generated at " + new Date())
					.setPrintBackgroundOnOddRows(true)
					.setUseFullPageWidth(true).build();

			JasperPrint jp;

			jp = DynamicJasperHelper.generateJasperPrint(dr,
					new ClassicLayoutManager(), mapList);

			return jp;
		} catch (JRException | ColumnBuilderException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
