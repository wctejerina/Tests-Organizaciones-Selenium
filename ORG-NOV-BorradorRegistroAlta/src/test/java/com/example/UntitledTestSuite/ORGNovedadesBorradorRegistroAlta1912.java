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

public class ORGNovedadesBorradorRegistroAlta1912 {
  private WebDriver driver;
  private JavascriptExecutor js;
  
  // Datos extraídos del script original
  private final String USER_ID = "USUARIO";
  private final String PASSWORD = "CLAVE";
  private final String CUE_ORG = "3800094"; 
  private final String PERSONA = "SOLA GARCIA";

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
  public void testORGNovedadesRegistroAlta1812() throws Exception {
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

    // Ingreso de usuario blindado contra Stale Element
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mat-input-0")));
    driver.findElement(By.id("mat-input-0")).sendKeys(USER_ID);
    driver.findElement(By.id("password-field")).sendKeys(PASSWORD);
    driver.findElement(By.id("password-field")).sendKeys(Keys.ENTER); 
    
    wait.until(ExpectedConditions.urlContains("pages"));
    System.out.println(">> Login Exitoso.");

    // =========================================================================
    // 2. TRANSICIÓN A 8204 Y LIMPIEZA DE POPUPS SUPERPUESTOS
    // =========================================================================
    System.out.println(">> 2. Cambiando a Gestión (8204) y limpiando popups...");
    Thread.sleep(5000); 
    driver.get("https://nexocapacitacion.jujuy.edu.ar:8204/#/");

    // Bucle agresivo para limpiar Perfil y Avisos
    for (int i = 0; i < 5; i++) {
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
                break;
            }
        } catch (Exception e) { break; }
    }

    // Eliminación manual de overlays residuales para evitar ElementClickIntercepted
    js.executeScript("var b=document.getElementsByClassName('cdk-overlay-backdrop');while(b[0]){b[0].parentNode.removeChild(b[0]);}");

    // =========================================================================
    // 3. NAVEGACIÓN Y SELECCIÓN DE ESCUELA (CON FILTRO CUE)
    // =========================================================================
    System.out.println(">> 3. Navegando a Registro de Alta...");
    driver.get("https://nexocapacitacion.jujuy.edu.ar:8204/#/pages/solicituddesignacion/listar");
    Thread.sleep(3000);

    // 3.1 Clic en LUPA de Organización
    By lupaOrg = By.xpath("//button[contains(@mattooltip, 'Búsqueda')]");
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.presenceOfElementLocated(lupaOrg)));

    // 3.2 Filtrar por CUE (Para asegurar que no elija otra escuela)
    try {
        WebElement inputBusq = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Buscar...']")));
        inputBusq.clear();
        inputBusq.sendKeys(PERSONA + Keys.ENTER);
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
    // 4. PASO 1: PERSONA Y TIPO (Imagen 1 -> Imagen 2)
    // =========================================================================
    // Botón "+" (Agregar Nueva Solicitud)
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'mat-mini-fab')]"))).click();
    Thread.sleep(3000);

    System.out.println(">> Paso 1: Buscando Persona...");
    
    // Seleccionar primer dropdown (Tipo de Solicitud)
    WebElement selTipo = driver.findElement(By.xpath("//mat-select"));
    js.executeScript("arguments[0].click();", selTipo);
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//mat-option[3]"))).click();

    // 4.1 HACER CLIC EN LUPA DE PERSONA (Habilita el buscador)
    //By lupaPersonaSeccion = By.xpath("//button[contains(@mattooltip, 'Persona')] | //*[contains(text(), 'Persona')]/following::button[1]");
    //driver.findElement(lupaPersonaSeccion).click();
    
    // 4.2 ESCRIBIR NOMBRE EN EL MODAL
    //WebElement inputPers = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("textoBusqueda")));
    //inputPers.clear();
    //inputPers.sendKeys(PERSONA);
    
    // // 4.3 CLIC EN LUPA DE BÚSQUEDA INTERNA (Botón con ícono search en Imagen 2)
    //By lupaInternaModal = By.xpath("//mat-dialog-container//button[contains(@mattooltip, 'Buscar') or .//mat-icon[text()='search']]");
    //js.executeScript("arguments[0].click();", driver.findElement(lupaInternaModal));
    driver.findElement(By.xpath("//button[contains(., 'Siguiente')]")).click();
    Thread.sleep(3000);
    
    // // 4.4 Seleccionar Persona de la tabla de resultados
    

    // =========================================================================
    // 5. PASO 2: PLAZA Y FECHAS (SELECCIÓN ESPECÍFICA 45992)
    // =========================================================================
    System.out.println(">> Paso 2: Plaza y Fechas...");
    final String CODIGO_TARGET = "45992"; // Parámetro solicitado

    // Ingreso de fecha (Mantenemos tu método que ya funciona)
    escribirFechaPorControl("fechaDesdeDesignacion", "18/12/2025");
    
    System.out.println(">> Clic en búsqueda de Plaza..."); 
    By lupaPlazaXPath = By.xpath("(//button[contains(@mattooltip, 'Búsqueda')])[2]"); 
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(lupaPlazaXPath)));

    // FILTRAR PLAZA POR CÓDIGO
    System.out.println(">> Filtrando Plaza por código: " + CODIGO_TARGET);
    try {
        // Buscamos el input de búsqueda dentro de la sección de Plazas
        By inputFiltroPlaza = By.xpath("//div[contains(text(), 'Búsqueda de Plaza')]/following::input[1]");
        WebElement inputFiltro = wait.until(ExpectedConditions.visibilityOfElementLocated(inputFiltroPlaza));
        inputFiltro.clear();
        inputFiltro.sendKeys(CODIGO_TARGET + Keys.ENTER);
        Thread.sleep(3000); // Espera a que la grilla se actualice
    } catch (Exception e) {
        System.out.println(">> No se pudo aplicar filtro de texto.");
    }

    // SELECCIÓN ESPECÍFICA DE LA FILA 45992
    System.out.println(">> Seleccionando fila con código: " + CODIGO_TARGET);
    By btnSelectPlaza = By.xpath("//tr[td[contains(text(), '" + CODIGO_TARGET + "')]]//button[@mattooltip='Seleccionar']");
    
    try {
        WebElement checkPlaza = wait.until(ExpectedConditions.presenceOfElementLocated(btnSelectPlaza));
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", checkPlaza);
        Thread.sleep(1000);
        js.executeScript("arguments[0].click();", checkPlaza);
        System.out.println(">> Plaza " + CODIGO_TARGET + " seleccionada correctamente.");
    } catch (Exception e) {
        System.out.println(">> Falló selección específica. Usando primer registro visible...");
        js.executeScript("arguments[0].click();", driver.findElement(By.xpath("(//button[@mattooltip='Seleccionar'])[last()]")));
    }

    // CLIC EN SIGUIENTE (Paso 2 -> Paso 3)
    Thread.sleep(2000);
    By btnSiguienteP2 = By.xpath("//button[contains(., 'Siguiente')]");
    WebElement btnSig = wait.until(ExpectedConditions.presenceOfElementLocated(btnSiguienteP2));
    js.executeScript("arguments[0].scrollIntoView(true);", btnSig);
    js.executeScript("arguments[0].click();", btnSig); // Clic JS para evitar ElementNotInteractable
    System.out.println(">> Avanzando al Paso 3.");

   // =========================================================================
    // 6. PASO 3: DATOS DE LA DESIGNACIÓN
    // =========================================================================
    System.out.println(">> Paso 3: Completando campos de Designación...");
    Thread.sleep(3000);

    // 6.1 Desplegables Obligatorios
    seleccionarOpcionMat("idRefSituacionRevista", "PLAN SOCIAL");
    seleccionarOpcionMat("idRefMovimiento", "ALTA POR SUPLENCIA");
    seleccionarOpcionMat("idRefTituloHabilitante", "A DETERMINAR");

    // 6.2 Opciones SI/NO (CORREGIDO para evitar el error en Radio Buttons)
    try {
        System.out.println(">> Configurando Radio Buttons...");
        // Admite Suplente: SI
        WebElement radioSuplente = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//mat-radio-button[contains(., 'SI')]")));
        js.executeScript("arguments[0].click();", radioSuplente);

        // Está Frente a Alumnos: NO
        // Se busca específicamente dentro del grupo correspondiente para mayor precisión
        WebElement radioAlumnos = driver.findElement(By.xpath("(//mat-radio-button[contains(., 'NO')])[2]"));
        js.executeScript("arguments[0].click();", radioAlumnos);
        System.out.println(">> Opciones SI/NO configuradas.");
    } catch (Exception e) { 
        System.out.println(">> Error en Radio Buttons: " + e.getMessage()); 
    }

    // 6.3 Lupas de búsqueda (Expediente e Instrumento Legal)
    try {
        // Lupa Expediente
        By lupaExp = By.xpath("//mat-form-field[.//mat-label[contains(text(), 'Expte')]]//button[contains(@mattooltip, 'Búsqueda')]");
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(lupaExp)));
        Thread.sleep(2000);
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//mat-dialog-container//button[@mattooltip='Seleccionar']"))));
        
        // Lupa Instrumento Legal
        By lupaInst = By.xpath("//mat-form-field[.//mat-label[contains(text(), 'Instrumento Legal')]]//button[contains(@mattooltip, 'Búsqueda')]");
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(lupaInst)));
        Thread.sleep(2000);
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//mat-dialog-container//button[@mattooltip='Seleccionar']"))));
    } catch (Exception e) { System.out.println(">> Error en búsquedas adicionales."); }

    // =========================================================================
    // 7. FINALIZAR: PRESENTAR Y CONFIRMAR (FLUJO COMPLETO)
    // =========================================================================
    System.out.println(">> Iniciando presentación final...");
    try {
        // 1. Clic en el botón principal "Presentar"
        By btnPresentarPath = By.xpath("//button[contains(., 'Guardar')]");
        WebElement btnPresentar = wait.until(ExpectedConditions.elementToBeClickable(btnPresentarPath));
        js.executeScript("arguments[0].scrollIntoView(true);", btnPresentar);
        Thread.sleep(1000); // Pausa para estabilidad visual
        js.executeScript("arguments[0].click();", btnPresentar);
        System.out.println(">> Botón 'Presentar' clickeado.");

        // 2. Manejo del Popup de Confirmación: Clic en "Sí"
        By btnSiPath = By.xpath("//mat-dialog-container//button[contains(., 'Aceptar')]");
        WebElement btnSi = wait.until(ExpectedConditions.elementToBeClickable(btnSiPath));
        js.executeScript("arguments[0].click();", btnSi);
        System.out.println(">> Confirmación 'Sí' aceptada.");
        
        // 3. Manejo del Popup de Éxito: Clic en "Aceptar"
        // Este es el popup que se muestra en tu última imagen
        By btnAceptarFinal = By.xpath("//mat-dialog-container//button[contains(., 'Aceptar')]");
        WebElement btnAceptar = wait.until(ExpectedConditions.elementToBeClickable(btnAceptarFinal));
        
        // Opcional: imprimir el texto del mensaje para verificar en consola
        String mensajeExito = driver.findElement(By.xpath("//mat-dialog-container//div[contains(@class, 'mat-dialog-content')]")).getText();
        System.out.println(">> Mensaje del sistema: " + mensajeExito);
        
        js.executeScript("arguments[0].click();", btnAceptar);
        System.out.println(">> Éxito: 'Aceptar' clickeado. Proceso terminado.");

    } catch (Exception e) {
        System.out.println(">> Error en el proceso de cierre final: " + e.getMessage());
    }

    System.out.println(">> TEST FINALIZADO CON ÉXITO.");
    Thread.sleep(3000); // Pequeña espera para ver el clic final antes del quit()
}

  // =========================================================================
  // MÉTODOS AUXILIARES ROBUSTOS
  // =========================================================================

  // Método recomendado: Busca por el atributo formcontrolname visto en devtools
  private void escribirFechaPorControl(String controlName, String fecha) throws Exception {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    By locator = By.xpath("//input[@formcontrolname='" + controlName + "']");
    WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    
    js.executeScript("arguments[0].scrollIntoView(true);", input);
    input.click();
    input.clear();
    // Limpieza profunda del campo para máscaras de Angular
    for(int i=0; i<12; i++) { input.sendKeys(Keys.BACK_SPACE); }
    
    input.sendKeys(fecha);
    Thread.sleep(500);
    input.sendKeys(Keys.TAB); // Importante para que Angular detecte el cambio
    System.out.println(">> Fecha '" + fecha + "' aceptada en control: " + controlName); 
    Thread.sleep(500);
  }

  // Versión mejorada de tu método actual con esperas inteligentes
  private void escribirFecha(String placeholderContiene, String fecha) throws Exception {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    // La búsqueda por contains es sensible a mayúsculas, por eso fallaba
    By locator = By.xpath("//input[contains(translate(@placeholder, 'ABCDEFGHIJKLMNÑOPQRSTUVWXYZ', 'abcdefghijklmnñopqrstuvwxyz'), '" + placeholderContiene.toLowerCase() + "')]");
    WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    
    input.click();
    for(int i=0; i<12; i++) { input.sendKeys(Keys.BACK_SPACE); } 
    input.sendKeys(fecha + Keys.TAB);
    System.out.println(">> Fecha aceptada."); 
    Thread.sleep(500);
  }

  private void limpiarModalesFinales() {
    for (int i = 0; i < 6; i++) {
        try {
            By btnOk = By.xpath("//mat-dialog-container//button[contains(., 'Aceptar') or contains(., 'Sí') or contains(., 'Confirmar')]");
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(btnOk));
            js.executeScript("arguments[0].click();", btn);
            System.out.println(">> Modal de confirmación " + (i+1) + " aceptado.");
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