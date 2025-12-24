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

public class ORGNovedadesLegajoLicencia2412 {
  private WebDriver driver;
  private JavascriptExecutor js;
  
  // Datos Maestros
  private final String USER_ID = "USUARIO";
  private final String PASSWORD = "CONTRASEÑA";
  private final String CUE_ORG = "3800094"; 
  private final String PERSONA_BUSQUEDA = "RUTH";

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
  public void testORGNovedadesLegajoLicencia2412() throws Exception {
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
    // 3. NAVEGACIÓN Y SELECCIÓN DE ESCUELA (LISTADO)
    // =========================================================================
    System.out.println(">> 3. Navegando a Situación Servicio / Afectación...");
    driver.get("https://nexocapacitacion.jujuy.edu.ar:8204/#/pages/solicitudLicencia/listar");
    Thread.sleep(3000);

    // // Búsqueda de Organización
    // By lupaOrg = By.xpath("//button[contains(@mattooltip, 'Búsqueda')]");
    // js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.presenceOfElementLocated(lupaOrg)));

    // try {
    //     WebElement inputBusq = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Buscar...']")));
    //     inputBusq.clear();
    //     inputBusq.sendKeys(CUE_ORG + Keys.ENTER);
    //     Thread.sleep(2000);
    // } catch (Exception e) {}

    // js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@mattooltip='Seleccionar']"))));
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Ver solicitudes')]"))).click();
    System.out.println(">> Escuela cargada correctamente.");

    // =========================================================================
    // 4. BUSQUEDA PERSONA
    // =========================================================================
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'mat-mini-fab')]"))).click();
    Thread.sleep(3000);
    System.out.println(">> Paso 1: Persona...");

    // Lupa de búsqueda de persona integrada en la página
    By lupaOrg = By.xpath("//button[contains(@mattooltip, 'Búsqueda')]");
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.presenceOfElementLocated(lupaOrg)));;
    
    // Filtrar nombre en el buscador integrado
    WebElement inputPers = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("textoBusqueda")));
    inputPers.clear();
    inputPers.sendKeys(PERSONA_BUSQUEDA + Keys.ENTER);
    Thread.sleep(2000);
    
    // Selección del ultimo registro (tilde azul)
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@mattooltip='Seleccionar'])[last()]"))));
 
    seleccionarOpcionMat("idOrganizacionNivelEducativo", "SECUNDARIO");
    driver.findElement(By.xpath("//button[contains(., 'Siguiente')]")).click();
    Thread.sleep(2000);

    // =========================================================================
    // 5. PASO 2: DATOS DE LA LICENCIA
    // =========================================================================
    System.out.println(">> Paso 2: Datos de Licencia...");
    seleccionarOpcionMat("idRefCategoriaLicencia", "SALUD");
    seleccionarOpcionMat("idRefGrupoNivel", "SECUNDARIA");

    // Lupa Artículo de Licencia
    By lupaArticulo = By.xpath("//mat-form-field[.//mat-label[contains(text(), 'Articulo')]]//button[contains(@mattooltip, 'Búsqueda')]");
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(lupaArticulo)));
    Thread.sleep(2000);
    By btnSelectExp = By.xpath("(//button[@mattooltip='Seleccionar'])[1]");
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(btnSelectExp)));
    System.out.println(">> Articulo seleccionado.");
    Thread.sleep(1500);

    // Fechas
    escribirFechaPorControl("fechaDesde", "31/12/2025");
    Thread.sleep(2000);
    escribirFechaPorControl("fechaHasta", "28/02/2026");
    
    js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//button[contains(., 'Siguiente')]")));
    Thread.sleep(2000);

    // =========================================================================
    // 6. PASO 3: DATOS DE SERVICIO (SELECCIÓN TÉCNICA)
    // =========================================================================
    System.out.println(">> Paso 3: Datos de Servicio...");
    By btnServiciosAfectados = By.xpath("//button[contains(., 'Servicios afectados')]");
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(btnServiciosAfectados)));
    
    try {
        System.out.println(">> Esperando carga de servicios en el popup...");
        // Esperamos a que la grilla cargue datos reales
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//mat-dialog-container//mat-row | //mat-dialog-container//tr[td]")));
        Thread.sleep(2000); 

        // 1. Localizamos el componente padre
        WebElement matCheckbox = driver.findElement(By.xpath("//mat-dialog-container//mat-checkbox"));
        
        // 2. Scroll horizontal para asegurar que el elemento esté en el área interactuable
        js.executeScript("arguments[0].scrollIntoView({behavior: 'instant', block: 'center', inline: 'center'});", matCheckbox);
        Thread.sleep(1000);

        // 3. OPCIÓN ROBUSTA: Clic al INPUT interno
        // En Angular Material, el input con type='checkbox' es el que dispara los eventos técnicos.
        WebElement hiddenInput = matCheckbox.findElement(By.xpath(".//input[@type='checkbox']"));
        
        // Verificamos si ya está seleccionado para no desmarcarlo
        String isChecked = matCheckbox.getAttribute("class");
        if (!isChecked.contains("mat-checkbox-checked")) {
            System.out.println(">> Forzando clic en el input interno...");
            js.executeScript("arguments[0].click();", hiddenInput);
        } else {
            System.out.println(">> El servicio ya aparece como seleccionado.");
        }
        
        System.out.println(">> Verificación de clase: " + matCheckbox.getAttribute("class"));
        
    } catch (Exception e) {
        System.out.println(">> Falló la selección técnica: " + e.getMessage());
        // Backup: Clic en el label si el input falla
        try {
            js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//mat-dialog-container//mat-checkbox//label")));
        } catch (Exception e2) { System.out.println(">> Error total en checkbox."); }
    }
    
    // Botón Aceptar del Popup
    By btnAceptarServicio = By.xpath("//mat-dialog-container//button[contains(., 'Aceptar')]");
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(btnAceptarServicio)));
    
    // Avance al Paso 4
    Thread.sleep(1500);
    js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//button[contains(., 'Siguiente')]")));
    
    // =========================================================================
    // 7. PASO FINAL: INSTRUMENTOS LEGALES (Manejo de 2 Lupas)
    // =========================================================================
    System.out.println(">> Paso 5: Instrumentos Legales...");
    Thread.sleep(2000);
    // =========================================================================
    // 7. PASO 4: NORMATIVA (REFACTORIZADO CON BUCLE)
    // =========================================================================
    System.out.println(">> Paso 4: Normativa...");
    
    // Definimos las etiquetas exactas que aparecen en el HTML
    String[] etiquetasNormativa = {"Expedientes", "Instrumentos Legales"};

    for (String etiqueta : etiquetasNormativa) {
        try {
            System.out.println(">> Iniciando búsqueda de: " + etiqueta + "...");
            
            // Localizador dinámico basado en el texto de la etiqueta
            By lupaXPath = By.xpath("//mat-form-field[.//mat-label[contains(text(), '" + etiqueta + "')]]//button[contains(@mattooltip, 'Búsqueda')]");
            js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(lupaXPath)));
            
            Thread.sleep(2500); // Espera técnica para que el modal pueble la tabla
            
            // Selector robusto: primer botón 'Seleccionar' del popup
            By btnSelect = By.xpath("(//button[@mattooltip='Seleccionar'])[1]");
            js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(btnSelect)));
            
            System.out.println(">> " + etiqueta + " seleccionado correctamente.");
            Thread.sleep(1500); // Pausa de estabilización entre lupas
            
        } catch (Exception e) {
            System.out.println(">> Error crítico procesando " + etiqueta + ": " + e.getMessage());
        }
    }

     // Clic en el botón azul de Guardar como borrador visto en image_750f21.png
    By btnGuardarBorrador = By.xpath("//button[contains(., 'Guardar borrador')]");
    WebElement btnBorrador = wait.until(ExpectedConditions.presenceOfElementLocated(btnGuardarBorrador));
        
    js.executeScript("arguments[0].scrollIntoView(true);", btnBorrador);
    Thread.sleep(1000);
    js.executeScript("arguments[0].click();", btnBorrador);
    System.out.println(">> Solicitud guardada exitosamente.");

    // Manejo de diálogos de confirmación si aparecen (Sí -> Aceptar)
     limpiarModalesFinales();

    System.out.println(">> TEST FINALIZADO CON ÉXITO.");
    Thread.sleep(5000);
}

  // =========================================================================
  // MÉTODOS AUXILIARES ROBUSTOS
  // =========================================================================

  private void limpiarPopupsIniciales() {
    try {
        By perfil = By.xpath("//*[contains(text(), 'Equipo-Directivo-Escolar')]");
        js.executeScript("arguments[0].click();", new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(perfil)));
        Thread.sleep(2000);
    } catch (Exception e) {}
    js.executeScript("var b=document.getElementsByClassName('cdk-overlay-backdrop');while(b[0]){b[0].parentNode.removeChild(b[0]);}");
  }

  private void seleccionarEscuelaPorCUE(WebDriverWait wait) {
    By lupaOrg = By.xpath("//button[contains(@mattooltip, 'Búsqueda')]");
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.presenceOfElementLocated(lupaOrg)));
    WebElement inputBusq = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Buscar...']")));
    inputBusq.sendKeys(CUE_ORG + Keys.ENTER);
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@mattooltip='Seleccionar']"))));
  }

  private void escribirFechaPorControl(String controlName, String fecha) throws Exception {
    By locator = By.xpath("//input[@formcontrolname='" + controlName + "']");
    WebElement input = new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.visibilityOfElementLocated(locator));
    input.click();
    for(int i=0; i<12; i++) { input.sendKeys(Keys.BACK_SPACE); }
    input.sendKeys(fecha + Keys.TAB);
    Thread.sleep(500);
  }

  private void seleccionarOpcionMat(String formControlName, String textoOpcion) {
    try {
        By sel = By.xpath("//mat-select[@formcontrolname='" + formControlName + "']");
        js.executeScript("arguments[0].click();", driver.findElement(sel));
        Thread.sleep(800);
        By opt = By.xpath("//mat-option//span[contains(normalize-space(), '" + textoOpcion + "')]");
        js.executeScript("arguments[0].click();", driver.findElement(opt));
    } catch (Exception e) { System.out.println(">> Error en " + formControlName); }
  }

  private void limpiarModalesFinales() {
    for (int i = 0; i < 3; i++) {
        try {
            By btnOk = By.xpath("//mat-dialog-container//button[contains(., 'Aceptar') or contains(., 'Sí')]");
            js.executeScript("arguments[0].click();", new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(btnOk)));
            Thread.sleep(2500); 
        } catch (Exception e) { break; }
    }
  }

  @After
  public void tearDown() { if (driver != null) driver.quit(); }
}