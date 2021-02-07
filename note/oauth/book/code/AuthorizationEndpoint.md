## OAuth2.0 授权流程代码分析

在使用Spring时 我们只需要调用`/oauth/authorize`就可完成授权，今天我们就来一探究竟

### AuthorizationEndpoint

`AuthorizationEndpoint` 提供里服务的内部授权端点如下：

```java
	@RequestMapping(value = "/oauth/authorize")
	public ModelAndView authorize(Map<String, Object> model, @RequestParam Map<String, String> parameters,
			SessionStatus sessionStatus, Principal principal) {

		// 通过 OAuth2RequestFactor 创建 AuthorizationRequest , 根据穿入的 client_id 等信息
		AuthorizationRequest authorizationRequest = getOAuth2RequestFactory().createAuthorizationRequest(parameters);

		Set<String> responseTypes = authorizationRequest.getResponseTypes();

		if (!responseTypes.contains("token") && !responseTypes.contains("code")) {
			throw new UnsupportedResponseTypeException("Unsupported response types: " + responseTypes);
		}

		if (authorizationRequest.getClientId() == null) {
			throw new InvalidClientException("A client id must be provided");
		}

		try {
            //Oauth2授权的第一步就是要确保用户是否已经登陆，然后才会
            //这里体现的是SecurityContext中是否包涵了已经授权的Authentication身份
			if (!(principal instanceof Authentication) || !((Authentication) principal).isAuthenticated()) {
				throw new InsufficientAuthenticationException(
						"User must be authenticated with Spring Security before authorization can be completed.");
			}

			ClientDetails client = getClientDetailsService().loadClientByClientId(authorizationRequest.getClientId());

			//解析的重定向URI是来自参数的重定向URI或者来自clientDetails的重定向URI。不管怎样，我们都需要在授权请求中存储它。
			String redirectUriParameter = authorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI);
			String resolvedRedirect = redirectResolver.resolveRedirect(redirectUriParameter, client);
			if (!StringUtils.hasText(resolvedRedirect)) {
				throw new RedirectMismatchException(
						"A redirectUri must be either supplied or preconfigured in the ClientDetails");
			}
			authorizationRequest.setRedirectUri(resolvedRedirect);

			//校验client请求的是一组有效的scope,通过比对表oauth_client_detail
			oauth2RequestValidator.validateScope(authorizationRequest, client);

			//预同意处理(ApprovalStoreUserApprovalHandler)
            ///1. 校验所有的scope是否已经全部是自动同意授权，如果全部自动授权同意，则设置authorizationRequest
            ///中属性approved为true,否则走2
            ///2. 查询client_id下所有oauth_approvals，校验在有效时间内Scope授权的情况，如果在有效时间内Scope授权全部同意，
            ///则设置authorizationRequest中属性approved为true,否则为false
			authorizationRequest = userApprovalHandler.checkForPreApproval(authorizationRequest,
					(Authentication) principal);
			// TODO: is this call necessary?
			boolean approved = userApprovalHandler.isApproved(authorizationRequest, (Authentication) principal);
			authorizationRequest.setApproved(approved);

			// 如果预授权结果是同意，直接将code重定向到redirect_uri
			if (authorizationRequest.isApproved()) {
				if (responseTypes.contains("token")) {
					return getImplicitGrantResponse(authorizationRequest);
				}
				if (responseTypes.contains("code")) {
					return new ModelAndView(getAuthorizationCodeResponse(authorizationRequest,
							(Authentication) principal));
				}
			}
			//将authorizationRequest和authorizationRequest的不可变映射存储在会话中，
			// 该映射将用于在approveOrDeny（）中进行验证
			model.put(AUTHORIZATION_REQUEST_ATTR_NAME, authorizationRequest);
			model.put(ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, unmodifiableMap(authorizationRequest));

			return getUserApprovalPageResponse(model, authorizationRequest, (Authentication) principal);

		}
		catch (RuntimeException e) {
			sessionStatus.setComplete();
			throw e;
		}

	}
```




