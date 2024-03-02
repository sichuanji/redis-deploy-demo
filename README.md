# redis 部署 案列

Redis For Java 练习代码 

## linux下搭建集群

环境要求

- centos 7 
- docker
- docker compose
- 并配置好端口映射
## 方式一：单机
docker下启动一个redis实列即可（需要替换自己redis的位置
`docker run -p 7005:7005 --name redis-v /mydata/redis6/data:/data -v /mydata/redis6/conf/redis.conf:/etc/redis/redis.conf -d redis redis-server /etc/redis/redis.conf`
## 方式二：集群搭建 redis-cluster
### 配置好redis的配置文件
1、建立一个持久化文件夹，后面用于redis的创建的映射
```shell
mkdir ./redis
mkdir ./config # 配置文件位置
mkdir ./data # 持久化数据文件夹
```
2、config下创建redis.config 配置文件，并修改为如下
```
appendonly yes
port 7000
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
```

### doker中创建redis实列 使用docker-compose
建立docker-compose.yml
```shell
version: '3.1'
services:
  # redis1配置
  redis1:
    image: daocloud.io/library/redis:6.0.4
    container_name: redis-1
    restart: always
    network_mode: "host"
    volumes:
      - /opt/docker/redis-cluster/redis-1/data:/data
      - /opt/docker/redis-cluster/redis-1/redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
  # redis2配置
  redis2:
    image: daocloud.io/library/redis:6.0.4
    container_name: redis-2
    restart: always
    network_mode: "host"
    volumes:
      - /opt/docker/redis-cluster/redis-2/data:/data
      - /opt/docker/redis-cluster/redis-2/redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
  # redis3配置
  redis3:
    image: daocloud.io/library/redis:6.0.4
    container_name: redis-3
    restart: always
    network_mode: "host"
    volumes:
      - /opt/docker/redis-cluster/redis-3/data:/data
      - /opt/docker/redis-cluster/redis-3/redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
  # redis4配置
  redis4:
    image: daocloud.io/library/redis:6.0.4
    container_name: redis-4
    restart: always
    network_mode: "host"
    volumes:
      - /opt/docker/redis-cluster/redis-4/data:/data
      - /opt/docker/redis-cluster/redis-4/redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
  # redis5配置
  redis5:
    image: daocloud.io/library/redis:6.0.4
    container_name: redis-5
    restart: always
    network_mode: "host"
    volumes:
      - /opt/docker/redis-cluster/redis-5/data:/data
      - /opt/docker/redis-cluster/redis-5/redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
  # redis6配置
  redis6:
    image: daocloud.io/library/redis:6.0.4
    container_name: redis-6
    restart: always
    network_mode: "host"
    volumes:
      - /opt/docker/redis-cluster/redis-6/data:/data
      - /opt/docker/redis-cluster/redis-6/redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
```
运行 
`docker-compose up -d`
### docker-compose，初始化集群
任意进入一个redis，初始化集群
`redis-cli --cluster create 192.168.56.10:7000 192.168.56.10:7001 192.168.56.10:7002 192.168.56.10:7003 192.168.56.10:7004 192.168.56.10:7005 --cluster-replicas 1 `

### 测试
如果下面命令出现多个节点，即为配置成功
`cluster nodes`
## 方式三： sentinel

Sentinel模型通常由一个或多个Sentinel实例组成，它们监控着多个Redis主节点和它们的从节点，并在主节点失效时自动执行故障转移，将从节点提升为主节点，以确保服务的可用性。

## 方式四：分片
直接启动几台redis，然后客户端根据一致性hash函数进行分片，将数据保存到指定的redis实列上