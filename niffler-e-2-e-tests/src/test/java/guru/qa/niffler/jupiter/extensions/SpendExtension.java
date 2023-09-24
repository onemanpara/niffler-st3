package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.api.spend.SpendServiceClient;
import guru.qa.niffler.jupiter.annotations.Spend;
import guru.qa.niffler.models.SpendJson;
import org.junit.jupiter.api.extension.*;

import java.util.Date;

public class SpendExtension implements BeforeEachCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendExtension.class);

    private SpendServiceClient spendService = new SpendServiceClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Spend annotation = context.getRequiredTestMethod().getAnnotation(Spend.class);
        if (annotation != null) {
            SpendJson spend = new SpendJson();
            spend.setUsername(annotation.username());
            spend.setDescription(annotation.description());
            spend.setAmount(annotation.amount());
            spend.setCategory(annotation.category());
            spend.setSpendDate(new Date());
            spend.setCurrency(annotation.currency());
            SpendJson createdSpend = spendService.addSpend(spend);
            context.getStore(NAMESPACE).put(context.getUniqueId(), createdSpend);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext
                .getStore(SpendExtension.NAMESPACE)
                .get(extensionContext.getUniqueId(), SpendJson.class);
    }
}
