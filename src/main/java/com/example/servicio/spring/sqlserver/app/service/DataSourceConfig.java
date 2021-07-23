package com.example.servicio.spring.sqlserver.app.service;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionAzureKeyVaultProvider;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;

/**
 * <b>Class</b>: DataSourceConfig<br/>
 * Copyright: &copy; 2018 Banco de Cr&eacute;dito del Per&uacute;.<br/>
 * Company: Banco de Cr&eacute;dito del Per&uacute;.<br/>
 *
 * @author Banco de Cr&eacute;dito del Per&uacute; (BCP) <br/>
 *         <u>Service Provider</u>: Everis Peru <br/>
 *         <u>Changes</u>:<br/>
 *         <ul>
 *         <li>Ene 16, 2018 Creaci&oacute;n de Clase.</li>
 *         </ul>
 * @version 1.0
 */
@Lazy
@Configuration
public class DataSourceConfig {

	private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

	/**
	 * Bean.
	 *
	 * @return DataSource
	 */
	@Bean
	public DataSource dataSource() {
		String conec = "jdbc:sqlserver://diwa.database.windows.net:1433;database=Encripcion;user=jeffer@diwa;password=Bcp2491.;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		dataSource.setUrl(conec);

		setKeyVaultProvider();
		return dataSource;
	}

	private void setKeyVaultProvider() {
		try {
			
			SQLServerColumnEncryptionAzureKeyVaultProvider akvProvider = new SQLServerColumnEncryptionAzureKeyVaultProvider(
					"2f175547-f878-4eef-a2ce-08c837c74267",
					"c31fd227-f268-4645-8f42-03725d09e3ff");
			Map<String, SQLServerColumnEncryptionKeyStoreProvider> keyStoreMap = new HashMap<>();
			keyStoreMap.put(akvProvider.getName(), akvProvider);
			SQLServerConnection.registerColumnEncryptionKeyStoreProviders(keyStoreMap);

		} catch (Exception e) {
			log.debug("context", e);
		}
	}

	
	

}
