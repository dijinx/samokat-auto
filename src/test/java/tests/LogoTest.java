package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pageobject.MainPage;
import static org.junit.jupiter.api.Assertions.*;

public class LogoTest {
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
    void testSamokatLogoClick() {
        MainPage mainPage = new MainPage(driver);
        String initialUrl = driver.getCurrentUrl();
        
        // Кликаем на логотип Самоката
        mainPage.clickSamokatLogo();
        
        // Проверяем, что остались на той же странице
        assertEquals(initialUrl, driver.getCurrentUrl());
    }

    @Test
    void testYandexLogoClick() {
        MainPage mainPage = new MainPage(driver);
        String initialUrl = driver.getCurrentUrl();
        
        // Кликаем на логотип Яндекса
        mainPage.clickYandexLogo();
        
        // Ждем появления новой вкладки
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Проверяем, что открылась новая вкладка
        assertEquals(2, driver.getWindowHandles().size());
        
        // Переключаемся на новую вкладку
        String newWindowHandle = driver.getWindowHandles().stream()
                .filter(handle -> !handle.equals(driver.getWindowHandle()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(newWindowHandle);
        driver.switchTo().window(newWindowHandle);
        
        // Проверяем, что перешли на страницу Яндекса
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL in new tab: " + currentUrl);
        assertTrue(currentUrl.contains("yandex") || currentUrl.contains("dzen") || currentUrl.contains("ya.ru"));
        
        // Закрываем новую вкладку и возвращаемся к исходной
        driver.close();
        driver.switchTo().window(driver.getWindowHandles().iterator().next());
    }
} 