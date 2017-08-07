package com.tactfactory.statesgeneration.utils.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebService {
	// This method actually connects to the external web service
	public String invokeService(String urlString) {

		StringBuffer chaine = new StringBuffer("");
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestProperty("User-Agent", "");
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.connect();

			InputStream inputStream = connection.getInputStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					inputStream));
			String line = "";
			while ((line = rd.readLine()) != null) {
				chaine.append(line);
			}
		} catch (IOException e) {
			// Writing exception to log
			e.printStackTrace();
		}
		return chaine.toString();
	}
}
