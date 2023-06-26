package com.vision.configuration;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {
	
	/*@Autowired
	private Environment env;*/
	
	private static String secreatKey;
	
	@Value("${encryptor.password}")
	public void setFtpPassword(String secreatKey) {
		JasyptConfig.secreatKey = secreatKey;
	}
	
	@Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//        config.setPassword(env.getProperty("encryptor.password"));
        config.setPassword(secreatKey);
        config.setAlgorithm("PBEWithSHA1AndDESede");
        config.setKeyObtentionIterations(1000);
        config.setPoolSize(1);
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
        
        
        /*StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithSHA1AndDESede");
        encryptor.setPassword(env.getProperty("encryptor.password"));
        return encryptor;*/
        
    }

}
