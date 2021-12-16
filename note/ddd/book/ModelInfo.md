# 从贫血模型到领域驱动设计

### 贫血模型

在编程领域有一个十分著名的公式：



> 程序 = 数据结构 + 算法

在面向对象编程的世界中，将现实世界中事物抽象为对应的类，数据结构对应着承载业务数据的各种类的字段，比如一个人可能就抽象为 Person 类，人具有年龄，身高等属性，抽象为Person 类的 age , height 等字段。而算法则对应业务需求的驱动引起的数据变化的过程和方式，比如要表示一个人长高了 10cm ，则可以给 Person 类设计一个 grow(int height) 方法来表示“长高”这一动作。面向对象的话题很多，这里只简单介绍一下，不做深入展开。那么贫血模型又是什么意思呢？

贫血模型是指只有业务字段以及字段 getter 和 setter 的实体对象。这个概念其实对于一个 Java - Spring 应用来说并不陌生，大多数时候在我们项目中都使用了贫血模型。贫血模型中只包含了数据，而不包含任何的业务逻辑。在一些系统中，数据库映射实体、RPC 请求和返回结果等被设计为贫血模型。与贫血模型相对的一个概念是充血模型：在贫血模型的基础上，充血模型的对象拥有了自身的业务方法，能够对外暴露一些“动作”。上文中的 grow 方法就可以看作是充血模型的特征之一。贫血模型就好比只有骨架的生物，加上自身的业务方法后，能变为有血有肉的实体。

下面从面向对象编程 (OOP) 和测试性两个角度对贫血模型和充血模型进行简单对比。

### 贫血模型与 OOP

假设这样一个场景，一个订单 Order 需要在做取消操作之前，需要检查状态是否正确，如果正确则可以进行取消。使用贫血模型可能有如下设计：

```java
@Getter
@Setter
public class Order {
private Integer status;
private String orderId;
}

public class OrderService {

public static final Integer ORDER_STATUS_SUBMIT = 1;
public static final Integer ORDER_STATUS_CANCELED = 2;
//取消订单
public boolean cancel(Order order) {
    if (!canCancel(order)) {
        return false;
    }
    order.setStatus(ORDER_STATUS_CANCELED);
    //可能有其他操作。。。
    return true;
}
//判断是否可以取消
public boolean canCancel(Order order) {
    return ORDER_STATUS_SUBMIT.equals(order.getStatus());
}
} 
```


这里的 Order 就是典型的贫血模型，不包含任何的取消订单的业务逻辑，所有的业务逻辑都交给了 OrderService 进行管理。同样的场景，使用充血模型可能有如下设计：

```java
@Getter
public class Order {

public static final Integer STATUS_SUBMIT = 1;
public static final Integer STATUS_CANCELED = 2;

private Integer status;
private String orderId;
//取消订单
public boolean cancel() {
    if (!canCancel()) {
        return false;
    }
    this.status = STATUS_CANCELED;
    return true;
}
//是否可以取消
public boolean canCancel() {
    return STATUS_SUBMIT.equals(status);
}
} 
```


可以看到，只一个类！Order 类具有类 canCancel 方法表示订单能否被取消，cancel 方法对订单进行实际取消动作。

咋一看可以发现两种设计区别不是那么大，贫血模型在借助了 Lombok 等工具后代码量与充血模型相近。那么两种实体模型有什么内在不同呢？以下进行简单的理论分析：

- 从面向对象的特性来考虑：贫血模型对于订单完全没有封装，只是数据容器，借由 OrderService 类进行业务管理，其业务字段完全暴露出去。OrderService 几乎没有任何的业务字段，而是关于订单的业务方法；贫血模型能够使用继承达到字段共享的目的，能够有不同的子类承载差异化的数据，但是缺少业务方法，没办法在实体上利用“多态”这一特性，因为其对象行为完全是由管理类 OrderService 完成的，多态只能由管理类来体现。贫血模型中，实体是数据模型，管理类代表了实体的业务，有没有觉得这两个类本身就该是一个类？
- 高内聚低耦合原则，Order 类与 OrderService 是高度耦合的。假设有这样一个场景：我们拿到第三方提供的贫血模型的 Order 类，想判断订单是否可以取消，怎么展开代码？我们不仅需要了解 Order 类的字段，还需要知道 OrderService 的存在及其使用方法。在我们了解 OrderService 这样的管理类的存在时，调用其 canCancel 方法即可；然而在不知道 OrderService 存在的情况情况下，我们很可能自己写一个，重复代码就这么产生了！假设我们自己写一个方法进行判断，就必须知道 order.status = 1 的时候订单才能取消：作为调用者的我们需要知道字段的业务含义才能进行业务操作，负担太重，而且很容易出错！假如后期 Order 类的提供者业务中增加了可以取消订单的场景，那之前自己写的判断是不是需要作相应的修改？


另外，取消订单时 Order 类应该怎么修改自身数据结构、能不能被取消，应该是 Order 类自己进行管理，让 OrderService 来管理也增加了调用者的使用负担，因为调用者必须知道管理类的存在。

贫血模型设计是低内聚高耦合的。

### 贫血模型与可测试性

良好的代码设计一定是便于测试的，当发现自己写的代码很难进行测试时，很可能在设计上存在或多或少的问题。接下来从可测试性对两种模型进行分析，考虑一下场景：一个箱子 Box 中，可以放置各种颜色的铅笔 Pencil，数量不限，同时要求 Box 能显示内含所有铅笔的总数。现在业务要求是：去掉 Box 中颜色为红色的铅笔。

数据库表为：

[![1.png](http://dockone.io/uploads/article/20201120/a28a983577c69b3b5dee93f5e529af9f.png)](http://dockone.io/uploads/article/20201120/a28a983577c69b3b5dee93f5e529af9f.png)


贫血模型可能这么设计：

```java
@Getter
@Setter
public class Box {
private Integer id;
private Integer pencilAmount;
}

@Getter
@Setter
public class Pencil {
private Integer id;
private Integer boxId;
private String color;
}

@Service
public class BoxService {
@Autowired
private PencilMapper pencilMapper;
@Autowired
private BoxMapper boxMapper;
public static final String COLOR_RED = "red";

//业务方法：移除 box 中红色的铅笔
public void removeRedPencils(Integer boxId){
    List<Pencil> redPencils = pencilMapper.findByBoxIdAndColor(boxId, COLOR_RED);
    if (!redPencils.isEmpty()) {
        Box box = boxMapper.findById(boxId);
        Integer oldAmount = box.getPencilAmount();
        box.setPencilAmount(oldAmount - redPencils.size());
        pencilMapper.batchDelete(redPencils);
        boxMapper.update(box);
    }
}
}
//忽略 数据库层的 Mapper 相关代码
充血模型设计可能如下：

@NoArgsConstructor //为了一些框架能反射生成实体
public class Box {
private Integer id;
private List<Pencil> pencils;

public Integer getId() {
    return id;
}

public Box(Integer id, List<Pencil> pencils) {
    this.id = id;
    this.pencils = (pencils==null? Collections.emptyList():pencils);
}

public int getTotalAmount() {
    return pencils.size();
}
//业务方法：移除 box 中红色的铅笔
public void removeRedPencil() {
    pencils.removeIf(Pencil::isRed);
}
}

@NoArgsConstructor
public class Pencil {
private Integer id;
private String color;
public static final String COLOR_RED = "red";

public Pencil(Integer id, String color) {
    this.id = id;
    this.color = color;
}

public boolean isRed() {
    return COLOR_RED.equals(color);
}
}

public class BoxAppService {

private BoxRepository boxRepository;

public BoxAppService(BoxRepository boxRepository) {
    this.boxRepository = boxRepository;
}
//业务入口
public void removeBoxRedPencil(Integer boxId) {
    Box box = boxRepository.get(boxId);
    box.removeRedPencils();
    boxRepository.update(box);
}
}
//忽略数据库层的代码
```


代码中省略了可能出现的错误处理和数据库层的代码，由于充血模型更加 OOP 一些，其业务方法可读性很好，几乎接近自然语言，同时注意到充血模型的 Box 类由于有较好的封装，并没有无脑暴露 setter，而是按照业务需要暴露必要的方法和属性。

从可测试性来考虑：在贫血模型中，2 个实体类只是数据容器，不用写测试；2 个数据库访问的 Mapper 共 4 个方法需要写数据库集成测试，BoxService 是主业务逻辑实现，也是测试的重点。它的测试怎么写呢？有 2 个选择：（1）作为一个数据库集成测试：则需要准备好数据库初始化 sql ，执行初始化 sql 和业务方法后，需要另写 sql 查询数据库该 Box 的红色 Pencil ，对结果进行为空断言，并检查 Box 的 Pencil 总数是否正确；如果选择此方法，则系统中多了一个集成测试！（2）使用 Mock 的方式模拟 2 个 Mapper，给 2 个查询方法 mock 返回数据，并断言 Mapper 的 batchDelete 和 update 方法有被正确的调用，且参数正确，如下：

```java
public class BoxServiceTest {
@Mock
PencilMapper pencilMapper;
@Mock
BoxMapper boxMapper;
@InjectMocks
BoxService boxService;
private static final String RED = "red";

@BeforeEach
void setUp() {
    initMocks(this);
}

/**
 * 2 次 mock 和测试结果的断言的业务可读性太低
 */
@Test
void should_delete_red_pencil() {
    //prepare
    Integer boxId = 1;
    List<Pencil> pencilList = Arrays.asList(
    createPencil(100, boxId, RED),
            createPencil(101, boxId, RED));
    Box box = createBox(boxId, 10);
    when(pencilMapper.findByBoxIdAndColor(1, RED))
            .thenReturn(pencilList);
    when(boxMapper.findById(boxId)).thenReturn(box);
    //when
    boxService.removeRedPencils(boxId);
    //then
    Assertions.assertEquals(box.getPencilAmount(), 8);
    verify(pencilMapper, times(1)).batchDelete(pencilList);
    verify(boxMapper, times(1)).update(box);
}

private Box createBox(Integer id, Integer amount) {
    Box box = new Box();
    box.setId(id);
    box.setPencilAmount(amount);
    return box;
}

private Pencil createPencil(Integer id, Integer boxId, String color) {
    Pencil pencil = new Pencil();
    pencil.setId(id);
    pencil.setBoxId(boxId);
    pencil.setColor(color);
    return pencil;
}
} 
```


可以看到，使用 Mock 的形式写的单元测试业务可读性很差。另外一个方面，在贫血模型架构下很难进行一些较好的测试工程实践，比如：测试驱动开发（TDD）。TDD 要求在写出业务代码前，先写它的测试和断言结果，然后再正式开始编码业务方法让测试通过。看看上面的示例，在没有业务代码的情况下准确的 Mock 各个 mapper 方法返回值和断言其实是相当困难的！

再来简单分析充血模型：2 个实体类包含业务逻辑，可读性很好，2 个单元测试就能覆盖主业务场景；BoxAppService 也极为简单，Mock 一下 BoxRepository 即可。充血模型设计将业务操作和数据库操作解耦了。测试业务时，只需要验证业务模型包含的数据是正确的，所有的业务测试都是单元测试，速度很快；对仓储层 BoxRepository 的测试是集成测试。仓储层一般采用成熟的数据库框架，复用率很高。借助于 Hibernate 等 ORM 工具，在足够熟练的情况下甚至可以不写测试。

一个良好的测试体系应该符合金字塔原则：大量的单元测试、中量的集成测试、少量的契约测试。贫血模型的代码与充血模型相比测试性较低，与数据层的高耦合使得测试很难写，而且集成测试占比也会较高，测试运行效率较低。

[![2.png](http://dockone.io/uploads/article/20201120/ca64b42214020130f554e72efd9c25bb.png)](http://dockone.io/uploads/article/20201120/ca64b42214020130f554e72efd9c25bb.png)



### 充血模型与领域驱动设计

大型软件项目的最复杂之处不是实现，而是软件所服务的真正的领域。领域驱动设计（Domain Driven Design）是用来处理这些高度复杂领域一种工程实践方法论，而充血模型在 DDD 的具体实现上占用重要地位。

所谓领域，如管理用户登陆、密码的“用户领域”，负责订单生成、流转的“订单领域“，都是具体的软件业务场景，在微服务架构下甚至可以简单理解为每一个微服务。所谓领域驱动，是指系统设计是由领域业务来驱动的。通过与领域专家的沟通，由领域业务建模抽象指导代码设计，这和数据驱动有很大的不同。数据驱动是针对领域业务，直接设计数据库表、字段，反过来生成领域实体。而领域驱动直接针对领域业务抽象出实体，而后再考虑数据库层面如何设计。领域驱动设计中的聚合根、实体、值对象等领域模型都是充血模型，对象方法提供的领域服务能体现出该领域中最核心的业务。至于数据库访问，对外暴露的 rest 接口等，都只是对领域对象持久化和操作的手段。

下图就是广为流传的 DDD 六边形架构。领域模型位于最中间，包含的实体对象和领域服务构成了整个领域的业务模型，在设计上领域模型尽可能少的依赖外部基础设施，只是纯粹的业务类。应用程序层负责组织领域方法的调用；六边形的各个边表示不同的端口，每个端口要么处理输入（领域操作），要么处理输出（查询或者持久化等）。对于每一种类型都有一个适配器与之适配，这里的适配器是相对领域模型而言的。比如：某个外部客户端使用 Http 协议和领域进行交互，则定义一个 Http 的适配器即可；另一个客户端可能使用 TCP 协议进行交互，则实现一个 TCP 适配器即可；领域对象需要使用 Mysql 进行持久化，则定义一个 Mysql 的适配器，即仓储模型 Repository。端口的含义是抽象的，输入和输出通过适配器转化为核心的领域模型中的对象或者事件，由领域模型内部进行业务处理即可。

![3.png](http://dockone.io/uploads/article/20201120/bcbaad3a1e7c0fa9951849cfcaea7cee.png)



### 后记

以上简单的对比了贫血模型和充血模型在面向对象和测试性方面的差异，并简单介绍了充血模型在 DDD 中的使用。

虽然贫血模型有着一些缺点，但是在快速迭代和开发速度上具有很大优势，精准的数据层访问能提高系统性能，与需要快速响应需求的互联网行业甚为匹配。随着业务的复杂和需求的变化，业务方法分散使得重复代码越来越多的出现、可维护性会越来越差。

充血模型虽然更加 OOP，也有着更好的可读性和测试性，在大型固定需求的场景下能明显提高工程质量，但是在需求快速更迭时需要考虑对象的设计的重构，面向对象的数据结构和传统结构化数据库天然的不匹配使得数据库设计较为困难，稍有不慎容易引发性能问题，在一些需求经常变化的小项目中反而可能降低生产力。
