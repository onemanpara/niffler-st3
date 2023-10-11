package guru.qa.niffler.config;

public class LocalConfig implements Config {

    static final LocalConfig config = new LocalConfig();

    private LocalConfig() {
    }

    @Override
    public String databaseHost() {
        return "localhost";
    }

    @Override
    public String nifflerFrontendUrl() {
        return "http://127.0.0.1:3000";
    }

    @Override
    public String nifflerSpendUrl() {
        return "http://127.0.0.1:8093";
    }

    @Override
    public String nifflerAuthUrl() {
        return "http://127.0.0.1:9000";
    }

    @Override
    public String nifflerUserdataUrl() {
        return "http://127.0.0.1:8090";
    }

    @Override
    public String currencyGrpcAddress() {
        return "localhost";
    }

    @Override
    public int currencyGrpcPort() {
        return 8092;
    }
}
