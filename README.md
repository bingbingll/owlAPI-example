# owlAPI-example
这是一个基于owl-api创建的示例程序，演示如何创建一个概念图，并在此概念图上增加实例数据。
使用前需要熟悉IRI,OWL,RDF,RDFS的概念和语法。这些是很有必要的否者你很难理解什么是概念图！
其次需要你下载protege软件，你可以使用该软件手动创建一个概念图来加深你的了解。
同时也可以通过该软件打开本项目生成的文件进行查看。
#相关链接  
RDF: https://www.runoob.com/rdf/rdf-tutorial.html  
protege: https://protege.stanford.edu/products.php#desktop-protege
owlapi: https://github.com/owlcs/owlapi  
# owl相关介绍  
概念：OWL是一种本体表示语言。常用于定义本体，换句话说相当于定义关系型数据库的一张表。提供快速、灵活的数据建模能力。  
常用的属性介绍：  
（1）	owl：class：OWL最重要的定义元素，定义了因共有某些属性而同属一组的一些个体，本体的大部分推理能力是基于类别推理的。   
（2）	rdfs：subClassOf：表示两个类的子属关系，可以指定一个或多个关于“一个类是另一类的子类”的陈述来创建一个类结构。    
（3）	rdfs：subPropertyOf：是一个二元关系，表示属性，表示两个属性的子属关系。通过称为一个或多个陈述式声明“某属性是另外一个或多个属性的子属性”可建立属性层次。   
（4）	owl：DatatypeProperty：是OWL的数据类型属性术语，它表示类实例与RDF文字或XML模式数据类型间的关系。   
（5）	owl：ObjectProperty：是OWL的对象属性定义术语，它表示两个类间的关系。owl：ObjectProperty和owl：DatatypeProperty都是RDF类rdf：Property的子类。   
（6）	rdfs：domain：定义了一个属性的定义域，用来约束该属性可以适用的个体。如果一个个体以一个属性和另一个体关联，并且该属性使用某个类别作为它的一个定义域，那么该个体必然属于这个类。   
（7）	rdfs：range：定义了一个属性的值域，用作限制该个体可以成为属性的值。  
（8）	owl：equivalentClass：术语可以声明两个类为等价类，即它们拥有相同的实例。等价性可以创建同义类。   
（9）	owl：equivalentProperty：术语可以声明两个属性为等价属性。相互等价的属性将一个个体关联到同一组别个体。它也可以被称为创建同义属性。  

# 工程介绍  
## 使用示例  
推荐使用 turtle 文件进行操作方便阅读。本工程中 [OntologyHelper](https://github.com/bingbingll/owlAPI-example/blob/master/src/main/java/com/example/demo/utils/OntologyHelper.java) 类封装了一些常用的操作类可以直接方便使用。  
示例程序[DemoApplicationTests](https://github.com/bingbingll/owlAPI-example/blob/master/src/test/java/com/example/demo/DemoApplicationTests.java)   
## owlAPI使用心得  
1、OWL-API 非我们所理解的面向对象常规调用操作，他的操作相当于你要创建什么对象首先要get一个然后根据个节点在get(object var) 这样放入，不是set操作。   
2、因此我们需要借助protege客户端进行分析操作。所有的放入都要使用 *Axiom 相关接口放入。比如我们要创建一个class然后需要进行断言放入。    
3、owl api 可以创建概念图也可以创建实例图，概念图就是一个设计，实例图就是真实的业务数据。  
