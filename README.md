CI/CD Pipeline para MEL (Mapas de Eventos Locales)
==================================================

Este repositorio utiliza un pipeline de CI/CD configurado con GitHub Actions para automatizar la construcción, prueba y despliegue de la aplicación MEL.

Descripción del Pipeline
------------------------

El pipeline de CI/CD para MEL está diseñado para asegurar que el código en la rama uat sea probado, construido y desplegado automáticamente en Firebase App Distribution, garantizando que las nuevas funcionalidades y correcciones de errores sean distribuidas eficientemente para pruebas internas.

Configuración del Pipeline
--------------------------

El archivo de configuración del pipeline se encuentra en la siguiente ruta dentro del repositorio: .github/workflows/ci-cd-pipeline.yml.

### Activadores del Pipeline

*   **Ramas Monitoreadas:**
    
    *   El pipeline se ejecuta automáticamente cuando se hace un push o se crea un pull\_request en la rama uat.
        
*   **Motivo:**
    
    *   Esto asegura que cualquier cambio en esta rama sea verificado y preparado antes de ser fusionado en la rama principal, permitiendo un flujo de trabajo ágil y seguro.
        

### Jobs del Pipeline

#### 1\. Build

*   **Entorno:**
    
    *   runs-on: ubuntu-latest
        
    *   Se utiliza un entorno Ubuntu para asegurar la compatibilidad y estabilidad en el proceso de construcción.
        
*   **Pasos:**
    
    1.  **Checkout del Código:**
        
        *   actions/checkout@v2
            
        *   Clona el código del repositorio en el entorno de ejecución.
            
        *   **Motivo:** Permite trabajar con la última versión del código en la rama uat.
            
    2.  **Configuración de JDK 11:**
        
        *   actions/setup-java@v2
            
        *   Establece JDK 11 como el entorno de Java necesario para la construcción del proyecto.
            
        *   **Motivo:** JDK 11 es requerido para compilar y ejecutar la aplicación Android.
            
    3.  **Cache de Dependencias de Gradle:**
        
        *   actions/cache@v2
            
        *   Almacena en caché las dependencias de Gradle para acelerar futuras construcciones.
            
        *   **Motivo:** Reducir el tiempo de construcción al reutilizar dependencias previamente descargadas.
            
    4.  **Permisos de Ejecución para gradlew:**
        
        *   chmod +x gradlew
            
        *   Asegura que el script de Gradle tiene los permisos necesarios para ser ejecutado.
            
        *   **Motivo:** Evitar errores de permisos durante la construcción.
            
    5.  **Construcción con Gradle:**
        
        *   ./gradlew build
            
        *   Compila la aplicación utilizando Gradle.
            
        *   **Motivo:** Generar los artefactos de construcción necesarios para el despliegue.
            
    6.  **Ejecución de Pruebas Unitarias:**
        
        *   ./gradlew test
            
        *   Ejecuta las pruebas unitarias definidas en el proyecto.
            
        *   **Motivo:** Validar que las nuevas modificaciones no introduzcan errores en la base de código.
            
    7.  **Subida de Resultados de Pruebas:**
        
        *   actions/upload-artifact@v2
            
        *   Guarda los resultados de las pruebas como artefactos en GitHub Actions.
            
        *   **Motivo:** Facilitar la revisión de los resultados de las pruebas después de la ejecución del pipeline.
            

#### 2\. Deploy

*   **Entorno:**
    
    *   runs-on: ubuntu-latest
        
    *   Se ejecuta en un entorno Ubuntu, asegurando consistencia en el proceso de despliegue.
        
*   **Dependencia:**
    
    *   needs: build
        
    *   El job de despliegue depende del job de construcción exitoso.
        
*   **Condición:**
    
    *   if: github.ref == 'refs/heads/uat'
        
    *   Se asegura que el despliegue solo ocurra si el código proviene de la rama uat.
        
*   **Pasos:**
    
    1.  **Checkout del Código:**
        
        *   actions/checkout@v2
            
        *   Vuelve a clonar el código para asegurar que se despliega la misma versión que pasó las pruebas.
            
    2.  **Configuración de JDK 11:**
        
        *   actions/setup-java@v2
            
        *   Establece JDK 11 como el entorno de Java necesario para el despliegue.
            
    3.  **Despliegue a Firebase App Distribution:**
        
        *   ./gradlew appDistributionUploadRelease
            
        *   Despliega la aplicación a Firebase App Distribution utilizando el token de Firebase.
            
        *   **Motivo:** Facilitar la distribución de la aplicación a los testers internos.
            

### Seguridad y Gestión de Credenciales

*   **Firebase Token:**
    
    *   El token de Firebase utilizado para el despliegue está almacenado de forma segura en los secretos del repositorio (secrets.FIREBASE\_TOKEN), garantizando que las credenciales sensibles no sean expuestas.
