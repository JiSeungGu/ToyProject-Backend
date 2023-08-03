package com.example.common.config;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
public class JasyptConfig {
    @Autowired
    private Environment environment;

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        // intellij 상에서는 정상적으로 값을 가지고 오지 못함 (OS 환경 변수가 아니라 intellij 환경 변수를 바라보기 때문임)
        // 정상 동작을 원한다면 command 창에서 java -jar 명령을 통해 실행을 시켜야 함 (테스트를 위해 intellij에서 실행 시키려면 암호화를 풀어서 실행하거나 intellij 환경 변수를 줘야 함)
        // intellij 환경변수를 주려면 RUN > Edit Configurations 의 VM Option 에 -Djasypt.encryptor.password=키 와 같이 입력한다.
      // 사용 가능한 경우 설정 파일에서 속성을 찾고, 그렇지 않으면 환경 변수에서 값을 가져옵니다.
        log.info("System.getenv(\"toyproject_encryptor_password\") : {}", System.getenv("toyproject_encryptor_password"));
        config.setPassword(System.getenv("toyproject_encryptor_password"));
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
      /*
      config.setPassword("hE+JoRLkL9uMo3siN71AckR8cNYV8qhbv9HUnUGxm6U=");
      config.setStringOutputType("base64");
      config.setKeyObtentionIterations(100);
      config.setPoolSize("1");
      config.setProvider(new BouncyCastleProvider());
      config.setAlgorithm("PBEWITHMD5ANDDES");
      config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
      config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
      encryptor.setConfig(config);

       */
      return encryptor;
    }

  public static void main(String[] args) {
    StringEncryptor encryptor = new JasyptConfig().stringEncryptor();
    System.out.println(encryptor.encrypt("Toy"));
    System.out.println(encryptor.decrypt("lYAKu7JvLBnC/+LO7frZ/pwfIh2rE36Sa1lX0Lcoigk4xg29VEsV3Otpv7zU8lv/"));
  }
}
