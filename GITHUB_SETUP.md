# Инструкции по созданию GitHub репозитория

## Шаги для создания репозитория на GitHub:

### 1. Создание репозитория на GitHub
1. Перейдите на https://github.com
2. Нажмите кнопку "New" или "+" в правом верхнем углу
3. Выберите "New repository"
4. Заполните форму:
   - **Repository name**: `samokat-selenium-tests`
   - **Description**: `Selenium test automation project for scooter rental website`
   - **Visibility**: Public (или Private по вашему выбору)
   - **Initialize this repository with**: НЕ ставьте галочки (у нас уже есть код)
5. Нажмите "Create repository"

### 2. Добавление удаленного репозитория
После создания репозитория выполните следующие команды:

```bash
# Добавить удаленный репозиторий (замените YOUR_USERNAME на ваше имя пользователя)
git remote add origin https://github.com/YOUR_USERNAME/samokat-selenium-tests.git

# Отправить код в репозиторий
git push -u origin master

# Отправить feature ветку
git push -u origin feature/enhanced-testing
```

### 3. Создание Pull Request
1. Перейдите на страницу вашего репозитория
2. GitHub предложит создать Pull Request для ветки `feature/enhanced-testing`
3. Нажмите "Compare & pull request"
4. Заполните форму:
   - **Title**: `Enhanced Selenium Test Automation with Comprehensive Features`
   - **Description**: 
   ```
   ## 🚀 Enhanced Selenium Test Automation Project
   
   This PR includes comprehensive test automation for the scooter rental website with the following features:
   
   ### ✅ New Features
   - Complete order flow testing with status verification
   - Multi-browser support (Chrome & Firefox)
   - Enhanced Page Object pattern implementation
   - Robust element interaction with JavaScript clicks
   - Comprehensive logging and error handling
   - Random metro station selection
   - Modal window handling and verification
   
   ### 🧪 Test Coverage
   - Order placement tests (top and bottom buttons)
   - FAQ section testing
   - Logo click testing
   - Order status verification
   - Form analysis and validation
   - Page element analysis
   
   ### 🔧 Technical Improvements
   - Removed unused imports
   - Enhanced error handling
   - Improved element selectors
   - Better test stability
   - Comprehensive documentation
   
   ### 📊 Test Results
   - All tests pass successfully
   - Stable execution in both Chrome and Firefox
   - Complete order flow with status verification working
   
   ## 🚀 How to Run
   ```bash
   # Run in Chrome (default)
   mvn test
   
   # Run in Firefox
   mvn test -Dbrowser=firefox
   
   # Run specific test
   mvn test -Dtest=OrderTest#testCompleteOrderFlowWithStatusCheck
   ```
   ```
5. Нажмите "Create pull request"

## Ссылка на Pull Request
После создания PR, ссылка будет выглядеть примерно так:
```
https://github.com/YOUR_USERNAME/samokat-selenium-tests/pull/1
```

## Альтернативный способ (если GitHub CLI установлен)
```bash
# Создать репозиторий
gh repo create samokat-selenium-tests --public --description "Selenium test automation project for scooter rental website"

# Добавить удаленный репозиторий
git remote add origin https://github.com/YOUR_USERNAME/samokat-selenium-tests.git

# Отправить код
git push -u origin master
git push -u origin feature/enhanced-testing

# Создать Pull Request
gh pr create --title "Enhanced Selenium Test Automation with Comprehensive Features" --body "See description above"
``` 