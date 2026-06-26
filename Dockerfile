# Stage 1: Build 后端
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY oil-well-system/pom.xml .
RUN mvn dependency:go-offline -B 2>/dev/null || true
COPY oil-well-system/src ./src
RUN mvn package -DskipTests -q

# Stage 2: Build 前端
FROM node:20-alpine AS frontend-build
WORKDIR /app
COPY oil-well-vue/package*.json ./
RUN npm install
COPY oil-well-vue/ .
RUN npm run build

# Stage 3: 运行时 (Ubuntu + MySQL + Redis + Nginx + Java)
FROM ubuntu:22.04
ENV DEBIAN_FRONTEND=noninteractive

# 安装所有依赖
RUN apt-get update && \
    apt-get install -y \
    mysql-server-8.0 \
    redis-server \
    nginx \
    openjdk-17-jre-headless \
    supervisor \
    && rm -rf /var/lib/apt/lists/*

# 配置 MySQL utf8mb4
RUN mkdir -p /etc/mysql/conf.d && \
    printf "[mysqld]\ncharacter-set-server=utf8mb4\ncollation-server=utf8mb4_unicode_ci\nskip-host-cache\nuser=mysql\n" > /etc/mysql/conf.d/custom.cnf

# Nginx 配置 - 反向代理 API 到后端
RUN rm -f /etc/nginx/sites-enabled/default
COPY <<'NGINX_EOF' /etc/nginx/conf.d/default.conf
server {
    listen 80;
    client_max_body_size 50M;

    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /user/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /authority/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /src/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /admin/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
NGINX_EOF

# Supervisor 配置 - 管理多进程
COPY <<'SUPERVISOR_EOF' /etc/supervisor/conf.d/supervisord.conf
[supervisord]
nodaemon=true
user=root
logfile=/var/log/supervisor/supervisord.log
pidfile=/var/run/supervisord.pid

[program:mysqld]
command=/usr/sbin/mysqld --user=mysql
autostart=true
autorestart=true
startretries=3
priority=10
stopasgroup=true

[program:redis]
command=/usr/bin/redis-server
autostart=true
autorestart=true
startretries=3
priority=20

[program:nginx]
command=/usr/sbin/nginx -g "daemon off;"
autostart=true
autorestart=true
startretries=3
priority=30

[program:backend]
command=java -jar /app/backend.jar
autostart=true
autorestart=true
startretries=3
priority=40
stopasgroup=true
environment=MYSQL_URL="jdbc:mysql://localhost:3306/tb_oil_well_cost?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8",MYSQL_USER="root",MYSQL_PASSWORD="root123",REDIS_HOST="localhost",REDIS_PORT="6379"
SUPERVISOR_EOF

# 入口脚本 - 首次初始化 + 启动所有服务
COPY <<'ENTRYPOINT_EOF' /entrypoint.sh
#!/bin/bash
set -e

# 初始化 MySQL 数据目录（首次运行）
if [ ! -d "/var/lib/mysql/mysql" ]; then
    echo ">>> Initializing MySQL data directory..."
    mysqld --initialize-insecure --user=mysql
fi

# 以跳过网络方式启动 MySQL（用于初始化）
echo ">>> Starting MySQL for initialization..."
mkdir -p /var/run/mysqld
chown mysql:mysql /var/run/mysqld
mysqld --user=mysql --skip-networking --socket=/var/run/mysqld/mysqld.sock &
MYSQL_PID=$!

# 等待 MySQL 就绪
for i in $(seq 30 -1 0); do
    if mysqladmin ping --socket=/var/run/mysqld/mysqld.sock --silent 2>/dev/null; then
        echo ">>> MySQL is ready"
        break
    fi
    if [ "$i" -eq 0 ]; then
        echo ">>> MySQL failed to start"
        exit 1
    fi
    sleep 1
done

# 首次运行：导入初始化 SQL + 设置 root 密码
if [ ! -f "/var/lib/mysql/.init_done" ]; then
    echo ">>> Running init.sql..."
    mysql -u root --socket=/var/run/mysqld/mysqld.sock < /docker-entrypoint-initdb.d/init.sql
    echo ">>> Setting root password..."
    mysql -u root --socket=/var/run/mysqld/mysqld.sock -e \
        "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root123'; FLUSH PRIVILEGES;"
    touch /var/lib/mysql/.init_done
    echo ">>> Initialization complete"
fi

# 关闭临时 MySQL（设置密码后用密码认证）
export MYSQL_PWD=root123
mysqladmin -u root --socket=/var/run/mysqld/mysqld.sock shutdown
unset MYSQL_PWD
wait $MYSQL_PID

# 启动所有服务
echo ">>> Starting all services via supervisor..."
exec /usr/bin/supervisord -c /etc/supervisor/conf.d/supervisord.conf -n
ENTRYPOINT_EOF

RUN chmod +x /entrypoint.sh

# 复制编译产物
COPY --from=backend-build /app/target/*.jar /app/backend.jar
COPY --from=frontend-build /app/dist /usr/share/nginx/html
COPY init.sql /docker-entrypoint-initdb.d/

EXPOSE 80
VOLUME ["/var/lib/mysql"]
ENTRYPOINT ["/entrypoint.sh"]
