package guru.qa.niffler.config;

public class DockerConfig implements Config {

    static final DockerConfig config = new DockerConfig();

    private DockerConfig() {
    }

    @Override
    public String databaseHost() {
        return "niffler-all-db";
    }

    @Override
    public String nifflerFrontendUrl() {
        return "niffler-frontend:3000";
    }

    @Override
    public String nifflerSpendUrl() {
        return "niffler-spend:8093";
    }
}
