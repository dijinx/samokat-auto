package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import pageobject.MainPage;
import pageobject.OrderPage;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderFormAnalysisTest {
    private static final Logger logger = LogManager.getLogger(OrderFormAnalysisTest.class);
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
    void analyzeOrderFormAfterDateSelection() {
        logger.info("=== АНАЛИЗ ФОРМЫ ЗАКАЗА ПОСЛЕ ВЫБОРА ДАТЫ ===");
        MainPage mainPage = new MainPage(driver);
        assertTrue(mainPage.clickOrderButton(0), "Кнопка 'Заказать' не была нажата успешно");
        fillFirstPage();
        OrderPage orderPage = new OrderPage(driver);
        orderPage.clickNextButton();
        logger.info("\n--- АНАЛИЗ ВТОРОЙ СТРАНИЦЫ ФОРМЫ ---");
        orderPage.analyzeSecondPageFields();
        orderPage.selectDate();
        orderPage.analyzeAfterDateSelection();
        orderPage.fillRemainingFields();
        orderPage.analyzeOrderButton();
        logger.info("\n--- НАЖАТИЕ КНОПКИ ЗАКАЗА НА ВТОРОЙ СТРАНИЦЕ ---");
        assertTrue(orderPage.clickOrderButton(), "Кнопка 'Заказать' на второй странице не была нажата успешно");
    }

    private void fillFirstPage() {
        logger.info("Заполняем первую страницу...");
        MainPage mainPage = new MainPage(driver);
        mainPage.enterName("Тест");
        mainPage.enterSurname("Тестов");
        mainPage.enterAddress("Москва, ул. Тестовая, д. 1");
        mainPage.selectMetro("Сокольники");
        mainPage.enterPhone("+79991234567");
    }

    private void analyzeSecondPageFields() {
        logger.info("Анализируем поля второй страницы:");
        OrderPage orderPage = new OrderPage(driver);
        logger.info("  Поле даты: {}", orderPage.isDateInputPresent() ? "найдено" : "не найдено");
        logger.info("  Выпадающий список аренды: {}", orderPage.isRentalDropdownPresent() ? "найден" : "не найден");
        logger.info("  Чекбоксов цвета: {}", orderPage.getColorCheckboxesCount());
        logger.info("  Поле комментария: {}", orderPage.getCommentInputsCount() > 0 ? "найдено" : "не найдено");
        logger.info("  Кнопок заказа: {}", orderPage.getOrderButtonsCount());
    }

    private void selectDate() {
        logger.info("--- ВЫБОР ДАТЫ ---");
        OrderPage orderPage = new OrderPage(driver);
        orderPage.clickDateInput();
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        if (orderPage.getAvailableDaysCount() > 0) {
            orderPage.clickFirstAvailableDay();
            logger.info("  Выбран день");
        } else if (orderPage.getCalendarDaysCount() > 0) {
            orderPage.clickFirstCalendarDay();
            logger.info("  Выбран день (альтернативный)");
        }
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void analyzeAfterDateSelection() {
        logger.info("--- АНАЛИЗ ПОСЛЕ ВЫБОРА ДАТЫ ---");
        OrderPage orderPage = new OrderPage(driver);
        String dateValue = orderPage.getDateInputValue();
        logger.info("  Значение поля даты: '{}'", dateValue);
        logger.info("  Выпадающий список аренды доступен: {}", orderPage.isRentalDropdownPresent());
        logger.info("  Чекбоксов цвета доступно: {}", orderPage.getColorCheckboxesCount());
        for (int i = 0; i < orderPage.getColorCheckboxesCount(); i++) {
            String id = orderPage.getColorCheckboxId(i);
            String label = orderPage.getColorCheckboxLabel(id);
            logger.info("    Чекбокс {}: {} (id: {})", i, label, id);
        }
    }

    private void fillRemainingFields() {
        logger.info("--- ЗАПОЛНЕНИЕ ОСТАЛЬНЫХ ПОЛЕЙ ---");
        OrderPage orderPage = new OrderPage(driver);
        try { orderPage.clickBody(); Thread.sleep(1000); } catch (Exception e) {}
        orderPage.clickRentalDropdown();
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        logger.info("  Опций аренды: {}", orderPage.getRentalOptionsCount());
        if (orderPage.getRentalOptionsCount() > 0) {
            orderPage.clickFirstRentalOption();
            logger.info("  Выбрана опция");
        }
        orderPage.clickFirstColorCheckbox();
        logger.info("  Выбран цвет");
        orderPage.enterComment("Тестовый комментарий");
        logger.info("  Введен комментарий");
    }

    private void analyzeOrderButton() {
        logger.info("--- АНАЛИЗ КНОПКИ ЗАКАЗА ---");
        OrderPage orderPage = new OrderPage(driver);
        logger.info("  Кнопок заказа найдено: {}", orderPage.getOrderButtonsCount());
        // Клик и анализ подтверждения и успешного заказа оставляем в PO
    }
} 