package com.tactfactory.statesgeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.tactfactory.statesgeneration.entities.Product;
import com.tactfactory.statesgeneration.jasperreports.BuildReports;
import com.tactfactory.statesgeneration.utils.introspection.DumpFields;

public class StatesGeneration {

	public static void main(String[] args) {

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
		reportsBuilder.generateDefaultOneTab("Test", baseAtt, listFielder);


		// GenerateReports reports = new GenerateReports();
		// reports.generateReport("WaveMakerExample.jrxml", "output",
		// JasperReportTypes.HTML, new ReportDataSourceExtracter() {
		//
		// @Override
		// public String getStringDatas() {
		// //
		// https://www.wavemaker.com/learn/how-tos/generate-pdf-file-using-jasper-reports/
		// WebService service = new WebService();
		// return service
		// .invokeService("http://e1255fbaf8cb8.cloud.wavemakeronline.com/RestJasper/services/hrdb/Department?sort=location&r=json");
		// }
		// });
	}

}
