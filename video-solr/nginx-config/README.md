
## 配置Nginx作Riak负载均衡

> 配置Nginx代理主要满足：

`1. 负载均衡`

`2. Referer导致的403禁止访问`

## 启动关闭命令

`启动`： su root -c "/usr/sbin/nginx -c /home/ops/riak-proxy/nginx.conf"

`关闭`： su root -c "kill `cat nginx.pid`"

## Nginx问题

### 错误信息

```
2015/10/17 17:02:38 [crit] 6646#0: *15 open() "/var/lib/nginx/tmp/proxy/6/00/0000000006" failed (13: Permission denied) while reading upstream, client: 192.168.6.200, server: localhost, request: "GET /riak/frames/25a7afc4c7f8ed0be17613b7dcc165ff.png HTTP/1.1", upstream: "http://192.168.32.19:8098/riak/frames/25a7afc4c7f8ed0be17613b7dcc165ff.png", host: "192.168.31.11:1888"
```

### 解决方案

`chmod -R 777 /var/lib/nginx/tmp`