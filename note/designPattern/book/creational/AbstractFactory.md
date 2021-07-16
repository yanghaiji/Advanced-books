## 抽象工厂模式

## 模式定义

抽象工厂模式(Abstract Factory Pattern)：提供一个创建一系列相关或相互依赖对象的接口，而无须指定它们具体的类。抽象工厂模式又称为Kit模式，属于对象创建型模式。

## 模式结构

抽象工厂模式包含如下角色：

- AbstractFactory：抽象工厂
- ConcreteFactory：具体工厂
- AbstractProduct：抽象产品
- Product：具体产品

![../_images/AbatractFactory.jpg](https://design-patterns.readthedocs.io/zh_CN/latest/_images/AbatractFactory.jpg)

## 时序图

![../_images/seq_AbatractFactory.jpg](https://design-patterns.readthedocs.io/zh_CN/latest/_images/seq_AbatractFactory.jpg)