package com.example.UntitledTestSuite;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.time.Duration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;

public class ORGTEST10412 {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  JavascriptExecutor js;
  @Before
  public void setUp() throws Exception {
    WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
    
    // --headless: Ejecuta el navegador sin interfaz gráfica (más rápido y estable en servidores)
    // options.addArguments("--headless=new"); // Usa esta opción si no necesitas ver la GUI
    // --no-sandbox: Necesario a veces en entornos restringidos/servidores.
    options.addArguments("--no-sandbox"); 

    // --disable-dev-shm-usage: Útil para evitar errores de memoria en entornos Linux (aunque estás en Windows, no hace daño).
    options.addArguments("--disable-dev-shm-usage"); 

    // --remote-allow-origins: Crucial para conexiones DevTools en versiones recientes de Chrome/Selenium
    options.addArguments("--remote-allow-origins=*");

    // --- NUEVO: Maximizar la ventana al inicio ---
    options.addArguments("start-maximized");

    // 3. Inicializar el driver con las opciones
    driver = new ChromeDriver(options);

    baseUrl = "https://nexocapacitacion.jujuy.edu.ar:4200/#/auth/login";
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
    js = (JavascriptExecutor) driver;
  }

  @Test
  public void testORGTEST10412() throws Exception {
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/auth/login");
    
    // Configuración de la espera explícita
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); 

    // --- MANEJO DEL BOTÓN INICIAL 'Aceptar' ---
    By aceptarButtonLocator = By.xpath("//span[normalize-space()='Aceptar']");
    
    try {
        // Espera hasta que el botón 'Aceptar' sea visible y clickeable
        wait.until(ExpectedConditions.elementToBeClickable(aceptarButtonLocator));
        driver.findElement(aceptarButtonLocator).click();
        // [NUEVO] PAUSA DE ESTABILIZACIÓN - 2 segundos      
        Thread.sleep(4000);
    } catch (TimeoutException e) {
        // Si el botón no aparece en 15 segundos, lo ignoramos y continuamos.
        System.out.println("El botón 'Aceptar' no apareció o ya fue clickeado.");
    }
    // --- FIN DEL MANEJO DE BOTÓN ---

    // 1. Ingreso de Usuario (mat-input-0)
    wait.until(ExpectedConditions.elementToBeClickable(By.id("mat-input-0")));
    driver.findElement(By.id("mat-input-0")).clear();
    driver.findElement(By.id("mat-input-0")).sendKeys("USUARIO_ORGANIZACION");
    driver.findElement(By.xpath("//div/div")).click();
    driver.findElement(By.id("mat-input-0")).click();
    driver.findElement(By.id("password-field")).clear();
    driver.findElement(By.id("password-field")).sendKeys("CLAVE_ORGANIZACION");
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Olvide mi Contraseña'])[1]/following::span[1]")).click();
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/pages/SIARH-ORGANIZACIONES");
    Thread.sleep(3000);
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='ORGANIZACIONES'])[1]/following::span[1]")).click();
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4303/");
    
    // Espera para el segundo diálogo/modal que aparece en la nueva URL
    By dialogButtonLocator = By.xpath("//mat-dialog-container[@id='mat-dialog-0']/app-dialog/div[2]/button/span");
    wait.until(ExpectedConditions.elementToBeClickable(dialogButtonLocator));
    driver.findElement(dialogButtonLocator).click();
    Thread.sleep(3000);
    By planesDeEstudioLocator = By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Planes de Estudio'])[1]/following::span[1]");
    
    // Hacemos scroll hasta el elemento para asegurarnos de que sea visible
    WebElement element = driver.findElement(planesDeEstudioLocator);
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

    // Esperamos explícitamente a que este elemento esté quieto y clickeable
    wait.until(ExpectedConditions.elementToBeClickable(planesDeEstudioLocator));
    
    // Hacemos el clic
    driver.findElement(planesDeEstudioLocator).click();

    // --- ÚLTIMO CLIC  ---
    By organizacionesLocator = By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='ORGANIZACIONES'])[3]/following::span[1]");
    wait.until(ExpectedConditions.elementToBeClickable(organizacionesLocator));
    driver.findElement(organizacionesLocator).click();
    Thread.sleep(10000);
  }
  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
