package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.api.category.CategoryServiceClient;
import guru.qa.niffler.jupiter.annotations.Category;
import guru.qa.niffler.models.CategoryJson;
import org.junit.jupiter.api.extension.*;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    private final CategoryServiceClient categoryServiceClient = new CategoryServiceClient();


    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Category annotation = context.getRequiredTestMethod().getAnnotation(Category.class);
        if (annotation != null) {
            CategoryJson category = new CategoryJson();
            category.setUsername(annotation.username());
            category.setCategory(annotation.category());
            categoryServiceClient.addCategory(category);
            context.getStore(NAMESPACE).put(context.getUniqueId(), category);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext
                .getStore(CategoryExtension.NAMESPACE)
                .get(extensionContext.getUniqueId(), CategoryJson.class);
    }

}