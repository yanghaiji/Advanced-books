# Elasticsearch 中常用的数据类型

## 核心数据类型

- **string**

  `text` and `keyword`

- **Numeric**

  `long`, `integer`, `short`, `byte`, `double`, `float`, `half_float`, `scaled_float`

- **Date**

  `date`

- **Date nanoseconds**

  `date_nanos`

- **Boolean**

  `boolean`

- **binary**

  `binary`

- **range**

  `integer_range`, `float_range`, `long_range`, `double_range`, `date_range`, `ip_range`

## 复杂数据类型

- **Object**

  `object` for single JSON objects

- **Nested**

  `nested` for arrays of JSON objects

## 地图数据类型

- **Geo-point**

  `geo_point` for lat/lon points（纬度/经度）

- **Geo-shape**

  `geo_shape` for complex shapes like polygons（适用于多边形等复杂形状）

## 专用的数据类型

- **IP**

  `ip` for IPv4 and IPv6 addresses

- **Completion data type**

  `completion` to provide auto-complete suggestions

- **Token count**

  `token_count` to count the number of tokens in a string

- **`mapper-murmur3`**

  `murmur3` to compute hashes of values at index-time and store them in the index

- **`mapper-annotated-text`**

  `annotated-text` to index text containing special markup (typically used for identifying named entities)

- **Percolator**

  Accepts queries from the query-dsl

- **Join**

  Defines parent/child relation for documents within the same index

- **Rank feature**

  Record numeric feature to boost hits at query time.

- **Rank features**

  Record numeric features to boost hits at query time.

- **Dense vector**

  Record dense vectors of float values.

- **Sparse vector**

  Record sparse vectors of float values.

- **Search-as-you-type**

  A text-like field optimized for queries to implement as-you-type completion

- **Alias**

  Defines an alias to an existing field.

- **Flattened**

  Allows an entire JSON object to be indexed as a single field.

- **Shape**

  `shape` for arbitrary cartesian geometries.

- **Histogram**

  `histogram` for pre-aggregated numerical values for percentiles aggregations.

- **Constant keyword**

  Specialization of `keyword` for the case when all documents have the same value.