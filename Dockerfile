# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the local maven repository to the container
# This is crucial because swisseph is not in public repos
COPY .m2/repository /root/.m2/repository

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
# We use -o (offline) to prefer local repo, but since we only copied swisseph, 
# we might need to let it download others. 
# Better approach: install the local jar explicitly if we had the jar file, 
# but copying the repo structure is easier given the previous steps.
# However, copying the whole .m2 from host might be too much or not possible if not in context.
# Let's assume the user runs docker build from project root.
# We need to make sure the swisseph jar is available.
# Since we installed it to ~/.m2/repository/org/himalay/swisseph/2.01.00-01/swisseph-2.01.00-01.jar
# We should probably copy just that part or install it manually in the dockerfile.

# Let's try a more robust approach: Copy the swisseph jar from the project "swisseph" dir (where we built it)
# and install it in the container.
# The user has the source in `swisseph` folder.
# We can rebuild it or just install the jar if we can find it.
# The `swisseph` folder is in the project root.

# Let's build swisseph from source inside the container to be safe and self-contained.
# But swisseph repo structure was a bit weird.
# Easier: Copy the local repo specific artifact.
# But we can't easily access ~/.m2 from docker build context unless we copy it to the project dir first.

# Alternative: We already have the source in `swisseph` directory in the project root.
# Let's build it first.

COPY swisseph ./swisseph
RUN cd swisseph && mvn install

# Now build the main app
RUN mvn clean package

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/purnima-1.0.0-jar-with-dependencies.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
