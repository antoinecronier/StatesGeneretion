package com.tactfactory.statesgeneration.jasperreports.datasource;

import net.sf.jasperreports.engine.JRDataSource;

public class FactoryClassDataSource {

	/**
	 * Factory Class responsible for setting the JRdatasource.
	 *
	 * @return
	 */
	public static JRDataSource generateDS() {
		return new JRFileSystemDataSource("Path of the desired folder");
	}
}
