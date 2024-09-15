# Passo 1: Utilize a imagem do OpenJDK 17 como base
FROM openjdk:17

# Passo 2: Defina a variável de ambiente para armazenar o diretório da aplicação
ENV APP_HOME=/usr/app

# Passo 3: Crie o diretório da aplicação
RUN mkdir -p $APP_HOME

# Passo 4: Defina o diretório de trabalho para o diretório da aplicação
WORKDIR $APP_HOME

# Passo 5: Copie o arquivo JAR construído para o diretório da aplicação
COPY target/connect_city_api-0.0.1-SNAPSHOT.jar $APP_HOME/spring-app.jar

# Passo 5.1: Copie o certificado SSL (keystore.p12) para o diretório da aplicação
COPY keystore.p12 $APP_HOME

# Passo 6: Exponha a porta 8443 para o tráfego HTTPS
EXPOSE 8443

# Passo 7: Defina o comando para iniciar a aplicação
CMD ["java", "-jar", "spring-app.jar"]
