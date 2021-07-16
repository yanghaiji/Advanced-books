## 实体与聚合根

实体是DDD(Domain Driven Design)中核心概念.Eric Evans是这样描述实体的 "一个没有从其属性,而是通过连续性和身份的线索来定义的对象"

实体通常映射到关系型数据库的表中.

### 实体类

- 简单的实体类

  ```
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class SysUserEntity {
      private String id;
      private String userName;
      private String pwd;
      ......
  }
  ```

- 具有复合键的实体类

  ```
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class SysUserRoleEntity {
      private String id;
      private String userName;
      private String pwd;
      private String roleId;
      ......
  }
  ```

在应用服务中使用示例:

```java
public class ApiServiceImpl implements ApiService {
    public void testApi(){
        List<SysUserEntity> user = dao.findAll();
    }
}
```

### 聚合根

"*聚合是域驱动设计中的一种模式.DDD的聚合是一组可以作为一个单元处理的域对象.例如,订单及订单系列的商品,这些是独立的对象,但将订单(连同订单系列的商品)视为一个聚合通常是很有用的*"

- 示例

  ```java
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class SysUser {
      private String id;
    	.....
      private List<SysRole> role;
      private List<SysDepartment> department;
      
  }
  ```

  看这概念好像很高深，但实际的操作这个和我们经常写DO，VO，DTO，BO 几乎是一样的

当然每个聚合根如果都公用的属性我们也是需要进行抽离的。

- 示例

  ```java
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class SysUser extends BaseEntity{
    	.....
      private List<SysRole> role;
      private List<SysDepartment> department;
      
  }
  
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class SysOrder extends BaseEntity{
    	.....
      private List<SysOrderInfo> orderInfo;
      private List<SysLogistics> logistics;
      
  }
  
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class BaseEntity{
      private String id;
    	private Date creteTime;
      private String createBy;
      private Date updateTime;
      private String updateBy;   
      private int isDel;
  }
  ```

  
