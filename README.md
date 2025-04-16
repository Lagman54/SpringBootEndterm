# Setup Database

### Create databases
```postgresql
CREATE DATABASE order_db;
CREATE DATABASE delivery_db;
```

### Create dev_user:
```postgresql
CREATE USER dev_user WITH PASSWORD 'secret123';
ALTER USER dev_user CREATEDB;
```

# Start kafka
From \docker directory run:
```
docker-compose up
```