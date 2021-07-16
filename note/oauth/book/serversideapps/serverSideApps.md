## 服务器端应用

- [授权码流程](#授权码流程)
    - [OAuth安全性](#OAuth安全性)
    - [授权请求参数](#授权请求参数)
    - [用户批准请求](#用户批准请求)
- [实现流程](#实现流程)
- [可能的错误](#可能的错误)

服务器端应用程序是处理OAuth服务器时遇到的最常见的应用程序类型。这些应用程序在Web服务器上运行，
其中该应用程序的源代码对公众不可用，因此它们可以维护其客户机密的机密性。

下图说明了一个典型示例，其中用户与正在与客户端进行通信的浏览器进行交互。客户端和API服务器之间具有独立的安全通信通道。
用户的浏览器从不直接向API服务器发出请求，一切首先通过客户端进行。

![user_browser_client_server](https://www.oauth.com/wp-content/uploads/2018/07/user_browser_client_server.png)

服务器端应用程序使用authorization_code授权类型。在此流程中，在用户授权应用程序之后，
该应用程序会收到一个“授权码”，然后可以将其交换为访问令牌。

### 授权码流程

授权代码是客户端将交换访问令牌的临时代码。代码本身是从授权服务器获取的，在授权服务器上，用户有机会查看客户端正在请求的信息，并批准或拒绝该请求。

授权代码流比其他授予类型提供了一些好处。当用户授权应用程序时，他们将使用URL中的临时代码重定向回应用程序。应用程序将该代码交换为访问令牌。当应用程序请求访问令牌时，该请求将通过客户端密码进行身份验证，从而降低了攻击者拦截授权码并自己使用授权码的风险。这也意味着访问令牌对用户是永远不可见的，因此这是将令牌传递回应用程序的最安全方法，从而降低了令牌泄漏给其他人的风险。

Web流程的第一步是向用户请求授权。这是通过创建授权请求链接供用户单击来完成的。

授权URL通常采用以下格式：

```
https://authorization-server.com/oauth/authorize``?client_id=a17c21ed``&response_type=code``&state=5ca75bd30``&redirect_uri=https%3A%2F%2Fexample-app.com%2Fauth``&scope=photos
```

确切的URL端点将由您要连接的服务指定，但参数名称将始终相同。

请注意，您很可能需要先在服务上注册您的重定向URL，然后该URL才能被接受。这也意味着您不能更改每个请求的重定向URL。相反，您可以使用`state`参数来自定义请求。请参阅下面的详细信息。

用户访问授权页面后，服务会向用户显示请求说明，包括应用程序名称，范围等。（有关示例屏幕截图，请参阅“批准请求”。）如果用户单击“批准”，则服务器将使用您在查询字符串参数中提供的“代码”和相同的“状态”参数重定向回应用程序。重要的是要注意，这不是访问令牌。您可以使用授权码执行的唯一操作是发出获取访问令牌的请求。

#### OAuth安全性

直到2019年，OAuth 2.0规范仅建议对移动和JavaScript应用使用PKCE扩展。现在，最新的OAuth安全BCP建议也将PKCE用于服务器端应用程序，因为它还提供了一些其他好处。通用OAuth服务可能需要一些时间才能适应此新建议，但是，如果您要从头开始构建服务器，则绝对应该为所有类型的客户端支持PKCE。

#### 授权请求参数

以下参数用于发出授权请求。您应该使用以下参数构建查询字符串，并将其附加到从其文档获得的应用程序的授权端点。

##### `response_type=code`

`response_type`设置为`code`指示您想要授权码作为响应。

##### `client_id`

该`client_id`是你的应用程序标识符。首次向服务注册应用程序时，您将收到一个client_id。

##### `redirect_uri` （可选的）

该`redirect_uri`是可选的取决于API，但强烈建议。这是授权完成后您希望用户重定向到的URL。这必须与您先前在服务中注册的重定向URL相匹配。

##### `scope` （可选的）

包含一个或多个范围值（以空格分隔）以请求其他访问级别。这些值将取决于特定的服务。

##### `state`

该`state`参数具有两个功能。当用户被重定向回您的应用程序时，您作为状态包括的任何值也将包含在重定向中。这使您的应用有机会在被定向到授权服务器的用户和再次返回之间保留数据，例如使用state参数作为会话密钥。这可用于指示授权完成后应用程序中要执行的操作，例如，指示授权后重定向到应用程序的哪些页面。

如果state参数每个请求包含一个随机值，它也可以用作CSRF保护机制。当用户被重定向回您的应用程序时，请仔细检查状态值是否与您最初设置的值匹配。

##### PKCE

如果服务支持Web服务器应用程序的PKCE，请在此处也包括PKCE质询和质询方法。单页应用程序和移动应用程序中的完整示例对此进行了描述。

将所有这些查询字符串参数组合到授权URL中，然后将用户的浏览器定向到那里。通常，应用会将这些参数放在登录按钮中，或者将从应用自身的登录URL作为HTTP重定向发送此URL。

#### 用户批准请求

在用户被带到服务并看到请求之后，他们将允许或拒绝该请求。如果它们允许请求，它们将被重定向回指定的重定向URL以及查询字符串中的授权代码。然后，应用程序需要将此授权码交换为访问令牌。

#### 将授权码交换为访问令牌

要交换访问令牌的授权代码，该应用向服务的令牌端点发出POST请求。该请求将具有以下参数。

##### `grant_type` （需要）

该`grant_type`参数必须设置为“ authorization_code”。

##### `code` （需要）

此参数用于从授权服务器接收的授权代码，该代码将在此请求的查询字符串参数“ code”中。

##### `redirect_uri` （可能需要）

如果重定向URL包含在初始授权请求中，则它也必须包含在令牌请求中，并且必须相同。一些服务支持注册多个重定向URL，而一些服务则需要在每个请求中指定重定向URL。有关详细信息，请查看服务的文档。

##### 客户端身份验证（必填）

在请求访问令牌时，该服务将要求客户端进行身份验证。通常，服务支持通过HTTP基本身份验证使用客户端的`client_id`和进行客户端身份验证`client_secret`。但是，某些服务通过接受`client_id`和`client_secret`作为POST正文参数来支持身份验证。由于OAuth 2.0规范将这项决定权留给了服务，因此请查看服务的文档以了解服务的期望。

##### PKCE验证程序

如果该服务支持Web服务器应用程序的PKCE，则客户端在交换授权码时也需要包括后续PKCE参数。再次，请参阅单页应用程序和移动应用程序以获取使用PKCE扩展的完整示例。

### 实现流程

高级概述是这样的：

- 使用应用程序的客户端ID，重定向URL和状态参数创建登录链接
- 用户看到授权提示并批准请求
- 使用身份验证代码将用户重定向回应用程序的服务器
- 该应用将身份验证代码交换为访问令牌

##### 该应用启动授权请求

该应用程序通过制作包含ID，范围和状态的URL来启动流程。该应用程序可以将其放入`[`]()标签。

```
<a href="https://authorization-server.com/oauth/authorize
?response_type=code&client_id=mRkZGFjM&state=5ca75bd30
&scope=photos&code_challenge_method=S256
&code_challenge=6nGtF5eij1YuEqQXr7L9OxA0RLHZ21tEQNZq1DZJzuY">
Connect Your Account</a>
```

##### 用户批准请求

定向到授权服务器后，用户将看到下图所示的授权请求。如果用户批准了该请求，则它们将连同身份验证代码和状态参数一起重定向回应用程序。

![img](https://www.oauth.com/wp-content/uploads/2016/08/okta_oauth-diagrams_20170622-01-1.png)

​						授权请求示例

##### 该服务将用户重定向回应用程序

该服务发送重定向标头，将用户的浏览器重定向回发出请求的应用程序。重定向将在URL中包含“代码”和原始的“状态”。

```
https://example-app.com/cb?code=Yzk5ZDczMzRlNDEwY&state=5ca75bd30
```

##### 该应用将身份验证代码交换为访问令牌

该应用使用授权代码通过向授权服务器发出POST请求来获取访问令牌。

```
POST /oauth/token HTTP/1.1
Host: authorization-server.com
 
code=Yzk5ZDczMzRlNDEwY
&grant_type=code
&redirect_uri=https://example-app.com/cb
&client_id=mRkZGFjM
&client_secret=ZGVmMjMz
&code_verifier=a6b602d858ae0da189dacd297b188ef308dc754bd9cc359ac2e1d8d1
```

授权服务器将验证请求，并在访问令牌到期时以访问令牌和可选的刷新令牌进行响应。

响应：

```
{
  "access_token": "AYjcyMzY3ZDhiNmJkNTY",
  "refresh_token": "RjY2NjM5NzA2OWJjuE7c",
  "token_type": "Bearer",
  "expires": 3600
}
```

### 可能的错误

在某些情况下，授权期间您可能会收到错误响应。

通过使用查询字符串中的其他参数重定向回提供的重定向URL来指示错误。始终会有一个错误参数，并且重定向也可能包括`error_description`和`error_uri`。

例如，

```
https://example-app.com/cb?error=invalid_scope
```

尽管服务器返回了`error_description`密钥，但该错误描述并不旨在显示给用户。相反，您应该向用户显示您自己的错误消息。这样一来，您便可以告诉用户要采取的适当措施来纠正问题，如果您正在构建多语言网站，还可以使您本地化错误消息。

#### 无效的重定向网址

如果提供的重定向URL无效，授权服务器将不会重定向到它。相反，它可能会向用户显示一条描述问题的消息。

#### 无法识别 `client_id`

如果无法识别客户端ID，授权服务器将不会重定向用户。而是，它可能会显示一条描述问题的消息。

#### 用户拒绝请求

如果用户拒绝授权请求，则服务器会将用户重定向到带有`error=access_denied`查询字符串的重定向URL ，并且不会显示任何代码。此时应由应用程序决定向用户显示什么。

#### 无效的参数

如果一个或多个参数无效，例如缺少必需的值，或者`response_type`参数错误，则服务器将重定向到重定向URL，并包含描述问题的查询字符串参数。错误参数的其他可能值为：

`invalid_request`: The request is missing a required parameter, includes an invalid parameter value, or is otherwise malformed.

`unauthorized_client`: The client is not authorized to request an authorization code using this method.

`unsupported_response_type`: The authorization server does not support obtaining an authorization code using this method.

`invalid_scope`: The requested scope is invalid, unknown, or malformed.

`server_error`: The authorization server encountered an unexpected condition which prevented it from fulfilling the request.

`temporarily_unavailable`: The authorization server is currently unable to handle the request due to a temporary overloading or maintenance of the server.

**译**

`invalid_request`：请求缺少必需的参数，包含无效的参数值或格式错误。

`unauthorized_client`：客户端无权使用此方法请求授权码。

`unsupported_response_type`：授权服务器不支持使用此方法获取授权码。

`invalid_scope`：要求的范围无效，未知或格式错误。

`server_error`：授权服务器遇到意外情况，导致其无法满足请求。

`temporarily_unavailable`：授权服务器当前由于服务器的暂时超载或维护而无法处理该请求。

另外，服务器可能包含参数`error_description`以及`error_uri`有关错误的其他信息。

