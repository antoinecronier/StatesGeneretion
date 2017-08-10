package com.tactfactory.statesgeneration.jasperreports.datasource;

import java.util.ArrayList;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignField;

public class JRFileSystemDataSourceProvider implements JRDataSourceProvider {

	@Override
	public boolean supportsGetFieldsOperation() {
		return false;
	}

	@Override
	public JRField[] getFields(JasperReport jr) throws JRException,
			UnsupportedOperationException {

		ArrayList fields = new ArrayList();
		String[] fieldNames = JRFileSystemDataSource.fieldNames();
		for (String s : fieldNames) {
			JRDesignField field = new JRDesignField();
			field.setName(s);
			field.setValueClassName("java.lang.String");
			fields.add(field);
		}
		return (JRField[]) fields.toArray(new JRField[fields.size()]);
	}

	@Override
	public JRDataSource create(JasperReport jr) throws JRException {
		return new JRFileSystemDataSource("Enter the path of folder");
	}

	@Override
	public void dispose(JRDataSource jrds) throws JRException {
	}

}
