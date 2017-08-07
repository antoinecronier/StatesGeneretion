package com.tactfactory.statesgeneration.jasperreports;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import com.tactfactory.statesgeneration.utils.StringManager;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class BuildReports {



	public BuildReports() {

	}

	public void generateDefaultOneTab(String title, ArrayList<String> fields,
			ArrayList<Map<String, Object>> mapList) {
		FastReportBuilder drb = new FastReportBuilder();
		DynamicReport dr;
		try {
			for (String field : fields) {
				String columGodName = StringManager.toTitle(field);

				drb.addColumn(columGodName, field, mapList.get(0).get(field)
						.getClass(), 30);
			}
			dr = drb.setTitle(title)
					.setSubtitle("This report was generated at " + new Date())
					.setPrintBackgroundOnOddRows(true)
					.setUseFullPageWidth(true).build();

			JasperPrint jp;

			jp = DynamicJasperHelper.generateJasperPrint(dr,
					new ClassicLayoutManager(), mapList);

			JasperViewer.viewReport(jp); // finally display the report report
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ColumnBuilderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
