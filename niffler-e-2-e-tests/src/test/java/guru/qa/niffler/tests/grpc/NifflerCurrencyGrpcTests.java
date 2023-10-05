package guru.qa.niffler.tests.grpc;

import guru.qa.grpc.niffler.grpc.CalculateRequest;
import guru.qa.grpc.niffler.grpc.CalculateResponse;
import guru.qa.grpc.niffler.grpc.CurrencyResponse;
import guru.qa.grpc.niffler.grpc.CurrencyValues;
import io.qameta.allure.Story;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static guru.qa.grpc.niffler.grpc.CurrencyValues.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NifflerCurrencyGrpcTests extends BaseGrpcTest {

    @ParameterizedTest(name = "Запрос на все валюты возвращает валюту {0}")
    @EnumSource(value = CurrencyValues.class, names = {"RUB", "USD", "KZT", "EUR"})
    void getAllCurrenciesShouldReturnEveryCurrency(CurrencyValues currency) {
        CurrencyResponse response = currencyStub.getAllCurrencies(EMPTY);
        assertEquals(4, response.getCurrenciesList().size());
        assertTrue(response.getCurrenciesList().stream().anyMatch(c -> c.getCurrency().equals(currency)));
    }

    @ParameterizedTest(name = "Возвращается та же сумма что и отправляется, если валюта траты {0} и валюта для отображения {0}")
    @EnumSource(value = CurrencyValues.class, names = {"RUB", "USD", "KZT", "EUR"})
    @Story("Конвертация между одинаковыми валютами")
    void shouldReturnSameValueIfSpendCurrencyAndDesiredCurrencyAreSame(CurrencyValues currency) {
        final double amount = 150;
        CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(currency)
                .setDesiredCurrency(currency)
                .build();
        CalculateResponse result = currencyStub.calculateRate(request);
        assertEquals(amount, result.getCalculatedAmount());
    }

    static Stream<Arguments> shouldCalculateRateFromMainCurrency() {
        return Stream.of(
                Arguments.of(100, RUB, USD, 1.5),
                Arguments.of(100, RUB, EUR, 1.39),
                Arguments.of(100, RUB, KZT, 714.29),
                Arguments.of(-100, RUB, USD, -1.5),
                Arguments.of(-100, RUB, EUR, -1.39),
                Arguments.of(-100, RUB, KZT, -714.29)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "При конвертации {0} {1} в валюту {2} результат равен {3} {2}")
    @Story("Конвертация трат из основной валюту")
    void shouldCalculateRateFromMainCurrency(double amount, CurrencyValues spendCurrency, CurrencyValues desiredCurrency, double expectedResult) {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .build();
        CalculateResponse result = currencyStub.calculateRate(request);

        assertEquals(expectedResult, result.getCalculatedAmount());
    }

    static Stream<Arguments> shouldCalculateRateToMainCurrency() {
        return Stream.of(
                Arguments.of(2, USD, RUB, 133.33),
                Arguments.of(1.4, EUR, RUB, 100.8),
                Arguments.of(715, KZT, RUB, 100.1),
                Arguments.of(-2, USD, RUB, -133.33),
                Arguments.of(-1.4, EUR, RUB, -100.8),
                Arguments.of(-715, KZT, RUB, -100.1)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "При конвертации {0} {1} в валюту {2} результат равен {3} {2}")
    @Story("Конвертация трат в основную валюту")
    void shouldCalculateRateToMainCurrency(double amount, CurrencyValues spendCurrency, CurrencyValues desiredCurrency, double expectedResult) {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .build();
        CalculateResponse result = currencyStub.calculateRate(request);

        assertEquals(expectedResult, result.getCalculatedAmount());
    }

    static Stream<Arguments> shouldCalculateRateBetweenAdditionalCurrencies() {
        return Stream.of(
                Arguments.of(5, USD, EUR, 4.63),
                Arguments.of(-10, USD, KZT, -4761.9),
                Arguments.of(4, EUR, USD, 4.32),
                Arguments.of(-9, EUR, KZT, -4628.57),
                Arguments.of(10, KZT, USD, 0.02),
                Arguments.of(-5, KZT, EUR, -0.01)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "При конвертации {0} {1} в валюту {2} результат равен {3} {2}")
    @Story("Конвертация трат между неосновными валютами")
    void shouldCalculateRateBetweenAdditionalCurrencies(double amount, CurrencyValues spendCurrency, CurrencyValues desiredCurrency, double expectedResult) {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .build();
        CalculateResponse result = currencyStub.calculateRate(request);

        assertEquals(expectedResult, result.getCalculatedAmount());
    }

    static Stream<Arguments> shouldReturnZeroIfAmountIsZeroWhenCalculateRate() {
        return Stream.of(
                Arguments.of(0, RUB, USD, 0),
                Arguments.of(0, RUB, EUR, 0),
                Arguments.of(0, RUB, KZT, 0),
                Arguments.of(0, USD, RUB, 0),
                Arguments.of(0, EUR, RUB, 0),
                Arguments.of(0, KZT, RUB, 0)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "При конвертации {0} {1} в {2} возвращается {0} {2}")
    @Story("Конвертация траты со значением 0")
    void shouldReturnZeroIfAmountIsZeroWhenCalculateRate(double amount, CurrencyValues spendCurrency, CurrencyValues desiredCurrency) {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .build();
        CalculateResponse result = currencyStub.calculateRate(request);

        assertEquals(amount, result.getCalculatedAmount());
    }

}
