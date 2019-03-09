package teammates.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for login page in development server.
 */
public class DevServerLoginPage extends LoginPage {

    @FindBy(id = "email")
    private WebElement emailTextBox;

    @FindBy(id = "isAdmin")
    private WebElement isAdminCheckBox;

    @FindBy(xpath = "/html/body/form/div/p[3]/input[1]")
    private WebElement loginButton;

    public DevServerLoginPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        waitForElementVisibility(By.tagName("h3"));
        return getPageSource().contains("<h3>Not logged in</h3>");
    }

    @Override
    public void loginAdminAsInstructor(
            String adminUsername, String adminPassword, String instructorUsername) {
        fillTextBox(emailTextBox, instructorUsername);
        click(isAdminCheckBox);
        click(loginButton);
        waitForElementVisibility(By.tagName("h1"));
        browser.isAdminLoggedIn = true;
    }

    @Override
    public StudentHomePage loginAsStudent(String username, String password) {
        return loginAsStudent(username, password, StudentHomePage.class);
    }

    @Override
    public <T extends AppPage> T loginAsStudent(String username, String password, Class<T> typeOfPage) {
        fillTextBox(emailTextBox, username);
        click(loginButton);
        waitForElementVisibility(By.tagName("h1"));
        browser.isAdminLoggedIn = false;
        return changePageType(typeOfPage);
    }
}
