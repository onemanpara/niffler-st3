package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import jakarta.persistence.EntityManagerFactory;

public class JpaExtension implements SuiteExtension {

    @Override
    public void afterAllTests() {
        EntityManagerFactoryProvider.INSTANCE.allStoredEntityManagerFactories()
                .forEach(EntityManagerFactory::close);
    }

}
