package guru.qa.niffler.api.spend;

import guru.qa.niffler.api.RestService;
import guru.qa.niffler.models.SpendJson;
import io.qameta.allure.Step;

import java.io.IOException;

public class SpendServiceClient extends RestService {

    private final SpendService spendService = retrofit.create(SpendService.class);

    public SpendServiceClient() {
        super(config.nifflerSpendUrl());
    }

    @Step("Create spend")
    public SpendJson addSpend(SpendJson spend) throws IOException {
        return spendService.addSpend(spend)
                .execute()
                .body();
    }
}
