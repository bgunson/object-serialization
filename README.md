
## Reflective Object Serialization Project

[Demo vid](https://drive.google.com/file/d/1dMy9y4_NcssSt6rjaxNv1t4xy7GpSj2X/view?usp=sharing)

This is a project that lets a user create 5 different objects and input their fields which is then 
serialized to a JSON string using [org.json](https://github.com/stleary/JSON-java). These operations take place on a theoretical server. The serialized
object is then sent to a socket via a server socket where it gets deserialized and vizualized 
on the client side. 

I have used a Maven project structure to organize my external libraries and test classes. It is most
easily ran from an IDE that supports Maven projects. I have represented the server/client in respective
packages in _src/main/java_. There are two main methods, one being on the server side in _Sender.java_ and the 
other being in _Receiver.java_ on the client side. In addition the 5 test objects which the user can create
reside in the _objects_ package. All creation and de/serialization is acheived at runtime via the _java.lang.reflect_
package.
