# Elasticsearch 增删改

> 在前面我们已经安装好了elasticsearch 已经kibana ，如果您还没有安装可以参考之前的文档进行安装

##  创建文档

我们创建一个index为megacorp的文档，基本参数如下

```
{index}/_doc/{id}, /{index}/_doc, or /{index}/_create/{id}
```

> 如果您安装了kibana可以在kibana里直接执行如下命令，入没有安装可以在postman通过http请求执行

这里的id您可以自己指定，或者让elasticsearch为您自动生成

```json
# 创建 索引为megacorp 类型为 employee
PUT /megacorp/_create/1
{
  "first_name": "John",
  "last_name": "Smith",
  "age": 25,
  "about": "I love to go rock climbing",
  "interests": [
    "sports",
    "music"
  ]
}
```

执行成功后我们会看到如下结果

```json
{
  "_index" : "megacorp",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 0,
  "_primary_term" : 1
}

```

如果您想验证创建的后的内容与您是否一直，可以执行如下命令进行验证

```
GET /megacorp/_doc/1
# 或者
# GET /megacorp/_search
```

会得到如下的信息

```json
{
  "_index" : "megacorp",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 1,
  "_seq_no" : 0,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "first_name" : "John",
    "last_name" : "Smith",
    "age" : 25,
    "about" : "I love to go rock climbing",
    "interests" : [
      "sports",
      "music"
    ]
  }
}

```

---

## 修改文档

修改文档与创建文档类似，您可以选择PUT 或者POST，参数如下

```
{index}/_doc/{id}
```

这里我们将上面创建的文档`about`字段内添加 `!`

```json
POST /megacorp/_doc/1
{
  "first_name": "John",
  "last_name": "Smith",
  "age": 25,
  "about": "I love to go rock climbing!",
  "interests": [
    "sports",
    "music"
  ]
}
```

成功指定后返回的接口如下

```json
{
  "_index" : "megacorp",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 2,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 1,
  "_primary_term" : 1
}

```

这里会有两点变化，`result` 的类型发生了变化，`_version`的版本进行了递增



## 删除文档

对于任何的数据库而言，都会支持删除操作，`Elasticsearch`当然也支持，我们可以在同一个index下在创建几个

```json
PUT /megacorp/_doc/2
{
    "first_name" :  "Jane",
    "last_name" :   "Smith",
    "age" :         32,
    "about" :       "I like to collect rock albums",
    "interests":  [ "music" ]
}

PUT /megacorp/_doc/3
{
    "first_name" :  "Douglas",
    "last_name" :   "Fir",
    "age" :         35,
    "about":        "I like to build cabinets",
    "interests":  [ "forestry" ]
}
```

我们首先确认在`megacorp`这个index下有几个对象

```
GET /megacorp/_search
```

可以看到，之前的三个资源已经创建成功，这是我们将id为3的删除

```
DELETE /megacorp/_doc/3
```

或者我们将整个megacorp下的索引资源进行删除

```
DELETE /megacorp
```

这是我们在执行 `GET /megacorp/_search`,会发现提示404，说明我们想要查询的资源已经不存在了，具体如下

```json
{
  "error" : {
    "root_cause" : [
      {
        "type" : "index_not_found_exception",
        "reason" : "no such index [megacorp]",
        "resource.type" : "index_or_alias",
        "resource.id" : "megacorp",
        "index_uuid" : "_na_",
        "index" : "megacorp"
      }
    ],
    "type" : "index_not_found_exception",
    "reason" : "no such index [megacorp]",
    "resource.type" : "index_or_alias",
    "resource.id" : "megacorp",
    "index_uuid" : "_na_",
    "index" : "megacorp"
  },
  "status" : 404
}
```

