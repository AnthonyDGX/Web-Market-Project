/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javax.sql.DataSource;

/**
 *
 * @author DGX
 */
public class DataSourceFactory {

	public enum DriverType {
		embedded, server
	};
	
	// Choic du type de driver : embedded ou serveur
	private static final DriverType TYPE = DriverType.server;
	/**
	 * Renvoie la source de données (server ou embbeded)
	 * @return  la source de données
	 */
	public static DataSource getDataSource() {
		DataSource result;

		switch (TYPE) {
			case server: // Derby mode serveur, doit être démarré indépendamment
				org.apache.derby.jdbc.ClientDataSource ds = new org.apache.derby.jdbc.ClientDataSource();
				ds.setDatabaseName("sample");
				ds.setUser("app");
				ds.setPassword("app");
				// The host on which Network Server is running
				ds.setServerName("localhost");
				// port on which Network Server is listening
				ds.setPortNumber(1527);
				result = ds;
				break;
			default: // Derby mode embedded, démarré automatiquement avec l'application
				org.apache.derby.jdbc.EmbeddedDataSource es = new org.apache.derby.jdbc.EmbeddedDataSource();
				es.setCreateDatabase("create");
				es.setDatabaseName("embedded_sample");
				result = es;
		}

		return result;
	}

}

