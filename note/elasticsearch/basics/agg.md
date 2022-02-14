# 聚合查询

Elasticsearch 有一个功能叫聚合（aggregations），允许我们基于数据生成一些精细的分析结果。聚合与 SQL 中的 `GROUP BY` 类似但更强大。

## 基本语法

```
GET /index/_search
GET /megacorp/_search
{
  "aggs": {
    "NAME": {
      "AGG_TYPE": {}
    }
  }
}
```

## sum

求员工的年龄综合

```json
GET /megacorp/_search
{
  "aggs": {
    "employee age sum": {
      "sum": {
        "field": "age"
      }
    }
  }
}
```

`min` 、`max`、`avg` `sum` 这几个的用法几乎一直

## terms

举个例子，挖掘出员工中最受欢迎的兴趣爱好：

```json
GET /megacorp/_search
{
  "aggs": {
    "all_interests": {
      "terms": {
        "field": "interests.keyword",
        "size": 1
      }
    }
  }
}
```

结果如下：

```
{
  "took" : 1,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
   // 忽略...
  },
  "aggregations" : {
    "all_interests" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : "music",
          "doc_count" : 2
        }
      ]
    }
  }
}

```

---

## filter

```
GET /megacorp/_search
{
  "aggs": {
    "filter aggs": {
      "filter": {
        "term": {
          "last_name.keyword": "Fir"
        }
      }
    }
  }
}
```

## top_hits

```
GET /megacorp/_search
{
  "aggs": {
    "top_hits ": {
      "top_hits": {
        "size": 1
      }
    }
  }
}
```

## geo_distance

```
GET /megacorp/_search
{
  "aggs": {
    "NAME": {
      "geo_distance": {
        "field": "location",
        "origin": {
          "lat": 52.376,
          "lon": 4.894
        },
        "ranges": [
          {
            "from": 100,
            "to": 300
          }
        ]
      }
    }
  }
}
```

这里的聚合类型还很多，大家可以慢慢的发掘

## 组合示例

每个查询可以进行多个聚合条件进行约束，如:我们要查询员工里最受欢迎的爱好，已经这里最大的年龄？

```json
GET /megacorp/_search
{
  "aggs": {
    "all interests": {
      "terms": {
        "field": "interests.keyword",
        "size": 1
      }
    },
    "employee age max": {
      "max": {
        "field": "age"
      }
      
    }
  }
}
```

