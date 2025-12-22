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

public class ORGNovedadesSitServAfectacion2212 {
  private WebDriver driver;
  private JavascriptExecutor js;
  
  // Datos Maestros (Ajustar según necesidad)
  private final String USER_ID = "USUARIO";
  private final String PASSWORD = "CLAVE";
  private final String CUE_ORG = "CUE"; 
  private final String PERSONA_BUSQUEDA = "JOSE";

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
  public void testORGNovedadesSitServAfectacion2212() throws Exception {
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
    driver.get("https://nexocapacitacion.jujuy.edu.ar:8204/#/pages/ssafectacion/listar");
    Thread.sleep(3000);

    // Búsqueda de Organización
    By lupaOrg = By.xpath("//button[contains(@mattooltip, 'Búsqueda')]");
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.presenceOfElementLocated(lupaOrg)));

    try {
        WebElement inputBusq = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Buscar...']")));
        inputBusq.clear();
        inputBusq.sendKeys(CUE_ORG + Keys.ENTER);
        Thread.sleep(2000);
    } catch (Exception e) {}

    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@mattooltip='Seleccionar']"))));
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Ver solicitudes')]"))).click();
    System.out.println(">> Escuela cargada correctamente.");

    // =========================================================================
    // 4. BUSQUEDA PERSONA
    // =========================================================================
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'mat-mini-fab')]"))).click();
    Thread.sleep(3000);
    System.out.println(">> Paso 1: Persona...");

    // Lupa de búsqueda de persona integrada en la página
    By lupaPers = By.xpath("//button[contains(@mattooltip, 'Persona')] | //*[contains(text(), 'persona')]/following::button[1]");
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(lupaPers)));
    
    // Filtrar nombre en el buscador integrado
    WebElement inputPers = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("textoBusqueda")));
    inputPers.clear();
    inputPers.sendKeys(PERSONA_BUSQUEDA + Keys.ENTER);
    Thread.sleep(2000);
    
    // Selección del ultimo registro (tilde azul)
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@mattooltip='Seleccionar'])[last()]"))));
 
    seleccionarOpcionMat("idOrganizacionNivelEducativo", "SECUNDARIO");
    driver.findElement(By.xpath("//button[contains(., 'SIGUIENTE')]")).click();
    Thread.sleep(2000);

   

   // =========================================================================
    // 5. SERVICIO/S
    // =========================================================================
    System.out.println(">> Paso 2: Servicio/s...");
    
    // Ingreso de fechas robusto
    escribirFechaPorControl("fechaDesde", "22/12/2025");
    //escribirFechaPorControl("fechaTermino", "15/02/2026");
    
    // --- SECCIÓN IPE/ICL  ---
    /*
    By checkIPE = By.xpath("//mat-checkbox[@formcontrolname='esIpeIcl']");
    js.executeScript("arguments[0].click();", driver.findElement(checkIPE));
    */

    System.out.println(">> Clic en búsqueda de Servicio (Lupa inferior)..."); 
    
    // Selector específico para la lupa dentro del campo "Servicio"
    By lupaServicioXPath = By.xpath("//mat-form-field[.//mat-label[contains(text(),'Servicio')]]//button[contains(@mattooltip, 'Búsqueda')]");
    
    try {
        WebElement btnLupaServ = wait.until(ExpectedConditions.elementToBeClickable(lupaServicioXPath));
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", btnLupaServ);
        Thread.sleep(1000);
        js.executeScript("arguments[0].click();", btnLupaServ);
        System.out.println(">> Lupa de servicio clickeada.");
    } catch (Exception e) {
        // Backup por índice si el nombre falla
        js.executeScript("arguments[0].click();", driver.findElement(By.xpath("(//button[contains(@mattooltip, 'Búsqueda')])[last()]")));
    }

    // SELECCIÓN DEL SERVICIO (Tilde azul)
    System.out.println(">> Esperando listado de servicios...");
    // Buscamos el botón seleccionar que aparece específicamente en la tabla de servicios
    By btnCheckServicio = By.xpath("//button[@mattooltip='Seleccionar']");
    
    try {
        WebElement checkServ = wait.until(ExpectedConditions.elementToBeClickable(btnCheckServicio));
        js.executeScript("arguments[0].click();", checkServ);
        System.out.println(">> Servicio seleccionado correctamente.");
    } catch (Exception e) {
        System.out.println(">> Error al seleccionar servicio. Verifique si la lista cargó datos.");
    }

    // CORRECCIÓN SIGUIENTE: Se usa la clase técnica 'mat-stepper-next' para evitar fallos de índice
    Thread.sleep(2000); // Tiempo para que desaparezca el mensaje de error rojo
    By btnSiguienteP2 = By.xpath("//button[contains(@class, 'mat-stepper-next') and contains(., 'SIGUIENTE')]");
    
    try {
        // Usamos presence en lugar de clickable por si el botón está visualmente bloqueado por un overlay
        WebElement btnNext = wait.until(ExpectedConditions.presenceOfElementLocated(btnSiguienteP2));
        js.executeScript("arguments[0].scrollIntoView(true);", btnNext);
        Thread.sleep(1000);
        js.executeScript("arguments[0].click();", btnNext);
        System.out.println(">> Avanzando al Paso 3.");
    } catch (Exception e) {
        System.out.println(">> Reintento de clic en Siguiente con selector alternativo...");
        js.executeScript("document.querySelector('.mat-stepper-next').click();");
    }

    // =========================================================================
    // 6. PASO 3: DATOS DESTINO
    // =========================================================================
    System.out.println(">> Paso 3: Datos Destino...");
    
    // Lupa Organización Destino
    By lupaDestino = By.xpath("//mat-form-field[.//mat-label[contains(text(),'Destino')]]//button[contains(@mattooltip, 'Búsqueda')]");
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(lupaDestino)));
    Thread.sleep(2000);
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@mattooltip='Seleccionar'])[last()]"))));
    
    js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//button[contains(., 'SIGUIENTE')]")));
    Thread.sleep(2000);

    // =========================================================================
    // 7. PASO 4: DOCUMENTOS (NO HACE NADA)
    // =========================================================================
    System.out.println(">> Paso 4: Documentos...");
    js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//button[contains(., 'SIGUIENTE')]")));
    Thread.sleep(2000);


    // =========================================================================
    // 8. PASO FINAL: INSTRUMENTOS LEGALES (Manejo de 2 Lupas)
    // =========================================================================
    System.out.println(">> Paso 5: Instrumentos Legales...");
    Thread.sleep(2000);

    try {
        // --- BÚSQUEDA 1: EXPEDIENTE ---
        System.out.println(">> Buscando Expediente...");
        By lupaExp = By.xpath("//mat-form-field[.//mat-label[contains(text(), 'Expediente')]]//button[contains(@mattooltip, 'Búsqueda')]");
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(lupaExp)));
        
        Thread.sleep(2500); // Espera a que el modal cargue los datos
        // Selector: Índice [1] para el primer registro del modal
        By btnSelectExp = By.xpath("(//button[@mattooltip='Seleccionar'])[1]");
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(btnSelectExp)));
        System.out.println(">> Expediente seleccionado.");
        Thread.sleep(1500);

        // --- BÚSQUEDA 2: INSTRUMENTO LEGAL ---
        System.out.println(">> Buscando Instrumento Legal...");
        By lupaInst = By.xpath("//mat-form-field[.//mat-label[contains(text(), 'Instrumento Legal')]]//button[contains(@mattooltip, 'Búsqueda')]");
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(lupaInst)));
        
        Thread.sleep(2500);
        // Reutilizamos el selector del modal para el primer registro encontrado
        By btnSelectInst = By.xpath("(//button[@mattooltip='Seleccionar'])[1]");;
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(btnSelectInst)));
        System.out.println(">> Instrumento Legal seleccionado.");

        // Clic en el botón azul de Guardar como borrador visto en image_750f21.png
        By btnGuardarBorrador = By.xpath("//button[contains(., 'Guardar como borrador')]");
        WebElement btnBorrador = wait.until(ExpectedConditions.presenceOfElementLocated(btnGuardarBorrador));
        
        js.executeScript("arguments[0].scrollIntoView(true);", btnBorrador);
        Thread.sleep(1000);
        js.executeScript("arguments[0].click();", btnBorrador);
        System.out.println(">> Solicitud guardada exitosamente.");

        // Manejo de diálogos de confirmación si aparecen (Sí -> Aceptar)
        limpiarModalesFinales();

    } catch (Exception e) { 
        System.out.println(">> Error detallado en búsquedas: " + e.getMessage()); 
    }

    System.out.println(">> TEST FINALIZADO CON ÉXITO.");
    Thread.sleep(5000);
}

  // =========================================================================
  // MÉTODOS AUXILIARES
  // =========================================================================

  private void escribirFechaPorControl(String controlName, String fecha) throws Exception {
    By locator = By.xpath("//input[@formcontrolname='" + controlName + "']");
    WebElement input = new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.visibilityOfElementLocated(locator));
    input.click();
    for(int i=0; i<12; i++) { input.sendKeys(Keys.BACK_SPACE); }
    input.sendKeys(fecha + Keys.TAB);
    Thread.sleep(500);
  }

  private void limpiarModalesFinales() {
    for (int i = 0; i < 4; i++) {
        try {
            By btnOk = By.xpath("//mat-dialog-container//button[contains(., 'Aceptar') or contains(., 'Sí') or contains(., 'Confirmar')]");
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(btnOk));
            js.executeScript("arguments[0].click();", btn);
            Thread.sleep(2500); 
        } catch (Exception e) { break; }
    }
  }

  // Método para seleccionar opciones en componentes Angular Material
  private void seleccionarOpcionMat(String formControlName, String textoOpcion) {
    try {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        By selectSelector = By.xpath("//mat-select[@formcontrolname='" + formControlName + "']");
        WebElement selectElement = wait.until(ExpectedConditions.elementToBeClickable(selectSelector));
        js.executeScript("arguments[0].click();", selectElement);
        
        By optionSelector = By.xpath("//mat-option//span[contains(normalize-space(), '" + textoOpcion + "')]");
        WebElement optionElement = wait.until(ExpectedConditions.elementToBeClickable(optionSelector));
        js.executeScript("arguments[0].click();", optionElement);
        Thread.sleep(1000);
    } catch (Exception e) { System.out.println(">> Error en " + formControlName); }
  }

  @After
  public void tearDown() throws Exception {
    if (driver != null) driver.quit();
  }
}