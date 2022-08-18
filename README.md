# A Simple Spring Framework

A simple framework for Java MVC, simulation of the Spring MVC framework.

- IOC
- AOP
- MVC, embedded Tomcat Server
- Annotation development, no XML configuration

## IOC

Dependency injected by Type. If the object to be injected is a class, its instance will be injected, and if the object is an interface, its implementation will be injected.

## AOP

V1: Support one aspect that takes effect for a given class, without using AspectJ

V2: Support one aspect that takes effect for a point-cut expression, using AspectJ

V3: Support multi aspects that takes effect for a point-cut expression, using AspectJ. `@Order` annotation can be used to control the order of execution of enhanced classes.

## MVC

Embedded Tomcat Server.

The `RequestMapping` annotation specifies the request method(GET or POST) and path.

The `ResponseBody` annotation determines whether the return value is in JSON format.

The `RequestParam` annotation sets an alias for the parameter, which is required for each parameter. The annotation's `value` is the real parameter name, which is due to the fact that Java will erase the field names after compiling the class file. Moreover, the parameter type should be Java primitive data type.

## Reference

[Doodle](https://github.com/zzzzbw/doodle)
