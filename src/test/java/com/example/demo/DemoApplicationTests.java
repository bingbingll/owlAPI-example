package com.example.demo;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;


@SpringBootTest
class DemoApplicationTests {
	//定义存放读取的文件路径
	String getPathname() {
		return System.getProperty("user.dir") + "\\src\\main\\resources\\ontology\\";
	}

	/**
	 * 创建一个本体类文件，默认生成owl格式文件，也可以指定生成的文件格式，类使用org.semanticweb.owlapi.formats 包下的实例类例如：
	 * manager.saveOntology(ontology,new TurtleDocumentFormat(), IRI.create(file.toURI()));
	 */
	@Test
	void createInitOWLFile() {
		try {
			//获取本体工程管理器
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			File file = new File(getPathname() + "owlapiexamples.owl");
			String base = "http://com.thinker.example";

			OWLOntology ontology = manager.createOntology(IRI.create(base));

			System.out.println("loaded ontology: " + ontology);

			boolean newFile = file.createNewFile();
			manager.saveOntology(ontology, IRI.create(file.toURI()));
			//释放资源
			manager.removeOntology(ontology);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (IOException | OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}

	@Test
	void createInitTTLFile() {
		try {
			File file = new File(getPathname() + "examples.ttl");
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			String base = "http://com.thinker.example";
			OWLOntology ontology = manager.createOntology(IRI.create(base));
			System.out.println("loaded ontology: " + ontology);

			boolean newFile = file.createNewFile();
			//保存创建
			manager.saveOntology(ontology, new TurtleDocumentFormat(), IRI.create(file.toURI()));
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建一个概念图包含，用户，省份，公司三个类及类的属性
	 */
	@Test
	void createConcept() {
		try {
			//指定生成的文件名称
			File file = new File(getPathname() + "examples.ttl");

			//获取一个本体管理
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

			OWLDataFactory factory = manager.getOWLDataFactory();
			//定义一个本体IRI既对应的URL，这个url不是可以被浏览器解析到的
			String base = "http://example.com/owl/examples/";
			//创建本体
			OWLOntology ontology = manager.createOntology(IRI.create(base));
			//定义本体类给定前缀
			//PrefixManager pm = new DefaultPrefixManager(null, null, base);
			//定义三个本体类
			OWLClass user = factory.getOWLClass(IRI.create(base, "User"));
			OWLClass province = factory.getOWLClass(IRI.create(base, "Province"));
			OWLClass company = factory.getOWLClass(IRI.create(base, "Company"));


			//添加到公理换句话本体表达的基本陈述
			Set<OWLDeclarationAxiom> axiomSet = new HashSet<>(3);
			//添加类
			axiomSet.add(factory.getOWLDeclarationAxiom(user));
			axiomSet.add(factory.getOWLDeclarationAxiom(province));
			axiomSet.add(factory.getOWLDeclarationAxiom(company));


			//添加完成
			manager.addAxioms(ontology, axiomSet);


			//定义类归属属性,关系使用has或is 作为前缀。
			//定义人所属的省份和人所属的公司，公司所属的省份
			OWLObjectProperty hasProvince = factory.getOWLObjectProperty(IRI.create("#hasProvince"));
			OWLObjectProperty hasCompany = factory.getOWLObjectProperty(IRI.create("#hasCompany"));


			OWLDeclarationAxiom hasCompanyAxiom = factory.getOWLDeclarationAxiom(hasCompany);
			OWLDeclarationAxiom hasProvinceAxiom = factory.getOWLDeclarationAxiom(hasProvince);

			Set<OWLDeclarationAxiom> hasAxiomSet = new HashSet<>(2);
			hasAxiomSet.add(hasCompanyAxiom);
			hasAxiomSet.add(hasProvinceAxiom);
			manager.addAxioms(ontology, hasAxiomSet);


			//定义实例的属性，既实际的属性要以名词命名。
			OWLDataProperty hasName = factory.getOWLDataProperty(IRI.create("#name"));
			OWLDataProperty hasAge = factory.getOWLDataProperty(IRI.create("#age"));
			OWLDataProperty hasLongitude = factory.getOWLDataProperty(IRI.create("#longitude"));
			OWLDataProperty hasLatitude = factory.getOWLDataProperty(IRI.create("#latitude"));
			OWLDataProperty hasAddr = factory.getOWLDataProperty(IRI.create("#addr"));
			//将数据属性添加到 OWLDeclarationAxiom 中。
			OWLDeclarationAxiom hasNameAxiom = factory.getOWLDeclarationAxiom(hasName);
			OWLDeclarationAxiom hasAgeAxiom = factory.getOWLDeclarationAxiom(hasAge);
			OWLDeclarationAxiom hasLongitudeAxiom = factory.getOWLDeclarationAxiom(hasLongitude);
			OWLDeclarationAxiom hasLatitudeAxiom = factory.getOWLDeclarationAxiom(hasLatitude);
			OWLDeclarationAxiom hasAddrAxiom = factory.getOWLDeclarationAxiom(hasAddr);


			//添加到set中方便操作。
			Set<OWLDeclarationAxiom> dataHasAxiomSet = new HashSet<>(5);
			//将name的标签进行添加
			dataHasAxiomSet.add(hasNameAxiom);
			dataHasAxiomSet.add(hasAgeAxiom);
			dataHasAxiomSet.add(hasLongitudeAxiom);
			dataHasAxiomSet.add(hasLatitudeAxiom);
			dataHasAxiomSet.add(hasAddrAxiom);
			manager.addAxioms(ontology, dataHasAxiomSet);

			//给属性添加标签
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

			//定义对象属性归于
			Set<OWLAxiom> domainsAndRanges = new HashSet<OWLAxiom>();
			//domain代表归属的类，range代表值类型
			domainsAndRanges.add(factory.getOWLObjectPropertyDomainAxiom(hasProvince, province));
			domainsAndRanges.add(factory.getOWLObjectPropertyRangeAxiom(hasProvince, province));

			domainsAndRanges.add(factory.getOWLObjectPropertyDomainAxiom(hasCompany, company));
			domainsAndRanges.add(factory.getOWLObjectPropertyRangeAxiom(hasCompany, company));
			//定义实例属性归属类
			domainsAndRanges.add(factory.getOWLDataPropertyDomainAxiom(hasAge, user));
			domainsAndRanges.add(factory.getOWLDataPropertyRangeAxiom(hasAge, factory.getIntegerOWLDatatype()));

			domainsAndRanges.add(factory.getOWLDataPropertyDomainAxiom(hasName, user));

			domainsAndRanges.add(factory.getOWLDataPropertyDomainAxiom(hasName, province));

			domainsAndRanges.add(factory.getOWLDataPropertyDomainAxiom(hasName, company));
			domainsAndRanges.add(factory.getOWLDataPropertyDomainAxiom(hasAddr, company));
			domainsAndRanges.add(factory.getOWLDataPropertyDomainAxiom(hasLatitude, company));
			domainsAndRanges.add(factory.getOWLDataPropertyRangeAxiom(hasLatitude, factory.getDoubleOWLDatatype()));

			domainsAndRanges.add(factory.getOWLDataPropertyDomainAxiom(hasLongitude, company));
			domainsAndRanges.add(factory.getOWLDataPropertyRangeAxiom(hasLongitude, factory.getDoubleOWLDatatype()));

			//定义类的标签
			OWLAnnotation userAnnotation = factory.getOWLAnnotation(factory.getRDFSLabel(), factory.getOWLLiteral("用户"));
			OWLAnnotation provinceAnnotation = factory.getOWLAnnotation(factory.getRDFSLabel(), factory.getOWLLiteral("省份"));
			OWLAnnotation companyAnnotation = factory.getOWLAnnotation(factory.getRDFSLabel(), factory.getOWLLiteral("公司"));

			//给类添加标签
			domainsAndRanges.add(factory.getOWLAnnotationAssertionAxiom(user.getIRI(), userAnnotation));
			domainsAndRanges.add(factory.getOWLAnnotationAssertionAxiom(province.getIRI(), provinceAnnotation));
			domainsAndRanges.add(factory.getOWLAnnotationAssertionAxiom(company.getIRI(), companyAnnotation));

			manager.addAxioms(ontology, domainsAndRanges.stream());

			manager.saveOntology(ontology, new TurtleDocumentFormat(), IRI.create(file.toURI()));
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加实例数据。根据定义好的概念图，及对象属性，数据属性，向概念图增加实际的数据。
	 */
	@Test
	void addData() {
		try {
			File file = new File(getPathname() + "examples.ttl");
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			//加载这个文件并转换为本体对象
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
			OWLDataFactory factory = manager.getOWLDataFactory();

			//创建实例既实际的业务数据
			OWLNamedIndividual baidu = factory.getOWLNamedIndividual(IRI.create("百度"));
			OWLNamedIndividual tengxun = factory.getOWLNamedIndividual(IRI.create("腾讯"));
			OWLNamedIndividual ali = factory.getOWLNamedIndividual(IRI.create("阿里"));

			OWLNamedIndividual zhangsan = factory.getOWLNamedIndividual(IRI.create("张三"));

			OWLNamedIndividual lisi = factory.getOWLNamedIndividual(IRI.create("李四"));

			OWLNamedIndividual beijing = factory.getOWLNamedIndividual(IRI.create("北京"));
			OWLNamedIndividual hangzhou = factory.getOWLNamedIndividual(IRI.create("杭州"));


			Set<OWLAxiom> indaxions = new HashSet<>();

			//给实例张三添加标签和注解
			OWLAnnotation label = factory.getOWLAnnotation(factory.getRDFSLabel(), factory.getOWLLiteral("标签张三"));
			OWLAnnotation comment = factory.getOWLAnnotation(factory.getRDFSComment(), factory.getOWLLiteral("这是一个解释的注解，这个人是张三"));
			OWLAnnotationAssertionAxiom assertionAxiomLabel = factory.getOWLAnnotationAssertionAxiom(zhangsan.getIRI(), label);
			OWLAnnotationAssertionAxiom assertionAxiomComment = factory.getOWLAnnotationAssertionAxiom(zhangsan.getIRI(), comment);
			indaxions.add(assertionAxiomLabel);
			indaxions.add(assertionAxiomComment);


			//获取文件中所有定义的类
			java.util.Set<OWLClass> classesInSignature = ontology.getClassesInSignature();
			//添加实例属于那个class下的。
			for (OWLClass owlClass : classesInSignature) {
				String s = owlClass.getIRI().getRemainder().get();
				if (s.equals("Company")) {
					indaxions.add(factory.getOWLClassAssertionAxiom(owlClass, baidu));
					indaxions.add(factory.getOWLClassAssertionAxiom(owlClass, tengxun));
					indaxions.add(factory.getOWLClassAssertionAxiom(owlClass, ali));
				} else if (s.equals("Province")) {
					indaxions.add(factory.getOWLClassAssertionAxiom(owlClass, hangzhou));
					indaxions.add(factory.getOWLClassAssertionAxiom(owlClass, beijing));
				} else if (s.equals("User")) {
					indaxions.add(factory.getOWLClassAssertionAxiom(owlClass, zhangsan));
					indaxions.add(factory.getOWLClassAssertionAxiom(owlClass, lisi));
				}
			}

			//获取图中定义的所有对象属性。
			java.util.Set<OWLObjectProperty> objectPropertiesInSignature = ontology.getObjectPropertiesInSignature();
			for (OWLObjectProperty owlObjectProperty : objectPropertiesInSignature) {
				String s = owlObjectProperty.getIRI().getRemainder().get();
				System.out.println(s);

				if (s.equals("hasCompany")) {
					//添加用户的所在的公司
					//张三在阿里工作
					indaxions.add(factory.getOWLObjectPropertyAssertionAxiom(owlObjectProperty, zhangsan, ali));
					//李四在百度工作
					indaxions.add(factory.getOWLObjectPropertyAssertionAxiom(owlObjectProperty, lisi, baidu));
				} else if (s.equals("hasProvince")) {
					//添加用户或公司所在的地点
					//张三在杭州。
					indaxions.add(factory.getOWLObjectPropertyAssertionAxiom(owlObjectProperty, zhangsan, hangzhou));
					//李四在北京
					indaxions.add(factory.getOWLObjectPropertyAssertionAxiom(owlObjectProperty, lisi, beijing));
					//阿里在杭州
					indaxions.add(factory.getOWLObjectPropertyAssertionAxiom(owlObjectProperty, ali, hangzhou));
					//百度在北京
					indaxions.add(factory.getOWLObjectPropertyAssertionAxiom(owlObjectProperty, baidu, beijing));

				}
			}

			//给部分属性赋值

			//获取定义的数据属性集合
			Set<OWLDataProperty> dataPropertiesInSignature = ontology.getDataPropertiesInSignature();
			Stream<OWLDataProperty> owlDataPropertyStream = ontology.dataPropertiesInSignature();
			owlDataPropertyStream.forEach(owlDataProperty -> {
				String s = owlDataProperty.getIRI().getRemainder().get();
				//写法同下...
			});
			for (OWLDataProperty owlDataProperty : dataPropertiesInSignature) {
				String s = owlDataProperty.getIRI().getRemainder().get();
				System.out.println("remainder:" + s);
				if (s.equals("name")) {
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, zhangsan, factory.getOWLLiteral("zhangsan")));
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, lisi, factory.getOWLLiteral("lisi")));
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, ali, factory.getOWLLiteral("ali")));
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, tengxun, factory.getOWLLiteral("tengxun")));
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, beijing, factory.getOWLLiteral("beijing")));
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, hangzhou, factory.getOWLLiteral("hangzhou")));
				} else if (s.equals("age")) {
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, zhangsan, factory.getOWLLiteral(32)));
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, lisi, factory.getOWLLiteral(29)));
				} else if (s.equals("addr")) {
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, beijing, factory.getOWLLiteral("中国.北京市")));
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, hangzhou, factory.getOWLLiteral("中国.浙江省.杭州市")));
				} else if (s.equals("latitude")) {
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, beijing, factory.getOWLLiteral(113.5522)));
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, hangzhou, factory.getOWLLiteral(112.6632)));
				} else if (s.equals("longitude")) {
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, beijing, factory.getOWLLiteral(96.21335)));
					indaxions.add(factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, hangzhou, factory.getOWLLiteral(96.1252)));
				}
			}

			//添加到图中，
			manager.addAxioms(ontology, indaxions.stream());
			//保存图
			manager.saveOntology(ontology, new TurtleDocumentFormat(), IRI.create(file.toURI()));
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}
}
