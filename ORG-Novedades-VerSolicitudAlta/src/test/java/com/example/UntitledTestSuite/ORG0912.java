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
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ORG0912 {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  JavascriptExecutor js;

  @Before
  public void setUp() throws Exception {
    WebDriverManager.chromedriver().setup();
    
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--no-sandbox"); 
    options.addArguments("--disable-dev-shm-usage"); 
    options.addArguments("--remote-allow-origins=*");
    options.addArguments("start-maximized"); 

    driver = new ChromeDriver(options);
    baseUrl = "https://nexocapacitacion.jujuy.edu.ar:4200/#/auth/login";
    
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
    js = (JavascriptExecutor) driver;
  }

  @Test
  public void testORG0912() throws Exception {
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/auth/login");

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

    // --- LOGIN ---
    try {
        By aceptarButtonLocator = By.xpath("//span[normalize-space()='Aceptar']");
        wait.until(ExpectedConditions.elementToBeClickable(aceptarButtonLocator));
        driver.findElement(aceptarButtonLocator).click();
        Thread.sleep(2000); 
    } catch (Exception e) {
        System.out.println("El botón 'Aceptar' no apareció, continuamos...");
    }

    wait.until(ExpectedConditions.elementToBeClickable(By.id("mat-input-0")));
    driver.findElement(By.id("mat-input-0")).clear();
    driver.findElement(By.id("mat-input-0")).sendKeys("USUARIO");

    driver.findElement(By.id("password-field")).clear();
    driver.findElement(By.id("password-field")).sendKeys("CONTRASEÑA");
    
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Olvide mi Contraseña'])[1]/following::span[1]")).click();
    
    // --- NAVEGACIÓN A SOLICITUD ---
    Thread.sleep(3000); 
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/pages/SIARH-SOLICITUD");
    
    // Botón Aceptar en nueva página
    try {
        By aceptarButtonLocator = By.xpath("//span[normalize-space()='Aceptar']");
        wait.until(ExpectedConditions.elementToBeClickable(aceptarButtonLocator));
        driver.findElement(aceptarButtonLocator).click();
        Thread.sleep(2000);
    } catch (Exception e) {
        System.out.println("El botón 'Aceptar' secundario no apareció.");
    }

    // --- SELECCIÓN DE PERFIL (ESCUELA) - CORREGIDO ---
    // Usamos //mat-dialog-container para forzar la búsqueda en el modal
    By perfilEscuelaLocator = By.xpath("//mat-dialog-container//*[contains(text(), 'Equipo-Directivo-Escolar')]");

    try {
        wait.until(ExpectedConditions.elementToBeClickable(perfilEscuelaLocator));
        driver.findElement(perfilEscuelaLocator).click();
        System.out.println("Perfil escolar seleccionado (Modal).");
        Thread.sleep(2000); 
    } catch (Exception e) {
        System.out.println("No se encontró el perfil en el modal o ya estaba seleccionado.");
    }

    // --- CAMBIO DE APLICACIÓN (PUERTO 8204) ---
    Thread.sleep(3000); 
    driver.get("https://nexocapacitacion.jujuy.edu.ar:8204/");

    // Diálogo 1 (Popups iniciales)
    By dialog1 = By.xpath("//mat-dialog-container[@id='mat-dialog-1']/app-dialog/div[2]/button/span");
    try {
        wait.until(ExpectedConditions.elementToBeClickable(dialog1));
        driver.findElement(dialog1).click();
        Thread.sleep(1000); 
    } catch (TimeoutException e) {
        System.out.println("Dialogo 1 no apareció.");
    }

    // Diálogo 2 (Rol)
    By dialog2 = By.xpath("//mat-dialog-container[@id='mat-dialog-0']/app-modal-rol/div/mat-card/mat-card-header/div/mat-card-title");
    try {
        wait.until(ExpectedConditions.elementToBeClickable(dialog2));
        driver.findElement(dialog2).click();
        Thread.sleep(2000);
    } catch (Exception e) {
         System.out.println("Dialogo de rol no apareció.");
    }

    // --- NAVEGACIÓN: GESTIÓN -> ALTA ---

    // 1. Clic en "Gestión de Novedades"
    By gestionBtn = By.xpath("//*[contains(text(), 'Gestión de Novedades')]");
    try {
        wait.until(ExpectedConditions.elementToBeClickable(gestionBtn));
        driver.findElement(gestionBtn).click();
        System.out.println("Menú 'Gestión de Novedades' desplegado.");
        Thread.sleep(2000); 
    } catch (Exception e) {
        WebElement btn = driver.findElement(gestionBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        Thread.sleep(2000);
    }

    // 2. Clic en "Registro de Alta"
    By registroAltaBtn = By.xpath("//*[contains(text(), 'Registro de Alta')]");
    try {
        wait.until(ExpectedConditions.elementToBeClickable(registroAltaBtn));
        driver.findElement(registroAltaBtn).click();
        System.out.println("Clic en 'Registro de Alta' exitoso.");
    } catch (Exception e) {
        WebElement btn = driver.findElement(registroAltaBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    // --- VER SOLICITUDES (Acción Final) ---
    // Buscamos el botón 'Ver solicitudes' dentro de la tarjeta
    By verSolicitudesBtn = By.xpath("//*[contains(text(), 'Ver solicitudes')]");
    
    try {
        // Scroll por si está muy abajo
        WebElement btnSolicitudes = driver.findElement(verSolicitudesBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnSolicitudes);
        Thread.sleep(500);

        wait.until(ExpectedConditions.elementToBeClickable(verSolicitudesBtn));
        btnSolicitudes.click();
        System.out.println("Botón 'Ver solicitudes' clickeado.");
        
        Thread.sleep(2000); 

    } catch (Exception e) {
        System.out.println("Error al clickear 'Ver solicitudes', intentando forzar...");
        WebElement btn = driver.findElement(verSolicitudesBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    // Pausa final
    System.out.println("Test finalizado. Pausando 10 segundos...");
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
}