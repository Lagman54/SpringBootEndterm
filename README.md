# Setup Database

### Create databases (if they exist, drop all schemas)
```postgresql
CREATE DATABASE order_db;
CREATE DATABASE delivery_db;
```

### Create dev_user:
```postgresql
CREATE USER dev_user WITH PASSWORD 'secret123';
ALTER ROLE dev_user WITH SUPERUSER;
```

# Start kafka
From \docker directory run:
```
docker-compose up
```