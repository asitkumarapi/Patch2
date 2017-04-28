package SampleSpringProject.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import SampleSpringProject.model.BlockCardResponse;
import SampleSpringProject.service.BlockCardService;

public class BlockcardServiceImpl implements BlockCardService {
	
	private static final Logger log = LoggerFactory.getLogger(BlockcardServiceImpl.class);
	
	@Value("${server.ssl.key-store-password}")
	private String keyStorePassword;
	@Value("${server.ssl.key-store-type}")
	private String keyStoreType;
	@Value("${server.ssl.key-store}")
	private Resource keyStoreLocation;
	
	private RestTemplate restTemplate = null;
	private String serviceAPI = "https://mt01vip1.mt01.mex.nsroot.net:10443/v1/card/afews/blkOvcAtch";
	HttpClient httpClient = null;
	HttpComponentsClientHttpRequestFactory requestFactory = null;
	RestTemplate rt = null;
	KeyStore keyStore = null;
	
	public RestTemplate getRestTemplate(){
		
		try {
			//keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			log.info("########### keyStore" + keyStoreType);
			keyStore = KeyStore.getInstance(keyStoreType);
			log.info("########### keyStore");
			log.info("########### keyStore " + keyStoreLocation.getFilename());
			log.info("########### keyStore " + keyStorePassword);
				keyStore.load(new FileInputStream(new File(keyStoreLocation.getFilename())), keyStorePassword.toCharArray());
			
		
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
			        new SSLContextBuilder()
			                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
			                .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
			                .build(),
			        NoopHostnameVerifier.INSTANCE);
		
			httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();

	    requestFactory = new HttpComponentsClientHttpRequestFactory();
	    requestFactory.setHttpClient(httpClient);
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     return new RestTemplate(requestFactory);
	}
		
	@Override
	public String blockCard() {
		restTemplate = getRestTemplate();
		String phoneNumber = "";		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		headers.set("Content-Type", "application/json");

		Map<String, String> param = new HashMap<String, String>();
		param.put("phoneNumber", phoneNumber);

		ResponseEntity<?> exchange = restTemplate.exchange(serviceAPI, HttpMethod.POST, new HttpEntity<String>(headers),
				BlockCardResponse.class, param);

		return exchange.getBody().toString();
	}

}
