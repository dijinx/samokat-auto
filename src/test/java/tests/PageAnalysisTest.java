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
        
        // Заголовок
        List<WebElement> headers = driver.findElements(By.xpath("//h1"));
        logger.info("Заголовки H1: {}", headers.size());
        for (WebElement header : headers) {
            logger.info("  H1: {}", header.getText());
        }
        
        // Кнопки заказа
        List<WebElement> orderButtons = driver.findElements(By.xpath("//button[contains(text(), 'Заказать')]"));
        logger.info("Кнопок 'Заказать': {}", orderButtons.size());
        for (int i = 0; i < orderButtons.size(); i++) {
            WebElement btn = orderButtons.get(i);
            logger.info("  Кнопка {}: {} | Класс: {}", i, btn.getText(), btn.getAttribute("class"));
        }
        
        // Логотипы
        List<WebElement> logos = driver.findElements(By.xpath("//img[contains(@alt, 'Самокат') or contains(@alt, 'Яндекс')]"));
        logger.info("Логотипов: {}", logos.size());
        for (WebElement logo : logos) {
            logger.info("  Логотип: {} | src: {}", logo.getAttribute("alt"), logo.getAttribute("src"));
        }
        
        // Кнопка статуса заказа
        List<WebElement> statusButtons = driver.findElements(By.xpath("//button[contains(text(), 'Статус заказа')]"));
        logger.info("Кнопок статуса заказа: {}", statusButtons.size());
        for (WebElement btn : statusButtons) {
            logger.info("  Статус кнопка: {} | Класс: {}", btn.getText(), btn.getAttribute("class"));
        }
    }

    private void analyzeFAQ() {
        logger.info("--- АНАЛИЗ FAQ ---");
        
        // Закрываем cookie banner если он есть
        try {
            WebElement cookieButton = driver.findElement(By.xpath("//button[contains(text(), 'да все привыкли')]"));
            if (cookieButton.isDisplayed()) {
                cookieButton.click();
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            // Cookie banner не найден или уже закрыт
        }
        
        // Вопросы FAQ
        List<WebElement> faqQuestions = driver.findElements(By.xpath("//div[@role='button' and contains(@class, 'accordion__button')]"));
        logger.info("FAQ вопросов: {}", faqQuestions.size());
        
        for (int i = 0; i < Math.min(3, faqQuestions.size()); i++) {
            WebElement question = faqQuestions.get(i);
            logger.info("  FAQ {}: {}", i, question.getText());
            logger.info("    Класс: {}", question.getAttribute("class"));
            
            // Кликаем по вопросу
            question.click();
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Ищем ответ
            List<WebElement> answers = driver.findElements(By.xpath("//div[contains(@class, 'accordion__panel')]"));
            logger.info("    Ответов видно: {}", answers.size());
            for (WebElement answer : answers) {
                if (answer.isDisplayed()) {
                    logger.info("    Ответ: {}...", answer.getText().substring(0, Math.min(50, answer.getText().length())));
                }
            }
        }
    }

    private void analyzeOrderButtons() {
        logger.info("--- АНАЛИЗ КНОПОК ЗАКАЗА И ФОРМЫ ---");
        
        // Верхняя кнопка заказа
        List<WebElement> topOrderButtons = driver.findElements(By.xpath("//button[contains(text(), 'Заказать')]"));
        if (topOrderButtons.size() > 0) {
            logger.info("Кликаем по верхней кнопке заказа...");
            topOrderButtons.get(0).click();
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            analyzeOrderForm("Верхняя кнопка");
        }
        
        // Возвращаемся на главную
        driver.get("https://qa-scooter.praktikum-services.ru/");
        
        // Нижняя кнопка заказа
        List<WebElement> allOrderButtons = driver.findElements(By.xpath("//button[contains(text(), 'Заказать')]"));
        if (allOrderButtons.size() > 1) {
            logger.info("Кликаем по нижней кнопке заказа...");
            allOrderButtons.get(allOrderButtons.size() - 1).click();
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            analyzeOrderForm("Нижняя кнопка");
        }
    }

    private void analyzeOrderForm(String buttonType) {
        logger.info("--- АНАЛИЗ ФОРМЫ ЗАКАЗА ({} )---", buttonType);
        
        // Поля ввода
        List<WebElement> inputs = driver.findElements(By.xpath("//input"));
        logger.info("Полей ввода: {}", inputs.size());
        
        for (WebElement input : inputs) {
            String placeholder = input.getAttribute("placeholder");
            String className = input.getAttribute("class");
            String id = input.getAttribute("id");
            logger.info("  Поле: placeholder='{}' | class='{}' | id='{}'", placeholder, className, id);
        }
        
        // Кнопка "Далее"
        List<WebElement> nextButtons = driver.findElements(By.xpath("//button[contains(text(), 'Далее')]"));
        logger.info("Кнопок 'Далее': {}", nextButtons.size());
        for (WebElement btn : nextButtons) {
            logger.info("  Кнопка Далее: {} | Класс: {}", btn.getText(), btn.getAttribute("class"));
        }
        
        // Анализ выпадающего списка метро
        analyzeMetroDropdown();
        
        // Анализ календаря даты
        analyzeDateCalendar();
        
        // Заполняем форму и переходим на вторую страницу
        fillOrderFormFirstPage();
        
        // Анализ второй страницы формы
        analyzeOrderFormSecondPage();
    }

    private void analyzeMetroDropdown() {
        logger.info("--- АНАЛИЗ ВЫПАДАЮЩЕГО СПИСКА МЕТРО ---");
        
        List<WebElement> metroInputs = driver.findElements(By.xpath("//input[contains(@placeholder, 'Станция метро')]"));
        if (metroInputs.size() > 0) {
            WebElement metroInput = metroInputs.get(0);
            logger.info("Поле метро найдено: {}", metroInput.getAttribute("placeholder"));
            
            // Кликаем и вводим текст
            metroInput.click();
            metroInput.sendKeys("Алтуфьево");
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Ищем опции
            List<WebElement> metroOptions = driver.findElements(By.xpath("//div[contains(@class, 'Order_Text__2broi')]"));
            logger.info("Опций метро: {}", metroOptions.size());
            
            for (int i = 0; i < Math.min(10, metroOptions.size()); i++) {
                WebElement option = metroOptions.get(i);
                logger.info("  Опция {}: {} | Класс: {}", i, option.getText(), option.getAttribute("class"));
            }
            
            // Кликаем по первой опции
            if (metroOptions.size() > 0) {
                try {
                    // Перепоиск элемента перед кликом
                    List<WebElement> freshOptions = driver.findElements(By.xpath("//div[contains(@class, 'Order_Text__2broi')]"));
                    if (freshOptions.size() > 0) {
                        // Используем JavaScript для клика, чтобы избежать StaleElementReference
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

    private void analyzeDateCalendar() {
        logger.info("--- АНАЛИЗ КАЛЕНДАРЯ ДАТЫ ---");
        
        List<WebElement> dateInputs = driver.findElements(By.xpath("//input[contains(@placeholder, 'Когда привезти')]"));
        if (dateInputs.size() > 0) {
            WebElement dateInput = dateInputs.get(0);
            logger.info("Поле даты найдено: {}", dateInput.getAttribute("placeholder"));
            logger.info("Класс поля даты: {}", dateInput.getAttribute("class"));
            
            // Кликаем по полю даты
            dateInput.click();
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Ищем календарь
            List<WebElement> calendarElements = driver.findElements(By.xpath("//div[contains(@class, 'react-datepicker')]"));
            logger.info("Элементов календаря: {}", calendarElements.size());
            
            if (calendarElements.size() > 0) {
                logger.info("Календарь найден!");
                
                // Ищем дни в календаре
                List<WebElement> dayElements = driver.findElements(By.xpath("//div[contains(@class, 'react-datepicker__day') and not(contains(@class, 'disabled'))]"));
                logger.info("Доступных дней: {}", dayElements.size());
                
                for (int i = 0; i < Math.min(5, dayElements.size()); i++) {
                    WebElement day = dayElements.get(i);
                    logger.info("  День {}: {} | Класс: {}", i, day.getText(), day.getAttribute("class"));
                }
                
                // Кликаем по первому доступному дню
                if (dayElements.size() > 0) {
                    dayElements.get(0).click();
                    logger.info("  Выбран день: {}", dayElements.get(0).getText());
                }
            } else {
                // Попробуем другие локаторы для календаря
                List<WebElement> datePickers = driver.findElements(By.xpath("//div[contains(@class, 'datepicker')]"));
                logger.info("Элементов datepicker: {}", datePickers.size());
                
                List<WebElement> calendarDays = driver.findElements(By.xpath("//td[contains(@class, 'day') and not(contains(@class, 'disabled'))]"));
                logger.info("Дней в календаре: {}", calendarDays.size());
                
                for (int i = 0; i < Math.min(5, calendarDays.size()); i++) {
                    WebElement day = calendarDays.get(i);
                    logger.info("  День календаря {}: {} | Класс: {}", i, day.getText(), day.getAttribute("class"));
                }
            }
        }
    }

    private void fillOrderFormFirstPage() {
        logger.info("--- ЗАПОЛНЕНИЕ ПЕРВОЙ СТРАНИЦЫ ФОРМЫ ---");
        
        // Заполняем поля
        List<WebElement> nameInputs = driver.findElements(By.xpath("//input[@placeholder='* Имя']"));
        if (nameInputs.size() > 0) {
            nameInputs.get(0).sendKeys("Тест");
        }
        
        List<WebElement> surnameInputs = driver.findElements(By.xpath("//input[@placeholder='* Фамилия']"));
        if (surnameInputs.size() > 0) {
            surnameInputs.get(0).sendKeys("Тестов");
        }
        
        List<WebElement> addressInputs = driver.findElements(By.xpath("//input[@placeholder='* Адрес: куда привезти заказ']"));
        if (addressInputs.size() > 0) {
            addressInputs.get(0).sendKeys("Москва, ул. Тестовая, д. 1");
        }
        
        List<WebElement> phoneInputs = driver.findElements(By.xpath("//input[@placeholder='* Телефон: на него позвонит курьер']"));
        if (phoneInputs.size() > 0) {
            phoneInputs.get(0).sendKeys("+79991234567");
        }
        
        // Кликаем "Далее"
        List<WebElement> nextButtons = driver.findElements(By.xpath("//button[contains(text(), 'Далее')]"));
        if (nextButtons.size() > 0) {
            nextButtons.get(0).click();
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void analyzeOrderFormSecondPage() {
        logger.info("--- АНАЛИЗ ВТОРОЙ СТРАНИЦЫ ФОРМЫ ---");
        
        // Поля второй страницы
        List<WebElement> dateInputs = driver.findElements(By.xpath("//input[contains(@placeholder, 'Когда привезти')]"));
        logger.info("Полей даты: {}", dateInputs.size());
        
        List<WebElement> rentalDropdowns = driver.findElements(By.xpath("//div[contains(@class, 'Dropdown-control')]"));
        logger.info("Выпадающих списков аренды: {}", rentalDropdowns.size());
        
        List<WebElement> colorCheckboxes = driver.findElements(By.xpath("//input[@type='checkbox']"));
        logger.info("Чекбоксов цвета: {}", colorCheckboxes.size());
        
        List<WebElement> commentInputs = driver.findElements(By.xpath("//input[@placeholder='Комментарий для курьера']"));
        logger.info("Полей комментария: {}", commentInputs.size());
        
        // Кнопка "Заказать"
        List<WebElement> orderButtons = driver.findElements(By.xpath("//button[contains(text(), 'Заказать')]"));
        logger.info("Кнопок 'Заказать' на второй странице: {}", orderButtons.size());
        
        // Заполняем вторую страницу
        if (dateInputs.size() > 0) {
            dateInputs.get(0).click();
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Выбираем первый доступный день
            List<WebElement> availableDays = driver.findElements(By.xpath("//div[contains(@class, 'react-datepicker__day') and not(contains(@class, 'disabled'))]"));
            if (availableDays.size() > 0) {
                availableDays.get(0).click();
            }
        }
        
        if (rentalDropdowns.size() > 0) {
            // Закрываем календарь если он открыт
            try {
                driver.findElement(By.tagName("body")).click();
                Thread.sleep(1000);
            } catch (Exception e) {
                // Игнорируем ошибки
            }
            
            rentalDropdowns.get(0).click();
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            List<WebElement> rentalOptions = driver.findElements(By.xpath("//div[contains(@class, 'Dropdown-option')]"));
            logger.info("Опций аренды: {}", rentalOptions.size());
            for (WebElement option : rentalOptions) {
                logger.info("  Опция аренды: {}", option.getText());
            }
            
            if (rentalOptions.size() > 0) {
                rentalOptions.get(0).click();
            }
        }
        
        if (colorCheckboxes.size() > 0) {
            colorCheckboxes.get(0).click();
        }
        
        if (commentInputs.size() > 0) {
            commentInputs.get(0).sendKeys("Тестовый комментарий");
        }
        
        // Кликаем "Заказать"
        if (orderButtons.size() > 0) {
            // Ищем кнопку с нужным классом
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
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Анализ модального окна подтверждения
            analyzeConfirmationModal();
        }
    }

    private void analyzeConfirmationModal() {
        logger.info("--- АНАЛИЗ МОДАЛЬНОГО ОКНА ПОДТВЕРЖДЕНИЯ ---");
        
        List<WebElement> confirmButtons = driver.findElements(By.xpath("//button[contains(text(), 'Да')]"));
        logger.info("Кнопок подтверждения 'Да': {}", confirmButtons.size());
        
        if (confirmButtons.size() > 0) {
            confirmButtons.get(0).click();
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Анализ окна успешного заказа
            List<WebElement> successModals = driver.findElements(By.xpath("//div[contains(text(), 'Заказ оформлен')]"));
            logger.info("Окон успешного заказа: {}", successModals.size());
            
            for (WebElement modal : successModals) {
                logger.info("  Модальное окно: {}", modal.getText());
            }
        }
    }

    private void analyzeLogos() {
        logger.info("--- АНАЛИЗ ЛОГОТИПОВ И ПЕРЕХОДОВ ---");
        
        driver.get("https://qa-scooter.praktikum-services.ru/");
        
        // Логотип Самокат
        List<WebElement> samokatLogos = driver.findElements(By.xpath("//img[contains(@alt, 'Самокат')]"));
        logger.info("Логотипов Самокат: {}", samokatLogos.size());
        
        if (samokatLogos.size() > 0) {
            logger.info("Кликаем по логотипу Самокат...");
            samokatLogos.get(0).click();
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            logger.info("URL после клика по логотипу Самокат: {}", driver.getCurrentUrl());
        }
        
        // Логотип Яндекс
        driver.get("https://qa-scooter.praktikum-services.ru/");
        List<WebElement> yandexLogos = driver.findElements(By.xpath("//img[contains(@alt, 'Яндекс')]"));
        logger.info("Логотипов Яндекс: {}", yandexLogos.size());
        
        if (yandexLogos.size() > 0) {
            logger.info("Кликаем по логотипу Яндекс...");
            yandexLogos.get(0).click();
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            logger.info("URL после клика по логотипу Яндекс: {}", driver.getCurrentUrl());
        }
    }

    private void analyzeOrderStatusPage() {
        logger.info("--- АНАЛИЗ СТРАНИЦЫ СТАТУСА ЗАКАЗА ---");
        
        driver.get("https://qa-scooter.praktikum-services.ru/");
        
        // Кнопка статуса заказа
        List<WebElement> statusButtons = driver.findElements(By.xpath("//button[contains(text(), 'Статус заказа')]"));
        logger.info("Кнопок статуса заказа: {}", statusButtons.size());
        
        if (statusButtons.size() > 0) {
            logger.info("Кликаем по кнопке статуса заказа...");
            statusButtons.get(0).click();
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Анализ полей на странице статуса
            List<WebElement> statusInputs = driver.findElements(By.xpath("//input"));
            logger.info("Полей на странице статуса: {}", statusInputs.size());
            
            for (WebElement input : statusInputs) {
                String placeholder = input.getAttribute("placeholder");
                String className = input.getAttribute("class");
                logger.info("  Поле статуса: placeholder='{}' | class='{}'", placeholder, className);
            }
            
            // Кнопка "Go!"
            List<WebElement> goButtons = driver.findElements(By.xpath("//button[contains(text(), 'Go!')]"));
            logger.info("Кнопок 'Go!': {}", goButtons.size());
            
            if (goButtons.size() > 0) {
                logger.info("Кликаем по кнопке 'Go!'...");
                goButtons.get(0).click();
                
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Анализ результата
                List<WebElement> resultElements = driver.findElements(By.xpath("//div[contains(@class, 'Track_NotFound')]"));
                logger.info("Элементов 'не найдено': {}", resultElements.size());
                
                for (WebElement element : resultElements) {
                    logger.info("  Результат: {}", element.getText());
                }
            }
        }
    }
} 