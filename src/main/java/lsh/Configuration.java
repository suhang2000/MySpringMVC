package lsh;

import lombok.Builder;
import lombok.Getter;

/**
 * Server configuration
 */
@Builder
@Getter
public class Configuration {
    // 启动类 BootStrap
    private Class<?> bootClass;
    // resource directory
    @Builder.Default
    private String resourcePath = "src/main/resources/";
    // JSP directory
    @Builder.Default
    private String viewPath = "/templates/";
    // static directory
    @Builder.Default
    private String assetPath = "/static/";
    // server port
    @Builder.Default
    private int serverPort = 9090;
    // tomcat docBase directory
    @Builder.Default
    private String docBase = "";
    // tomcat context directory
    @Builder.Default
    private String contextPath = "";
}
