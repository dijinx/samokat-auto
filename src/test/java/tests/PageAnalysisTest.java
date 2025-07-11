package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import pageobject.MainPage;
import pageobject.OrderPage;

public class PageAnalysisTest {
    private static final Logger logger = LogManager.getLogger(PageAnalysisTest.class);
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        String browser = System.getProperty("browser", "chrome");
        if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new org.openqa.selenium.firefox.FirefoxDriver();
        } else {
            WebDriverManager.chromedriver().setup();
            org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            driver = new ChromeDriver(options);
        }
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void analyzeMainPageAndOrderFlow() {
        logger.info("=== ПОЛНЫЙ АНАЛИЗ САЙТА САМОКАТ ===");
        driver.get("https://qa-scooter.praktikum-services.ru/");
        
        // Анализ главной страницы
        analyzeMainPage();
        
        // Анализ FAQ
        analyzeFAQ();
        
        // Анализ кнопок заказа и переходов на форму
        analyzeOrderButtons();
        
        // Анализ логотипов и переходов
        analyzeLogos();
        
        // Анализ страницы статуса заказа
        analyzeOrderStatusPage();
    }

    private void analyzeMainPage() {
        logger.info("--- АНАЛИЗ ГЛАВНОЙ СТРАНИЦЫ ---");
        MainPage mainPage = new MainPage(driver);
        var headers = mainPage.getHeadersText();
        logger.info("Заголовки H1: {}", headers.size());
        for (String header : headers) {
            logger.info("  H1: {}", header);
        }
        int orderButtonsCount = mainPage.getOrderButtonsCount();
        logger.info("Кнопок 'Заказать': {}", orderButtonsCount);
        var orderButtonsText = mainPage.getOrderButtonsText();
        var orderButtonsClass = mainPage.getOrderButtonsClass();
        for (int i = 0; i < orderButtonsCount; i++) {
            logger.info("  Кнопка {}: {} | Класс: {}", i, orderButtonsText.get(i), orderButtonsClass.get(i));
        }
        int logosCount = mainPage.getLogosCount();
        logger.info("Логотипов: {}", logosCount);
        var logosAlt = mainPage.getLogosAlt();
        var logosSrc = mainPage.getLogosSrc();
        for (int i = 0; i < logosCount; i++) {
            logger.info("  Логотип: {} | src: {}", logosAlt.get(i), logosSrc.get(i));
        }
        int statusButtonsCount = mainPage.getStatusButtonsCount();
        logger.info("Кнопок статуса заказа: {}", statusButtonsCount);
        var statusButtonsText = mainPage.getStatusButtonsText();
        var statusButtonsClass = mainPage.getStatusButtonsClass();
        for (int i = 0; i < statusButtonsCount; i++) {
            logger.info("  Статус кнопка: {} | Класс: {}", statusButtonsText.get(i), statusButtonsClass.get(i));
        }
    }

    private void analyzeFAQ() {
        logger.info("--- АНАЛИЗ FAQ ---");
        MainPage mainPage = new MainPage(driver);
        int faqCount = mainPage.getFaqQuestionsCount();
        logger.info("FAQ вопросов: {}", faqCount);
        for (int i = 0; i < Math.min(3, faqCount); i++) {
            logger.info("  FAQ {}: {}", i, mainPage.getFaqQuestionText(i));
            logger.info("    Класс: {}", mainPage.getFaqQuestionClass(i));
            mainPage.clickFaqQuestion(i);
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            int answersCount = mainPage.getFaqAnswersCount();
            logger.info("    Ответов видно: {}", answersCount);
            for (int j = 0; j < answersCount; j++) {
                if (mainPage.isFaqAnswerVisible(j)) {
                    String answerText = mainPage.getFaqAnswerText(j);
                    logger.info("    Ответ: {}...", answerText.substring(0, Math.min(50, answerText.length())));
                }
            }
        }
    }

    private void analyzeOrderButtons() {
        logger.info("--- АНАЛИЗ КНОПОК ЗАКАЗА И ФОРМЫ ---");
        MainPage mainPage = new MainPage(driver);
        // Верхняя кнопка заказа
        List<WebElement> topOrderButtons = mainPage.getOrderButtons();
        if (topOrderButtons.size() > 0) {
            logger.info("Кликаем по верхней кнопке заказа...");
            topOrderButtons.get(0).click();
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            analyzeOrderForm("Верхняя кнопка");
        }
        // Возвращаемся на главную
        driver.get("https://qa-scooter.praktikum-services.ru/");
        // Нижняя кнопка заказа
        List<WebElement> allOrderButtons = mainPage.getOrderButtons();
        if (allOrderButtons.size() > 1) {
            logger.info("Кликаем по нижней кнопке заказа...");
            allOrderButtons.get(allOrderButtons.size() - 1).click();
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            analyzeOrderForm("Нижняя кнопка");
        }
    }

    private void analyzeOrderForm(String buttonType) {
        logger.info("--- АНАЛИЗ ФОРМЫ ЗАКАЗА ({} )---", buttonType);
        OrderPage orderPage = new OrderPage(driver);
        // Поля ввода
        List<WebElement> inputs = orderPage.getAllInputs();
        logger.info("Полей ввода: {}", inputs.size());
        for (WebElement input : inputs) {
            String placeholder = input.getAttribute("placeholder");
            String className = input.getAttribute("class");
            String id = input.getAttribute("id");
            logger.info("  Поле: placeholder='{}' | class='{}' | id='{}'", placeholder, className, id);
        }
        // Кнопка "Далее"
        List<WebElement> nextButtons = orderPage.getNextButtons();
        logger.info("Кнопок 'Далее': {}", nextButtons.size());
        for (WebElement btn : nextButtons) {
            logger.info("  Кнопка Далее: {} | Класс: {}", btn.getText(), btn.getAttribute("class"));
        }
        // Анализ выпадающего списка метро
        analyzeMetroDropdown(orderPage);
        // Анализ календаря даты
        analyzeDateCalendar(orderPage);
        // Заполняем форму и переходим на вторую страницу
        fillOrderFormFirstPage(orderPage);
        // Анализ второй страницы формы
        analyzeOrderFormSecondPage(orderPage);
    }

    private void analyzeMetroDropdown(OrderPage orderPage) {
        logger.info("--- АНАЛИЗ ВЫПАДАЮЩЕГО СПИСКА МЕТРО ---");
        WebElement metroInput = orderPage.getMetroInput();
        if (metroInput != null) {
            logger.info("Поле метро найдено: {}", metroInput.getAttribute("placeholder"));
            metroInput.click();
            metroInput.sendKeys("Алтуфьево");
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            List<WebElement> metroOptions = orderPage.getMetroOptions();
            logger.info("Опций метро: {}", metroOptions.size());
            for (int i = 0; i < Math.min(10, metroOptions.size()); i++) {
                WebElement option = metroOptions.get(i);
                logger.info("  Опция {}: {} | Класс: {}", i, option.getText(), option.getAttribute("class"));
            }
            if (metroOptions.size() > 0) {
                try {
                    List<WebElement> freshOptions = orderPage.getMetroOptions();
                    if (freshOptions.size() > 0) {
                        WebElement option = freshOptions.get(0);
                        String optionText = option.getText();
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
                        logger.info("  Выбрана опция: {}", optionText);
                    }
                } catch (Exception e) {
                    logger.error("  Ошибка при выборе опции метро: {}", e.getMessage());
                }
            }
        }
    }

    private void analyzeDateCalendar(OrderPage orderPage) {
        logger.info("--- АНАЛИЗ КАЛЕНДАРЯ ДАТЫ ---");
        List<WebElement> dateInputs = orderPage.getAllInputs().stream()
                .filter(e -> e.getAttribute("placeholder") != null && e.getAttribute("placeholder").contains("Когда привезти"))
                .toList();
        if (dateInputs.size() > 0) {
            WebElement dateInput = dateInputs.get(0);
            logger.info("Поле даты найдено: {}", dateInput.getAttribute("placeholder"));
            logger.info("Класс поля даты: {}", dateInput.getAttribute("class"));
            dateInput.click();
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            List<WebElement> calendarElements = orderPage.getCalendarElements();
            logger.info("Элементов календаря: {}", calendarElements.size());
            if (calendarElements.size() > 0) {
                logger.info("Календарь найден!");
                List<WebElement> dayElements = orderPage.getAvailableDays();
                logger.info("Доступных дней: {}", dayElements.size());
                for (int i = 0; i < Math.min(5, dayElements.size()); i++) {
                    WebElement day = dayElements.get(i);
                    logger.info("  День {}: {} | Класс: {}", i, day.getText(), day.getAttribute("class"));
                }
                if (dayElements.size() > 0) {
                    dayElements.get(0).click();
                    logger.info("  Выбран день: {}", dayElements.get(0).getText());
                }
            } else {
                List<WebElement> datePickers = orderPage.getCalendarElements();
                logger.info("Элементов datepicker: {}", datePickers.size());
                List<WebElement> calendarDays = orderPage.getCalendarDays();
                logger.info("Дней в календаре: {}", calendarDays.size());
                for (int i = 0; i < Math.min(5, calendarDays.size()); i++) {
                    WebElement day = calendarDays.get(i);
                    logger.info("  День календаря {}: {} | Класс: {}", i, day.getText(), day.getAttribute("class"));
                }
            }
        }
    }

    private void fillOrderFormFirstPage(OrderPage orderPage) {
        logger.info("--- ЗАПОЛНЕНИЕ ПЕРВОЙ СТРАНИЦЫ ФОРМЫ ---");
        List<WebElement> nameInputs = orderPage.getAllInputs().stream().filter(e -> "* Имя".equals(e.getAttribute("placeholder"))).toList();
        if (nameInputs.size() > 0) { nameInputs.get(0).sendKeys("Тест"); }
        List<WebElement> surnameInputs = orderPage.getAllInputs().stream().filter(e -> "* Фамилия".equals(e.getAttribute("placeholder"))).toList();
        if (surnameInputs.size() > 0) { surnameInputs.get(0).sendKeys("Тестов"); }
        List<WebElement> addressInputs = orderPage.getAllInputs().stream().filter(e -> "* Адрес: куда привезти заказ".equals(e.getAttribute("placeholder"))).toList();
        if (addressInputs.size() > 0) { addressInputs.get(0).sendKeys("Москва, ул. Тестовая, д. 1"); }
        List<WebElement> phoneInputs = orderPage.getAllInputs().stream().filter(e -> "* Телефон: на него позвонит курьер".equals(e.getAttribute("placeholder"))).toList();
        if (phoneInputs.size() > 0) { phoneInputs.get(0).sendKeys("+79991234567"); }
        List<WebElement> nextButtons = orderPage.getNextButtons();
        if (nextButtons.size() > 0) {
            nextButtons.get(0).click();
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    private void analyzeOrderFormSecondPage(OrderPage orderPage) {
        logger.info("--- АНАЛИЗ ВТОРОЙ СТРАНИЦЫ ФОРМЫ ---");
        List<WebElement> dateInputs = orderPage.getAllInputs().stream()
                .filter(e -> e.getAttribute("placeholder") != null && e.getAttribute("placeholder").contains("Когда привезти"))
                .toList();
        logger.info("Полей даты: {}", dateInputs.size());
        List<WebElement> rentalDropdowns = orderPage.getRentalDropdowns();
        logger.info("Выпадающих списков аренды: {}", rentalDropdowns.size());
        List<WebElement> colorCheckboxes = orderPage.getColorCheckboxes();
        logger.info("Чекбоксов цвета: {}", colorCheckboxes.size());
        List<WebElement> commentInputs = orderPage.getCommentInputs();
        logger.info("Полей комментария: {}", commentInputs.size());
        List<WebElement> orderButtons = orderPage.getOrderButtons();
        logger.info("Кнопок 'Заказать' на второй странице: {}", orderButtons.size());
        if (dateInputs.size() > 0) {
            dateInputs.get(0).click();
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            List<WebElement> availableDays = orderPage.getAvailableDays();
            if (availableDays.size() > 0) { availableDays.get(0).click(); }
        }
        if (rentalDropdowns.size() > 0) {
            try {
                orderPage.getBody().click();
                Thread.sleep(1000);
            } catch (Exception e) { }
            rentalDropdowns.get(0).click();
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            List<WebElement> rentalOptions = orderPage.getRentalOptions();
            logger.info("Опций аренды: {}", rentalOptions.size());
            for (WebElement option : rentalOptions) {
                logger.info("  Опция аренды: {}", option.getText());
            }
            if (rentalOptions.size() > 0) { rentalOptions.get(0).click(); }
        }
        if (colorCheckboxes.size() > 0) { colorCheckboxes.get(0).click(); }
        if (commentInputs.size() > 0) { commentInputs.get(0).sendKeys("Тестовый комментарий"); }
        if (orderButtons.size() > 0) {
            WebElement targetButton = null;
            for (WebElement button : orderButtons) {
                String className = button.getAttribute("class");
                if (className.contains("Button_Middle__1CSJM")) {
                    targetButton = button;
                    logger.info("Найдена кнопка 'Заказать' с нужным классом: {}", className);
                    break;
                }
            }
            if (targetButton == null) {
                targetButton = orderButtons.get(0);
                logger.info("Используем первую найденную кнопку 'Заказать'");
            }
            targetButton.click();
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            analyzeConfirmationModal(orderPage);
        }
    }

    private void analyzeConfirmationModal(OrderPage orderPage) {
        logger.info("--- АНАЛИЗ МОДАЛЬНОГО ОКНА ПОДТВЕРЖДЕНИЯ ---");
        List<WebElement> confirmButtons = orderPage.getConfirmButtons();
        logger.info("Кнопок подтверждения 'Да': {}", confirmButtons.size());
        if (confirmButtons.size() > 0) {
            confirmButtons.get(0).click();
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            List<WebElement> successModals = orderPage.getSuccessModals();
            logger.info("Окон успешного заказа: {}", successModals.size());
            for (WebElement modal : successModals) {
                logger.info("  Модальное окно: {}", modal.getText());
            }
        }
    }

    private void analyzeLogos() {
        logger.info("--- АНАЛИЗ ЛОГОТИПОВ И ПЕРЕХОДОВ ---");
        driver.get("https://qa-scooter.praktikum-services.ru/");
        MainPage mainPage = new MainPage(driver);
        // Логотип Самокат
        List<WebElement> samokatLogos = mainPage.getSamokatLogos();
        logger.info("Логотипов Самокат: {}", samokatLogos.size());
        if (samokatLogos.size() > 0) {
            logger.info("Кликаем по логотипу Самокат...");
            samokatLogos.get(0).click();
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            logger.info("URL после клика по логотипу Самокат: {}", driver.getCurrentUrl());
        }
        // Логотип Яндекс
        driver.get("https://qa-scooter.praktikum-services.ru/");
        List<WebElement> yandexLogos = mainPage.getYandexLogos();
        logger.info("Логотипов Яндекс: {}", yandexLogos.size());
        if (yandexLogos.size() > 0) {
            logger.info("Кликаем по логотипу Яндекс...");
            yandexLogos.get(0).click();
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            logger.info("URL после клика по логотипу Яндекс: {}", driver.getCurrentUrl());
        }
    }

    private void analyzeOrderStatusPage() {
        logger.info("--- АНАЛИЗ СТРАНИЦЫ СТАТУСА ЗАКАЗА ---");
        driver.get("https://qa-scooter.praktikum-services.ru/");
        MainPage mainPage = new MainPage(driver);
        // Кнопка статуса заказа
        List<WebElement> statusButtons = mainPage.getStatusOrderButtons();
        logger.info("Кнопок статуса заказа: {}", statusButtons.size());
        if (statusButtons.size() > 0) {
            logger.info("Кликаем по кнопке статуса заказа...");
            statusButtons.get(0).click();
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            // Анализ полей на странице статуса
            List<WebElement> statusInputs = mainPage.getStatusInputs();
            logger.info("Полей на странице статуса: {}", statusInputs.size());
            for (WebElement input : statusInputs) {
                String placeholder = input.getAttribute("placeholder");
                String className = input.getAttribute("class");
                logger.info("  Поле статуса: placeholder='{}' | class='{}'", placeholder, className);
            }
            // Кнопка "Go!"
            List<WebElement> goButtons = mainPage.getGoButtons();
            logger.info("Кнопок 'Go!': {}", goButtons.size());
            if (goButtons.size() > 0) {
                logger.info("Кликаем по кнопке 'Go!'...");
                goButtons.get(0).click();
                try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                // Анализ результата
                List<WebElement> resultElements = mainPage.getTrackNotFoundElements();
                logger.info("Элементов 'не найдено': {}", resultElements.size());
                for (WebElement element : resultElements) {
                    logger.info("  Результат: {}", element.getText());
                }
            }
        }
    }
} 