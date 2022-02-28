# Routing

在高可用的集群环境中，创建的索引使用以下公式将文档路由到索引中的特定分片：

```
shard_num = hash(_routing) % num_primary_shards
```

用于的默认值是文档的`_id`。`_routing`

可以通过为每个文档指定自定义值来实现自定义工艺路线模式。例如：`routing`

```console
PUT my-index-000001/_doc/1?routing=user1&refresh=true 
{
  "title": "This is a document"
}

GET my-index-000001/_doc/1?routing=user1
```

本文档使用其路由值user1，而不是其 ID


获取、删除或更新文档时需要提供相同的routing值

该字段的值可在查询中访问：`_routing`

```console
GET my-index-000001/_search
{
  "query": {
    "terms": {
      "_routing": [ "user1" ] 
    }
  }
}
```

### 使用自定义路由进行搜索

自定义路由可以减少搜索的影响。无需将搜索请求扇出到索引中的所有分片，只需将请求发送到与特定路由值（或多个值）匹配的分片：

```console
GET my-index-000001/_search?routing=user1,user2 
{
  "query": {
    "match": {
      "title": "document"
    }
  }
}
```

此搜索请求将仅在与 和 路由值关联的`user1` `user2`分片上执行。

### 使路由值成为必需

使用自定义传送时，每当索引、获取、删除、更新文档时，请务必提供传送值。

忘记路由值可能会导致文档在多个分片上编制索引。作为保护措施，可以将字段配置为生成所有 CRUD 操作所需的自定义值：

```console
# 所有文件都需要路由。
PUT my-index-000002
{
  "mappings": {
    "_routing": {
    
      "required": true 
    }
  }
}

# 此索引请求将引发 .routing_missing_exception
PUT my-index-000002/_doc/1 
{
  "text": "No routing value provided"
}
```

### 具有自定义路由的唯一 ID

当索引文档指定自定义时，不能保证索引中所有分片的唯一性。实际上，如果使用不同的值编制索引，则具有相同内容的文档最终可能会位于不同的分片上。

用户有责任确保 ID 在整个索引中是唯一的。

### 路由到索引分区

可以配置索引，以便自定义路由值将转到分片的子集，而不是单个分片。这有助于降低最终出现不平衡群集的风险，同时仍可减少搜索的影响。

这是通过在创建索引时提供索引级别设置`index.routing_partition_size`来完成的。随着分区大小的增加，数据将变得越均匀分布，代价是每个请求必须搜索更多的分片。

当存在此设置时，用于计算分片的公式变为：

```
shard_num = (hash(_routing) + hash(_id) % routing_partition_size) % num_primary_shards
```

也就是说，该字段用于计算索引中的一组分片，然后用于选取该集中的分片。

要启用此功能，应具有`index.routing_partition_size`大于 1 且`index.number_of_shards`小于 的值。

启用后，分区索引将具有以下限制：

- 无法在其中创建具有[`联接`字段]()关系的映射。
- 索引中的所有映射都必须将`_routing`字段标记为必需。