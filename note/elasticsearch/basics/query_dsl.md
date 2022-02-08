# Elasticsearch 查询表达式

> 在前面我们已经知道如何创建数据，这里我将上文的创建在执行一边即可

查询表达式(Query DSL)是一种非常灵活又富有表现力的 查询语言。 Elasticsearch 使用它可以以简单的 JSON 接口来展现 Lucene 功能的绝大部分。在你的应用中，你应该用它来编写你的查询语句。它可以使你的查询语句更灵活、更精确、易读和易调试。

## 查询现有索引的数据mapping

语法

```json
GET /{index}/_mapping		
```

它可以回去指定索引每个字段的相信信息

```json
# 获取megacorp的mapping
GET /megacorp/_mapping
# 详细的结果
{
  "megacorp" : {
    "mappings" : {
      "properties" : {
        "about" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "age" : {
          "type" : "long"
        },
        "first_name" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "interests" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "last_name" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        }
      }
    }
  }
}

```


## query查询常用参数

- match 查询

  > 执行全文查询的标准查询, 包括模糊匹配以及短语或邻近查询

  ```json
  GET /megacorp/_search
  {
    "query": {
      "match": {
        "last_name": "Smith"
      }
    }
  }
  ```

- match_all

  *空查询（empty search）* —`{}`— 在功能上等价于使用 `match_all` 查询， 正如其名字一样，匹配所有文档：

  ```json
  GET /megacorp/_search
  {
    "query": {
      "match_all": {}
    }
  }
  ```

- multi_match

  > `multi_match` 查询可以在多个字段上执行相同的 `match` 查询

  ```json
  GET /megacorp/_search
  {
    "query": {
      "multi_match": {
        "query": "Smith",
        "fields": ["last_name","first_name"]
      }
    }
  }
  # query 查询的内容 ,fields 查询的字段
  ```

  

- range 查询

  > 查询指定字段中包含指定范围值(日期、数字或字符串)的文档
  >
  > **`gt`**		大于
  >
  > **`gte`** 	大于等于
  >
  > **`lt`**		小于
  >
  > **`lte`**	 小于等于

  ```json
  GET /megacorp/_search
  {
    "query": {
      "range": {
        "age": {
          "gte": 20,
          "lte": 30
        }
      }
    }
  }
  ```

- exists 查询

  > 查询指定字段中包含任意非空值的文档
  >
  > `exists` 查询和 `missing` 查询被用于查找那些指定字段中有值 (`exists`) 或无值 (`missing`) 的文档。这与SQL中的 `IS_NULL` (`missing`) 和 `NOT IS_NULL` (`exists`) 在本质上具有共性

  ```json
  GET /megacorp/_search
  {
    "query": {
      "exists": {
        "field":"age"
      }
    }
  }
  ```

- term 查询

  > 查询指定字段中精确包含指定词条的文档

  ```json
  GET /megacorp/_search
  {
    "query": {
      "term": {
        "last_name.keyword": {
          "value": "Smith"
        }
      }
    }
  }
  ```

- terms 查询

  > `terms` 查询和 `term` 查询一样，但它允许你指定多值进行匹配。如果这个字段包含了指定值中的任何一个值，那么这个文档满足条件

  ```json
  GET /megacorp/_search
  {
    "query": {
      "terms": {
        "first_name.keyword": [
          "John","Smith"
        ]
      }
    }
  }
  ```

  和 `term` 查询一样，`terms` 查询对于输入的文本不分析。它查询那些精确匹配的值（包括在大小写、重音、空格等方面的差异）

- fuzzy 查询

  > 查找指定字段包含与指定术语模糊相似的术语的文档

  ```json
  GET /megacorp/_search
  {
    "query": {
      "fuzzy": {
        "first_name.keyword": {
          "value": "ohn"
        }
      }
    }
  }
  ```

  

----

## 排序

```
GET /megacorp/_search
{
  "sort": [
    {
      "age": {
        "order": "desc"
      }
    }
  ],
  "size": 1
}
```

按照年龄降序排序取第一个结果如下:

```
{
  "took" : 0,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "megacorp",
        "_type" : "_doc",
        "_id" : "3",
        "_score" : null,
        "_source" : {
          "first_name" : "Douglas",
          "last_name" : "Fir",
          "age" : 35,
          "about" : "I like to build cabinets",
          "interests" : [
            "forestry"
          ]
        },
        "sort" : [
          35
        ]
      }
    ]
  }
}

```

## 合并查询

*查询语句(Query clauses)* 就像一些简单的组合块，这些组合块可以彼此之间合并组成更复杂的查询。这些语句可以是如下形式：

- *叶子语句（Leaf clauses）* (就像 `match` 语句) 被用于将查询字符串和一个字段（或者多个字段）对比。

- *复合(Compound)* 语句 主要用于 合并其它查询语句。 比如，一个 `bool` 语句 允许在你需要的时候组合其它语句，无论是 `must` 匹配、 `must_not` 匹配还是 `should` 匹配，同时它可以包含不评分的过滤器（filters）

  ```
  {
      "bool": {
          "must":     { "match": { "tweet": "elasticsearch" }},
          "must_not": { "match": { "name":  "mary" }},
          "should":   { "match": { "tweet": "full text" }},
          "filter":   { "range": { "age" : { "gt" : 30 }} }
      }
  }
  ```

  一条复合语句可以合并 *任何* 其它查询语句，包括复合语句，了解这一点是很重要的。这就意味着，复合语句之间可以互相嵌套，可以表达非常复杂的逻辑。

  > **`must`**
  >
  > 文档 *必须* 匹配这些条件才能被包含进来。
  >
  > **`must_not`**
  >
  > 文档 *必须不* 匹配这些条件才能被包含进来。
  >
  > **`should`**
  >
  > 如果满足这些语句中的任意语句，将增加 `_score` ，否则，无任何影响。它们主要用于修正每个文档的相关性得分。
  >
  > **`filter`**
  >
  > *必须* 匹配，但它以不评分、过滤模式来进行。这些语句对评分没有贡献，只是根据过滤标准来排除或包含文档。

  ```json
  GET /megacorp/_search
  {
    "query": {
      "bool": {
        "must": [
          {
            "match": {
              "last_name": "Smith"
            }
          }
        ],
        "filter": [
          {
            "range": {
              "age": {
                "gte": 30
              }
            }
          }
        ]
        , "should": [
          {
            "match": {
              "last_name": "Smith"
            }
          }
        ]
      }
    }
  }
  
  ```

---

## 验证查询

查询可以变得非常的复杂，尤其和不同的分析器与不同的字段映射结合时，理解起来就有点困难了。不过 `validate-query` API 可以用来验证查询是否合法。

```json
GET /megacorp/_validate/query
{
  "query": {
    "match": {
      "last_name": "Smith"
    }
  }
}
```

以上 `validate` 请求的应答告诉我们这个查询是不合法的：

```json
{
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "failed" : 0
  },
  "valid" : true
}
```

- 

- 理解错误信息

  为了找出 查询不合法的原因，可以将 `explain` 参数 加到查询字符串中：

  ```json
  GET /megacorp/_validate/query?explain
  {
    "query": {
      "last_name": {
        "match": "Smith"
      }
    }
  }
  ```

  explain 参数可以提供更多关于查询不合法的信息。

  ```
  {
    "valid" : false,
    "error" : "ParsingException[unknown query [last_name]]; nested: NamedObjectNotFoundException[[3:18] unknown field [last_name]];; org.elasticsearch.common.xcontent.NamedObjectNotFoundException: [3:18] unknown field [last_name]"
  }
  ```

- 理解查询

  对于合法查询，使用 `explain` 参数将返回可读的描述，这对准确理解 Elasticsearch 是如何解析你的 query 是非常有用的。这里与你看数据的执行计划类似：

  ```json
  GET /megacorp/_validate/query?explain
  {
    "query": {
      "match": {
        "about": "I love"
      }
    }
  }
  ```

  我们查询的每一个 index 都会返回对应的 `explanation` ，因为每一个 index 都有自己的映射和分析器：

  ```json
  {
    "_shards" : {
      "total" : 1,
      "successful" : 1,
      "failed" : 0
    },
    "valid" : true,
    "explanations" : [
      {
        "index" : "megacorp",
        "valid" : true,
        "explanation" : "about:i about:love"
      }
    ]
  }
  ```

  从 `explanation` 中可以看出，匹配 `I love` 的 `match` 查询被重写为两个针对 `about` 字段的 single-term 查询，一个single-term查询对应查询字符串分出来的一个term。

