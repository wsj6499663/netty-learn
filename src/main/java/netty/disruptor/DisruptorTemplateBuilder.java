package netty.disruptor;

public class DisruptorTemplateBuilder {
    private static DisruptorConfig.DisruptorConfigBuilder builder;

    public static DisruptorConfig.DisruptorConfigBuilder newBuilder(String name) {
        builder = DisruptorConfig.builder().name(name);
        return builder;
    }


}
