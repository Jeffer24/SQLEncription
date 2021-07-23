package com.example.servicio.spring.sqlserver.app;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.servicio.spring.sqlserver.app.entity.Patients;
import com.example.servicio.spring.sqlserver.app.repository.PatientBKRepository;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionAzureKeyVaultProvider;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;

@SpringBootApplication
public class SpringBootSqlServerApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootSqlServerApplication.class);

	@Autowired
	private PatientBKRepository patientRepo;

	@Autowired
	private DataSource data;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSqlServerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<Patients> list = patientRepo.findAll();
		list.forEach(System.out::println);
		setKeyVaultProvider();
		patientRepo.save(getObject());
	}

	private Patients getObject() {
		Patients patient = new Patients();
		patient.setPatientId(3L);
		patient.setFirstName("Pedro");
		patient.setMiddleName("Augusto");
		patient.setLastName("Perez");
		patient.setBirthDate(new Date());
		return patient;
	}

	private String setKeyVaultProvider() {
		try {

			SQLServerColumnEncryptionAzureKeyVaultProvider akvProvider = new SQLServerColumnEncryptionAzureKeyVaultProvider(
					"2f175547-f878-4eef-a2ce-08c837c74267", "c31fd227-f268-4645-8f42-03725d09e3ff");
			Map<String, SQLServerColumnEncryptionKeyStoreProvider> keyStoreMap = new HashMap<>();
			keyStoreMap.put(akvProvider.getName(), akvProvider);
			SQLServerConnection.registerColumnEncryptionKeyStoreProviders(keyStoreMap);
			return getEncryptedCEK(akvProvider,
					"0x014B12B65420CCBCFAAECC3C07533D2C962E4EFCE8C3E86C261B91100B4AD470BB34DE365C7D97E234B7E76C606E92F3B198EEEA316470A7FC10664C12A4964245");

		} catch (Exception e) {
			log.debug("context", e);
			return null;
		}
	}

	public static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for (byte b : a) {
			sb.append(String.format("%02x", b).toUpperCase());
		}
		return sb.toString();
	}

	private String getEncryptedCEK(SQLServerColumnEncryptionKeyStoreProvider storeProvider, String plainTextKey)
			throws SQLServerException {

		String algorithm = "RSA_OAEP";
		byte[] plainCEK = plainTextKey.getBytes();

		String keyAlias = "https://keyvaultencripcion.vault.azure.net/";

		byte[] decryptedCEK = storeProvider.decryptColumnEncryptionKey(keyAlias, algorithm, plainCEK);
		log.info(byteArrayToHex(decryptedCEK));

		return byteArrayToHex(decryptedCEK);
	}
}
