package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pageobject.MainPage;
import pageobject.OrderPage;
import static org.junit.jupiter.api.Assertions.*;
import org.openqa.selenium.WebElement;
import java.util.List;
import org.junit.jupiter.api.Test;

public class OrderTest {
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        String browser = System.getProperty("browser", "chrome");
        if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new org.openqa.selenium.firefox.FirefoxDriver();
        } else {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        }
        driver.manage().window().maximize();
        driver.get("https://qa-scooter.praktikum-services.ru/");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @ParameterizedTest
    @CsvSource({
        "Иван, Иванов, Москва ул. Тверская д. 1, +79991234567, 25.12.2024, сутки, black, Позвоните за час",
        "Петр, Петров, Санкт-Петербург Невский пр. д. 10, +79999876543, 26.12.2024, двое суток, grey, Осторожно с самокатом"
    })
    void testOrderFlowWithTopButton(String name, String surname, String address, String phone, String date, String rentalPeriod, String color, String comment) {
        MainPage mainPage = new MainPage(driver);
        OrderPage orderPage = new OrderPage(driver);
        
        // Начинаем заказ с верхней кнопки
        assertTrue(mainPage.clickOrderButton(0), "Кнопка 'Заказать' (верхняя) не была нажата успешно");
        
        // Заполняем первую страницу (станция метро выбирается случайно)
        orderPage.fillFirstPage(name, surname, address, "", phone);
        orderPage.clickNextButton();
        
        // Заполняем вторую страницу
        orderPage.fillSecondPage(date, rentalPeriod, color, comment);
        
        // Нажимаем кнопку "Заказать" с проверкой
        assertTrue(orderPage.clickOrderButton(), "Кнопка 'Заказать' не была нажата успешно");
        
        // Нажимаем кнопку "Да" с проверкой
        System.out.println("=== ПОДТВЕРЖДЕНИЕ ЗАКАЗА ===");
        boolean confirmResult = orderPage.confirmOrder();
        
        if (!confirmResult) {
            System.out.println("ОШИБКА: Кнопка 'Да' не сработала или заказ не был оформлен!");
            System.out.println("Возможно, есть проблемы с оформлением заказа на тестовом стенде.");
            
            // Дополнительная диагностика
            String statusMessage = orderPage.getOrderStatusMessage();
            System.out.println("Текущий статус: " + statusMessage);
            
            // Проверяем, есть ли модальное окно успешного заказа
            boolean successModal = orderPage.isSuccessModalVisible();
            System.out.println("Модальное окно успешного заказа видимо: " + successModal);
        }
        
        assertTrue(confirmResult, "Заказ не был успешно оформлен после нажатия кнопки 'Да'. Проверьте логи выше для деталей.");
        
        // Дополнительная проверка успешного оформления
        System.out.println("=== ФИНАЛЬНАЯ ПРОВЕРКА УСПЕШНОГО ЗАКАЗА ===");
        boolean successModalVisible = orderPage.isSuccessModalVisible();
        String orderStatus = orderPage.getOrderStatusMessage();
        
        System.out.println("Модальное окно успешного заказа видимо: " + successModalVisible);
        System.out.println("Статус заказа: " + orderStatus);
        
        // Проверяем, что заказ действительно оформлен
        assertTrue(successModalVisible || orderStatus.contains("оформлен") || orderStatus.contains("№") || orderStatus.contains("номер"), 
                  "Заказ не был оформлен. Статус: " + orderStatus);
        
        System.out.println("✓ ЗАКАЗ УСПЕШНО ОФОРМЛЕН И ПОДТВЕРЖДЕН!");
        
        // Нажимаем кнопку "Посмотреть статус"
        System.out.println("=== ПЕРЕХОД К ПРОСМОТРУ СТАТУСА ЗАКАЗА ===");
        boolean statusButtonClicked = orderPage.clickViewStatusButton();
        
        if (statusButtonClicked) {
            System.out.println("✓ УСПЕШНО ПЕРЕШЛИ НА СТРАНИЦУ СТАТУСА ЗАКАЗА!");
        } else {
            System.out.println("✗ НЕ УДАЛОСЬ ПЕРЕЙТИ НА СТРАНИЦУ СТАТУСА ЗАКАЗА!");
            System.out.println("Возможно, кнопка 'Посмотреть статус' не появилась или не работает.");
        }
        
        // Проверяем успешность перехода (не строго обязательно)
        if (statusButtonClicked) {
            System.out.println("✓ ПОЛНЫЙ ЦИКЛ ОФОРМЛЕНИЯ И ПРОСМОТРА СТАТУСА ЗАВЕРШЕН!");
        }
    }

    @ParameterizedTest
    @CsvSource({
        "Анна, Сидорова, Москва ул. Арбат д. 5, +79995554433, 27.12.2024, сутки, black, ",
        "Мария, Козлова, Санкт-Петербург ул. Марата д. 15, +79994443322, 28.12.2024, двое суток, grey, Быть осторожным"
    })
    void testOrderFlowWithBottomButton(String name, String surname, String address, String phone, String date, String rentalPeriod, String color, String comment) {
        MainPage mainPage = new MainPage(driver);
        OrderPage orderPage = new OrderPage(driver);
        
        // Начинаем заказ с нижней кнопки
        assertTrue(mainPage.clickOrderButton(1), "Кнопка 'Заказать' (нижняя) не была нажата успешно");
        
        // Заполняем первую страницу (станция метро выбирается случайно)
        orderPage.fillFirstPage(name, surname, address, "", phone);
        orderPage.clickNextButton();
        
        // Заполняем вторую страницу
        orderPage.fillSecondPage(date, rentalPeriod, color, comment);
        
        // Нажимаем кнопку "Заказать" с проверкой
        assertTrue(orderPage.clickOrderButton(), "Кнопка 'Заказать' не была нажата успешно");
        
        // Нажимаем кнопку "Да" с проверкой
        System.out.println("=== ПОДТВЕРЖДЕНИЕ ЗАКАЗА ===");
        boolean confirmResult = orderPage.confirmOrder();
        
        if (!confirmResult) {
            System.out.println("ОШИБКА: Кнопка 'Да' не сработала или заказ не был оформлен!");
            System.out.println("Возможно, есть проблемы с оформлением заказа на тестовом стенде.");
            
            // Дополнительная диагностика
            String statusMessage = orderPage.getOrderStatusMessage();
            System.out.println("Текущий статус: " + statusMessage);
            
            // Проверяем, есть ли модальное окно успешного заказа
            boolean successModal = orderPage.isSuccessModalVisible();
            System.out.println("Модальное окно успешного заказа видимо: " + successModal);
        }
        
        assertTrue(confirmResult, "Заказ не был успешно оформлен после нажатия кнопки 'Да'. Проверьте логи выше для деталей.");
        
        // Дополнительная проверка успешного оформления
        System.out.println("=== ФИНАЛЬНАЯ ПРОВЕРКА УСПЕШНОГО ЗАКАЗА ===");
        boolean successModalVisible = orderPage.isSuccessModalVisible();
        String orderStatus = orderPage.getOrderStatusMessage();
        
        System.out.println("Модальное окно успешного заказа видимо: " + successModalVisible);
        System.out.println("Статус заказа: " + orderStatus);
        
        // Проверяем, что заказ действительно оформлен
        assertTrue(successModalVisible || orderStatus.contains("оформлен") || orderStatus.contains("№") || orderStatus.contains("номер"), 
                  "Заказ не был оформлен. Статус: " + orderStatus);
        
        System.out.println("✓ ЗАКАЗ УСПЕШНО ОФОРМЛЕН И ПОДТВЕРЖДЕН!");
        
        // Нажимаем кнопку "Посмотреть статус"
        System.out.println("=== ПЕРЕХОД К ПРОСМОТРУ СТАТУСА ЗАКАЗА ===");
        boolean statusButtonClicked = orderPage.clickViewStatusButton();
        
        if (statusButtonClicked) {
            System.out.println("✓ УСПЕШНО ПЕРЕШЛИ НА СТРАНИЦУ СТАТУСА ЗАКАЗА!");
        } else {
            System.out.println("✗ НЕ УДАЛОСЬ ПЕРЕЙТИ НА СТРАНИЦУ СТАТУСА ЗАКАЗА!");
            System.out.println("Возможно, кнопка 'Посмотреть статус' не появилась или не работает.");
        }
        
        // Проверяем успешность перехода (не строго обязательно)
        if (statusButtonClicked) {
            System.out.println("✓ ПОЛНЫЙ ЦИКЛ ОФОРМЛЕНИЯ И ПРОСМОТРА СТАТУСА ЗАВЕРШЕН!");
        }
    }

    @Test
    void testCompleteOrderFlowWithStatusCheck() {
        System.out.println("=== ПОЛНЫЙ ТЕСТ ОФОРМЛЕНИЯ ЗАКАЗА С ПРОВЕРКОЙ СТАТУСА ===");
        
        MainPage mainPage = new MainPage(driver);
        OrderPage orderPage = new OrderPage(driver);
        
        // Начинаем заказ с верхней кнопки
        assertTrue(mainPage.clickOrderButton(0), "Кнопка 'Заказать' (верхняя) не была нажата успешно");
        
        // Заполняем первую страницу
        orderPage.fillFirstPage("Тест", "Тестов", "Москва, ул. Тестовая, д. 1", "", "+79991234567");
        orderPage.clickNextButton();
        
        // Заполняем вторую страницу
        orderPage.fillSecondPage("25.12.2024", "сутки", "black", "Тестовый комментарий");
        
        // Нажимаем кнопку "Заказать"
        assertTrue(orderPage.clickOrderButton(), "Кнопка 'Заказать' не была нажата успешно");
        
        // Подтверждаем заказ
        System.out.println("=== ПОДТВЕРЖДЕНИЕ ЗАКАЗА ===");
        boolean confirmResult = orderPage.confirmOrder();
        assertTrue(confirmResult, "Заказ не был успешно оформлен после нажатия кнопки 'Да'");
        
        // Проверяем успешное оформление
        boolean successModalVisible = orderPage.isSuccessModalVisible();
        String orderStatus = orderPage.getOrderStatusMessage();
        assertTrue(successModalVisible || orderStatus.contains("оформлен") || orderStatus.contains("№") || orderStatus.contains("номер"), 
                  "Заказ не был оформлен. Статус: " + orderStatus);
        
        System.out.println("✓ ЗАКАЗ УСПЕШНО ОФОРМЛЕН!");
        
        // Нажимаем кнопку "Посмотреть статус"
        System.out.println("=== ПЕРЕХОД К ПРОСМОТРУ СТАТУСА ЗАКАЗА ===");
        boolean statusButtonClicked = orderPage.clickViewStatusButton();
        
        // Проверяем успешность перехода на страницу статуса
        assertTrue(statusButtonClicked, "Не удалось перейти на страницу статуса заказа");
        
        System.out.println("✓ ПОЛНЫЙ ЦИКЛ ОФОРМЛЕНИЯ И ПРОСМОТРА СТАТУСА ЗАВЕРШЕН УСПЕШНО!");
    }
} 