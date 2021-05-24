# MeLi.Magneto

El proyecto está desarrollado con java, hosteado en AWS utilizando DynamoDB para la base de datos.

## Pre-requisitos
Cuenta en AWS para:
- base de Datos DynamoDB
  - crear una tabla "MutantDNA" con una columna partition key de tipo string llamada "dna"
  - crear una tabla "HumanDNA" con una columna partition key de tipo string llamada "dna"
- crear un usuario con acceso a la base de datos y generar access key y access secret.
- servidor linux EC2
  - agregar variables de entorno:
    - AWS_ACCESS_KEY_ID: valor del access key id generado para el usuario de AWS
    - AWS_SECRET_ACCESS_KEY: valor del secret generado para el usuario de AWS

## Build del proyecto e instalación
- Ejecutar el comando: "mvn package" en la raíz del proyecto.
- copiar el archivo rest-service-0.0.1-SNAPSHOT.jar generado en la carpeta target al servidor linux
- ejecutar el comando java -jar rest-service-0.0.1-SNAPSHOT.jar


## Prueba de la API
- para probar la api se debe verificar la url del servidor linux donde se instanció la API y utilizar el puerto 8080
- existe una instancia de la aplicación en la url: http://ec2-3-137-157-170.us-east-2.compute.amazonaws.com:8080

los endpoints de la api son:
POST - /mutant
http://ec2-3-137-157-170.us-east-2.compute.amazonaws.com:8080/mutant

GET - /stats
http://ec2-3-137-157-170.us-east-2.compute.amazonaws.com:8080/stats
