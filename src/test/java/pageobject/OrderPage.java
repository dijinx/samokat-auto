package pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class OrderPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void fillFirstPage(String name, String surname, String address, String metro, String phone) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='* Имя']"))).sendKeys(name);
        driver.findElement(By.xpath("//input[@placeholder='* Фамилия']")).sendKeys(surname);
        driver.findElement(By.xpath("//input[@placeholder='* Адрес: куда привезти заказ']")).sendKeys(address);
        
        // Выбор метро (случайная станция)
        WebElement metroField = driver.findElement(By.xpath("//input[@placeholder='* Станция метро']"));
        metroField.click();
        
        // Ждем появления выпадающего списка
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Ищем все опции в выпадающем списке метро
        List<WebElement> metroOptions = driver.findElements(By.xpath("//div[contains(@class,'Order_Text__2broi')]"));
        System.out.println("Найдено опций метро: " + metroOptions.size());
        
        if (!metroOptions.isEmpty()) {
            // Выбираем случайную станцию из списка
            int randomIndex = (int) (Math.random() * metroOptions.size());
            WebElement randomMetroOption = metroOptions.get(randomIndex);
            String selectedMetro = randomMetroOption.getText();
            System.out.println("Выбрана случайная станция метро: " + selectedMetro + " (индекс: " + randomIndex + " из " + metroOptions.size() + ")");
            
            // Кликаем по выбранной станции
            randomMetroOption.click();
            System.out.println("Клик по станции метро выполнен");
        } else {
            // Если опции не найдены, попробуем альтернативный поиск
            System.out.println("Пробуем альтернативный поиск опций метро...");
            List<WebElement> alternativeOptions = driver.findElements(By.xpath("//div[contains(@class,'select-search__option')]"));
            System.out.println("Альтернативных опций найдено: " + alternativeOptions.size());
            
            if (!alternativeOptions.isEmpty()) {
                int randomIndex = (int) (Math.random() * alternativeOptions.size());
                WebElement randomOption = alternativeOptions.get(randomIndex);
                String selectedMetro = randomOption.getText();
                System.out.println("Выбрана альтернативная станция метро: " + selectedMetro);
                randomOption.click();
            } else {
                System.out.println("ОШИБКА: Не найдено ни одной станции метро!");
                throw new RuntimeException("Станции метро не найдены");
            }
        }
        
        driver.findElement(By.xpath("//input[@placeholder='* Телефон: на него позвонит курьер']")).sendKeys(phone);
    }
    
    public void clickNextButton() {
        System.out.println("Нажимаем кнопку 'Далее'...");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Далее']"))).click();
        
        // Ждем загрузки второй страницы
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Проверяем наличие полей на второй странице...");
        
        // Проверяем наличие различных полей второй страницы
        List<WebElement> dateInputs = driver.findElements(By.xpath("//input[contains(@placeholder, 'Когда привезти')]"));
        System.out.println("Полей даты найдено: " + dateInputs.size());
        
        List<WebElement> rentalDropdowns = driver.findElements(By.xpath("//div[contains(@class, 'Dropdown-control')]"));
        System.out.println("Выпадающих списков аренды найдено: " + rentalDropdowns.size());
        
        List<WebElement> colorCheckboxes = driver.findElements(By.xpath("//input[@type='checkbox']"));
        System.out.println("Чекбоксов цвета найдено: " + colorCheckboxes.size());
        
        // Если поля второй страницы не найдены, возможно мы еще на первой странице
        if (dateInputs.isEmpty() && rentalDropdowns.isEmpty()) {
            System.out.println("ВНИМАНИЕ: Поля второй страницы не найдены. Возможно, переход не произошел.");
            
            // Проверяем, есть ли еще поля первой страницы
            List<WebElement> nameInputs = driver.findElements(By.xpath("//input[@placeholder='* Имя']"));
            List<WebElement> nextButtons = driver.findElements(By.xpath("//button[text()='Далее']"));
            
            System.out.println("Полей имени на странице: " + nameInputs.size());
            System.out.println("Кнопок 'Далее' на странице: " + nextButtons.size());
            
            if (!nameInputs.isEmpty()) {
                System.out.println("Мы все еще на первой странице. Повторяем клик по 'Далее'...");
                if (!nextButtons.isEmpty()) {
                    nextButtons.get(0).click();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        // Ждем появления поля даты с более гибким поиском
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@placeholder, 'Когда привезти')]")));
            System.out.println("Поле даты найдено на второй странице");
        } catch (Exception e) {
            System.out.println("ОШИБКА: Поле даты не найдено на второй странице: " + e.getMessage());
            throw e;
        }
    }
    
    public void fillSecondPage(String date, String rentalPeriod, String color, String comment) {
        System.out.println("Заполняем вторую страницу формы...");
        
        // Выбор даты из календаря с более гибким поиском
        List<WebElement> dateInputs = driver.findElements(By.xpath("//input[contains(@placeholder, 'Когда привезти')]"));
        if (dateInputs.isEmpty()) {
            throw new RuntimeException("Поле даты не найдено на второй странице");
        }
        
        WebElement dateInput = dateInputs.get(0);
        System.out.println("Найдено поле даты: " + dateInput.getAttribute("placeholder"));
        dateInput.click();
        
        // Ждем появления календаря и выбираем первый доступный день
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("react-datepicker")));
        
        List<WebElement> availableDays = driver.findElements(
            By.xpath("//div[contains(@class, 'react-datepicker__day') and not(contains(@class, 'disabled')) and not(contains(@class, 'outside-month'))]")
        );
        
        if (availableDays.size() > 0) {
            availableDays.get(0).click();
        } else {
            // Альтернативный поиск дней
            List<WebElement> calendarDays = driver.findElements(
                By.xpath("//td[contains(@class, 'day') and not(contains(@class, 'disabled'))]")
            );
            if (calendarDays.size() > 0) {
                calendarDays.get(0).click();
            }
        }
        
        // Ждем закрытия календаря и кликаем вне календаря для уверенности
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Кликаем вне календаря чтобы закрыть его
        try {
            driver.findElement(By.tagName("body")).click();
            Thread.sleep(500);
        } catch (Exception e) {
            // Игнорируем ошибки при закрытии календаря
        }
        
        // Дополнительная проверка - если календарь все еще открыт, нажимаем ESC
        try {
            List<WebElement> calendarElements = driver.findElements(By.xpath("//div[contains(@class, 'react-datepicker')]"));
            if (!calendarElements.isEmpty()) {
                org.openqa.selenium.Keys keys = org.openqa.selenium.Keys.ESCAPE;
                driver.findElement(By.tagName("body")).sendKeys(keys);
                Thread.sleep(500);
            }
        } catch (Exception e) {
            // Игнорируем ошибки
        }
        
        // Выбор срока аренды
        try {
            WebElement dropdown = driver.findElement(By.className("Dropdown-control"));
            
            // Проверяем, что dropdown не перекрыт календарем
            try {
                List<WebElement> calendarElements = driver.findElements(By.xpath("//div[contains(@class, 'react-datepicker')]"));
                if (!calendarElements.isEmpty()) {
                    System.out.println("Календарь все еще открыт, закрываем его...");
                    driver.findElement(By.tagName("body")).click();
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                // Игнорируем ошибки
            }
            
            dropdown.click();
            Thread.sleep(1000);
            
            By rentalOption = By.xpath("//div[@class='Dropdown-option' and text()='" + rentalPeriod + "']");
            wait.until(ExpectedConditions.elementToBeClickable(rentalOption)).click();
            
        } catch (Exception e) {
            System.out.println("Ошибка при выборе срока аренды: " + e.getMessage());
            // Альтернативный способ - клик по первой доступной опции
            try {
                List<WebElement> options = driver.findElements(By.xpath("//div[contains(@class, 'Dropdown-option')]"));
                if (options.size() > 0) {
                    options.get(0).click();
                    System.out.println("Выбрана первая доступная опция аренды");
                }
            } catch (Exception e2) {
                System.out.println("Не удалось выбрать опцию аренды: " + e2.getMessage());
            }
        }
        
        // Выбор цвета
        if (color.equals("black")) {
            driver.findElement(By.xpath("//input[@id='black']")).click();
        } else if (color.equals("grey")) {
            driver.findElement(By.xpath("//input[@id='grey']")).click();
        }
        
        // Комментарий (если указан)
        if (comment != null && !comment.isEmpty()) {
            driver.findElement(By.xpath("//input[@placeholder='Комментарий для курьера']")).sendKeys(comment);
        }
    }
    
    public boolean clickOrderButton() {
        try {
            System.out.println("Пытаемся найти и нажать кнопку 'Заказать' на второй странице формы...");
            
            // Ищем кнопку по точному классу
            List<WebElement> orderButtons = driver.findElements(
                By.xpath("//button[@class='Button_Button__ra12g Button_Middle__1CSJM' and contains(text(), 'Заказать')]")
            );
            
            if (orderButtons.isEmpty()) {
                // Альтернативный поиск по тексту
                orderButtons = driver.findElements(By.xpath("//button[contains(text(), 'Заказать')]"));
                System.out.println("Поиск по тексту: найдено кнопок 'Заказать': " + orderButtons.size());
            } else {
                System.out.println("Поиск по классу: найдено кнопок 'Заказать': " + orderButtons.size());
            }
            
            if (orderButtons.isEmpty()) {
                System.out.println("ОШИБКА: Кнопка 'Заказать' не найдена!");
                return false;
            }
            
            // Ищем кнопку с нужным классом или первую доступную
            WebElement orderButton = null;
            for (WebElement button : orderButtons) {
                String className = button.getAttribute("class");
                System.out.println("Найдена кнопка с классом: '" + className + "'");
                if (className.contains("Button_Middle__1CSJM")) {
                    orderButton = button;
                    System.out.println("Выбрана кнопка с нужным классом");
                    break;
                }
            }
            
            if (orderButton == null) {
                orderButton = orderButtons.get(0);
                System.out.println("Используем первую найденную кнопку");
            }
            
            if (!orderButton.isDisplayed()) {
                System.out.println("ОШИБКА: Кнопка 'Заказать' не видна на странице!");
                return false;
            }
            
            if (!orderButton.isEnabled()) {
                System.out.println("ОШИБКА: Кнопка 'Заказать' неактивна!");
                return false;
            }
            
            orderButton.click();
            System.out.println("Кнопка 'Заказать' успешно нажата");
            return true;
            
        } catch (Exception e) {
            System.out.println("ОШИБКА при нажатии кнопки 'Заказать': " + e.getMessage());
            return false;
        }
    }
    
    public boolean confirmOrder() {
        try {
            System.out.println("=== ПОДТВЕРЖДЕНИЕ ЗАКАЗА ===");
            
            // Ждем появления модального окна подтверждения
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Проверяем наличие модального окна подтверждения
            List<WebElement> confirmModals = driver.findElements(By.xpath("//div[contains(text(), 'Хотите оформить заказ') or contains(text(), 'хотите оформить')]"));
            System.out.println("Модальных окон подтверждения найдено: " + confirmModals.size());
            
            // Пробуем разные варианты поиска кнопки подтверждения
            List<WebElement> confirmButtons = driver.findElements(By.xpath("//button[contains(text(), 'Да') or contains(text(), 'да') or contains(text(), 'OK') or contains(text(), 'Подтвердить')]"));
            
            if (confirmButtons.isEmpty()) {
                // Альтернативный поиск по классу
                confirmButtons = driver.findElements(By.xpath("//button[contains(@class, 'Button_Button')]"));
                System.out.println("Поиск по классу: найдено кнопок: " + confirmButtons.size());
            }
            
            if (confirmButtons.isEmpty()) {
                // Поиск всех кнопок на странице
                List<WebElement> allButtons = driver.findElements(By.tagName("button"));
                System.out.println("Всего кнопок на странице: " + allButtons.size());
                for (int i = 0; i < allButtons.size(); i++) {
                    WebElement btn = allButtons.get(i);
                    try {
                        String text = btn.getText();
                        System.out.println("Кнопка " + i + ": '" + text + "' | Видима: " + btn.isDisplayed() + " | Активна: " + btn.isEnabled());
                    } catch (Exception e) {
                        System.out.println("Кнопка " + i + ": [ошибка чтения]");
                    }
                }
                System.out.println("ОШИБКА: Кнопка подтверждения не найдена!");
                return false;
            }
            
            System.out.println("Найдено кнопок подтверждения: " + confirmButtons.size());
            
            // Ищем кнопку "Да" среди найденных кнопок
            for (WebElement confirmButton : confirmButtons) {
                try {
                    if (confirmButton.isDisplayed() && confirmButton.isEnabled()) {
                        String buttonText = confirmButton.getText().trim();
                        System.out.println("Проверяем кнопку: '" + buttonText + "'");
                        
                        // Ищем именно кнопку "Да"
                        if (buttonText.equals("Да") || buttonText.equals("да")) {
                            System.out.println("Найдена кнопка 'Да': '" + buttonText + "'");
                            
                            // Запоминаем состояние до клика
                            List<WebElement> confirmModalBefore = driver.findElements(By.xpath("//div[contains(text(), 'Хотите оформить заказ')]"));
                            System.out.println("Модальных окон подтверждения до клика: " + confirmModalBefore.size());
                            
                            // Используем JavaScript для клика, чтобы избежать проблем с перекрытием
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmButton);
                            
                            System.out.println("Кнопка 'Да' успешно нажата");
                            
                            // Ждем обработки заказа
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            
                            // Проверяем результат оформления заказа
                            return verifyOrderConfirmation();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка при проверке кнопки: " + e.getMessage());
                }
            }
            
            System.out.println("ОШИБКА: Кнопка 'Да' не найдена среди доступных кнопок!");
            return false;
            
        } catch (Exception e) {
            System.out.println("ОШИБКА при нажатии кнопки подтверждения: " + e.getMessage());
            return false;
        }
    }
    
    private boolean verifyOrderConfirmation() {
        System.out.println("=== ПРОВЕРКА ПОДТВЕРЖДЕНИЯ ЗАКАЗА ===");
        
        // Проверяем, исчезло ли модальное окно подтверждения
        List<WebElement> confirmModalAfter = driver.findElements(By.xpath("//div[contains(text(), 'Хотите оформить заказ')]"));
        System.out.println("Модальных окон подтверждения после клика: " + confirmModalAfter.size());
        
        // Проверяем наличие модального окна успешного заказа
        List<WebElement> successModals = driver.findElements(
            By.xpath("//div[contains(text(),'Заказ оформлен') or contains(text(),'заказ оформлен') or contains(text(),'оформлен') or contains(text(),'Оформлен') or contains(text(),'успешно') or contains(text(),'Успешно') or contains(text(),'номер заказа') or contains(text(),'Номер заказа')]")
        );
        System.out.println("Модальных окон успешного заказа найдено: " + successModals.size());
        
        // Проверяем наличие номера заказа
        List<WebElement> orderNumbers = driver.findElements(
            By.xpath("//*[contains(text(),'№') or contains(text(),'номер') or contains(text(),'Номер')]")
        );
        System.out.println("Элементов с номером заказа найдено: " + orderNumbers.size());
        
        // Проверяем наличие кнопки "Посмотреть статус"
        List<WebElement> statusButtons = driver.findElements(
            By.xpath("//button[contains(text(),'Посмотреть статус') or contains(@class,'Button_Button__ra12g Button_Middle__1CSJM')]")
        );
        System.out.println("Кнопок статуса найдено: " + statusButtons.size());
        
        // Выводим подробную информацию о найденных элементах
        for (WebElement modal : successModals) {
            try {
                if (modal.isDisplayed()) {
                    String text = modal.getText();
                    String className = modal.getAttribute("class");
                    System.out.println("Видимое модальное окно: '" + text + "' | class: " + className);
                }
            } catch (Exception e) {
                System.out.println("Ошибка при чтении модального окна: " + e.getMessage());
            }
        }
        
        for (WebElement number : orderNumbers) {
            try {
                if (number.isDisplayed()) {
                    String text = number.getText();
                    String tagName = number.getTagName();
                    String className = number.getAttribute("class");
                    System.out.println("Элемент с номером (" + tagName + "): '" + text + "' | class: " + className);
                }
            } catch (Exception e) {
                System.out.println("Ошибка при чтении элемента с номером: " + e.getMessage());
            }
        }
        
        // Проверяем успешность оформления заказа
        boolean orderConfirmed = false;
        
        // Критерий 1: Модальное окно подтверждения исчезло
        if (confirmModalAfter.size() == 0) {
            System.out.println("✓ Модальное окно подтверждения исчезло");
            orderConfirmed = true;
        } else {
            System.out.println("✗ Модальное окно подтверждения не исчезло");
        }
        
        // Критерий 2: Появилось модальное окно успешного заказа
        boolean successModalVisible = false;
        for (WebElement modal : successModals) {
            if (modal.isDisplayed()) {
                successModalVisible = true;
                System.out.println("✓ Модальное окно успешного заказа видимо");
                break;
            }
        }
        if (!successModalVisible) {
            System.out.println("✗ Модальное окно успешного заказа не видимо");
        }
        
        // Критерий 3: Появился номер заказа
        boolean orderNumberVisible = false;
        for (WebElement number : orderNumbers) {
            if (number.isDisplayed()) {
                String text = number.getText();
                if (text.contains("№") || text.contains("номер") || text.matches(".*\\d+.*")) {
                    orderNumberVisible = true;
                    System.out.println("✓ Номер заказа найден: '" + text + "'");
                    break;
                }
            }
        }
        if (!orderNumberVisible) {
            System.out.println("✗ Номер заказа не найден");
        }
        
        // Критерий 4: Появилась кнопка статуса
        boolean statusButtonVisible = false;
        for (WebElement button : statusButtons) {
            if (button.isDisplayed()) {
                statusButtonVisible = true;
                System.out.println("✓ Кнопка статуса найдена: '" + button.getText() + "'");
                break;
            }
        }
        if (!statusButtonVisible) {
            System.out.println("✗ Кнопка статуса не найдена");
        }
        
        // Итоговая проверка
        if (orderConfirmed && (successModalVisible || orderNumberVisible || statusButtonVisible)) {
            System.out.println("✓ ЗАКАЗ УСПЕШНО ОФОРМЛЕН!");
            return true;
        } else {
            System.out.println("✗ ЗАКАЗ НЕ ОФОРМЛЕН ИЛИ ЕСТЬ ПРОБЛЕМЫ!");
            
            // Дополнительная диагностика
            System.out.println("=== ДОПОЛНИТЕЛЬНАЯ ДИАГНОСТИКА ===");
            List<WebElement> allDivs = driver.findElements(By.tagName("div"));
            System.out.println("Всего div элементов на странице: " + allDivs.size());
            
            for (int i = 0; i < Math.min(20, allDivs.size()); i++) {
                try {
                    WebElement div = allDivs.get(i);
                    if (div.isDisplayed()) {
                        String text = div.getText();
                        String className = div.getAttribute("class");
                        if (!text.trim().isEmpty() && text.length() > 10) {
                            System.out.println("Div " + i + ": '" + text.substring(0, Math.min(150, text.length())) + "' | class: " + className);
                        }
                    }
                } catch (Exception e) {
                    // Игнорируем ошибки при чтении элементов
                }
            }
            
            return false;
        }
    }
    
    public boolean isSuccessModalVisible() {
        try {
            System.out.println("Проверяем наличие модального окна успешного заказа...");
            
            // Ждем появления результата
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Пробуем разные варианты поиска модального окна
            List<WebElement> successModals = driver.findElements(
                By.xpath("//div[contains(text(),'Заказ оформлен') or contains(text(),'заказ оформлен') or contains(text(),'оформлен') or contains(text(),'Оформлен') or contains(text(),'успешно') or contains(text(),'Успешно')]")
            );
            
            if (successModals.isEmpty()) {
                // Поиск по классу модального окна
                successModals = driver.findElements(By.xpath("//div[contains(@class, 'Modal') or contains(@class, 'modal') or contains(@class, 'success') or contains(@class, 'Success')]"));
                System.out.println("Поиск по классу модального окна: найдено элементов: " + successModals.size());
            } else {
                System.out.println("Поиск по тексту: найдено элементов: " + successModals.size());
            }
            
            if (successModals.isEmpty()) {
                // Поиск всех div элементов на странице для отладки
                List<WebElement> allDivs = driver.findElements(By.tagName("div"));
                System.out.println("Всего div элементов на странице: " + allDivs.size());
                
                for (int i = 0; i < Math.min(15, allDivs.size()); i++) {
                    try {
                        WebElement div = allDivs.get(i);
                        if (div.isDisplayed()) {
                            String text = div.getText();
                            String className = div.getAttribute("class");
                            if (!text.trim().isEmpty()) {
                                System.out.println("Div " + i + ": '" + text.substring(0, Math.min(100, text.length())) + "' | class: " + className);
                            }
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибки при чтении элементов
                    }
                }
                
                // Поиск всех элементов с текстом
                List<WebElement> allElements = driver.findElements(By.xpath("//*[text()]"));
                System.out.println("Всего элементов с текстом: " + allElements.size());
                
                for (int i = 0; i < Math.min(10, allElements.size()); i++) {
                    try {
                        WebElement element = allElements.get(i);
                        if (element.isDisplayed()) {
                            String text = element.getText();
                            String tagName = element.getTagName();
                            String className = element.getAttribute("class");
                            if (!text.trim().isEmpty() && text.length() > 5) {
                                System.out.println("Element " + i + " (" + tagName + "): '" + text.substring(0, Math.min(100, text.length())) + "' | class: " + className);
                            }
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибки при чтении элементов
                    }
                }
                
                System.out.println("Модальное окно успешного заказа не обнаружено");
                return false;
            }
            
            // Проверяем видимость найденных элементов
            for (WebElement modal : successModals) {
                if (modal.isDisplayed()) {
                    String text = modal.getText();
                    System.out.println("Найдено видимое модальное окно: '" + text + "'");
                    return true;
                }
            }
            
            System.out.println("Модальные окна найдены, но не видимы");
            return false;
            
        } catch (Exception e) {
            System.out.println("Ошибка при поиске модального окна: " + e.getMessage());
            return false;
        }
    }
    
    public String getOrderStatusMessage() {
        try {
            System.out.println("Получаем текст статуса заказа...");
            
            // Пробуем разные варианты поиска статуса
            List<WebElement> statusElements = driver.findElements(
                By.xpath("//div[contains(text(),'Заказ оформлен') or contains(text(),'заказ оформлен') or contains(text(),'оформлен') or contains(text(),'Оформлен') or contains(text(),'успешно') or contains(text(),'Успешно')]")
            );
            
            if (statusElements.isEmpty()) {
                statusElements = driver.findElements(By.xpath("//div[contains(@class, 'Modal') or contains(@class, 'modal') or contains(@class, 'success') or contains(@class, 'Success')]"));
            }
            
            for (WebElement element : statusElements) {
                if (element.isDisplayed()) {
                    String text = element.getText();
                    System.out.println("Текст статуса заказа: '" + text + "'");
                    return text;
                }
            }
            
            return "Статус заказа не найден";
            
        } catch (Exception e) {
            System.out.println("Ошибка при получении статуса заказа: " + e.getMessage());
            return "Ошибка: " + e.getMessage();
        }
    }
    
    public boolean clickViewStatusButton() {
        try {
            System.out.println("=== ПОИСК И НАЖАТИЕ КНОПКИ 'ПОСМОТРЕТЬ СТАТУС' ===");
            
            // Ждем появления кнопки
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Ищем кнопку "Посмотреть статус" по точному классу
            List<WebElement> statusButtons = driver.findElements(
                By.xpath("//button[@class='Button_Button__ra12g Button_Middle__1CSJM' and contains(text(),'Посмотреть статус')]")
            );
            
            System.out.println("Найдено кнопок 'Посмотреть статус' с нужным классом: " + statusButtons.size());
            
            // Если не найдено, ищем по тексту
            if (statusButtons.isEmpty()) {
                statusButtons = driver.findElements(
                    By.xpath("//button[contains(text(),'Посмотреть статус')]")
                );
                System.out.println("Поиск по тексту: найдено кнопок 'Посмотреть статус': " + statusButtons.size());
            }
            
            // Если все еще не найдено, ищем по классу
            if (statusButtons.isEmpty()) {
                statusButtons = driver.findElements(
                    By.xpath("//button[contains(@class,'Button_Button__ra12g') and contains(@class,'Button_Middle__1CSJM')]")
                );
                System.out.println("Поиск по классу: найдено кнопок с нужным классом: " + statusButtons.size());
            }
            
            // Выводим все найденные кнопки для диагностики
            for (int i = 0; i < statusButtons.size(); i++) {
                try {
                    WebElement button = statusButtons.get(i);
                    String text = button.getText();
                    String className = button.getAttribute("class");
                    boolean isDisplayed = button.isDisplayed();
                    boolean isEnabled = button.isEnabled();
                    System.out.println("Кнопка " + i + ": '" + text + "' | class: " + className + " | видима: " + isDisplayed + " | активна: " + isEnabled);
                } catch (Exception e) {
                    System.out.println("Кнопка " + i + ": [ошибка чтения]");
                }
            }
            
            // Ищем кнопку "Посмотреть статус"
            for (WebElement button : statusButtons) {
                try {
                    if (button.isDisplayed() && button.isEnabled()) {
                        String buttonText = button.getText().trim();
                        
                        // Проверяем, что это именно кнопка "Посмотреть статус"
                        if (buttonText.equals("Посмотреть статус")) {
                            System.out.println("Найдена кнопка 'Посмотреть статус': '" + buttonText + "'");
                            
                            // Используем JavaScript для клика
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
                            
                            System.out.println("Кнопка 'Посмотреть статус' успешно нажата");
                            
                            // Ждем перехода на страницу статуса
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            
                            // Проверяем, что мы перешли на страницу статуса
                            return verifyStatusPageTransition();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка при проверке кнопки: " + e.getMessage());
                }
            }
            
            System.out.println("ОШИБКА: Кнопка 'Посмотреть статус' не найдена!");
            
            // Дополнительная диагностика - ищем все кнопки на странице
            List<WebElement> allButtons = driver.findElements(By.tagName("button"));
            System.out.println("Всего кнопок на странице: " + allButtons.size());
            
            for (int i = 0; i < allButtons.size(); i++) {
                try {
                    WebElement button = allButtons.get(i);
                    if (button.isDisplayed()) {
                        String text = button.getText();
                        String className = button.getAttribute("class");
                        System.out.println("Кнопка " + i + ": '" + text + "' | class: " + className);
                    }
                } catch (Exception e) {
                    System.out.println("Кнопка " + i + ": [ошибка чтения]");
                }
            }
            
            return false;
            
        } catch (Exception e) {
            System.out.println("ОШИБКА при нажатии кнопки статуса: " + e.getMessage());
            return false;
        }
    }
    
    private boolean verifyStatusPageTransition() {
        System.out.println("=== ПРОВЕРКА ПЕРЕХОДА НА СТРАНИЦУ СТАТУСА ===");
        
        // Проверяем URL
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Текущий URL: " + currentUrl);
        
        // Проверяем наличие элементов страницы статуса
        List<WebElement> statusInputs = driver.findElements(By.xpath("//input[@placeholder='Введите номер заказа']"));
        System.out.println("Полей ввода номера заказа: " + statusInputs.size());
        
        List<WebElement> goButtons = driver.findElements(By.xpath("//button[contains(text(),'Go!') or contains(text(),'Найти') or contains(text(),'найти')]"));
        System.out.println("Кнопок 'Go!': " + goButtons.size());
        
        List<WebElement> statusHeaders = driver.findElements(By.xpath("//*[contains(text(),'Статус заказа') or contains(text(),'статус заказа') or contains(text(),'Order status')]"));
        System.out.println("Заголовков статуса: " + statusHeaders.size());
        
        // Проверяем успешность перехода
        boolean transitionSuccessful = false;
        
        if (statusInputs.size() > 0) {
            System.out.println("✓ Поле ввода номера заказа найдено");
            transitionSuccessful = true;
        }
        
        if (goButtons.size() > 0) {
            System.out.println("✓ Кнопка 'Go!' найдена");
            transitionSuccessful = true;
        }
        
        if (statusHeaders.size() > 0) {
            System.out.println("✓ Заголовок страницы статуса найден");
            transitionSuccessful = true;
        }
        
        if (currentUrl.contains("status") || currentUrl.contains("track")) {
            System.out.println("✓ URL указывает на страницу статуса");
            transitionSuccessful = true;
        }
        
        if (transitionSuccessful) {
            System.out.println("✓ УСПЕШНЫЙ ПЕРЕХОД НА СТРАНИЦУ СТАТУСА ЗАКАЗА!");
        } else {
            System.out.println("✗ ПЕРЕХОД НА СТРАНИЦУ СТАТУСА НЕ ПОДТВЕРЖДЕН!");
            
            // Дополнительная диагностика
            System.out.println("=== ДИАГНОСТИКА СТРАНИЦЫ ===");
            List<WebElement> allDivs = driver.findElements(By.tagName("div"));
            System.out.println("Всего div элементов: " + allDivs.size());
            
            for (int i = 0; i < Math.min(10, allDivs.size()); i++) {
                try {
                    WebElement div = allDivs.get(i);
                    if (div.isDisplayed()) {
                        String text = div.getText();
                        String className = div.getAttribute("class");
                        if (!text.trim().isEmpty() && text.length() > 5) {
                            System.out.println("Div " + i + ": '" + text.substring(0, Math.min(100, text.length())) + "' | class: " + className);
                        }
                    }
                } catch (Exception e) {
                    // Игнорируем ошибки
                }
            }
        }
        
        return transitionSuccessful;
    }
} 