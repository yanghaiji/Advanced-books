# 使用 Kubernetes 部署 Seata Server

### 快速启动

创建 `seata-server.yaml`

```yaml
apiVersion: v1
kind: Service
metadata:
  name: seata-server
  namespace: default
  labels:
    k8s-app: seata-server
spec:
  type: NodePort
  ports:
    - port: 8091
      nodePort: 30091
      protocol: TCP
      name: http
  selector:
    k8s-app: seata-server

---

apiVersion: apps/v1
kind: Deployment
metadata:
  name: seata-server
  namespace: default
  labels:
    k8s-app: seata-server
spec:
  replicas: 1
  selector:
    matchLabels:
      k8s-app: seata-server
  template:
    metadata:
      labels:
        k8s-app: seata-server
    spec:
      containers:
        - name: seata-server
          image: docker.io/seataio/seata-server:latest
          imagePullPolicy: IfNotPresent
          env:
            - name: SEATA_PORT
              value: "8091"
            - name: STORE_MODE
              value: file
          ports:
            - name: http
              containerPort: 8091
              protocol: TCP
$ kubectl apply -f seata-server.yaml
```

## 自定义配置

### 环境变量

支持的环境变量和 Docker 相同，可以参考 [使用 Docker 部署 Seata Server](https://seata.io/zh-cn/docs/ops/deploy-by-docker.html)

### 使用自定义配置文件

指定配置文件可以通过挂载文件或使用 ConfigMap 的方式实现，挂载后需要通过指定 `SEATA_CONFIG_NAME` 指定配置文件位置，并且环境变量的值需要以`file:`开始, 如: `file:/root/seata-config/registry`

- Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: seata-server
  namespace: default
  labels:
    k8s-app: seata-server
spec:
  replicas: 1
  selector:
    matchLabels:
      k8s-app: seata-server
  template:
    metadata:
      labels:
        k8s-app: seata-server
    spec:
      containers:
        - name: seata-server
          image: docker.io/seataio/seata-server:latest
          imagePullPolicy: IfNotPresent
          env:
            - name: SEATA_CONFIG_NAME
              value: file:/root/seata-config/registry
          ports:
            - name: http
              containerPort: 8091
              protocol: TCP
          volumeMounts:
            - name: seata-config
              mountPath: /root/seata-config
      volumes:
        - name: seata-config
          configMap:
            name: seata-server-config

---
apiVersion: v1
kind: ConfigMap
metadata:
  name: seata-server-config
data:
  registry.conf: |
    registry {
        type = "nacos"
        nacos {
          application = "seata-server"
          serverAddr = "192.168.199.2"
        }
    }
    config {
      type = "nacos"
      nacos {
        serverAddr = "192.168.199.2"
        group = "SEATA_GROUP"
      }
    }
```

