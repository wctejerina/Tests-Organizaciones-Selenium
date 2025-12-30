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
import java.util.Random;

public class ORGOrganizEdificiosBusqAvanzada3012 {
  private WebDriver driver;
  private JavascriptExecutor js;
  private WebDriverWait wait;
  
  private final String USER_ID = "USUARIO";
  private final String PASSWORD = "CLAVE";

  @Before
  public void setUp() throws Exception {
    WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--remote-allow-origins=*", "start-maximized"); 
    driver = new ChromeDriver(options);
    wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    js = (JavascriptExecutor) driver;
  }

  @Test
  public void testORGNovedadesPresentarBorrador2912() throws Exception {
    wait = new WebDriverWait(driver, Duration.ofSeconds(40));

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

    System.out.println(">> Accediendo a Organizaciones -> Edificios...");
    Thread.sleep(5000);
    driver.get("https://nexocapacitacion.jujuy.edu.ar:4303/#/pages/edificio/listar");
    limpiarModales();
    try {
        By btnAceptarIni = By.xpath("//mat-dialog-container//button[contains(., 'Aceptar')]");
        wait.until(ExpectedConditions.elementToBeClickable(btnAceptarIni)).click();
        Thread.sleep(1000); 
    } catch (Exception e) {}

    // =========================================================================
    // 2. BÚSQUEDA SIMPLE ALEATORIA
    // =========================================================================
    String[] casosSimples = {"AR", "1234", "$%#&", "//", "+++"};
    for (String caso : casosSimples) {
        System.out.println(">> Probando búsqueda simple con: " + caso);
        WebElement inputBusq = wait.until(ExpectedConditions.elementToBeClickable(By.id("textoBusqueda")));
        
        inputBusq.sendKeys(caso + Keys.ENTER);
        Thread.sleep(2000); // Tiempo para procesar la grilla
        inputBusq.clear();
    }
    System.out.println(">> Iniciando 10 pruebas de búsqueda simple aleatoria...");

    // =========================================================================
    // 2.1 BÚSQUEDA SIMPLE ALEATORIA (10 INTENTOS)
    // =========================================================================
    for (int i = 1; i <= 10; i++) {
        String textoPrueba = generarTextoAleatorio(8); // Genera 8 caracteres mezclados
        System.out.println(">> Intento " + i + ": Buscando '" + textoPrueba + "'");
        
        WebElement inputBusq = wait.until(ExpectedConditions.elementToBeClickable(By.id("textoBusqueda")));
        inputBusq.sendKeys(textoPrueba + Keys.ENTER);
        Thread.sleep(2000); 
        inputBusq.clear();
        // Espera a que la grilla se actualice (ya sea con datos o con "Sin Información") 
    }

    // LIMPIEZA PREVIA A BÚSQUEDA AVANZADA
    System.out.println(">> Limpiando barra de búsqueda simple para evitar interferencias...");
    WebElement barSimple = driver.findElement(By.id("textoBusqueda"));
    barSimple.clear();
    barSimple.sendKeys(Keys.ENTER);
    Thread.sleep(2000);

    // =========================================================================
    // 3. BÚSQUEDA AVANZADA
    // =========================================================================
    
    // CASO A: Probar Código (Bloquea el resto)   
    System.out.println(">> Iniciando 10 pruebas de búsqueda para codigo...");
    for (int i = 1; i <= 5; i++) {
        String numeroPrueba = generarNumerosAleatorios(4); // Genera numeros string de 4 caracteres
        System.out.println(">> Intento " + i + ": Buscando '" + numeroPrueba + "'");
        abrirBusquedaAvanzada();
        System.out.println(">> Caso Avanzado: Código Único");
        ingresarTextoAvanzado("Código", numeroPrueba);
        clickBuscarAvanzado();
    }

    // CASO B: Combinación Región + Zona
    abrirBusquedaAvanzada();
    System.out.println(">> Caso Avanzado: Región VII + Zona 5");
    seleccionarOpcionMatAvanzada("Región", "REGION VII");
    seleccionarOpcionMatAvanzada("Zona", "ZONA 5");
    clickBuscarAvanzado();
    Thread.sleep(3000);

    // CASO C: Departamento y Localidad
    abrirBusquedaAvanzada();
    System.out.println(">> Caso Avanzado: Depto/Localidad (Recursivo)");
    seleccionarOpcionPorIndice("Departamento", 3); // Ejemplo: 3er depto
    Thread.sleep(1000); // Esperar carga de Localidad
    seleccionarOpcionPorIndice("Localidad", 3);    // 3ra localidad
    clickBuscarAvanzado();
    Thread.sleep(3000);
  }

  // =========================================================================
  // MÉTODOS DE APOYO
  // =========================================================================

  // Generador de caracteres mixtos (Letras, Números, Símbolos)
  private String generarNumerosAleatorios(int longitud) {
    String caracteres = "0123456789";
    StringBuilder sb = new StringBuilder();
    Random rnd = new Random();
    while (sb.length() < longitud) {
        sb.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
    }
    return sb.toString();
  }

  private String generarTextoAleatorio(int longitud) {
    String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    StringBuilder sb = new StringBuilder();
    Random rnd = new Random();
    while (sb.length() < longitud) {
        sb.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
    }
    return sb.toString();
  }

  private void clickBuscarAvanzado() throws Exception {
    System.out.println(">> Pulsando botón 'Buscar' del modal avanzado...");   
    // Selector ultra-preciso: Botón Buscar DENTRO del modal de edificios
    By btnBuscarModal = By.xpath("//app-modal-search-edificio//mat-dialog-actions//button[contains(., 'Buscar')]");
    wait.until(ExpectedConditions.elementToBeClickable(btnBuscarModal)).click();
    
    // // try {
    // //     WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnBuscarModal));
    // //     // Scroll al botón por si el modal es muy largo
    // //     js.executeScript("arguments[0].scrollIntoView(true);", btn);
    // //     Thread.sleep(500);
    // //     js.executeScript("arguments[0].click();", btn);
    // //     System.out.println(">> Búsqueda avanzada ejecutada exitosamente.");
    // // } catch (Exception e) {
    // //     System.out.println(">> Error al clickear 'Buscar' en modal: " + e.getMessage());
    // //     // Fallback agresivo por índice dentro del modal
    // //     js.executeScript("document.querySelector('app-modal-search-edificio mat-dialog-actions button:last-child').click();");
    // // }
    
    // // Esperamos a que el modal se cierre antes de continuar
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.tagName("app-modal-search-edificio")));
    Thread.sleep(2000); 
  }

  // // private void clickBuscarAvanzado() throws Exception {
  // //   By btnBuscar = By.xpath("//mat-dialog-actions//button[contains(., 'Buscar')]");
  // //   js.executeScript("arguments[0].click();", driver.findElement(btnBuscar));
  // //   Thread.sleep(2000);
  // // }

  private void abrirBusquedaAvanzada() throws Exception {
    Thread.sleep(2000); 
    // Localizamos el botón de apertura en la página principal
    By btnAvanzada = By.xpath("//button[contains(@mattooltip, 'Búsqueda Avanzada')]");
    js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(btnAvanzada)));
    
    // ESPERA CRÍTICA: Asegurar que el componente del modal esté cargado
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("app-modal-search-edificio")));
    System.out.println(">> Modal 'Opciones de Búsqueda' detectado.");
  }

  private void ingresarTextoAvanzado(String label, String texto) {
    By input = By.xpath("//mat-dialog-container//mat-form-field[.//mat-label[contains(text(), '" + label + "')]]//input");
    WebElement element = driver.findElement(input);
    element.clear();
    element.sendKeys(texto);
  }

  private void seleccionarOpcionMatAvanzada(String label, String textoOpcion) {
    try {
        By sel = By.xpath("//mat-dialog-container//mat-form-field[.//mat-label[contains(text(), '" + label + "')]]//mat-select");
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(sel)));
        Thread.sleep(500);
        By opt = By.xpath("//mat-option//span[contains(text(), '" + textoOpcion + "')]");
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(opt)));
    } catch (Exception e) { System.out.println(">> Error seleccionando " + label); }
  }

  private void seleccionarOpcionPorIndice(String label, int indice) {
    try {
        By sel = By.xpath("//mat-dialog-container//mat-form-field[.//mat-label[contains(text(), '" + label + "')]]//mat-select");
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(sel)));
        Thread.sleep(500);
        By opt = By.xpath("(//mat-option)[" + indice + "]");
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(opt)));
    } catch (Exception e) {}
  }
  
  private void limpiarModales() {
    try {
        By btnAceptar = By.xpath("//button[contains(., 'Aceptar')]");
        if (driver.findElements(btnAceptar).size() > 0) {
            js.executeScript("arguments[0].click();", driver.findElement(btnAceptar));
        }
    } catch (Exception e) {}
    js.executeScript("var b=document.getElementsByClassName('cdk-overlay-backdrop');while(b[0]){b[0].parentNode.removeChild(b[0]);}");
  }

  @After
  public void tearDown() { if (driver != null) driver.quit(); }
}