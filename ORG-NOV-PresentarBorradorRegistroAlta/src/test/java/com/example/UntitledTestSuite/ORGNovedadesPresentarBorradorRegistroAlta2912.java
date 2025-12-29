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
import java.util.List;

public class ORGNovedadesPresentarBorradorRegistroAlta2912 {
  private WebDriver driver;
  private JavascriptExecutor js;
  
  private final String USER_ID = "USUARIO";
  private final String PASSWORD = "CLAVE";
  private final String CUE_ORG = "3800094"; 

  @Before
  public void setUp() throws Exception {
    WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--remote-allow-origins=*", "start-maximized"); 
    driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    js = (JavascriptExecutor) driver;
  }

  @Test
  public void testORGNovedadesPresentarBorrador2912() throws Exception {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

    // =========================================================================
    // 1. LOGIN
    // =========================================================================
    System.out.println(">> 1. Iniciando Login...");
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4200/#/auth/login");

    try {
        By btnAceptarIni = By.xpath("//mat-dialog-container//button[contains(., 'Aceptar')]");
        wait.until(ExpectedConditions.elementToBeClickable(btnAceptarIni)).click();
        Thread.sleep(1000); 
    } catch (Exception e) {}

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mat-input-0"))).sendKeys(USER_ID);
    driver.findElement(By.id("password-field")).sendKeys(PASSWORD + Keys.ENTER); 
    wait.until(ExpectedConditions.urlContains("pages"));
    System.out.println(">> Login Exitoso.");

    // =========================================================================
    // 2. TRANSICIÓN A 8204 Y SELECCIÓN DE PERFIL
    // =========================================================================
    System.out.println(">> 2. Cambiando a Gestión (8204) y seleccionando perfil...");
    Thread.sleep(5000); 
    driver.get("https://nexocapacitacion.jujuy.edu.ar:8204/#/");
    
    // Selección de Perfil y Limpieza de Overlays
    limpiarModalesIniciales();

    // Navegar a la lista de Registro de Alta
    driver.get("https://nexocapacitacion.jujuy.edu.ar:8204/#/pages/solicituddesignacion/listar");
    Thread.sleep(3000);
    // Limpieza de avisos y selección de rol
    try {
            By btnAceptarVer = By.xpath("//button[contains(., 'Aceptar')]");
            By btnPerfil = By.xpath("//*[contains(text(), 'Equipo-Directivo-Escolar')]");
            
            if (driver.findElements(btnAceptarVer).size() > 0) {
                js.executeScript("arguments[0].click();", driver.findElement(btnAceptarVer));
                Thread.sleep(2000);
            }
            if (driver.findElements(btnPerfil).size() > 0) {
                js.executeScript("arguments[0].click();", driver.findElement(btnPerfil));
                System.out.println(">> Perfil seleccionado.");
                Thread.sleep(2000);
            }
        } catch (Exception e) {}

    js.executeScript("var b=document.getElementsByClassName('cdk-overlay-backdrop');while(b[0]){b[0].parentNode.removeChild(b[0]);}");

    // =========================================================================
    // 3. NAVEGACIÓN Y SELECCIÓN DE ESCUELA (CON FILTRO CUE)
    // =========================================================================
    System.out.println(">> 3. Navegando a Registro de Alta...");

    // 3.1 Clic en LUPA de Organización
    By lupaOrg = By.xpath("//button[contains(@mattooltip, 'Búsqueda')]");
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.presenceOfElementLocated(lupaOrg)));

    // 3.2 Filtrar por CUE (Para asegurar que no elija otra escuela)
    try {
        WebElement inputBusq = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Buscar...']")));
        inputBusq.clear();
        inputBusq.sendKeys(CUE_ORG + Keys.ENTER);
        Thread.sleep(10000);
    } catch (Exception e) {}

    ////3.3 Seleccionar Escuela (CORRECCIÓN: Sin prefijo mat-dialog-container)
    ///  AQUI
    By btnSelectOrg = By.xpath("//button[@mattooltip='Seleccionar']");
    wait.until(ExpectedConditions.elementToBeClickable(btnSelectOrg));
    js.executeScript("arguments[0].click();", driver.findElement(btnSelectOrg));

    // 3.4 Clic en Ver solicitudes
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Ver solicitudes')]"))).click();
    System.out.println(">> Escuela " + CUE_ORG + " cargada.");

    // =========================================================================
    // 4. BUCLE PARA PROCESAR TODOS LOS BORRADORES
    // =========================================================================
    boolean existenBorradores = true;

    while (existenBorradores) {
        // Buscamos filas que tengan el texto 'BORRADOR' en la columna Estado
        By filasBorrador = By.xpath("//tr[td[contains(., 'BORRADOR')]]");
        List<WebElement> borradores = driver.findElements(filasBorrador);

        if (borradores.size() > 0) {
            System.out.println(">> Borradores pendientes: " + borradores.size());
            procesarPrimerBorrador(wait);
            
            // Refrescar o volver a la lista si es necesario
            Thread.sleep(3000);
            driver.get("https://nexocapacitacion.jujuy.edu.ar:8204/#/pages/solicituddesignacion/listar");
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Ver solicitudes')]"))).click();
            Thread.sleep(2000);
        } else {
            System.out.println(">> No quedan más borradores por presentar.");
            existenBorradores = false;
        }
    }
  }

  private void procesarPrimerBorrador(WebDriverWait wait) throws Exception {
    // 1. Clic en "..." de la primera fila que sea BORRADOR
    By btnMenuAcciones = By.xpath("//tr[td[contains(., 'BORRADOR')]]//button[contains(@class, 'mat-menu-trigger')]");
    WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(btnMenuAcciones));
    js.executeScript("arguments[0].click();", menu);
    Thread.sleep(3000);
    
    // 2. Clic en Modificar
    By btnModificar = By.xpath("//div[contains(@class, 'mat-menu-panel')]//button[contains(., 'Modificar')]");
    wait.until(ExpectedConditions.elementToBeClickable(btnModificar)).click();
    System.out.println(">> Entrando a Modificar solicitud...");
    Thread.sleep(3000);

    // 3. Clic en Presentar
    // Bajamos hasta el final para asegurar visibilidad del botón
    By btnPresentar = By.xpath("//button[contains(., 'Presentar')]");
    WebElement btnPres = wait.until(ExpectedConditions.presenceOfElementLocated(btnPresentar));
    js.executeScript("arguments[0].scrollIntoView(true);", btnPres);
    Thread.sleep(3000);
    js.executeScript("arguments[0].click();", btnPres);

    // 4. Manejo de Confirmaciones (Sí -> Aceptar)
    System.out.println(">> Confirmando presentación...");
    limpiarModalesFinales();
  }

  // =========================================================================
  // MÉTODOS AUXILIARES
  // =========================================================================

  private void limpiarModalesIniciales() {
    try {
        By perfil = By.xpath("//*[contains(text(), 'Equipo-Directivo-Escolar')]");
        waitAndClick(perfil);
        Thread.sleep(2000);
    } catch (Exception e) {}
    // Quitar backdrop de carga
    js.executeScript("var b=document.getElementsByClassName('cdk-overlay-backdrop');while(b[0]){b[0].parentNode.removeChild(b[0]);}");
  }

  private void limpiarModalesFinales() {
    for (int i = 0; i < 3; i++) {
        try {
            By btnOk = By.xpath("//mat-dialog-container//button[contains(., 'Aceptar') or contains(., 'Sí')]");
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(btnOk));
            js.executeScript("arguments[0].click();", btn);
            Thread.sleep(2000); 
        } catch (Exception e) { break; }
    }
  }

  private void waitAndClick(By locator) {
    new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(locator)).click();
  }

  @After
  public void tearDown() {
    if (driver != null) driver.quit();
  }
}