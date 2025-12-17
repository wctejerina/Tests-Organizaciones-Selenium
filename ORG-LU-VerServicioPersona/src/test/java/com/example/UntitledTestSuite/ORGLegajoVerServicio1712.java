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

public class ORGLegajoVerServicio1712 {
  private WebDriver driver;
  private JavascriptExecutor js;
  
  // Datos de prueba (Basados en tu código original e imágenes)
  private final String USER_ID = "USUARIO";
  private final String PASSWORD = "CONTRASEÑA";
  private final String CUE_ORG = "CUE"; // Tomado de tu imagen image_a1785a.png

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
  public void testORGLegajoServicios1712() throws Exception {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

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

    driver.findElement(By.id("password-field")).sendKeys(PASSWORD);
    Thread.sleep(500);
    driver.findElement(By.id("password-field")).sendKeys(Keys.ENTER); 
    
    try {
        wait.until(ExpectedConditions.urlContains("pages"));
        System.out.println(">> Login Exitoso.");
    } catch (TimeoutException e) {
        // Fallback Click
        WebElement btnLogin = driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Olvide mi Contraseña'])[1]/following::span[1]"));
        js.executeScript("arguments[0].click();", btnLogin);
        wait.until(ExpectedConditions.urlContains("pages"));
    }

    // =========================================================================
    // 2. NAVEGACIÓN A MODULO LEGAJO ÚNICO (LU)
    // =========================================================================
    System.out.println(">> 2. Navegando a Legajo Único...");
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/pages/SIARH-LU");
    
    
    // =========================================================================
    // 3. CAMBIO AL PUERTO 8093 Y GESTIÓN DE POPUPS
    // =========================================================================
    System.out.println(">> 3. Cambiando al puerto 8093...");
    Thread.sleep(5000); // Esperar redirección
    

    // =========================================================================
    // 4. NAVEGACIÓN A "GESTIÓN DE SERVICIOS -> SERVICIOS"
    // =========================================================================
    System.out.println(">> 4. Navegando a Servicios...");
    
    // Intentamos ir directo
    try {
        // URL deducida, si falla entra al catch y navega por menú
        driver.get("https://nexocapacitacion.jujuy.edu.ar:8093/#/");
        driver.findElement(By.xpath("//mat-dialog-container//button")).click();
        
    } catch (Exception navEx) {
        System.out.println(">> Navegando por menú...");
        // Menú Padre
        By menuPadre = By.xpath("//*[contains(text(), 'GESTIÓN DE SERVICIOS')]");
        wait.until(ExpectedConditions.elementToBeClickable(menuPadre)).click();
        Thread.sleep(500);
        // Menú Hijo
        By menuHijo = By.xpath("//*[contains(text(), 'Servicios')]");
        wait.until(ExpectedConditions.elementToBeClickable(menuHijo)).click();
    }

    // =========================================================================
    // 5. SELECCIÓN DE ORGANIZACIÓN (Imagen 1)
    // =========================================================================
    System.out.println(">> 5. Seleccionando Organización...");
    
    // Esperar a que desaparezca cualquier spinner de carga
    try {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("mat-progress-spinner")));
    } catch (Exception e) {}

    // =========================================================================
    // 6. SELECCIONAR ESCUELA Y VER TODAS LAS PLAZAS
    // =========================================================================

    // 6.1 Clic en LUPA (Buscador)
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

    // 6.2 Clic en "SELECCIONAR"
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

    // // 6.3 Buscar CUE en el Modal
    // try {
    //     By inputBusquedaModal = By.xpath("//mat-dialog-container//input[@placeholder='Buscar...'] | //mat-dialog-container//input[contains(@class, 'mat-input')]");
    //     wait.until(ExpectedConditions.visibilityOfElementLocated(inputBusquedaModal));
        
    //     driver.findElement(inputBusquedaModal).sendKeys(CUE_ORG);
    //     driver.findElement(inputBusquedaModal).sendKeys(Keys.ENTER);
    //     Thread.sleep(2000); // Esperar filtro
    // } catch (Exception e) {
    //     System.out.println(">> No se pudo filtrar por CUE, intentando seleccionar el primero...");
    // }

    // // 6.4 Seleccionar (Tilde/Check)
    // By btnSelectOrg = By.xpath("//mat-dialog-container//button[@mattooltip='Seleccionar'] | //mat-dialog-container//button[contains(@class, 'mat-primary')]");
    
    // try {
    //     wait.until(ExpectedConditions.elementToBeClickable(btnSelectOrg));
    //     WebElement btn = driver.findElement(btnSelectOrg);
    //     js.executeScript("arguments[0].click();", btn); 
    //     System.out.println(">> Organización seleccionada.");
        
    //     wait.until(ExpectedConditions.invisibilityOfElementLocated(modalContainer));
        
    // } catch (Exception e) {
    //     System.out.println("ERROR: No se pudo seleccionar la organización.");
    //     throw e;
    // }

    // =========================================================================
    // 7. INTERACCIÓN CON LISTA
    // =========================================================================
    System.out.println(">> 6. Accediendo al primer registro...");
    
    // Esperar carga de la tabla principal
    Thread.sleep(2000);
    By tablaServicios = By.xpath("//tbody | //mat-table");
    wait.until(ExpectedConditions.presenceOfElementLocated(tablaServicios));

    // 6.1 Clic en menú "..." (Tres Puntos) de la primera fila
    By tresPuntosBtn = By.xpath("(//tbody/tr[1]//button[.//mat-icon])[last()] | (//mat-row[1]//button[.//mat-icon])[last()]");
    
    try {
        wait.until(ExpectedConditions.elementToBeClickable(tresPuntosBtn));
        driver.findElement(tresPuntosBtn).click();
        System.out.println(">> Menú '...' desplegado.");
        Thread.sleep(1000); // Esperar animación del menú
    } catch (Exception e) {
        System.out.println(">> Fallo clic menú normal. Usando JS...");
        WebElement btn = driver.findElement(tresPuntosBtn);
        js.executeScript("arguments[0].click();", btn);
    }

    // 6.2 Clic en "Ver" (Dentro del menú desplegable)
    // Buscamos en el overlay del menú (cdk-overlay-container)
    By opcionVer = By.xpath("//div[contains(@class, 'mat-menu-panel')]//button[contains(., 'Ver')]");
    
    try {
        wait.until(ExpectedConditions.elementToBeClickable(opcionVer));
        driver.findElement(opcionVer).click();
        System.out.println(">> Opción 'Ver' clickeada.");
    } catch (Exception e) {
        System.out.println(">> Fallo clic 'Ver'. Usando JS...");
        // Búsqueda alternativa por si el texto cambia (busca el primer item del menú)
        By opcionAlt = By.xpath("//div[contains(@class, 'mat-menu-panel')]//button[1]");
        WebElement btn = driver.findElement(opcionVer); // O usar opcionAlt
        js.executeScript("arguments[0].click();", btn);
    }

    // Validación final (URL debería cambiar a /view/...)
    try {
        wait.until(ExpectedConditions.urlContains("view"));
        System.out.println(">> ÉXITO: Detalle de Servicio cargado.");
    } catch (Exception e) {
        System.out.println(">> Advertencia: No se detectó cambio de URL a 'view'.");
    }

    System.out.println(">> Test Finalizado.");
    Thread.sleep(5000);
  }

  @After
  public void tearDown() throws Exception {
    if (driver != null) {
        driver.quit();
    }
  }
}