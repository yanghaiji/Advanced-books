## SpEl(Spring Expression Language) 介绍

Spring Expression Language（简称“SpEL”）是一种强大的表达式语言，支持在运行时查询和操作对象图。语言语法类似于统一 EL，但提供了额外的功能，最显着的是方法调用和基本的字符串模板功能。

虽然还有其他几种可用的 Java 表达式语言——OGNL、MVEL 和 JBoss EL，仅举几例——但创建 Spring 表达式语言是为了向 Spring 社区提供一种单一的、支持良好的表达式语言，可以在所有产品中使用

表达式语言支持以下功能：

- Literal expressions  文字表达 
- Boolean and relational operators 布尔和关系运算符
- Regular expressions 常用表达
- Class expressions 类表达式
- Accessing properties, arrays, lists, and maps 访问属性、数组、列表和Map
- Method invocation 方法调用
- Relational operators 关系运算符
- Assignment 任务
- Calling constructors 调用构造函数
- Bean references Bean 引用
- Array construction 数组构建
- Inline lists 内联列表
- Inline maps 内联地图
- Ternary operator三元运算符
- Variables 变量
- User-defined functions 用户定义函数
- Collection projection 收藏投影
- Collection selection 收藏选择
- Templated expressions 模板表达式

### 示例

本节介绍SpEL接口的简单使用及其表达语言。

以下代码引入了 SpEL API 来计算文字字符串表达式 `Hello World`.

```
ExpressionParser parser = new SpelExpressionParser();
Expression exp = parser.parseExpression("'Hello World'"); 
String message = (String) exp.getValue();
```

消息变量的值为`'Hello World'`。

您最有可能使用的 SpEL 类和接口位于 `org.springframework.expression`包及其子包中，例如`spel.support`.

该`ExpressionParser`接口负责解析表达式字符串。在前面的示例中，表达式字符串是由周围的单引号表示的字符串文字。该`Expression`接口负责评估先前定义的表达式字符串。分别在调用and时可以抛出`ParseException`和 and 的 两个异常。`EvaluationException``parser.parseExpression``exp.getValue`

SpEL 支持广泛的功能，例如调用方法、访问属性和调用构造函数。

在下面的方法调用示例中，我们`concat`在字符串文字上调用方法：

```
ExpressionParser parser = new SpelExpressionParser();
Expression exp = parser.parseExpression("'Hello World'.concat('!')"); 
String message = (String) exp.getValue();
```

的值`message`现在是“Hello World！”。

以下调用 JavaBean 属性的示例调用了该`String`属性`Bytes`：

```
ExpressionParser parser = new SpelExpressionParser();

// invokes 'getBytes()'
Expression exp = parser.parseExpression("'Hello World'.bytes"); 
byte[] bytes = (byte[]) exp.getValue();
```

此行将文字转换为字节数组。

SpEL 还通过使用标准点符号（例如`prop1.prop2.prop3`）以及属性值的相应设置来支持嵌套属性 。也可以访问公共字段。

以下示例显示了如何使用点表示法来获取文字的长度：

```
ExpressionParser parser = new SpelExpressionParser();

// invokes 'getBytes().length'
Expression exp = parser.parseExpression("'Hello World'.bytes.length"); 
int length = (Integer) exp.getValue();
```

SpEL 的更常见用法是提供针对特定对象实例（称为根对象）进行评估的表达式字符串。以下示例显示如何把`name`从`Inventor`类的实例中检索属性或创建布尔条件：

```
// Create and set a calendar
GregorianCalendar c = new GregorianCalendar();
c.set(1856, 7, 9);

// The constructor arguments are name, birthday, and nationality.
Inventor tesla = new Inventor("Nikola Tesla", c.getTime(), "Serbian");

ExpressionParser parser = new SpelExpressionParser();

Expression exp = parser.parseExpression("name"); // Parse name as an expression
String name = (String) exp.getValue(tesla);
// name == "Nikola Tesla"

exp = parser.parseExpression("name == 'Nikola Tesla'");
boolean result = exp.getValue(tesla, Boolean.class);
// result == true
```

###  理解`EvaluationContext`

该`EvaluationContext`接口用于计算表达式以解析属性、方法或字段并帮助执行类型转换。Spring 提供了两种实现。

- `SimpleEvaluationContext`: 公开基本 SpEL 语言功能和配置选项的子集，用于不需要完整范围的 SpEL 语言语法并且应该有意义地限制的表达式类别。示例包括但不限于数据绑定表达式和基于属性的过滤器。
- `StandardEvaluationContext`：公开全套 SpEL 语言功能和配置选项。您可以使用它来指定默认根对象并配置每个可用的评估相关策略。

`SimpleEvaluationContext`旨在仅支持 SpEL 语言语法的一个子集。它不包括 Java 类型引用、构造函数和 bean 引用。它还要求您明确选择对表达式中的属性和方法的支持级别。默认情况下，`create()`静态工厂方法仅启用对属性的读取访问。您还可以获得构建器来配置所需的确切支持级别，针对以下一项或某些组合：

- `PropertyAccessor`仅自定义（无反射）
- 只读访问的数据绑定属性
- 用于读取和写入的数据绑定属性

##### 类型转换

默认情况下，SpEL 使用 Spring 核心 ( `org.springframework.core.convert.ConversionService`) 中可用的转换服务。此转换服务带有许多用于常见转换的内置转换器，但也是完全可扩展的，因此您可以在类型之间添加自定义转换。此外，它是泛型感知的。这意味着，当您在表达式中使用泛型类型时，SpEL 会尝试转换以维护遇到的任何对象的类型正确性。

这在实践中意味着什么？假设赋值 using`setValue()`用于设置`List`属性。属性的类型实际上是`List`. SpEL 认识到列表的元素`Boolean`在放入之前需要转换为。以下示例显示了如何执行此操作：

```java
class Simple {
    public List<Boolean> booleanList = new ArrayList<Boolean>();
}

Simple simple = new Simple();
simple.booleanList.add(true);

EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

// "false" is passed in here as a String. SpEL and the conversion service
// will recognize that it needs to be a Boolean and convert it accordingly.
parser.parseExpression("booleanList[0]").setValue(context, simple, "false");

// b is false
Boolean b = simple.booleanList.get(0);
```

### 解析器配置

可以使用解析器配置对象 ( `org.springframework.expression.spel.SpelParserConfiguration`)来配置 SpEL 表达式解析器。配置对象控制一些表达式组件的行为。例如，如果您对数组或集合进行索引，并且指定索引处的元素是`null`，SpEL可以自动创建元素。这在使用由属性引用链组成的表达式时很有用。如果您索引数组或列表并指定超出数组或列表当前大小末尾的索引，SpEL 可以自动增长数组或列表以容纳该索引。为了在指定索引处添加元素，SpEL 将尝试在设置指定值之前使用元素类型的默认构造函数创建元素。如果元素类型没有默认构造函数，`null`将被添加到数组或列表中。如果没有知道如何设置值的内置或自定义转换器，`null`则将保留在指定索引处的数组或列表中。以下示例演示了如何自动增长列表：

```
class Demo {
    public List<String> list;
}

// Turn on:
// - auto null reference initialization
// - auto collection growing
SpelParserConfiguration config = new SpelParserConfiguration(true,true);

ExpressionParser parser = new SpelExpressionParser(config);

Expression expression = parser.parseExpression("list[3]");

Demo demo = new Demo();

Object o = expression.getValue(demo);

// demo.list will now be a real collection of 4 entries
// Each entry is a new empty String
```

### SpEL Compilation

Spring Fram

ework 4.1 包含一个基本的表达式编译器。表达式通常是解释性的，这在评估期间提供了很多动态灵活性，但不能提供最佳性能。对于偶尔的表达式使用，这很好，但是，当被其他组件（例如 Spring Integration）使用时，性能可能非常重要，并且不需要真正的动态性。

SpEL 编译器旨在满足这一需求。在求值期间，编译器生成一个 Java 类，该类包含运行时的表达式行为，并使用该类来实现更快的表达式求值。由于缺少表达式周围的类型，编译器在执行编译时使用在表达式的解释评估期间收集的信息。例如，它不完全从表达式中知道属性引用的类型，但在第一次解释评估期间，它会找出它是什么。当然，如果各种表达式元素的类型随时间发生变化，则基于这些导出信息进行编译可能会在以后造成麻烦。出于这个原因，编译最适合类型信息不会因重复评估而改变的表达式。

考虑以下基本表达式：

```
someArray[0].someProperty.someOtherProperty < 0.1
```

由于前面的表达式涉及数组访问、一些属性取消引用和数值运算，因此性能提升非常明显。在运行 50000 次迭代的示例微基准测试中，使用解释器进行评估需要 75 毫秒，而使用表达式的编译版本仅需要 3 毫秒。

### 编译器配置

默认情况下，编译器不会打开，但您可以通过两种不同的方式之一打开它。您可以通过使用解析器配置过程前面讨论过或在 SpEL 用法嵌入到另一个组件中时使用 Spring 属性来打开它。本节讨论这两个选项。

编译器可以在`org.springframework.expression.spel.SpelCompilerMode`枚举中捕获的三种模式之一中运行 。模式如下：

- `OFF` （默认）：编译器关闭。
- `IMMEDIATE`: 在立即模式下，表达式会尽快编译。这通常是在第一次解释评估之后。如果编译的表达式失败（通常是由于类型改变，如前所述），表达式求值的调用者会收到一个异常。
- `MIXED`：在混合模式下，表达式会随着时间在解释模式和编译模式之间静默切换。经过一定数量的解释运行后，它们切换到编译形式，如果编译形式出现问题（例如类型改变，如前所述），表达式会再次自动切换回解释形式。稍后，它可能会生成另一个编译形式并切换到它。基本上，用户进入`IMMEDIATE`模式的异常是在内部处理的。

`IMMEDIATE`mode 存在是因为`MIXED`mode 可能会导致具有副作用的表达式出现问题。如果编译的表达式在部分成功后崩溃了，它可能已经做了一些影响系统状态的事情。如果发生这种情况，调用者可能不希望它在解释模式下静默地重新运行，因为部分表达式可能会运行两次。

选择模式后，使用`SpelParserConfiguration`来配置解析器。以下示例显示了如何执行此操作：

```
SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE,
    this.getClass().getClassLoader());

SpelExpressionParser parser = new SpelExpressionParser(config);

Expression expr = parser.parseExpression("payload");

MyMessage message = new MyMessage();

Object payload = expr.getValue(message);
```

指定编译器模式时，还可以指定类加载器（允许传递null）。编译表达式在任何提供的子类加载器中定义。重要的是要确保，如果指定了类加载器，它可以看到表达式计算过程中涉及的所有类型。如果未指定类加载器，则使用默认类加载器（通常是在表达式求值期间运行的线程的上下文类加载器）。

第二种配置编译器的方法是在 SpEL 嵌入在其他组件中时使用，并且可能无法通过配置对象对其进行配置。在这些情况下，也能够设定`spring.expression.compiler.mode` 通过JVM系统属性（或通过属性 `SpringProperties`机构）的一个 `SpelCompilerMode`枚举值（`off`，`immediate`，或`mixed`）。

##### 编译器限制

从 Spring Framework 4.1 开始，基本的编译框架已经到位。但是，该框架尚不支持编译所有类型的表达式。最初的重点是可能在性能关键上下文中使用的常用表达式。目前无法编译以下类型的表达式：

- 涉及赋值的表达式
- 依赖转换服务的表达式
- 使用自定义解析器或访问器的表达式
- 使用选择或投影的表达式

将来可以编译更多类型的表达式。

### Bean 定义中的表达式

您可以使用带有基于 XML 或基于注释的配置元数据的 SpEL 表达式来定义`BeanDefinition`实例。在这两种情况下，定义表达式的语法都是`#{  }`.

#### XML 配置

可以使用表达式设置属性或构造函数参数值，如以下示例所示：

```xml
<bean id="numberGuess" class="org.spring.samples.NumberGuess">
    <property name="randomNumber" value="#{ T(java.lang.Math).random() * 100.0 }"/>

    <!-- other properties -->
</bean>
```

应用程序上下文中的所有 bean 都可用作具有公共 bean 名称的预定义变量。这包括用于访问运行时环境的标准上下文 bean，例如`environment`（类型 `org.springframework.core.env.Environment`）以及`systemProperties`和 `systemEnvironment`（类型`Map`）。

以下示例显示了对`systemProperties`作为 SpEL 变量的bean 的访问：

```xml
<bean id="taxCalculator" class="org.spring.samples.TaxCalculator">
    <property name="defaultLocale" value="#{ systemProperties['user.region'] }"/>

    <!-- other properties -->
</bean>
```

请注意，您不必在`#`此处使用符号作为预定义变量的前缀。

您还可以按名称引用其他 bean 属性，如以下示例所示：

```xml
<bean id="numberGuess" class="org.spring.samples.NumberGuess">
    <property name="randomNumber" value="#{ T(java.lang.Math).random() * 100.0 }"/>

    <!-- other properties -->
</bean>

<bean id="shapeGuess" class="org.spring.samples.ShapeGuess">
    <property name="initialShapeSeed" value="#{ numberGuess.randomNumber }"/>

    <!-- other properties -->
</bean>
```

#### 注解配置

要指定默认值，您可以将`@Value`注释放在字段、方法以及方法或构造函数参数上。

以下示例设置字段的默认值：

```java
public class FieldValueTestBean {

    @Value("#{ systemProperties['user.region'] }")
    private String defaultLocale;

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getDefaultLocale() {
        return this.defaultLocale;
    }
}
```

