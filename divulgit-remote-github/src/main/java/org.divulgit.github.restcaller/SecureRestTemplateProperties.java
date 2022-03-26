package org.divulgit.github.restcaller;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("secure-rest")
@Data
class SecureRestTemplateProperties {

  /**
   * URL location, typically with file:// scheme, of a CA trust store file in JKS format.
   */
  String trustStore;

  /**
   * The store password of the given trust store.
   */
  char[] trustStorePassword;

  /**
   * One of the SSLContext algorithms listed at
   * https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#SSLContext .
   */
  String protocol = "TLSv1.2";
}