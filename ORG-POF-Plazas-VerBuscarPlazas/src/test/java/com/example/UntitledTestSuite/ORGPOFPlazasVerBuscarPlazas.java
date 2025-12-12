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

public class ORGPOFPlazasVerBuscarPlazas {
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
  public void testORGPOFLISTARPLAZAS1112() throws Exception {
    // --- INICIO: LOGIN Y NAVEGACIÓN BÁSICA ---
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/auth/login");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

    try {
        By aceptar = By.xpath("//span[normalize-space()='Aceptar']");
        wait.until(ExpectedConditions.elementToBeClickable(aceptar));
        driver.findElement(aceptar).click();
        Thread.sleep(1000); 
    } catch (Exception e) {}

    driver.findElement(By.id("mat-input-0")).sendKeys("USUARIO");
    driver.findElement(By.id("password-field")).sendKeys("CONTRASEÑA");
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Olvide mi Contraseña'])[1]/following::span[1]")).click();

    Thread.sleep(3000); 
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/pages/SIARH-POF");

    try {
        By aceptar2 = By.xpath("//span[normalize-space()='Aceptar']");
        wait.until(ExpectedConditions.elementToBeClickable(aceptar2));
        driver.findElement(aceptar2).click();
    } catch (Exception e) {}

    // Tarjeta POF
    By pofCard = By.xpath("//*[contains(text(), 'POF')]/following::span[1]");
    try {
        wait.until(ExpectedConditions.elementToBeClickable(pofCard));
        driver.findElement(pofCard).click();
    } catch (Exception e) {
        js.executeScript("arguments[0].click();", driver.findElement(pofCard));
    }

    // Cambio de Puerto 8181
    Thread.sleep(3000); 
    driver.get("https://nexocapacitacion.jujuy.edu.ar:8181/");

    // try {
    //     By dialog1 = By.xpath("//mat-dialog-container[@id='mat-dialog-1']/app-dialog/div[2]/button/span");
    //     wait.until(ExpectedConditions.elementToBeClickable(dialog1));
    //     driver.findElement(dialog1).click();
    // } catch (Exception e) {}

    // // Selección de Perfil (Equipo Directivo)
    // By perfilLocator = By.xpath("//mat-dialog-container//*[contains(text(), 'Equipo-Directivo-Escolar')]");
    // try {
    //     wait.until(ExpectedConditions.elementToBeClickable(perfilLocator));
    //     driver.findElement(perfilLocator).click();
    //     System.out.println("Perfil seleccionado.");
    //     Thread.sleep(3000); 
    // } catch (Exception e) {
    //     System.out.println("Perfil ya seleccionado o no apareció.");
    // }

    // Navegación a Plazas
    By plazasBtn = By.xpath("//*[contains(text(), 'Plazas') and not(contains(text(), 'ocupar'))]");
    try {
        wait.until(ExpectedConditions.elementToBeClickable(plazasBtn));
        driver.findElement(plazasBtn).click();
        System.out.println("Menú 'Plazas' clickeado.");
    } catch (Exception e) {
        js.executeScript("arguments[0].click();", driver.findElement(plazasBtn));
    }
    
    // Esperamos a que cargue la pantalla de "Búsqueda de Organización"
    Thread.sleep(3000);

    // =========================================================================
    // ETAPA 1: SELECCIONAR ESCUELA Y VER TODAS LAS PLAZAS
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
    // ETAPA 2: BUSCAR PLAZA ESPECÍFICA (CUPOF)
    // =========================================================================
    
    // VARIABLE: El número de CUPOF que quieres buscar
    String cupofBuscado = "405473"; 

    System.out.println("Buscando plaza con CUPOF: " + cupofBuscado);

    // 1. UBICAR EL BUSCADOR DE PLAZAS
    // En la pantalla de lista (tu imagen image_1df056.png), hay un input que dice "Buscar..."
    By buscadorPlazas = By.xpath("//input[@placeholder='Buscar...']");
    By lupaPlazasBtn = By.xpath("//input[@placeholder='Buscar...']/following::button[1]"); // El botón lupa al lado

    try {
        // Esperamos que el buscador esté listo
        wait.until(ExpectedConditions.presenceOfElementLocated(buscadorPlazas));
        driver.findElement(buscadorPlazas).clear();
        driver.findElement(buscadorPlazas).sendKeys(cupofBuscado);
        
        // Clic en la lupa para filtrar (o ENTER)
        driver.findElement(lupaPlazasBtn).click();
        System.out.println("Filtro por CUPOF aplicado.");
        
        // Esperamos a que la tabla se actualice (Importante)
        Thread.sleep(2000); 

    } catch (Exception e) {
        System.out.println("No se encontró el buscador de plazas. Intentando buscar con ENTER...");
        driver.findElement(buscadorPlazas).sendKeys(Keys.ENTER);
    }

    // =========================================================================
    // ETAPA 3: ACCIONES EN LA PLAZA ENCONTRADA (VER)
    // =========================================================================
    // Buscamos una celda que contenga el texto del CUPOF
    By celdaCupof = By.xpath("//tbody//td[contains(text(), '" + cupofBuscado + "')]");
    
    try {
        wait.until(ExpectedConditions.visibilityOfElementLocated(celdaCupof));
        System.out.println("¡Plaza " + cupofBuscado + " encontrada en la tabla!");
    } catch (Exception e) {
        System.out.println("No se ve el CUPOF en la tabla. Puede que no exista o el filtro falló.");
    }

    // 2. CLIC EN "..." (Tres Puntos) DE ESA FILA ESPECÍFICA
    // Truco XPath: Buscamos la fila (tr) que contiene el CUPOF, y dentro de esa fila buscamos el botón 'more_horiz'
    By tresPuntosLocator = By.xpath("//tr[.//td[contains(text(), '" + cupofBuscado + "')]]//button[.//mat-icon[@data-mat-icon-name='more_horiz']]");
    
    try {
        wait.until(ExpectedConditions.presenceOfElementLocated(tresPuntosLocator));
        WebElement btnMore = driver.findElement(tresPuntosLocator);
        js.executeScript("arguments[0].click();", btnMore);
        System.out.println("Menú de acciones (...) clickeado para el CUPOF " + cupofBuscado);
        Thread.sleep(1000); 
    } catch (Exception e) {
        System.out.println("Error al hacer clic en (...). Usando selector genérico de primera fila...");
        // Fallback: Si el filtro funcionó, la primera fila DEBERÍA ser la correcta.
        WebElement firstRowBtn = driver.findElement(By.xpath("(//tbody//tr[1]//button[.//mat-icon[@data-mat-icon-name='more_horiz']])[1]"));
        js.executeScript("arguments[0].click();", firstRowBtn);
    }

    // 3. CLIC EN "VER" (Igual que antes)
    By verOptionLocator = By.xpath("//div[contains(@class, 'mat-menu-panel')]//button//*[contains(text(), 'Ver')]");
    
    try {
        wait.until(ExpectedConditions.presenceOfElementLocated(verOptionLocator));
        WebElement btnVer = driver.findElement(verOptionLocator);
        js.executeScript("arguments[0].click();", btnVer);
        System.out.println("Opción 'Ver' clickeada exitosamente.");
    } catch (Exception e) {
        System.out.println("Error: No se encontró la opción 'Ver'.");
    }

    // Pausa final
    System.out.println("Test Finalizado. Mostrando ficha de la plaza...");
    Thread.sleep(7000);
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
  }
}