For application.properties

server.port = 8080 #O el puerto que elijas
spring.datasource.url=jdbc:mysql://localhost:3306/juego_dados?createDatabaseIfNotExist=true #3306 o puerto configurado en MySQL
spring.datasource.username="tu_nombre_de_usuario" #El configurado en MySQL
spring.datasource.password="tu_contraseña" #La configurada en MySQL
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQLDialect
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JWT
jwt.secret=#La clave de encriptacion generada en pasos anteriores
jwt.expiration=86400000 #O el tiempo de expiración que quieras, representado en milisegundos
