package io.springboot.twitter.configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.undertow.Undertow.Builder;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.ConfidentialPortManager;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.SecurityInfo;
import io.undertow.servlet.api.SecurityConstraint;
import io.undertow.servlet.api.TransportGuaranteeType;
import io.undertow.servlet.api.WebResourceCollection;

@Configuration
@Profile(value = { "pro" })
public class UndertowConfiguration implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {
	
	@Value("${server.ssl.enabled:false}")
	private boolean sslEnable;
	
	@Value("${server.port}")
	private Integer port;
	
	private static final Integer HTTP_PORT = 80;
	
	@Override
	public void customize(UndertowServletWebServerFactory factory) {
		
		//开启了https,则监听80端口，重定向
		if(sslEnable) {
			
			factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {
				@Override
				public void customize(Builder builder) {
					builder.addHttpListener(HTTP_PORT, "0.0.0.0");
					builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);				// 开启http2
				}
			});
			
			factory.addDeploymentInfoCustomizers(new UndertowDeploymentInfoCustomizer() {
				@Override
				public void customize(DeploymentInfo deploymentInfo) {
					
					SecurityConstraint securityConstraint = new SecurityConstraint();
					
					WebResourceCollection webResourceCollection = new WebResourceCollection();
					webResourceCollection.addUrlPattern("/*");
					
					securityConstraint.addWebResourceCollection(webResourceCollection);
					securityConstraint.setTransportGuaranteeType(TransportGuaranteeType.CONFIDENTIAL);
					securityConstraint.setEmptyRoleSemantic(SecurityInfo.EmptyRoleSemantic.PERMIT);
					
					deploymentInfo.addSecurityConstraint(securityConstraint);
					deploymentInfo.setConfidentialPortManager(new ConfidentialPortManager() {

						@Override
						public int getConfidentialPort(HttpServerExchange exchange) {
							return port;
						}
					});
				}
				
			});
		}
	}
}
