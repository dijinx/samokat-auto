package pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class MainPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Закрытие cookie banner
    private void closeCookieBanner() {
        try {
            WebElement cookieButton = driver.findElement(By.xpath("//button[contains(text(), 'да все привыкли')]"));
            if (cookieButton.isDisplayed()) {
                cookieButton.click();
                Thread.sleep(500);
            }
        } catch (Exception e) {
            // Cookie banner не найден или уже закрыт
        }
    }

    // Клик по кнопке заказа (верхней или нижней)
    public boolean clickOrderButton(int index) {
        try {
            System.out.println("Пытаемся найти и нажать кнопку 'Заказать' с индексом " + index + "...");
            closeCookieBanner();
            
            List<WebElement> orderButtons = driver.findElements(By.xpath("//button[contains(text(), 'Заказать')]"));
            
            if (orderButtons.isEmpty()) {
                System.out.println("ОШИБКА: Кнопки 'Заказать' не найдены!");
                return false;
            }
            
            if (index >= orderButtons.size()) {
                System.out.println("ОШИБКА: Индекс " + index + " превышает количество кнопок (" + orderButtons.size() + ")!");
                return false;
            }
            
            System.out.println("Найдено кнопок 'Заказать': " + orderButtons.size());
            WebElement orderButton = orderButtons.get(index);
            
            if (!orderButton.isDisplayed()) {
                System.out.println("ОШИБКА: Кнопка 'Заказать' с индексом " + index + " не видна на странице!");
                return false;
            }
            
            if (!orderButton.isEnabled()) {
                System.out.println("ОШИБКА: Кнопка 'Заказать' с индексом " + index + " неактивна!");
                return false;
            }
            
            orderButton.click();
            System.out.println("Кнопка 'Заказать' с индексом " + index + " успешно нажата");
            return true;
            
        } catch (Exception e) {
            System.out.println("ОШИБКА при нажатии кнопки 'Заказать' с индексом " + index + ": " + e.getMessage());
            return false;
        }
    }

    // FAQ
    public void clickFaqQuestion(int index) {
        closeCookieBanner();
        WebElement question = driver.findElements(By.xpath("//div[@role='button' and contains(@class, 'accordion__button')]")).get(index);
        wait.until(ExpectedConditions.elementToBeClickable(question)).click();
    }
    
    public boolean isFaqAnswerVisible(int index) {
        WebElement answer = driver.findElements(By.xpath("//div[contains(@class, 'accordion__panel')]")).get(index);
        return answer.isDisplayed() && !answer.getAttribute("class").contains("panelHidden");
    }
    
    public String getFaqAnswerText(int index) {
        return driver.findElements(By.xpath("//div[contains(@class, 'accordion__panel')]")).get(index).getText();
    }

    // Логотипы
    public void clickSamokatLogo() {
        closeCookieBanner();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@class,'LogoScooter')]"))).click();
    }
    
    public void clickYandexLogo() {
        closeCookieBanner();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@class,'LogoYandex')]"))).click();
    }

    // Статус заказа
    public boolean clickOrderStatusButton() {
        try {
            System.out.println("Нажимаем кнопку 'Статус заказа'...");
            closeCookieBanner();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Статус заказа']"))).click();
            System.out.println("Кнопка 'Статус заказа' успешно нажата");
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка при нажатии кнопки 'Статус заказа': " + e.getMessage());
            return false;
        }
    }
    
    public boolean enterOrderNumber(String orderNumber) {
        try {
            System.out.println("Вводим номер заказа: " + orderNumber);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Введите номер заказа']"))).sendKeys(orderNumber);
            System.out.println("Номер заказа успешно введен");
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка при вводе номера заказа: " + e.getMessage());
            return false;
        }
    }
    
    public boolean clickGoButton() {
        try {
            System.out.println("Нажимаем кнопку 'Go!'...");
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Go!']"))).click();
            System.out.println("Кнопка 'Go!' успешно нажата");
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка при нажатии кнопки 'Go!': " + e.getMessage());
            return false;
        }
    }
    
    public boolean isOrderNotFoundMessageVisible() {
        try {
            System.out.println("Проверяем наличие сообщения о том, что заказ не найден...");
            
            // Ждем появления результата
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Пробуем разные варианты поиска сообщения
            List<WebElement> messages = driver.findElements(
                By.xpath("//div[contains(text(),'Заказ не найден') or contains(text(),'заказ не найден') or contains(text(),'не найден') or contains(text(),'Не найден')]")
            );
            
            if (messages.isEmpty()) {
                // Поиск по классу
                messages = driver.findElements(By.xpath("//div[contains(@class, 'Track_NotFound')]"));
                System.out.println("Поиск по классу Track_NotFound: найдено элементов: " + messages.size());
            } else {
                System.out.println("Поиск по тексту: найдено элементов: " + messages.size());
            }
            
            if (messages.isEmpty()) {
                // Поиск всех div элементов на странице для отладки
                List<WebElement> allDivs = driver.findElements(By.tagName("div"));
                System.out.println("Всего div элементов на странице: " + allDivs.size());
                
                for (int i = 0; i < Math.min(10, allDivs.size()); i++) {
                    try {
                        WebElement div = allDivs.get(i);
                        if (div.isDisplayed()) {
                            String text = div.getText();
                            String className = div.getAttribute("class");
                            if (!text.trim().isEmpty()) {
                                System.out.println("Div " + i + ": '" + text.substring(0, Math.min(50, text.length())) + "' | class: " + className);
                            }
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибки при чтении элементов
                    }
                }
                
                System.out.println("Сообщение о том, что заказ не найден, не обнаружено");
                return false;
            }
            
            // Проверяем видимость найденных элементов
            for (WebElement message : messages) {
                if (message.isDisplayed()) {
                    String text = message.getText();
                    System.out.println("Найдено видимое сообщение: '" + text + "'");
                    return true;
                }
            }
            
            System.out.println("Сообщения найдены, но не видимы");
            return false;
            
        } catch (Exception e) {
            System.out.println("Ошибка при поиске сообщения: " + e.getMessage());
            return false;
        }
    }
    
    public String getOrderStatusMessage() {
        try {
            System.out.println("Получаем текст сообщения о статусе заказа...");
            
            // Пробуем разные варианты поиска сообщения
            List<WebElement> messages = driver.findElements(
                By.xpath("//div[contains(text(),'Заказ не найден') or contains(text(),'заказ не найден') or contains(text(),'не найден') or contains(text(),'Не найден')]")
            );
            
            if (messages.isEmpty()) {
                messages = driver.findElements(By.xpath("//div[contains(@class, 'Track_NotFound')]"));
            }
            
            for (WebElement message : messages) {
                if (message.isDisplayed()) {
                    String text = message.getText();
                    System.out.println("Текст сообщения: '" + text + "'");
                    return text;
                }
            }
            
            return "Сообщение не найдено";
            
        } catch (Exception e) {
            System.out.println("Ошибка при получении сообщения: " + e.getMessage());
            return "Ошибка: " + e.getMessage();
        }
    }
} 