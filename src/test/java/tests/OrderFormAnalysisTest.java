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

public class OrderFormAnalysisTest {
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
        System.out.println("=== АНАЛИЗ ФОРМЫ ЗАКАЗА ПОСЛЕ ВЫБОРА ДАТЫ ===");
        
        // Кликаем по кнопке заказа
        MainPage mainPage = new MainPage(driver);
        assertTrue(mainPage.clickOrderButton(0), "Кнопка 'Заказать' не была нажата успешно");
        
        // Заполняем первую страницу
        fillFirstPage();
        
        // Переходим на вторую страницу
        driver.findElement(By.xpath("//button[text()='Далее']")).click();
        
        System.out.println("\n--- АНАЛИЗ ВТОРОЙ СТРАНИЦЫ ФОРМЫ ---");
        
        // Анализируем поля второй страницы
        analyzeSecondPageFields();
        
        // Выбираем дату
        selectDate();
        
        // Анализируем что доступно после выбора даты
        analyzeAfterDateSelection();
        
        // Заполняем остальные поля
        fillRemainingFields();
        
        // Анализируем кнопку заказа
        analyzeOrderButton();
        
        // Нажимаем кнопку "Заказать" на второй странице
        System.out.println("\n--- НАЖАТИЕ КНОПКИ ЗАКАЗА НА ВТОРОЙ СТРАНИЦЕ ---");
        OrderPage orderPage = new OrderPage(driver);
        assertTrue(orderPage.clickOrderButton(), "Кнопка 'Заказать' на второй странице не была нажата успешно");
    }

    private void fillFirstPage() {
        System.out.println("Заполняем первую страницу...");
        
        driver.findElement(By.xpath("//input[@placeholder='* Имя']")).sendKeys("Тест");
        driver.findElement(By.xpath("//input[@placeholder='* Фамилия']")).sendKeys("Тестов");
        driver.findElement(By.xpath("//input[@placeholder='* Адрес: куда привезти заказ']")).sendKeys("Москва, ул. Тестовая, д. 1");
        
        // Выбираем метро
        WebElement metroInput = driver.findElement(By.xpath("//input[@placeholder='* Станция метро']"));
        metroInput.click();
        metroInput.sendKeys("Сокольники");
        driver.findElement(By.xpath("//div[contains(@class,'Order_Text__2broi') and text()='Сокольники']")).click();
        
        driver.findElement(By.xpath("//input[@placeholder='* Телефон: на него позвонит курьер']")).sendKeys("+79991234567");
    }

    private void analyzeSecondPageFields() {
        System.out.println("Анализируем поля второй страницы:");
        
        // Поле даты
        List<WebElement> dateInputs = driver.findElements(By.xpath("//input[contains(@placeholder, 'Когда привезти')]"));
        System.out.println("  Поле даты: " + (dateInputs.size() > 0 ? "найдено" : "не найдено"));
        
        // Выпадающий список срока аренды
        List<WebElement> rentalDropdowns = driver.findElements(By.xpath("//div[contains(@class, 'Dropdown-control')]"));
        System.out.println("  Выпадающий список аренды: " + (rentalDropdowns.size() > 0 ? "найден" : "не найден"));
        
        // Чекбоксы цвета
        List<WebElement> colorCheckboxes = driver.findElements(By.xpath("//input[@type='checkbox']"));
        System.out.println("  Чекбоксов цвета: " + colorCheckboxes.size());
        
        // Поле комментария
        List<WebElement> commentInputs = driver.findElements(By.xpath("//input[@placeholder='Комментарий для курьера']"));
        System.out.println("  Поле комментария: " + (commentInputs.size() > 0 ? "найдено" : "не найдено"));
        
        // Кнопка заказа
        List<WebElement> orderButtons = driver.findElements(By.xpath("//button[contains(text(), 'Заказать')]"));
        System.out.println("  Кнопок заказа: " + orderButtons.size());
    }

    private void selectDate() {
        System.out.println("\n--- ВЫБОР ДАТЫ ---");
        
        WebElement dateInput = driver.findElement(By.xpath("//input[@placeholder='* Когда привезти самокат']"));
        dateInput.click();
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Ищем доступные дни
        List<WebElement> availableDays = driver.findElements(By.xpath("//div[contains(@class, 'react-datepicker__day') and not(contains(@class, 'disabled'))]"));
        System.out.println("  Доступных дней: " + availableDays.size());
        
        if (availableDays.size() > 0) {
            availableDays.get(0).click();
            System.out.println("  Выбран день: " + availableDays.get(0).getText());
        } else {
            // Альтернативный поиск
            List<WebElement> calendarDays = driver.findElements(By.xpath("//td[contains(@class, 'day') and not(contains(@class, 'disabled'))]"));
            if (calendarDays.size() > 0) {
                calendarDays.get(0).click();
                System.out.println("  Выбран день (альтернативный): " + calendarDays.get(0).getText());
            }
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void analyzeAfterDateSelection() {
        System.out.println("\n--- АНАЛИЗ ПОСЛЕ ВЫБОРА ДАТЫ ---");
        
        // Проверяем, что поле даты заполнено
        WebElement dateInput = driver.findElement(By.xpath("//input[@placeholder='* Когда привезти самокат']"));
        String dateValue = dateInput.getAttribute("value");
        System.out.println("  Значение поля даты: '" + dateValue + "'");
        
        // Проверяем доступность других полей
        List<WebElement> rentalDropdowns = driver.findElements(By.xpath("//div[contains(@class, 'Dropdown-control')]"));
        System.out.println("  Выпадающий список аренды доступен: " + (rentalDropdowns.size() > 0));
        
        List<WebElement> colorCheckboxes = driver.findElements(By.xpath("//input[@type='checkbox']"));
        System.out.println("  Чекбоксов цвета доступно: " + colorCheckboxes.size());
        
        for (int i = 0; i < colorCheckboxes.size(); i++) {
            WebElement checkbox = colorCheckboxes.get(i);
            String id = checkbox.getAttribute("id");
            String label = driver.findElement(By.xpath("//label[@for='" + id + "']")).getText();
            System.out.println("    Чекбокс " + i + ": " + label + " (id: " + id + ")");
        }
    }

    private void fillRemainingFields() {
        System.out.println("\n--- ЗАПОЛНЕНИЕ ОСТАЛЬНЫХ ПОЛЕЙ ---");
        
        // Закрываем календарь если он открыт
        try {
            driver.findElement(By.tagName("body")).click();
            Thread.sleep(1000);
        } catch (Exception e) {
            // Игнорируем ошибки
        }
        
        // Выбираем срок аренды
        WebElement rentalDropdown = driver.findElement(By.xpath("//div[contains(@class, 'Dropdown-control')]"));
        rentalDropdown.click();
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        List<WebElement> rentalOptions = driver.findElements(By.xpath("//div[contains(@class, 'Dropdown-option')]"));
        System.out.println("  Опций аренды: " + rentalOptions.size());
        
        for (int i = 0; i < rentalOptions.size(); i++) {
            try {
                // Перепоиск элемента для каждого индекса
                List<WebElement> freshOptions = driver.findElements(By.xpath("//div[contains(@class, 'Dropdown-option')]"));
                if (i < freshOptions.size()) {
                    WebElement option = freshOptions.get(i);
                    String optionText = option.getText();
                    System.out.println("    Опция " + i + ": " + optionText);
                }
            } catch (Exception e) {
                System.out.println("    Ошибка при чтении опции " + i + ": " + e.getMessage());
            }
        }
        
        if (rentalOptions.size() > 0) {
            try {
                // Перепоиск и выбор первой опции
                List<WebElement> freshOptions = driver.findElements(By.xpath("//div[contains(@class, 'Dropdown-option')]"));
                if (freshOptions.size() > 0) {
                    WebElement option = freshOptions.get(0);
                    String optionText = option.getText();
                    // Используем JavaScript для клика
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
                    System.out.println("  Выбрана опция: " + optionText);
                }
            } catch (Exception e) {
                System.out.println("  Ошибка при выборе опции аренды: " + e.getMessage());
            }
        }
        
        // Выбираем цвет
        List<WebElement> colorCheckboxes = driver.findElements(By.xpath("//input[@type='checkbox']"));
        if (colorCheckboxes.size() > 0) {
            try {
                // Перепоиск и выбор первого чекбокса
                List<WebElement> freshCheckboxes = driver.findElements(By.xpath("//input[@type='checkbox']"));
                if (freshCheckboxes.size() > 0) {
                    WebElement checkbox = freshCheckboxes.get(0);
                    String id = checkbox.getAttribute("id");
                    // Используем JavaScript для клика
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
                    System.out.println("  Выбран цвет с id: " + id);
                }
            } catch (Exception e) {
                System.out.println("  Ошибка при выборе цвета: " + e.getMessage());
            }
        }
        
        // Вводим комментарий
        try {
            WebElement commentInput = driver.findElement(By.xpath("//input[@placeholder='Комментарий для курьера']"));
            commentInput.sendKeys("Тестовый комментарий");
            System.out.println("  Введен комментарий");
        } catch (Exception e) {
            System.out.println("  Ошибка при вводе комментария: " + e.getMessage());
        }
    }

    private void analyzeOrderButton() {
        System.out.println("\n--- АНАЛИЗ КНОПКИ ЗАКАЗА ---");
        
        List<WebElement> orderButtons = driver.findElements(By.xpath("//button[contains(text(), 'Заказать')]"));
        System.out.println("  Кнопок заказа найдено: " + orderButtons.size());
        
        for (int i = 0; i < orderButtons.size(); i++) {
            WebElement button = orderButtons.get(i);
            System.out.println("    Кнопка " + i + ": " + button.getText() + " | Класс: " + button.getAttribute("class"));
        }
        
        if (orderButtons.size() > 0) {
            System.out.println("  Кликаем по кнопке заказа...");
            orderButtons.get(0).click();
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Анализируем модальное окно подтверждения
            List<WebElement> confirmButtons = driver.findElements(By.xpath("//button[contains(text(), 'Да')]"));
            System.out.println("  Кнопок подтверждения 'Да': " + confirmButtons.size());
            
            if (confirmButtons.size() > 0) {
                System.out.println("  Кликаем по кнопке подтверждения...");
                confirmButtons.get(0).click();
                
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Анализируем окно успешного заказа
                List<WebElement> successModals = driver.findElements(By.xpath("//div[contains(text(), 'Заказ оформлен')]"));
                System.out.println("  Окон успешного заказа: " + successModals.size());
                
                for (WebElement modal : successModals) {
                    System.out.println("    Модальное окно: " + modal.getText());
                }
            }
        }
    }
} 