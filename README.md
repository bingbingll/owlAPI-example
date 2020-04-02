# owlAPI-example
本工程基于owl-api创建概念图既实例图的示例程序，演示如何创建一个概念图，并在此概念图上增加实例数据。
使用前需要熟悉IRI,OWL,RDF,RDFS的概念和语法。这些是很有必要的否者你很难理解什么是概念图！
其次需要你下载protege软件，你可以使用该软件手动创建一个概念图来加深你的了解。
同时也可以通过该软件打开本项目生成的文件进行查看。
实际项目中时通过owlapi创建一个概念图，然后依据概念图的规定抽取业务数据库的数据存放到图形数据库中，然后通过图形数据库进行查询进行页面
展示。
# 相关链接  
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
1、强烈建议使用protege客户端先手动设计一个图，并在该图中增加实例数据，然后在看本工程有助于快速了解，否则很难理解api的设计。<br />
2、OWL-API 非我们所理解的面向对象常规调用操作，他的框架时面向对象，但是你使用的接口是过程编写，例如我们要给一个数据属性添加label看如下代码：<br />
>       
    //定义全局数据属性，既实际的属性要以名词命名。
    OWLDataProperty hasName = factory.getOWLDataProperty(IRI.create("#name"));
    //DataProperty添加省略可查看本示例工程
    //获取一个注解属性标签类
    OWLAnnotationProperty nameLabel = factory.getRDFSLabel();
    //定义一个文字类并赋值
    OWLLiteral nameLiteral = factory.getOWLLiteral("名称");
    //获取一个全局的注解类并添加标签栏和文字类
    OWLAnnotation owlAnnotation = factory.getOWLAnnotation(nameLabel, nameLiteral);
    //获取一个注解断言公理然后将要添加的标签数据属性类和全局的注解类进行绑定
    OWLAnnotationAssertionAxiom nameLabelAxiom = factory.getOWLAnnotationAssertionAxiom(hasName.getIRI(), owlAnnotation);
    Set<OWLAnnotationAssertionAxiom> dataAnnotationAxiomSet = new HashSet<>();
    //添加到集合中
    dataAnnotationAxiomSet.add(nameLabelAxiom);
    //接口调用添加
    manager.addAxioms(ontology,dataAnnotationAxiomSet); 
    //以上操作可以简单理解为每一个知识点都是一个对象，但需要你将多个对象组装成一个文件。
3、所有的放入都要使用 *Axiom 相关接口放入。比如我们要创建一个class然后需要进行断言放入。<br />
4、owl api 可以创建概念图也可以创建实例图，概念图就是schema相当于数据库，一个类相当于表的设计，实例图就是真实的业务数据。<br />
5、所有添加的词汇操作都在owlapi-distribution-5.1.12.jar的org.semanticweb.owlapi.vocab 包下，这里涵盖了很多用到的词汇和类。
示例：
>   
>>
    // 要对整张图添加注解.
    String VersionInfo="这是一个 Comment 信息";
    //首先设置一个文字对象
    OWLLiteral lit = df.getOWLLiteral(VersionInfo,"zh");
    // 创建注解并于指定是哪个词汇的常量
    OWLAnnotationProperty owlAnnotationProperty = df.getOWLAnnotationProperty(OWLRDFVocabulary.OWL_VERSION_INFO
          .getIRI());
    //将文字对象和注解属性进行绑定获取到注解类
    OWLAnnotation anno = df.getOWLAnnotation(owlAnnotationProperty, lit);
    // 现在为本体整张图添加注解并进行应用。
    m.applyChange(new AddOntologyAnnotation(o, anno));
    //以上操作方式可以 举一反三 针对不同的需求使用org.semanticweb.owlapi.vocab包下不同的词汇类中的不同常量进行添加。
>>  
    //结果展示
    <http://graphSchema.org/test> rdf:type owl:Ontology ;
                                  rdf:Description "这是一个 Description 信息！"@zh ;
                                  rdfs:comment "这是一个 Comment 信息"@zh ;
                                  owl:versionInfo "这是一个 VersionInfo 信息！"@zh .

6、若是还有不清楚不知道如何编写时请查看 owl api 第三版，第四版官网示例代码：[code-examples](https://github.com/owlcs/owlapi/wiki/Documentation#code-examples)