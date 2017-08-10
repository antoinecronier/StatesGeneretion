package com.tactfactory.statesgeneration.jasperreports;

import java.util.ArrayList;
import java.util.Map;

public class ReportPart {
	private String title;
	private ArrayList<String> fields = new ArrayList<String>();
	private ArrayList<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the fields
	 */
	public ArrayList<String> getFields() {
		return fields;
	}
	/**
	 * @param fields the fields to set
	 */
	public void setFields(ArrayList<String> fields) {
		this.fields = fields;
	}
	/**
	 * @return the mapList
	 */
	public ArrayList<Map<String, Object>> getMapList() {
		return mapList;
	}
	/**
	 * @param mapList the mapList to set
	 */
	public void setMapList(ArrayList<Map<String, Object>> mapList) {
		this.mapList = mapList;
	}
}
