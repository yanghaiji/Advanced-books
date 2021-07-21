### SpEl 表达式语法

#### 文字表达

  支持的文字表达式类型是字符串、数值（整数、实数、十六进制）、布尔值和空值。字符串由单引号分隔。要将单引号本身放在字符串中，请使用两个单引号字符。

  ```
  ExpressionParser parser = new SpelExpressionParser();
  
  // evals to "Hello World"
  String helloWorld = (String) parser.parseExpression("'Hello World'").getValue();
  
  double avogadrosNumber = (Double) parser.parseExpression("6.0221415E+23").getValue();
  
  // evals to 2147483647
  int maxValue = (Integer) parser.parseExpression("0x7FFFFFFF").getValue();
  
  boolean trueValue = (Boolean) parser.parseExpression("true").getValue();
  
  Object nullValue = parser.parseExpression("null").getValue();
  ```

  

#### Properties, Arrays, Lists, Maps, and Indexers

  使用属性引用进行导航很容易。为此，请使用句点来指示嵌套的属性值

  ```
  ExpressionParser parser = new SpelExpressionParser();
  // evals to 1856
  int year = (Integer) parser.parseExpression("birthdate.year + 1900").getValue(context);
  
  String city = (String) parser.parseExpression("placeOfBirth.city").getValue(context);
  ```

  属性名称的第一个字母允许不区分大小写。因此，上面例子中的表达式可以分别写成`Birthdate.Year + 1900`和 `PlaceOfBirth.City`。此外，属性可以选择性地通过方法调用来访问——例如，`getPlaceOfBirth().getCity()`而不是 `placeOfBirth.city`.

  数组和列表的内容使用方括号表示法获取，如下例所示：

  ```
  ExpressionParser parser = new SpelExpressionParser();
  EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
  
  // Inventions Array
  
  // evaluates to "Induction motor"
  String invention = parser.parseExpression("inventions[3]").getValue(
          context, tesla, String.class);
  
  // Members List
  
  // evaluates to "Nikola Tesla"
  String name = parser.parseExpression("members[0].name").getValue(
          context, ieee, String.class);
  
  // List and Array navigation
  // evaluates to "Wireless communication"
  String invention = parser.parseExpression("members[0].inventions[6]").getValue(
          context, ieee, String.class);
  ```

  map的内容是通过在括号内指定文字键值来获得的。在下面的例子中，因为`officers`映射的键是字符串，我们可以指定字符串文字：

  ```
  // Officer's Dictionary
  
  Inventor pupin = parser.parseExpression("officers['president']").getValue(
          societyContext, Inventor.class);
  
  // evaluates to "Idvor"
  String city = parser.parseExpression("officers['president'].placeOfBirth.city").getValue(
          societyContext, String.class);
  
  // setting values
  parser.parseExpression("officers['advisors'][0].placeOfBirth.country").setValue(
          societyContext, "Croatia");
  ```

####  Inline Lists

  ```
  // evaluates to a Java list containing the four numbers
  List numbers = (List) parser.parseExpression("{1,2,3,4}").getValue(context);
  
  List listOfLists = (List) parser.parseExpression("{{'a','b'},{'x','y'}}").getValue(context);
  ```

  `{}`本身意味着一个空列表。出于性能原因，如果列表本身完全由固定文字组成，则会创建一个常量列表来表示表达式（而不是在每次评估时构建一个新列表）。

#### Map

您还可以使用`{key:value}`符号直接在表达式中表示映射

```java
// evaluates to a Java map containing the two entries
Map inventorInfo = (Map) parser.parseExpression("{name:'Nikola',dob:'10-July-1856'}").getValue(context);

Map mapOfMaps = (Map) parser.parseExpression("{name:{first:'Nikola',last:'Tesla'},dob:{day:10,month:'July',year:1856}}").getValue(context);
```

`{:}`本身就意味着一个空Map。出于性能原因，如果映射本身由固定文字或其他嵌套常量结构（列表或映射）组成，则会创建一个常量映射来表示表达式（而不是在每次评估时构建一个新映射）。映射键的引用是可选的（除非键包含句点 ( `.`)）

#### 数组构造

  ```
  int[] numbers1 = (int[]) parser.parseExpression("new int[4]").getValue(context);
  
  // Array with initializer
  int[] numbers2 = (int[]) parser.parseExpression("new int[]{1,2,3}").getValue(context);
  
  // Multi dimensional array
  int[][] numbers3 = (int[][]) parser.parseExpression("new int[4][5]").getValue(context);
  ```

#### 方法

  您可以使用典型的 Java 编程语法来调用方法。您还可以对文字调用方法。还支持可变参数

  ```
  // string literal, evaluates to "bc"
  String bc = parser.parseExpression("'abc'.substring(1, 3)").getValue(String.class);
  
  // evaluates to true
  boolean isMember = parser.parseExpression("isMember('Mihajlo Pupin')").getValue(
          societyContext, Boolean.class);
  ```

#### 关系运算符

  使用标准运算符表示法支持关系运算符（等于、不等于、小于、小于或等于、大于和大于或等于）

  ```
  // evaluates to true
  boolean trueValue = parser.parseExpression("2 == 2").getValue(Boolean.class);
  
  // evaluates to false
  boolean falseValue = parser.parseExpression("2 < -5.0").getValue(Boolean.class);
  
  // evaluates to true
  boolean trueValue = parser.parseExpression("'black' < 'block'").getValue(Boolean.class);
  ```

  大于和小于的比较`null`遵循一个简单的规则：`null`被视为无（即不是零）。因此，任何其他值总是大于`null`（`X > null`总是`true`）并且没有其他值永远小于没有（`X < null`总是`false`）

  每个符号运算符也可以指定为纯字母等效项。这避免了使用的符号对嵌入表达式的文档类型（例如在 XML 文档中）具有特殊含义的问题。

  - `lt`( `<`)
  - `gt`( `>`)
  - `le`( `<=`)
  - `ge`( `>=`)
  - `eq`( `==`)
  - `ne`( `!=`)
  - `div`( `/`)
  - `mod`( `%`)
  - `not`( `!`)

##### 逻辑运算符

  SpEL 支持以下逻辑运算符：

  - `and`( `&&`)
  - `or`( `||`)
  - `not`( `!`)

  以下示例显示了如何使用逻辑运算符：

  ```
  // -- AND --
  
  // evaluates to false
  boolean falseValue = parser.parseExpression("true and false").getValue(Boolean.class);
  
  // evaluates to true
  String expression = "isMember('Nikola Tesla') and isMember('Mihajlo Pupin')";
  boolean trueValue = parser.parseExpression(expression).getValue(societyContext, Boolean.class);
  
  // -- OR --
  
  // evaluates to true
  boolean trueValue = parser.parseExpression("true or false").getValue(Boolean.class);
  
  // evaluates to true
  String expression = "isMember('Nikola Tesla') or isMember('Albert Einstein')";
  boolean trueValue = parser.parseExpression(expression).getValue(societyContext, Boolean.class);
  
  // -- NOT --
  
  // evaluates to false
  boolean falseValue = parser.parseExpression("!true").getValue(Boolean.class);
  
  // -- AND and NOT --
  String expression = "isMember('Nikola Tesla') and !isMember('Mihajlo Pupin')";
  boolean falseValue = parser.parseExpression(expression).getValue(societyContext, Boolean.class);
  ```

##### 数学运算符

  您可以`+`对数字和字符串使用加法运算符 ( )。您只能对数字使用减法 ( `-`)、乘法 ( `*`) 和除法 ( `/`) 运算符。您还可以对数字使用模数 ( `%`) 和指数幂 ( `^`) 运算符。强制执行标准运算符优先级

  ```
  // Addition
  int two = parser.parseExpression("1 + 1").getValue(Integer.class);  // 2
  
  String testString = parser.parseExpression(
          "'test' + ' ' + 'string'").getValue(String.class);  // 'test string'
  
  // Subtraction
  int four = parser.parseExpression("1 - -3").getValue(Integer.class);  // 4
  
  double d = parser.parseExpression("1000.00 - 1e4").getValue(Double.class);  // -9000
  
  // Multiplication
  int six = parser.parseExpression("-2 * -3").getValue(Integer.class);  // 6
  
  double twentyFour = parser.parseExpression("2.0 * 3e0 * 4").getValue(Double.class);  // 24.0
  
  // Division
  int minusTwo = parser.parseExpression("6 / -3").getValue(Integer.class);  // -2
  
  double one = parser.parseExpression("8.0 / 4e0 / 2").getValue(Double.class);  // 1.0
  
  // Modulus
  int three = parser.parseExpression("7 % 4").getValue(Integer.class);  // 3
  
  int one = parser.parseExpression("8 / 5 % 2").getValue(Integer.class);  // 1
  
  // Operator precedence
  int minusTwentyOne = parser.parseExpression("1+2-3*8").getValue(Integer.class);  // -21
  ```

#### 赋值运算符

  要设置属性，请使用赋值运算符 ( `=`)。这通常在对 的调用中完成，`setValue`但也可以在对 的调用中完成`getValue`。

  ```
  Inventor inventor = new Inventor();
  EvaluationContext context = SimpleEvaluationContext.forReadWriteDataBinding().build();
  
  parser.parseExpression("name").setValue(context, inventor, "Aleksandar Seovic");
  
  // alternatively
  String aleks = parser.parseExpression(
          "name = 'Aleksandar Seovic'").getValue(context, inventor, String.class);
  ```

#### 变量

  您可以使用`#variableName`语法引用表达式中的变量

  有效的变量名称必须由以下支持的一个或多个字符组成。

  - 字母：`A`到`Z`和`a`到`z`
  - 数字：`0`到`9`
  - 下划线： `_`
  - 美元符号： `$`

  ```
  Inventor tesla = new Inventor("Nikola Tesla", "Serbian");
  
  EvaluationContext context = SimpleEvaluationContext.forReadWriteDataBinding().build();
  context.setVariable("newName", "Mike Tesla");
  
  parser.parseExpression("name = #newName").getValue(context, tesla);
  System.out.println(tesla.getName())  // "Mike Tesla"
  ```

  ##### 在`#this`和`#root`变量

  该`#this`变量总是被定义并且是指当前的评价对象（针对其不合格的引用解析）。该`#root`变量始终被定义并引用根上下文对象。尽管`#this`可能会随着表达式的组件的计算而变化，但`#root`始终指的是根。

  ```
  // create an array of integers
  List<Integer> primes = new ArrayList<Integer>();
  primes.addAll(Arrays.asList(2,3,5,7,11,13,17));
  
  // create parser and set variable 'primes' as the array of integers
  ExpressionParser parser = new SpelExpressionParser();
  EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataAccess();
  context.setVariable("primes", primes);
  
  // all prime numbers > 10 from the list (using selection ?{...})
  // evaluates to [11, 13, 17]
  List<Integer> primesGreaterThanTen = (List<Integer>) parser.parseExpression(
          "#primes.?[#this>10]").getValue(context);
  ```

#### Bean 引用

  如果已使用 bean 解析器配置了评估上下文，则可以使用`@`符号从表达式中查找 bean 

  ```
  ExpressionParser parser = new SpelExpressionParser();
  StandardEvaluationContext context = new StandardEvaluationContext();
  context.setBeanResolver(new MyBeanResolver());
  
  // This will end up calling resolve(context,"something") on MyBeanResolver during evaluation
  Object bean = parser.parseExpression("@something").getValue(context);
  ```

  要访问工厂 bean 本身，您应该在 bean 名称前面加上一个`&`符号

  ```
  ExpressionParser parser = new SpelExpressionParser();
  StandardEvaluationContext context = new StandardEvaluationContext();
  context.setBeanResolver(new MyBeanResolver());
  
  // This will end up calling resolve(context,"&foo") on MyBeanResolver during evaluation
  Object bean = parser.parseExpression("&foo").getValue(context);
  ```

 #### 三元运算符（If-Then-Else）

 您可以使用三元运算符在表达式中执行 if-then-else 条件逻辑

```
String falseString = parser.parseExpression(
        "false ? 'trueExp' : 'falseExp'").getValue(String.class);
```

在这种情况下，布尔值`false`会返回字符串 value `'falseExp'`。一个更现实的例子如下：

```
parser.parseExpression("name").setValue(societyContext, "IEEE");
societyContext.setVariable("queryName", "Nikola Tesla");

expression = "isMember(#queryName)? #queryName + ' is a member of the ' " +
        "+ Name + ' Society' : #queryName + ' is not a member of the ' + Name + ' Society'";

String queryResultString = parser.parseExpression(expression)
        .getValue(societyContext, String.class);
// queryResultString = "Nikola Tesla is a member of the IEEE Society"
```

#### 表达式模板

表达式模板允许将文字文本与一个或多个评估块混合。每个评估块都用您可以定义的前缀和后缀字符分隔。一个常见的选择是`#{ }`用作分隔符，如以下示例所示：

```
String randomPhrase = parser.parseExpression(
        "random number is #{T(java.lang.Math).random()}",
        new TemplateParserContext()).getValue(String.class);

// evaluates to "random number is 0.7038186818312008"
```

字符串的计算方法是将文字文本`'random number is '`与`#{ }`分隔符内的表达式的计算结果（在这种情况下，调用该`random()`方法的结果）连接起来。该`parseExpression()`方法的第二个参数的类型是`ParserContext`。该`ParserContext`接口用于影响表达式的解析方式，以支持表达式模板功能。定义`TemplateParserContext`如下：

```
public class TemplateParserContext implements ParserContext {

    public String getExpressionPrefix() {
        return "#{";
    }

    public String getExpressionSuffix() {
        return "}";
    }

    public boolean isTemplate() {
        return true;
    }
}
```

