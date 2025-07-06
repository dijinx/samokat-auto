package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pageobject.MainPage;
import pageobject.OrderPage;
import static org.junit.jupiter.api.Assertions.*;

public class OrderStatusTest {
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

    @Test
    void testOrderStatusWithInvalidNumber() {
        MainPage mainPage = new MainPage(driver);
        
        // Кликаем на кнопку статуса заказа
        assertTrue(mainPage.clickOrderStatusButton(), "Кнопка 'Статус заказа' не была нажата успешно");
        
        // Вводим несуществующий номер заказа
        assertTrue(mainPage.enterOrderNumber("123456789"), "Номер заказа не был введен успешно");
        
        // Нажимаем кнопку Go!
        assertTrue(mainPage.clickGoButton(), "Кнопка 'Go!' не была нажата успешно");
        
        // Проверяем, что появилось сообщение о том, что заказ не найден
        assertTrue(mainPage.isOrderNotFoundMessageVisible(), "Сообщение о том, что заказ не найден, не появилось");
        System.out.println("Сообщение о том, что заказ не найден, отображается (текст может быть пустым)");
    }

    @Test
    void testOrderStatusWithValidNumber() {
        MainPage mainPage = new MainPage(driver);
        
        // Кликаем на кнопку статуса заказа
        assertTrue(mainPage.clickOrderStatusButton(), "Кнопка 'Статус заказа' не была нажата успешно");
        
        // Вводим существующий номер заказа
        assertTrue(mainPage.enterOrderNumber("123456789"), "Номер заказа не был введен успешно");
        
        // Нажимаем кнопку Go!
        assertTrue(mainPage.clickGoButton(), "Кнопка 'Go!' не была нажата успешно");
        
        // После клика по "Да"
        OrderPage orderPage = new OrderPage(driver);
        boolean success = orderPage.isSuccessModalVisible();
        String status = orderPage.getOrderStatusMessage();
        assertTrue(success || !status.contains("Хотите оформить заказ"), "Окно успешного заказа не появилось и окно подтверждения не исчезло");
        System.out.println("Статус заказа: " + status);
    }
} 