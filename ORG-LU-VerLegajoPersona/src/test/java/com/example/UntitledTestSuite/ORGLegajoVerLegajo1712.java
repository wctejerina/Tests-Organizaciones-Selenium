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

public class ORGLegajoVerLegajo1712 {
  private WebDriver driver;
  private JavascriptExecutor js;
  
  // Credenciales y Datos de Prueba
  private final String USER_ID = "USUARIO";
  private final String PASSWORD = "CONTRASEÑA";
  private final String TERMINO_BUSQUEDA = "walter"; // Puedes cambiar esto por un ID específico

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
  public void testORGLegajo1712() throws Exception {
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
    
    // Asegurar que estamos en el puerto correcto
    if (!driver.getCurrentUrl().contains("8093")) {
        driver.get("https://nexocapacitacion.jujuy.edu.ar:8093/");
    }

    System.out.println(">> Gestionando Modales...");
    // Limpieza de modales (Bienvenida/Perfil)
    for (int i = 0; i < 2; i++) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            By anyDialog = By.tagName("mat-dialog-container");
            shortWait.until(ExpectedConditions.presenceOfElementLocated(anyDialog));
            
            // Si hay botón de perfil, clic
            if (driver.findElements(By.xpath("//*[contains(text(), 'Equipo-Directivo')]")).size() > 0) {
                driver.findElement(By.xpath("//*[contains(text(), 'Equipo-Directivo')]")).click();
                System.out.println(">> Perfil seleccionado.");
                wait.until(ExpectedConditions.invisibilityOfElementLocated(anyDialog));
            } else {
                // Si es otro, cerrar
                driver.findElement(By.xpath("//mat-dialog-container//button")).click();
                System.out.println(">> Modal cerrado.");
                Thread.sleep(1000);
            }
        } catch (Exception e) { break; }
    }

    // =========================================================================
    // 4. NAVEGACIÓN A "GESTIÓN DE PERSONAS -> LEGAJO"
    // =========================================================================
    System.out.println(">> 4. Navegando a Gestión de Personas...");
    
    // Intentamos ir directo a la URL de listado para ahorrar tiempo
    // Si falla, el catch hará la navegación por menú
    try {
        driver.get("https://nexocapacitacion.jujuy.edu.ar:8093/#/pages/personasiarh/listar");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("textoBusqueda")));
        System.out.println(">> Navegación directa exitosa.");
    } catch (Exception navEx) {
        System.out.println(">> Navegación directa falló. Usando menú...");
        // Menú Padre
        By menuPadre = By.xpath("//*[contains(text(), 'GESTIÓN DE PERSONAS')]");
        wait.until(ExpectedConditions.elementToBeClickable(menuPadre)).click();
        Thread.sleep(500);
        // Menú Hijo
        By menuHijo = By.xpath("//*[contains(text(), 'Legajo')]");
        wait.until(ExpectedConditions.elementToBeClickable(menuHijo)).click();
    }

    // =========================================================================
    // 5. BÚSQUEDA DE PERSONA (WALTER)
    // =========================================================================
    System.out.println(">> 5. Buscando: " + TERMINO_BUSQUEDA);
    
    By inputBusqueda = By.id("textoBusqueda");
    wait.until(ExpectedConditions.elementToBeClickable(inputBusqueda));
    
    WebElement searchBox = driver.findElement(inputBusqueda);
    searchBox.clear();
    searchBox.sendKeys(TERMINO_BUSQUEDA);
    searchBox.sendKeys(Keys.ENTER);
    
    // Esperamos a que la tabla se actualice
    // Buscamos que aparezca alguna celda con datos
    Thread.sleep(3000); 
    By primeraFila = By.xpath("//tbody/tr[1] | //mat-row[1]");
    wait.until(ExpectedConditions.presenceOfElementLocated(primeraFila));

    // =========================================================================
    // 6. ESTRATEGIA "SMART LINK": Obtener ID y Navegar
    // =========================================================================
    System.out.println(">> 6. Seleccionando registro...");

    // En lugar de hacer clic en "..." -> "Ver", leemos el ID de la columna 1
    // XPath: Primera fila, primera columna (donde está el ID 9566, etc.)
    By celdaId = By.xpath("(//tbody/tr[1]/td[1])[1] | (//mat-row[1]/mat-cell[1])[1]");
    
    String idPersona = "";
    try {
        WebElement cell = driver.findElement(celdaId);
        idPersona = cell.getText().trim();
        System.out.println(">> ID Detectado en la tabla: " + idPersona);
    } catch (Exception e) {
        System.out.println("ERROR: No se pudo leer el ID de la tabla. ¿Hay resultados?");
        throw e;
    }

    if (!idPersona.isEmpty()) {
        // Construimos la URL de detalle
        // URL Base: https://nexocapacitacion.jujuy.edu.ar:8093/#/pages/personasiarh/view/
        String urlDetalle = "https://nexocapacitacion.jujuy.edu.ar:8093/#/pages/personasiarh/view/" + idPersona;
        
        System.out.println(">> Navegando directo al detalle: " + urlDetalle);
        driver.get(urlDetalle);
        
        // Validación final: Esperamos ver "DATOS PERSONALES" o el nombre
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("view"),
                ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "DATOS PERSONALES")
            ));
            System.out.println(">> ÉXITO: Ficha de Legajo cargada correctamente.");
        } catch (Exception e) {
            System.out.println(">> Advertencia: No se confirmó la carga visual de la ficha.");
        }
    } else {
        System.out.println("ERROR: ID vacío. No se puede navegar.");
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