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

public class ORGNovedadesSolicitudBajaLegajo {
  private WebDriver driver;
  private JavascriptExecutor js;
  
  private final String USER_ID = "USUARIO";
  private final String PASSWORD = "CONTRASEÑA";

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
  public void testORGNovedadesSolicitudLegajoBaja1612() throws Exception {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
    
    // =========================================================================
    // 1. LOGIN
    // =========================================================================
    System.out.println(">> 1. Iniciando Login...");
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/auth/login");

    try {
        By aceptar = By.xpath("//span[normalize-space()='Aceptar']");
        wait.until(ExpectedConditions.elementToBeClickable(aceptar)).click();
        Thread.sleep(1000); 
    } catch (Exception e) {}

    try {
        By usuarioInput = By.id("mat-input-0");
        wait.until(ExpectedConditions.visibilityOfElementLocated(usuarioInput));
        driver.findElement(usuarioInput).clear();
        driver.findElement(usuarioInput).sendKeys(USER_ID);
    } catch (Exception e) {
        driver.findElement(By.xpath("//input[@type='text']")).sendKeys(USER_ID);
    }

    WebElement passInput = driver.findElement(By.id("password-field"));
    passInput.sendKeys(PASSWORD);
    Thread.sleep(500);
    passInput.sendKeys(Keys.ENTER); 
    
    try {
        wait.until(ExpectedConditions.urlContains("pages"));
        System.out.println(">> Login Exitoso.");
    } catch (TimeoutException e) {
        WebElement btnLogin = driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Olvide mi Contraseña'])[1]/following::span[1]"));
        js.executeScript("arguments[0].click();", btnLogin);
        wait.until(ExpectedConditions.urlContains("pages"));
    }

    // =========================================================================
    // 2. NAVEGACIÓN A SOLICITUD (PUERTO 4200)
    // =========================================================================
    System.out.println(">> 2. Navegando a Módulo Solicitud...");
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/pages/SIARH-SOLICITUD");
    
    
    // =========================================================================
    // 3. CAMBIO AL PUERTO 8204 Y GESTIÓN DE POPUPS
    // =========================================================================
    System.out.println(">> 3. Cambiando al puerto 8204...");
    Thread.sleep(5000); 
    driver.get("https://nexocapacitacion.jujuy.edu.ar:8204/");

    System.out.println(">> Gestionando Modales (Nueva Versión / Bienvenida / Perfil)...");
    
    // Bucle para limpiar todos los popups que aparezcan (hasta 3 intentos)
    for (int i = 0; i < 3; i++) {
        try {
            // Buscamos cualquier diálogo abierto
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(4));
            By anyDialog = By.tagName("mat-dialog-container");
            shortWait.until(ExpectedConditions.presenceOfElementLocated(anyDialog));
            System.out.println(">> Diálogo detectado (Iteración " + (i+1) + ")");

            // Caso A: Selector de Perfil (Lo que queremos)
            By perfilBtn = By.xpath("//mat-dialog-container//*[contains(text(), 'Equipo-Directivo-Escolar')]");
            if (driver.findElements(perfilBtn).size() > 0) {
                driver.findElement(perfilBtn).click();
                System.out.println(">> Perfil 'Equipo Directivo' SELECCIONADO.");
                wait.until(ExpectedConditions.invisibilityOfElementLocated(anyDialog));
                break; // Salimos del bucle si ya seleccionamos perfil
            } 
            
            // Caso B: "Nueva versión disponible" o "Bienvenida" (Botón Aceptar/Cerrar)
            // Buscamos un botón que diga "Aceptar" o simplemente el primer botón disponible
            By btnAceptarPopup = By.xpath("//mat-dialog-container//button[contains(., 'Aceptar') or contains(., 'Cerrar')] | //mat-dialog-container//button");
            if (driver.findElements(btnAceptarPopup).size() > 0) {
                driver.findElement(btnAceptarPopup).click();
                System.out.println(">> Popup informativo cerrado (Nueva versión/Bienvenida).");
                Thread.sleep(2000); // Esperar al siguiente popup si lo hay
            }

        } catch (Exception e) {
            System.out.println(">> No hay más diálogos bloqueantes.");
            break;
        }
    }

    // =========================================================================
    // 4. NAVEGACIÓN A "SOLICITUDES DE LEGAJO"
    // =========================================================================
    System.out.println(">> 4. Navegando a Solicitudes de Legajo...");
    
    // Intentamos ir por menú primero
    try {
        By menuPadre = By.xpath("//*[contains(text(), 'Solicitudes de Legajo')]");
        wait.until(ExpectedConditions.elementToBeClickable(menuPadre)).click();
        Thread.sleep(1000);
        
        By menuHijo = By.xpath("//*[contains(text(), 'Baja')]");
        wait.until(ExpectedConditions.elementToBeClickable(menuHijo)).click();
        System.out.println(">> Menú navegado correctamente.");
        
    } catch (Exception e) {
        System.out.println(">> Error en menú. Navegando por URL directa...");
        driver.get("https://nexocapacitacion.jujuy.edu.ar:8204/#/pages/solicitud-legajo/listar");
    }

    // =========================================================================
    // 5. SELECCIONAR ESCUELA Y VER TODAS LAS PLAZAS
    // =========================================================================

    // 5.1 Clic en LUPA (Buscador)
    By lupaBtnLocator = By.xpath("//button[contains(@mattooltip, 'Búsqueda')]"); // "Búsqueda"
    try {
        wait.until(ExpectedConditions.presenceOfElementLocated(lupaBtnLocator));
        WebElement btn = driver.findElement(lupaBtnLocator);
        js.executeScript("arguments[0].click();", btn);
        System.out.println("[1/3] Lupa clickeada.");
        Thread.sleep(1500);
    } catch (Exception e) {
        System.out.println("[2/3] Lupa no encontrada. Usando ENTER en el input...");
        driver.findElement(By.tagName("input")).sendKeys(Keys.ENTER);
    }

    // 5.2 Clic en "SELECCIONAR"
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
        System.out.println("[3/3] Escuela seleccionada (Botón 'Seleccionar' clickeado).");
        
        // IMPORTANTE: Esperar a que cargue la SEGUNDA tabla (la de Plazas)
        Thread.sleep(4000); 
        
    } catch (Exception e) {
        System.out.println("ERROR CRÍTICO: No se encontró el botón 'Seleccionar' escuela.");
        throw e; // Fallamos el test si no podemos entrar a la escuela
    }

       
    // [YA SELECCIONAMOS LA ESCUELA EN EL PASO ANTERIOR]
    System.out.println("Escuela seleccionada. Esperando carga de lista...");
    Thread.sleep(4000); // Esperar a que aparezca la tabla de plazas


    // =========================================================================
    //  SELECCIÓN DE PERSONA
    // =========================================================================
    // System.out.println(">> 6. Seleccionando Persona...");

    // // 1. Clic en LUPA de Persona
    // By lupaPersona = By.xpath("//*[contains(text(), 'Persona')]/ancestor::mat-form-field//button");
    // wait.until(ExpectedConditions.elementToBeClickable(lupaPersona)).click();

    // 2. Seleccionar Persona
    // try {
    //     By btnSelectPers = By.xpath("//mat-dialog-container//button[@mattooltip='Seleccionar']");
    //     wait.until(ExpectedConditions.elementToBeClickable(btnSelectPers));
        
    //     WebElement btn = driver.findElement(btnSelectPers);
    //     js.executeScript("arguments[0].click();", btn);
    //     System.out.println(">> Persona seleccionada.");
        
    //     wait.until(ExpectedConditions.invisibilityOfElementLocated(By.tagName("mat-dialog-container")));
    // } catch (Exception e) {
    //     System.out.println("ERROR: No se pudo seleccionar la persona.");
    //     throw e;
    // }

    // =========================================================================
    // 6. VER SOLICITUDES (Acción Final)
    // =========================================================================
    System.out.println(">> 6. Ver Solicitudes...");
    
    // 6.1 Buscamos botón "Ver Solicitudes"
   
    By botonAplicar = By.xpath("//button[contains(., 'Ver solicitudes')]");
    
    try {
        wait.until(ExpectedConditions.elementToBeClickable(botonAplicar));
        driver.findElement(botonAplicar).click();
        System.out.println(">> Botón 'Ver Plazas sin ocupar' clickeado (Normal).");
    } catch (Exception e) {
        System.out.println(">> Botón normal no reaccionó. Forzando con JS...");
        js.executeScript("arguments[0].click();", driver.findElement(botonAplicar));
    }

    // 6.2 Limpieza de seguridad (por si quedó algún calendario abierto)
    try {
        Thread.sleep(1000);
        if (driver.findElements(By.className("cdk-overlay-backdrop")).size() > 0) {
             System.out.println(">> Cerrando calendario residual...");
             js.executeScript("document.body.click()");
        }
    } catch (Exception e) {}
    
    System.out.println(">> Test Ver Baja de Legajos finalizado.");
    Thread.sleep(10000); 
  }

  @After
  public void tearDown() throws Exception {
    if (driver != null) {
        driver.quit();
    }
  }
}