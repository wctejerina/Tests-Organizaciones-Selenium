package com.example.UntitledTestSuite;

import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

public class ORGPOFCargosFuncionales1012 {
  private WebDriver driver;
  private JavascriptExecutor js;

  @Before
  public void setUp() throws Exception {
    WebDriverManager.chromedriver().setup();
    
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--no-sandbox"); 
    options.addArguments("--disable-dev-shm-usage"); 
    options.addArguments("--remote-allow-origins=*");
    options.addArguments("start-maximized"); 

    driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    js = (JavascriptExecutor) driver;
  }

  @Test
  public void testORGPOFCargosFuncionales1012() throws Exception {
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/auth/login");
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

    // --- 1. LOGIN ---
    try {
        By aceptar = By.xpath("//span[normalize-space()='Aceptar']");
        wait.until(ExpectedConditions.elementToBeClickable(aceptar));
        driver.findElement(aceptar).click();
        Thread.sleep(1000); 
    } catch (Exception e) {}

    driver.findElement(By.id("mat-input-0")).sendKeys("USUARIO");
    driver.findElement(By.id("password-field")).sendKeys("CONTRASEÑA);
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Olvide mi Contraseña'])[1]/following::span[1]")).click();

    // --- 2. NAVEGACIÓN A SIARH-POF ---
    Thread.sleep(3000); 
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/pages/SIARH-POF");

    try {
        By aceptar2 = By.xpath("//span[normalize-space()='Aceptar']");
        wait.until(ExpectedConditions.elementToBeClickable(aceptar2));
        driver.findElement(aceptar2).click();
        Thread.sleep(1000);
    } catch (Exception e) {}

    Thread.sleep(3000); 
    
    System.out.println("Navegando directamente a Cargos Funcionales...");
    driver.get("https://nexocapacitacion.jujuy.edu.ar:8181/#/pages/cargofuncional/listar");
    
    // Esperamos un momento a que la tabla/vista cargue
    Thread.sleep(2000);

    // =========================================================================
    // 5. BÚSQUEDA ("AR" + ENTER)
    // =========================================================================
    By searchInput = By.id("textoBusqueda");
    
    try {
        // Esperamos que el input de búsqueda esté listo
        wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        
        driver.findElement(searchInput).click();
        driver.findElement(searchInput).clear();
        driver.findElement(searchInput).sendKeys("AR");
        driver.findElement(searchInput).sendKeys(Keys.ENTER);
        System.out.println("Búsqueda 'AR' enviada exitosamente.");
        
    } catch (Exception e) {
        System.out.println("Error: No se encontró la barra de búsqueda en la página de listado.");
        throw e;
    }

    // Pausa final para ver el resultado
    Thread.sleep(5000);
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
  }
}