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

public class ORGPOFPlazasSinOcupar {
  private WebDriver driver;
  private JavascriptExecutor js;
  
  // Define las credenciales de prueba
  private final String USER_ID = "USUARIO";
  private final String PASSWORD = "CONTRASEÑA*";
  private final String CUE_ESCUELA = "CUE"; // CUE para filtrar la escuela

  @Before
  public void setUp() throws Exception {
    WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--no-sandbox"); 
    options.addArguments("--disable-dev-shm-usage"); 
    options.addArguments("--remote-allow-origins=*");
    options.addArguments("start-maximized"); 

    driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5)); // Implicit wait bajo
    js = (JavascriptExecutor) driver;
  }

  @Test
  public void testORGPLAZASINOCUPAR1512() throws Exception {

    // =========================================================================
    // 1. INICIO: LOGIN Y NAVEGACIÓN BÁSICA
    // =========================================================================
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/auth/login");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

    try {
        By aceptar = By.xpath("//span[normalize-space()='Aceptar']");
        wait.until(ExpectedConditions.elementToBeClickable(aceptar));
        driver.findElement(aceptar).click();
        Thread.sleep(1000); 
    } catch (Exception e) {}

    driver.findElement(By.id("mat-input-0")).sendKeys(USER_ID);
    driver.findElement(By.id("password-field")).sendKeys(PASSWORD);
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Olvide mi Contraseña'])[1]/following::span[1]")).click();

    // =========================================================================
    // 2. CAMBIO A MÓDULO POF Y PUERTO 8181
    // =========================================================================
    System.out.println(">> 2. Cambiando al módulo POF y puerto 8181...");
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/pages/SIARH-POF");

    By pofCard = By.xpath("//*[contains(text(), 'POF')]/following::span[1]");
    wait.until(ExpectedConditions.elementToBeClickable(pofCard)).click();

    Thread.sleep(5000); // Espera necesaria para la redirección al 8181
    driver.get("https://nexocapacitacion.jujuy.edu.ar:8181/");

    // Intentamos cerrar cualquier modal residual
    try {
        By closeDialog = By.xpath("//mat-dialog-container//button");
        if (driver.findElements(closeDialog).size() > 0) {
            driver.findElement(closeDialog).click();
        }
    } catch (Exception e) {}


    // =========================================================================
    // 3. NAVEGACIÓN A PLAZAS SIN OCUPAR
    // =========================================================================
    System.out.println(">> 3. Navegando a Plazas sin Ocupar...");
    
    
    // Navegación a Plazas
    By plazasBtn = By.xpath("//*[contains(text(), 'Plazas sin ocupar')]");
    try {
        wait.until(ExpectedConditions.elementToBeClickable(plazasBtn));
        driver.findElement(plazasBtn).click();
        System.out.println("Menú 'Plazas' clickeado.");
    } catch (Exception e) {
        js.executeScript("arguments[0].click();", driver.findElement(plazasBtn));
    }


    // =========================================================================
    // 4. SELECCIONAR ESCUELA Y VER TODAS LAS PLAZAS
    // =========================================================================

    // 1. Clic en LUPA (Buscador)
    By lupaBtnLocator = By.xpath("//button[contains(@mattooltip, 'Búsqueda')]"); // "Búsqueda"
    try {
        wait.until(ExpectedConditions.presenceOfElementLocated(lupaBtnLocator));
        WebElement btn = driver.findElement(lupaBtnLocator);
        js.executeScript("arguments[0].click();", btn);
        System.out.println("[1/4] Lupa clickeada.");
        Thread.sleep(1500);
    } catch (Exception e) {
        System.out.println("[1/4] Lupa no encontrada. Usando ENTER en el input...");
        driver.findElement(By.tagName("input")).sendKeys(Keys.ENTER);
    }

    // 2. Clic en "SELECCIONAR"
    // Buscamos el botón con tooltip "Seleccionar" en la tabla de resultados.
    By seleccionarBtnLocator = By.xpath("//button[@mattooltip='Seleccionar']");
    
    try {
        // Esperamos a que aparezca la fila con la escuela
        wait.until(ExpectedConditions.presenceOfElementLocated(seleccionarBtnLocator));
        WebElement btnSelect = driver.findElement(seleccionarBtnLocator);
        
        // Hacemos scroll y clic forzado
        js.executeScript("arguments[0].scrollIntoView(true);", btnSelect);
        Thread.sleep(500);
        js.executeScript("arguments[0].click();", btnSelect);
        System.out.println("[2/4] Escuela seleccionada (Botón 'Seleccionar' clickeado).");
        
        // IMPORTANTE: Esperar a que cargue la SEGUNDA tabla (la de Plazas)
        Thread.sleep(4000); 
        
    } catch (Exception e) {
        System.out.println("ERROR CRÍTICO: No se encontró el botón 'Seleccionar' escuela.");
        throw e; // Fallamos el test si no podemos entrar a la escuela
    }

       
    // [YA SELECCIONAMOS LA ESCUELA EN EL PASO ANTERIOR]
    System.out.println("Escuela seleccionada. Esperando carga de lista de Plazas...");
    Thread.sleep(4000); // Esperar a que aparezca la tabla de plazas


    // =========================================================================
    // 5. CONFIGURACIÓN DE FECHAS (POR ESCRITURA DIRECTA)
    // =========================================================================
    // Definimos el rango deseado.
    String fechaDesdeStr = "05/12/2025";
    String fechaHastaStr = "31/12/2025";
    
    System.out.println(">> 6. Configurando rango de fechas: " + fechaDesdeStr + " al " + fechaHastaStr);

    // 5.1 Encontrar los inputs de fecha.
    // Buscamos por el placeholder
    By inputDesdeLocator = By.xpath("//input[contains(@placeholder, 'Desde') or contains(@data-placeholder, 'Desde')]");
    By inputHastaLocator = By.xpath("//input[contains(@placeholder, 'Hasta') or contains(@data-placeholder, 'Hasta')]");
    
    try {
        // --- FECHA DESDE ---
        WebElement inputDesde = wait.until(ExpectedConditions.elementToBeClickable(inputDesdeLocator));
        // Limpieza segura: Ctrl+A -> Backspace
        inputDesde.sendKeys(Keys.CONTROL + "a");
        inputDesde.sendKeys(Keys.BACK_SPACE);
        Thread.sleep(500); 
        inputDesde.sendKeys(fechaDesdeStr);
        System.out.println(">> Fecha Desde escrita.");
        Thread.sleep(500);

        // --- FECHA HASTA ---
        WebElement inputHasta = wait.until(ExpectedConditions.elementToBeClickable(inputHastaLocator));
        inputHasta.sendKeys(Keys.CONTROL + "a");
        
        Thread.sleep(500);
        inputHasta.sendKeys(fechaHastaStr);
        // IMPORTANTE: TAB para validar y cerrar cualquier popup
        inputHasta.sendKeys(Keys.TAB); 
        System.out.println(">> Fecha Hasta escrita y validada (TAB).");
        Thread.sleep(1000); 

    } catch (Exception e) {
        System.out.println("ERROR CRÍTICO: No se pudieron escribir las fechas.");
        throw e;
    }
    
    // 5.2 Clic en botón "Ver Plazas sin ocupar"
    // Usamos el texto del botón, es lo más seguro.
    By botonAplicar = By.xpath("//button[contains(., 'Ver Plazas sin ocupar')]");
    
    try {
        wait.until(ExpectedConditions.elementToBeClickable(botonAplicar));
        driver.findElement(botonAplicar).click();
        System.out.println(">> Botón 'Ver Plazas sin ocupar' clickeado (Normal).");
    } catch (Exception e) {
        System.out.println(">> Botón normal no reaccionó. Forzando con JS...");
        js.executeScript("arguments[0].click();", driver.findElement(botonAplicar));
    }

    // 5.3 Limpieza de seguridad (por si quedó algún calendario abierto)
    try {
        Thread.sleep(1000);
        if (driver.findElements(By.className("cdk-overlay-backdrop")).size() > 0) {
             System.out.println(">> Cerrando calendario residual...");
             js.executeScript("document.body.click()");
        }
    } catch (Exception e) {}

    
    System.out.println(">> Test de Plazas sin Ocupar finalizado.");
    Thread.sleep(10000); 
  }

  @After
  public void tearDown() throws Exception {
    if (driver != null) {
        driver.quit();
    }
  }
}