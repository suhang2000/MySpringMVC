package lsh;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lsh.aop.AOP;
import lsh.core.BeanContainer;
import lsh.ioc.IOC;
import lsh.mvc.server.Server;
import lsh.mvc.server.TomcatServer;

/**
 * Starter
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class MainApplication {
    // global configuration
    @Getter
    private static Configuration configuration = Configuration.builder().build();
    // default server
    @Getter
    private static Server server;

    public static void run(Class<?> bootClass) {
        run(Configuration.builder().bootClass(bootClass).build());
    }

    public static void run(Class<?> bootClass, int port) {
        run(Configuration.builder().bootClass(bootClass).serverPort(port).build());
    }

    public static void run(Configuration configuration) {
        new MainApplication().start(configuration);
    }

    private void start(Configuration configuration) {
        try {
            MainApplication.configuration = configuration;
            String basePackage = configuration.getBootClass().getPackage().getName();
            BeanContainer.getInstance().loadBeans(basePackage);
            new AOP().doAOP();
            new IOC().doIOC();

            server = new TomcatServer(configuration);
            server.startServer();
        } catch (Exception e) {
            log.error("start failed", e);
        }
    }
}
