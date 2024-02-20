package com.zalando.ecommerce.config;

import com.zalando.ecommerce.model.Product;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Principal;
import java.util.List;

@Configuration
@OpenAPIDefinition
public class OpenAPIConfiguration {
    private final String devUrl = "http://localhost:8080/api/v1";
    private final String dockerUrl = "http://localhost:8081/api/v1";
    private final String gcpUrl = "https://soy-envelope-405012.lm.r.appspot.com/api/v1";

    @Bean
    public OpenAPI openAPI() {
        Server devServer = new Server()
                .url(devUrl)
                .description("Server URL in Development Environment");
        Server dockerServer = new Server()
                .url(dockerUrl)
                .description("Server URL in Docker Container");
        Server gcpServer = new Server().url(gcpUrl)
                .description("Server URL in Production Environment");

        Contact contact = new Contact()
                .email("mauhay.anna.liza@gmail.com")
                .name("Anna Liza Mauhay");

        Info info = new Info()
                .title("Z-Commerce API")
                .version("1.0.0")
                .contact(contact)
                .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html"))
                .description("""
                        Z-Commerce is a RESTful API designed as the backbone of your online shopping experience. Deployed on Google Cloud Platform, this API seamlessly manages all aspects from product listings to order fulfillment. Endpoints are rigorously tested through unit and integration tests.
                                                     
                        Z-Commerce caters to three user roles: Customers, Sellers, and Admin, each with tailored permissions and functionalities. The system utilizes APIKeys to deter anonymous calls to the endpoints. Secured endpoints are further protected with JWT authentication.
                                                     
                        For developers, comprehensive documentation using Swagger (OAS 3.1) is available on [SwaggerHub](https://app.swaggerhub.com/apis-docs/MAUHAYANNALIZA/Z-E-Commerce/1.0.0), offering clarity and ease of integration. Additionally, a Postman collection hosted on site1 simplifies API exploration and testing.
                        """);

        return new OpenAPI()
                .info(info)
                .servers(List.of(gcpServer, dockerServer, devServer));
    }

    static {
        SpringDocUtils.getConfig()
                .removeRequestWrapperToIgnore(Principal.class, HttpServletRequest.class);
    }
}
