cat > README.md << 'EOF'
# AsteroidsFX

A component-based Asteroids game built with Java 25, JPMS, ServiceLoader, and Spring Framework.

## Requirements
- JDK 25
- Maven 3.9+

## Run
```bash
mvn install -DskipTests
cd core && mvn exec:exec
```

## Microservices (optional)
```bash
cd services/scoring-service && mvn spring-boot:run
cd services/wave-config-service && mvn spring-boot:run
```

## Remove/add a plugin
Drop or remove a JAR from `/plugins` and restart — no recompilation needed.
EOF
git add README.md
git commit -m "Add README"
git push