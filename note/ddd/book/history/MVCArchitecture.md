# MVC 及其替代方案

创建可维护的应用程序一直是构建应用程序的真正长期挑战。

然而，早在20世纪70年代，混合责任是常见的做法，人们仍在努力发现如何做得更好。随着应用程序复杂性的增长，对 UI 进行更改将不可避免地意味着对业务逻辑的更改，从而增加了更改的复杂性、执行这些更改所花费的时间以及 Bug 的可能性（因为会更改更多代码）。

MCV通过促进前端和后端之间的"关注点分离"来解决这些问题。

## **1979 – 模型-视图-控制器**

为了解决上面解释的问题[，1979年，Trygve Reenskaug提出了MVC模式](http://heim.ifi.uio.no/~trygver/1979/mvc-2/1979-12-MVC.pdf)作为分离关注点的一种方式，将UI与业务逻辑隔离开来。该模式用于桌面GUI的上下文[，该GUI自1973年以来一直存在](https://en.wikipedia.org/wiki/History_of_the_graphical_user_interface#Xerox_PARC)。



MVC 模式将代码分为三个概念单元：

- 模型表示业务逻辑;
- 视图表示UI中的小部件：按钮，文本框等;
- 控制器提供视图和模型之间的协调。这意味着：
  - 决定显示哪些视图以及使用哪些数据;
  - 将用户操作（即单击按钮）转换为业务逻辑。

> 模型可以是单个对象（相当无趣），也可以是对象的某种结构。
>
> Trygve Reenskaug 1979， [MVC](http://heim.ifi.uio.no/~trygver/1979/mvc-2/1979-12-MVC.pdf)

关于原始 MVC 模式，需要了解的其他重要概念是：

1. 视图直接使用模型数据对象来显示其数据;
2. 当模型数据更改时，它会触发一个立即更新视图的事件（请记住，在1979年没有HTTP）;
3. 通常，每个视图都与一个控制器相关联。
4. 每个屏幕可以包含几对视图和控制器;
5. 每个控制器可能有多个视图。

今天的 HTTP Request & amp ; Response 范例（我很熟悉）没有使用这种原始的 MVC 风格，因为在这种情况从视图流向控制器，就像我所熟悉的一样，但在另一个方向上，它直接从模型流向视图，而不通过控制器。

此外，在当前的"请求和响应"范例中，当数据库中的数据发生更改时，它不会在浏览器中显示的视图中触发更新（尽管这可以使用 Web 套接字实现）。要查看更新的数据，用户需要执行新请求，并且更新的数据将始终通过控制器返回。

## **1987/2000 – PAC / 分层模型-视图-控制器**

PAC，又名HMVC，在UI各部分***的小部件化的***上下文中提供了增强的模块化。

例如，当我们有一个视图，其部分在其他几个视图中以完全相同的格式使用，甚至只是在同一视图中重复使用。一个实际的例子是网页的一个部分，其中包含RSS源的内容，这些内容在其他页面中重复使用。

使用HMVC，处理主请求的控制器将子请求转发给其他控制器，以便获得小部件的渲染，然后将其合并到主视图的渲染中。

就个人而言，我在HTTP请求/响应范例的上下文中遇到过几次需要这样做，但我发现让UI对可以呈现小部件的控制器进行AJAX调用是一种更简单的方法。这保留了模块化的好处，而不会增加嵌套控制器调用的复杂性，以及这些子请求可以缓存在类似Varnish的东西中的优点。

## 1996 **– 模型-视图-演示者**



MCV 模式极大地改进了当时的编程范式。然而，随着应用程序复杂性的增加，对进一步解耦的需求也在增加。

1996年，IBM子公司Taligent公开了他们的MVP模式，基于MVC。我们的想法是进一步将模型与 UI 问题隔离开来：

- 视图是被动的，不知道模型;
- 专注于不包含业务逻辑的瘦控制器（演示器），它们只是在模型中调用命令和/或查询，将原始数据传递给视图;
- 数据更改不会直接触发视图中的更新：它始终通过演示器，而演示器又会更新视图。这允许控制器（演示者）在更新视图之前执行额外的与表示相关的逻辑。例如，还要更新与数据库中更改的数据相关的数据;
- 每个视图都有一个演示者。

这更接近我习惯在今天的请求/响应范例中看到的内容：**流程始终通过控制器/演示器**。尽管如此，演示者仍不会主动更新视图，它始终需要执行新请求才能使更改可见。

在 MVP 中，演示者也称为[主管控制器](https://martinfowler.com/eaaDev/SupervisingPresenter.html)。

## **2005 – 模型-视图-视图模型**

同样，由于应用程序增加了复杂性，2005年，微软的WPF和Silverlight架构师之一[John Gossman宣布了MVVM模式](https://blogs.msdn.microsoft.com/johngossman/2005/10/08/introduction-to-modelviewviewmodel-pattern-for-building-wpf-apps/)，目标是进一步将UI设计与代码隔离开来，并提供从视图到数据模型的数据绑定。

> [MVVM] 是 [MVC] 的一种变体，是为现代 UI 开发平台量身定制的，其中视图由设计人员而不是经典开发人员负责。[...]应用程序的 UI 部分使用与业务逻辑或数据后端不同的工具、语言和不同的人员进行开发。
>
> John Gossman 2005， [《Model/View/ViewModel Pattern 简介》](https://blogs.msdn.microsoft.com/johngossman/2005/10/08/introduction-to-modelviewviewmodel-pattern-for-building-wpf-apps/)

控制器被视图模型"替换"：

> [视图]对键盘快捷键进行编码，控件本身管理与输入设备的交互，这是MVC中控制器的责任（在现代GUI开发中，控制器究竟发生了什么是一个漫长的题外话......我倾向于认为它只是淡入背景。它仍然存在，但我们不必像1979年那样考虑它）。
>
> John Gossman 2005， [《Model/View/ViewModel Pattern 简介》](https://blogs.msdn.microsoft.com/johngossman/2005/10/08/introduction-to-modelviewviewmodel-pattern-for-building-wpf-apps/)

MVVM背后的想法是：

- 一个视图模型只对应于一个视图，反之亦然;
- 将视图逻辑移动到视图模型以简化视图;
- 视图中使用的数据与 ViewModel 中的数据之间的一对一映射;
- 将 ViewModel 中的数据绑定到视图中的数据，以便在 ViewModel 中更改数据时，它会立即反映在视图中。

就像在原始 MVC 模式中一样，这种方法在传统的请求/响应范例中是不可能的，因为 ViewModel 无法主动更新视图（除非使用 Web 套接字），而 MVVM 需要它。此外，根据我的经验，ViewModel具有与视图中使用的数据匹配的属性这一事实并不是控制器中的常见做法。

## **模型-视图-演示者-视图模型**

在为云构建复杂的企业应用程序时，我更喜欢将应用程序UI结构合理化为M-V-P-VM，其中ViewModel是Martin Fowler在2004年称之为[表示模型](https://martinfowler.com/eaaDev/PresentationModel.html)。

- #### 型

  一组包含所有业务逻辑和用例的类;

- #### 视图

  一个模板，用于使用模板引擎生成HTML;

- #### 视图模型 （又名[演示模型）](https://martinfowler.com/eaaDev/PresentationModel.html))

  从查询（或从中提取原始数据的模型实体）接收原始数据，并保存要在模板中使用的数据。它还封装了复杂的表示逻辑，以简化模板。我发现 ViewModel 的使用尤其重要，因为我们不会试图在模板中使用实体，这使我们能够将视图与模型完全隔离：

  - 模型中的更改（即实体结构中的更改）可能会冒泡并影响视图模型，但不会影响模板;
  - 复杂的表示逻辑不会泄漏到域中（即在与表示逻辑完全相关的业务实体中创建方法），因为我们可以将其封装在ViewModel中;
  - 模板的依赖关系将变为显式，因为它们必须在 ViewModel 中设置。例如，使这些依赖项可见可以帮助我们决定应该从数据库中紧急加载什么以防止N + 1问题。

- #### 主持人

  接收 HTTP 请求，触发命令或查询，使用查询、ViewModel、模板和模板引擎返回的数据生成 HTML 并将其发送回客户端。"所有视图"交互通过演示者进行。

  下面是一个简单（且幼稚）的代码示例，说明我是如何做到的：

  ```php
  <?php
  // src/UI/Admin/Some/Controller/Namespace/Detail/SomeEntityDetailController.php
  
  namespace UI\Admin\Some\Controller\Namespace\Detail;
  
  // use ...
  
  final class SomeEntityDetailController
  {
      /**
       * @var SomeRepositoryInterface
       */
      private $someRepository;
    
      /**
       * @var RelatedRepositoryInterface
       */
      private $relatedRepository;
  
      /**
       * @var TemplateEngineInterface
       */
      private $templateEngine;
  
      public function __construct(
          SomeRepositoryInterface $someRepository,
          RelatedRepositoryInterface $relatedRepository,
          TemplateEngineInterface $templateEngine
      ) {
          $this->someRepository = $someRepository;
          $this->relatedRepository = $relatedRepository;
          $this->templateEngine = $templateEngine;
      }
  
      /**
       * @return mixed
       */
      public function get(int $someEntityId)
      {
          $mainEntity = $this->someRepository->getById($someEntityId);
          $relatedEntityList = $this->relatedRepository->getByParentId($someEntityId);
  
          return $this->templateEngine->render(
              '@Some/Controller/Namespace/Detail/details.html.twig',
              new DetailsViewModel($mainEntity, $relatedEntityList)
          );
      }
  }
  ```

  ```php
  <?php
  // src/UI/Admin/Some/Controller/Namespace/Detail/DetailsViewModel.php
  
  namespace UI\Admin\Some\Controller\Namespace\Detail;
  
  // use ...
  
  final class DetailsViewModel implements TemplateViewModelInterface
  {
      /**
       * @var array
       */
      private $mainEntity = [];
  
      /**
       * @var array
       */
      private $relatedEntityList = [];
  
      /**
       * @var bool
       */
      private $shouldDisplayFancyDialog = false;
  
      /**
       * @var bool
       */
      private $canEditData = false;
  
      /**
       * @param SomeEntity $mainEntity
       * @param RelatedEntity[] $relatedEntityList
       */
      public function __construct(SomeEntity $mainEntity, array $relatedEntityList)
      {
          $this->mainEntity = [
              'name' => $mainEntity->getName(),
              'description' => $mainEntity->getResume(),
          ];
  
          foreach ($relatedEntityList as $relatedEntity) {
              $this->relatedEntityList[] = [
                  'title' => $relatedEntity->getTitle(),
                  'subtitle' => $relatedEntity->getSubtitle(),
              ];
          }
          
          $this->shouldDisplayFancyDialog = /* ... some complex conditional using the entities data ... */ ;
          
          $this->canEditData = /* ... another complex conditional using the entities data ... */ ;
      }
  
      public function getMainEntity(): array
      {
          return $this->mainEntity;
      }
  
      public function getRelatedEntityList(): array
      {
          return $this->relatedEntityList;
      }
  
      public function shouldDisplayFancyDialog(): bool
      {
          return $this->shouldDisplayFancyDialog;
      }
  
      public function canEditData(): bool
      {
          return $this->canEditData;
      }
  }
  ```



模板和 ViewModel 具有一对一的匹配，这意味着视图只能与特定的 ViewModel 一起使用，反之亦然。这实际上甚至让我想，也许**我们可以将模板和ViewModel封装在View对象中，有效地将控制器与模板和ViewModel分离**，使其依赖于通用的视图接口，但我从未尝试过这一点。

## **结论**

我们可能会在网络上找到MVC的其他变体。但是，这些是我发现更有趣和/或与我的工作相关的。

尽管如此，我在这里引用的模式是为桌面应用程序和/或富客户端的上下文创建的，因此它们并不总是100%符合请求/响应范例。

如果你正在做企业云应用程序，并且你正在使用MVC，很可能你实际上是在使用更接近MVP的东西，但无论如何，我的观点不是遵循MVC的特定变体或知道所有名称并对其非常严格，我的观点是我们应该**从所有这些中学习， 因此，我们可以根据需要使用和适应**。像往常一样，最终目标是**低耦合**和**高内聚**：**关注点分离**。

 