package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pageobject.MainPage;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FaqTest {
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
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
    void testFaqDropdownOpens(int questionIndex) {
        MainPage mainPage = new MainPage(driver);
        
        // Проверяем, что ответ изначально скрыт
        assertFalse(mainPage.isFaqAnswerVisible(questionIndex));
        
        // Кликаем на вопрос
        mainPage.clickFaqQuestion(questionIndex);
        
        // Ждем появления ответа
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(d -> mainPage.isFaqAnswerVisible(questionIndex));
        
        // Проверяем, что ответ видим и не пустой
        assertTrue(mainPage.isFaqAnswerVisible(questionIndex));
        assertFalse(mainPage.getFaqAnswerText(questionIndex).isEmpty());
    }
} 