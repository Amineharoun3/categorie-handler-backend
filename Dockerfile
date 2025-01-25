# Utiliser une image de base légère pour Java
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier les fichiers nécessaires pour construire l'application
COPY . .

# Exécuter Maven pour packager l'application (remplacez cette commande si un jar est déjà généré)
RUN ./mvnw package -DskipTests

# Exposer le port 8080
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java", "-jar", "target/categorie_handler-0.0.1-SNAPSHOT.jar"]
