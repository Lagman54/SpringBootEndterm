
CREATE DATABASE order_db OWNER dev_user;
CREATE DATABASE customer_db OWNER dev_user;
CREATE DATABASE delivery_db OWNER dev_user;
CREATE DATABASE user_db OWNER dev_user;

-- (Optional) Grant all privileges explicitly
GRANT ALL PRIVILEGES ON DATABASE order_db TO dev_user;
GRANT ALL PRIVILEGES ON DATABASE customer_db TO dev_user;
GRANT ALL PRIVILEGES ON DATABASE delivery_db TO dev_user;
GRANT ALL PRIVILEGES ON DATABASE user_db TO dev_user;