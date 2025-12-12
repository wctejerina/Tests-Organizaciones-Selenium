# Automatización de Pruebas - Modulo Organizaciones

Este proyecto contiene scripts automatizados desarrollados en Java con Selenium y Maven.

## Tecnologías
* Java 25
* Maven
* Selenium 4.20
* WebDriverManager (Gestión automática de drivers)

## Pasos
* Descargar JDK 25
* Descargar Selenium con pip install (requiere tener Node JS)
* Descargar Maven, agregar la raiz y /bin a las variables del sistema

## Cómo ejecutar
Para correr la prueba, descargar la carpeta y cambiar USUARIO y CONTRASEÑA dentro del codigo .java. Ingresar a la  carpeta (dentro esta /src y pom.xml) y a usar el siguiente comando en la terminal:

mvn clean test -Dtest=ORGTEST10412 (Dependiendo del nombre de fichero .java)
